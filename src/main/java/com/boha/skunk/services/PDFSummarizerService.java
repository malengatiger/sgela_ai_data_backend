package com.boha.skunk.services;


import com.boha.skunk.data.ExamLink;
import com.boha.skunk.data.SummarizedExam;
import com.boha.skunk.util.E;
import com.google.auth.oauth2.GoogleCredentials;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("all")
@Service
public class PDFSummarizerService {

    String projectId = "busha-2024";
    String location = "europe-west1";
    String modelName = "gemini-1.5-flash-001";
    String mm = E.AMP + E.AMP + E.AMP + E.AMP + "PDFSummarizerService ";
    private static final Logger logger = LoggerFactory.getLogger(PDFSummarizerService.class);
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    VertexAI vertexAI;
    GenerativeModel model;
    final SgelaFirestoreService sgelaFirestoreService;
    final HTMLCreator htmlCreator;
    @Value("${spring.profiles.active}")
    private String activeProfile;

    /**
     * Service to handle summarization of Exam pdf file
     *
     * @param sgelaFirestoreService
     * @param htmlCreator
     */
    public PDFSummarizerService(SgelaFirestoreService sgelaFirestoreService, HTMLCreator htmlCreator) {
        this.sgelaFirestoreService = sgelaFirestoreService;
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
            throws IOException, ExecutionException, InterruptedException {
        var msg = mm + "summarizePdf:  " + "examLinkId:  " + examLinkId + " - weeks: " + weeks;
        logger.info(msg);

        String instruction1 = "You are an expert document summarizer. This document is an exam paper. " +
                "Extract all the concepts necessary to prepare and conduct research for a similar exam.  " +
                "The output should always be in markdown format";

        ExamLink examLink = sgelaFirestoreService.getExamLink(examLinkId);
        //
        SummarizedExam summarizedExam = new SummarizedExam();
        summarizedExam.setPdfUri(examLink.cloudStorageUri);
        summarizedExam.setExamLinkId(examLink.getId());
        summarizedExam.setDate(String.valueOf(new DateTime().toDateTimeISO()));


        if (vertexAI == null) {
            vertexAI = new VertexAI(projectId, location);
            model = new GenerativeModel(modelName, vertexAI);
            logger.info(mm + "vertexAI and model set up: " + model.getModelName());
        }
        Part part = PartMaker.fromMimeTypeAndData("application/pdf", examLink.getCloudStorageUri());
        Content content = ContentMaker.fromMultiModalData(instruction1, part);
        GenerateContentResponse response = model.generateContent(content);

        String concepts = ResponseHandler.getText(response);
        summarizedExam.setConcepts(concepts);

        if (activeProfile.equals("dev")) {
            var msg2 = mm + "summarized Exam concepts: " + E.RED_DOT + concepts;
            logger.info(msg2);
        }
        //
        createLessonPlan(weeks, concepts, summarizedExam);
        htmlCreator.createHTML(summarizedExam, examLink);
        sgelaFirestoreService.addSummarizedExam(summarizedExam);

        logger.info(mm + E.LEAF + E.LEAF + E.LEAF
                + " SummarizePdf Agent completed!! " + E.LEAF + E.LEAF + E.LEAF);
        return summarizedExam;

    }

    private void createLessonPlan(int weeks, String concepts,
                                  SummarizedExam summarizedExam) throws IOException {
        if (activeProfile.equals("dev")) {
            var msg3a = mm + "create a lessonPlan  .... ";
            logger.info(msg3a);
        }

        String instruction2 = "Create a detailed preparation plan for the exam based on the concepts below. " +
                "Arrange the plan into " + weeks + " weeks of preparation and research. " +
                "Be sure to include web links for research. The output should always be in markdown format. :\n " + concepts;
        Content content2 = ContentMaker.fromString(instruction2);
        GenerateContentResponse response2 = model.generateContent(content2);

        String lessonPlan = ResponseHandler.getText(response2);
        summarizedExam.setLessonPlan(lessonPlan);

        if (activeProfile.equals("dev")) {
            var msg3 = mm + " Exam LessonPlan  response: " + lessonPlan;
            logger.info(msg3);
        }

    }

    @Lazy
    @Bean
    GoogleCredentials googleCredentials() throws IOException {
        var creds =
                GoogleCredentials.getApplicationDefault();
        if (creds.createScopedRequired()) {
            creds = creds.createScoped(
                    "https://www.googleapis.com/auth/cloud-platform");
        }
        if (activeProfile.equals("dev")) {
            String message = String.format("googleCredentials are %s .", G.toJson(creds));
            logger.info(message);
        }
        return creds;
    }
}
