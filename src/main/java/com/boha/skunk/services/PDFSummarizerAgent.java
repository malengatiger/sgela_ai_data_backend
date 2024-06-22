package com.boha.skunk.services;


import com.boha.skunk.data.ExamLink;
import com.boha.skunk.data.SummarizedExam;
import com.boha.skunk.util.E;
import com.boha.skunk.util.ErrorMessage;
import com.boha.skunk.util.SummarizerException;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("all")
@Service
public class PDFSummarizerAgent {

    String projectId = "busha-2024";
    String location = "europe-west1";
    String modelName = "gemini-1.5-flash-001";
    String mm = E.AMP + E.AMP + E.AMP + E.AMP + "PDFSummarizerAgent ";
    private static final Logger logger = LoggerFactory.getLogger(PDFSummarizerAgent.class);
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    VertexAI vertexAI;
    GenerativeModel model;
    final SgelaFirestoreService sgelaFirestoreService;
    final CloudStorageService cloudStorageService;
    final HTMLCreator htmlCreator;
    @Value("${spring.profiles.active}")
    private String activeProfile;

    /**
     * Service to handle summarization of Exam pdf file
     *
     * @param sgelaFirestoreService
     * @param htmlCreator
     */
    public PDFSummarizerAgent(SgelaFirestoreService sgelaFirestoreService, CloudStorageService cloudStorageService, HTMLCreator htmlCreator) {
        this.sgelaFirestoreService = sgelaFirestoreService;
        this.cloudStorageService = cloudStorageService;
        this.htmlCreator = htmlCreator;
        var msg = mm + "constructor, model: " + modelName + " - projectId: "
                + projectId + " - location: " + location;
        logger.info(msg);
    }

