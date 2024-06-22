package com.boha.skunk.services;

import com.boha.skunk.data.ExamLink;
import com.boha.skunk.data.SummarizedExam;
import com.boha.skunk.data.UploadResponse;
import com.boha.skunk.util.DirectoryUtils;
import com.boha.skunk.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@SuppressWarnings("all")
@Service
public class HTMLCreator {

    final CloudStorageService cloudStorageService;

    /**
     * Service to handle HTML file generation
     *
     * @param cloudStorageService
     */
    public HTMLCreator(CloudStorageService cloudStorageService) {
        this.cloudStorageService = cloudStorageService;
    }

    String mm = E.RED_DOT + E.RED_DOT + E.AMP + "HTMLCreator ";
    private static final Logger logger = LoggerFactory.getLogger(HTMLCreator.class);
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    @Value("${spring.profiles.active}")
    private String activeProfile;

    /**
     * Generate HTML file from @see{@link SummarizedExam} @see{@link ExamLink}
     * Upload file to Cloud Storage
     *
     * @param summarizedExam
     * @param examLink
     * @return @see{@link UploadResponse}
     * @throws IOException
     */
    public UploadResponse createHtmlFile(SummarizedExam summarizedExam, ExamLink examLink) throws IOException {
        if (activeProfile.equals("dev")) {
            var msg3a = mm + "create a HTML document from SummarizedExam  and ExamLink .... ";
            logger.info(msg3a);
        }
        var dir = DirectoryUtils.createDirectoryIfNotExists("summarized");
        var file = new File(dir, summarizedExam.getExamLinkId() + "_" + DateTime.now().toDateTimeISO().toString() + ".html");

        generateHTML(summarizedExam, examLink, file);
        var response = cloudStorageService.uploadFile(file, summarizedExam.getExamLinkId(), 4);


        summarizedExam.setAgentResponseUri(response.gsUri);
        summarizedExam.setAgentResponseUrl(response.downloadUrl);
        //
        if (activeProfile.equals("dev")) {
            logger.info(mm + E.HAND2 + E.HAND2 + "\ncreateHTML completed;  UploadResponse: " + G.toJson(response));
        } else {
            file.delete();
        }

        return response;
    }

    private void generateHTML(SummarizedExam summarizedExam, ExamLink examLink, File file) throws IOException {
        String top = " <!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<div style=\"display: inline-block; margin-bottom: 28px;\">\n" + // Use a div for grouping
                "<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(
                new ClassPathResource("images/sgela_logo_clear.png").getInputStream().readAllBytes())
                + "\" alt=\"Sgela AI Logo\" width=\"48\" height=\"48\">" +
                "<h4 style=\"display: inline-block; margin-left: 10px; font-size: 14px;\">Sgela AI Exam Prepper</h4>\n" +
                "</div>\n<br>" +
                "<h2>" + examLink.getTitle() + "</h2>\n" +
                "<h4>" + examLink.getDocumentTitle() + "</h4>\n" +
                "<h2>" + examLink.getSubject() + "</h2>" +
                "<br><p>This document has been prepared for you using Vertex AI and the Gemini LLM models</p>\n";

        String bottom = "</body>\n" +
                "</html>";
        //
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        Node document = parser.parse(summarizedExam.getConcepts());
        String conceptsHTML = renderer.render(document);


        Node document2 = parser.parse(summarizedExam.getLessonPlan());
        String lessonPlanHTML = renderer.render(document2);
        String blank = "<br>";
        Node document3 = parser.parse(summarizedExam.getAnswers());
        String answersHTML = renderer.render(document3);

        // Write conceptsHTML and lessonPlanHTML and answersHTML to file
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(top);
            writer.write(conceptsHTML);
            writer.write(blank);
            writer.write(blank);
            writer.write(lessonPlanHTML);
            writer.write(blank);
            writer.write(blank);
            writer.write(answersHTML);
            writer.write(bottom);
        }
        // Log the contents of the file
        String fileContent = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        if (activeProfile.equals("dev")) {
            logger.info(mm + "Exam HTML file contents: " + E.RED_APPLE + " "
                    + fileContent.length() + " bytes\n");
        }
    }

}
