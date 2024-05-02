package com.boha.skunk.services;

import com.boha.skunk.util.DirectoryUtils;
import com.nfbsoftware.latex.LaTeXConverter;
import com.qkyrie.markdown2pdf.Markdown2PdfConverter;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class MarkdownLaTexConverterService {

    static OKHelper okHelper;
    static final String mm = "\uD83E\uDD66\uD83E\uDD66\uD83E\uDD66 MarkdownLaTexConverterService  \uD83D\uDC9B";
    static final Logger logger = Logger.getLogger(MarkdownLaTexConverterService.class.getSimpleName());

//    public static void main(String[] args) throws Exception {
//        //okHelper = new OKHelper();
//        //File file = downloadPdf("https://drive.google.com/file/d/1iFsSbl26UC58p2cIpt6pCnNESAPmWzaF/view");
//        File file = new File("profile.md");
//        if (!file.exists()) {
//            logger.severe(mm+"File does not exist: " + file.getAbsolutePath());
//            return;
//        }
//        doLatex();
//        File mdFile = convertMarkdownToPDF(profileMarkdown);
//        logger.severe(mm+"markdown as pdf: " + mdFile.getAbsolutePath());
//
//    }




    public File combine(File pdfFile, File imageFile) throws Exception {
        // Create a temporary file for the combined PDF
        File combinedPdfFile = new File("combined.pdf");

        try (PDDocument pdfDocument = Loader.loadPDF(pdfFile);
             PDDocument imageDocument = new PDDocument();
             PDDocument combinedDocument = new PDDocument()) {

            // Load the image file and convert it to a PDF
            PDImageXObject imageXObject = PDImageXObject.createFromFileByContent(imageFile, imageDocument);
            PDPage imagePage = new PDPage();
            imageDocument.addPage(imagePage);
            try (PDPageContentStream contentStream = new PDPageContentStream(imageDocument, imagePage)) {
                contentStream.drawImage(imageXObject, 0, 0);
            }

            // Merge the pages from the pdfFile into the combinedDocument
            for (PDPage page : pdfDocument.getPages()) {
                combinedDocument.addPage(page);
            }

            // Merge the pages from the imageDocument into the combinedDocument
            for (PDPage page : imageDocument.getPages()) {
                combinedDocument.addPage(page);
            }

            // Save the combinedDocument to the combinedPdfFile
            combinedDocument.save(combinedPdfFile);
        }


        return combinedPdfFile;
    }
    public File convertMarkdownToPDF(String markdownString) throws Exception {
        // Create a temporary file for the converted PDF
        var dir = DirectoryUtils.createDirectoryIfNotExists("converted_files");
        File convertedPdfFile = new File(dir, "markdown_"
                + System.currentTimeMillis() + ".pdf");

        // Use Markdown2PdfConverter to convert the Markdown string to PDF
        Markdown2PdfConverter.newConverter()
                .readFrom(() -> markdownString)
                .writeTo(out -> {
                    try (OutputStream outputStream = new FileOutputStream(convertedPdfFile)) {
                        // Write the PDF content to the output stream
                        outputStream.write(out);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .doIt();

        return convertedPdfFile;
    }

    public File convertLatexToPNG(String laTexString) throws Exception {

        var dir = DirectoryUtils.createDirectoryIfNotExists("converted_files");
        File outFile = new File(dir, "latex_"
                + System.currentTimeMillis() + ".png");

        LaTeXConverter.convertToImage(outFile, laTexString);
        logger.info(mm + "imageFile: " + outFile.getPath());
        logger.info(mm + "imageFile: " + outFile.length() + " bytes");
        return outFile;
    }

}
