package com.boha.skunk.vertex;


import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

@SuppressWarnings("all")
@Service
public class PdfSummarizer {

    static final String mm = "\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35 " +
            "PdfSummarizer \uD83D\uDC9C";
    static final Logger logger = Logger.getLogger(PdfSummarizer.class.getSimpleName());

    String projectId = "sgela-ai-33";
    String location = "us-east4";
    String modelName = "gemini-1.5-pro-preview-0409";


    // Analyzes the given video input.
    public  String summarizePdf(String pdfUrl, String prompt)
            throws IOException {
        // Initialize client that will be used to send requests. This client only needs
        // to be created once, and can be reused for multiple requests.
        logger.info(mm + " Summarizing PDF with VertexAI ...");
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            String pdfUri = convertUrlToUriString(pdfUrl);

            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            GenerateContentResponse response = model.generateContent(
                    ContentMaker.fromMultiModalData(
                            prompt,
                            PartMaker.fromMimeTypeAndData("application/pdf", pdfUri)
                    ));

            String output = ResponseHandler.getText(response);
            logger.info(mm + " Summarizing PDF result: " + output);
            return output;
        } catch (Exception e) {
            logger.info(mm + " Summarizing PDF error: " + e.getMessage());
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }
    public static String convertUrlToUriString(String url) {
        try {
            URI uri = new URI(url);
            return uri.toString();
        } catch (URISyntaxException e) {
            System.err.println("Invalid URL: " + url);
            return null;
        }
    }
}
