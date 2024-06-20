package com.boha.skunk.services;

import com.boha.skunk.data.SummarizedPdf;
import com.boha.skunk.data.UploadResponse;
import com.boha.skunk.util.DirectoryUtils;
import com.boha.skunk.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("all")
@Service
public class PDFCreator {

    final CloudStorageService cloudStorageService;

    public PDFCreator(CloudStorageService cloudStorageService) {
        this.cloudStorageService = cloudStorageService;
    }

    String mm = E.AMP + E.AMP + E.AMP + E.AMP + "PDFCreator ";
    private static final Logger logger = LoggerFactory.getLogger(PDFCreator.class);
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public UploadResponse createPDF(SummarizedPdf sPdf) throws IOException {
        // Create a PDF document
        var msg3a = mm + "create a pdf document to consolidate ai results  .... " ;
        logger.info(msg3a);
        var dir = DirectoryUtils.createDirectoryIfNotExists("summarized");
        var file = new File(dir, sPdf.getExamLinkId() + System.currentTimeMillis() + ".pdf");
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(file));

        // Create a document object
        Document doc = new Document(pdfDoc);

        doc.add(new TextArea("Exam Paper"));

        // Add each string as a paragraph to the document
        String[] strings = sPdf.getConcepts().split("\n\n");
        for (String str : strings) {
            doc.add(new Paragraph(str));
        }
        String[] strings2 = sPdf.getLessonPlan().split("\n\n");
        for (String str : strings2) {
            doc.add(new Paragraph(str));
        }

        // Close the document
        doc.close();
        var response = cloudStorageService.uploadFile(file, sPdf.getExamLinkId(), 4);
        sPdf.setAgentResponseUri(response.gsUri);
        sPdf.setAgentResponseUrl(response.downloadUrl);
        logger.info(mm + "createPDF done, upload response: " +G.toJson(response));
        return response;
    }

}