    /**
     * Summarize Exam pdf file and create a lesson plan in html format
     *
     * @param examLinkId
     * @param weeks
     * @return SummarizedExam object @see @{@link SummarizedExam}
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public SummarizedExam summarizePdf(Long examLinkId, int weeks)
            throws IOException, ExecutionException, InterruptedException, SummarizerException {
        var msg = mm + "summarizePdf:  " + "examLinkId:  " + examLinkId + " - weeks: " + weeks;
        logger.info(msg);

        String instruction1 = "You are an expert document summarizer. This document is an exam paper. " +
                "Extract all the concepts necessary to prepare and conduct research for a similar exam.  " +
                "The output should always be in markdown format. Where possible, respond in the same language as the input.";

        try {
            ExamLink examLink = sgelaFirestoreService.getExamLink(examLinkId);
            //
            SummarizedExam summarizedExam = new SummarizedExam();
            summarizedExam.setPdfUri(examLink.cloudStorageUri);
            summarizedExam.setExamLinkId(examLink.getId());
            summarizedExam.setDate(String.valueOf(new DateTime().toDateTimeISO()));

            setVertex();
            String concepts = processConcepts(examLink.getCloudStorageUri(), instruction1, summarizedExam);
            if (concepts.length() == 0) {
                throw new RuntimeException("No concepts found");
            }
            createLessonPlan(weeks, concepts, summarizedExam);
            answerQuestions(examLink.getCloudStorageUri(), summarizedExam);
            htmlCreator.createHtmlFile(summarizedExam, examLink);

            sgelaFirestoreService.addSummarizedExam(summarizedExam);

            var msg3a = mm + " Total Tokens used  : " + E.FERN
                    + summarizedExam.getTotalTokens() + " " + E.FERN;
            logger.info(msg3a);

            logger.info(mm + E.LEAF + E.LEAF + E.LEAF
                    + " SummarizePdf Agent completed!! " + E.LEAF + E.LEAF + E.LEAF);
            return summarizedExam;
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SummarizerException(new ErrorMessage(
                    400, DateTime.now().toDateTimeISO().toString(),
                    " summarizePdf failed: " + e.getMessage()));
        }

    }

    private String processConcepts(String uri, String instruction, SummarizedExam summarizedExam) throws IOException {
        if (activeProfile.equals("dev")) {
            var msg2 = mm + " start processConcepts: " + uri + "\n -  instruction: " + E.RED_DOT + instruction;
            logger.info(msg2);
        }
        Part part = PartMaker.fromMimeTypeAndData("application/pdf", uri);
        Content content = ContentMaker.fromMultiModalData(instruction, part);
        GenerateContentResponse response = model.generateContent(content);

        String concepts = ResponseHandler.getText(response);
        summarizedExam.setConcepts(concepts);
        setTokens(response, summarizedExam);

        if (activeProfile.equals("dev")) {
            var msg2 = mm + "summarized Exam concepts: " + concepts.length() + " bytes " + E.RED_DOT;
            logger.info(msg2);
        }
        return concepts;
    }

    private void setVertex() {
        if (vertexAI == null) {
            vertexAI = new VertexAI(projectId, location);
            model = new GenerativeModel(modelName, vertexAI);
            logger.info(mm + "vertexAI and model set up: " + model.getModelName());
        }
    }

    private void answerQuestions(String uri, SummarizedExam summarizedExam) throws IOException {
        if (activeProfile.equals("dev")) {
            var msg3a = mm + "answerQuestions from exam pdf  .... ";
            logger.info(msg3a);
        }
        String instruction = "You are an expert Exam tutor. This document is an exam paper. " +
                "Extract all the questions in the exam paper and answer each in detail. " +
                "If you cannot answer the question please say so. " +
                "Identify each question by it's number.  " +
                "The output should always be in markdown format. " +
                "Where possible, respond in the same language as the input.";

        Part part = PartMaker.fromMimeTypeAndData("application/pdf", uri);
        Content content = ContentMaker.fromMultiModalData(instruction, part);
        GenerateContentResponse response = model.generateContent(content);

        String answers = ResponseHandler.getText(response);
        summarizedExam.setAnswers(answers);
        setTokens(response, summarizedExam);

        if (activeProfile.equals("dev")) {
            var msg3a = mm + " ... Exam questions answered  ....  " + E.BLUE_DOT
                    + " " + answers.length();
            logger.info(msg3a);
        }
    }

    private void setTokens(GenerateContentResponse response, SummarizedExam summarizedExam) {
        var u = response.getUsageMetadata();
        int totalTokens = u.getTotalTokenCount();
        int promptTokens = u.getPromptTokenCount();
        int candidatesTokens = u.getCandidatesTokenCount();

        summarizedExam.setTotalTokens(summarizedExam.getTotalTokens() + totalTokens);
        summarizedExam.setPromptTokens(summarizedExam.getPromptTokens() + promptTokens);
        summarizedExam.setCandidatesTokens(summarizedExam.getCandidatesTokens() + candidatesTokens);

    }

    private void createLessonPlan(int weeks, String concepts,
                                  SummarizedExam summarizedExam) throws Exception {
        try {
            if (activeProfile.equals("dev")) {
                var msg3a = mm + "create a lessonPlan  .... ";
                logger.info(msg3a);
            }

            String instruction = "Create a detailed preparation plan for the exam based on the concepts below. " +
                    "Arrange the plan into " + weeks + " weeks of preparation and research. " +
                    "Be sure to include web links for research. The output should always be in markdown format.  " +
                    "Where possible, respond in the same language as the input.:\n " + concepts;

            Content content = ContentMaker.fromString(instruction);
//            List<SafetySetting> safetySettings = new ArrayList<>();
//            safetySettings.add(new SafetySetting.getDefaultInstance().toBuilder().setTemperature(0.0).build());
//            model.withSafetySettings(safetySettings);
            GenerateContentResponse contentResponse = model.generateContent(content);

            String lessonPlan = ResponseHandler.getText(contentResponse);
            summarizedExam.setLessonPlan(lessonPlan);
            setTokens(contentResponse, summarizedExam);

            if (activeProfile.equals("dev")) {
                var msg3 = mm + " Exam LessonPlan  response: " + lessonPlan.length() + " bytes " + E.RED_DOT;
                logger.info(msg3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}

//@Lazy
//    @Bean
//    GoogleCredentials googleCredentials() throws IOException {
//        var creds =
//                GoogleCredentials.getApplicationDefault();
//        if (creds.createScopedRequired()) {
//            creds = creds.createScoped(
//                    "https://www.googleapis.com/auth/cloud-platform");
//        }
//        if (activeProfile.equals("dev")) {
//            String message = String.format("googleCredentials are %s .", G.toJson(creds));
//            logger.info(message);
//        }
//        return creds;
//    }
//}
