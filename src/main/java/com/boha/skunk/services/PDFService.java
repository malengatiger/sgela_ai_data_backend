package com.boha.skunk.services;


import com.boha.skunk.data.ExamLink;
import com.boha.skunk.data.SummarizedPdf;
import com.boha.skunk.util.E;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.CountTokensRequest;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("all")
@Service
public class PDFService {

    String projectId = "busha-2024";
    String location = "europe-west1";
    String modelName = "gemini-1.5-flash-001";
    String mm = E.AMP + E.AMP + E.AMP + E.AMP + "PDFService ";
    private static final Logger logger = LoggerFactory.getLogger(PDFService.class);
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
     VertexAI vertexAI;
     GenerativeModel model;
     final SgelaFirestoreService sgelaFirestoreService;
     final PDFCreator pdfCreator;

    public PDFService(SgelaFirestoreService sgelaFirestoreService, PDFCreator pdfCreator) {
        this.sgelaFirestoreService = sgelaFirestoreService;
        this.pdfCreator = pdfCreator;
        var msg = mm + "constructor, model: " + modelName + " - projectId: "
                + projectId + " - location: " + location ;
        logger.info(msg);
    }

    /*
    You are an expert document summarizer. This document is an exam paper. Extract all the concepts necessary to prepare for a similar exam.
     */
    public SummarizedPdf summarizePdf(String instruction, Long examLinkId, int weeks)
            throws IOException, ExecutionException, InterruptedException {
        var msg = mm + "summarizePdf:  " + "instruction:  " + instruction + " - examLinkId: " + examLinkId;
        ExamLink examLink = sgelaFirestoreService.getExamLink(examLinkId);
        logger.info(msg);
        SummarizedPdf summarizedPdf = new SummarizedPdf();
        summarizedPdf.setPdfUri(examLink.cloudStorageUri);
        summarizedPdf.setExamLinkId(examLink.getId());
        summarizedPdf.setDate(String.valueOf(new DateTime().toDateTimeISO()));


        if (vertexAI == null) {
            vertexAI = new VertexAI(projectId, location);
            model = new GenerativeModel(modelName, vertexAI);

        }
        Part part = PartMaker.fromMimeTypeAndData("application/pdf", examLink.getCloudStorageUri());
        Content content = ContentMaker.fromMultiModalData(instruction, part);
        GenerateContentResponse response = model.generateContent(content);

        String output = ResponseHandler.getText(response);
        var msg2 = mm + "summarized Pdf: " + output;
        logger.info(msg2);

        summarizedPdf.setConcepts(output);
        //
        var msg3a = mm + "create a lessonPlan  .... " ;
        logger.info(msg3a);

        String instruction2 = "Create a detailed preparation plan for the exam based on the concepts below. " +
                "Arrange the plan into "+weeks+" weeks of preparation and research. " +
                "Be sure to include web links for research:\n " + output;
        Content content2 = ContentMaker.fromString(instruction2);
        GenerateContentResponse response2 = model.generateContent(content2);

        String output2 = ResponseHandler.getText(response2);
        var msg3 = mm + " lessonPlan  response: " + output2;
        summarizedPdf.setLessonPlan(output2);
        logger.info(msg3);
        int count = CountTokensRequest.getDefaultInstance().getContentsCount();
        var msg3b = mm + " content count: " + count;
        logger.info(msg3b);

        pdfCreator.createPDF(summarizedPdf);
        sgelaFirestoreService.addSummarizedPdf(summarizedPdf);
        logger.info(mm + "summarizePdf:  finished");
        return summarizedPdf;

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
        String message = String.format("googleCredentials is %s .", G.toJson(creds));
        logger.info(message);
        return creds;
    }
}
