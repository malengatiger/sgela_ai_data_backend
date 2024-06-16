package com.boha.skunk.services;

import com.boha.skunk.data.ExamLink;
import com.boha.skunk.data.ExamPageContent;
import com.boha.skunk.util.DirectoryUtils;
import com.boha.skunk.util.Util;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.boha.skunk.services.CloudStorageService.*;

@SuppressWarnings({"all"})
@Service

public class DocumentProcessor {
    static final Logger logger = LoggerFactory.getLogger(ExamPageContentService.class.getSimpleName());
    private static final String mm = "DocumentProcessor: ";
    final CloudStorageService cloudStorageService;
    final SgelaFirestoreService sgelaFirestoreService;
    private static final String vv = " \uD83D\uDD35";

    public DocumentProcessor(CloudStorageService cloudStorageService, SgelaFirestoreService sgelaFirestoreService) {
        this.cloudStorageService = cloudStorageService;
        this.sgelaFirestoreService = sgelaFirestoreService;
    }

    public List<ExamPageContent> extractPageContentForExam(Long examLinkId) throws Exception {
        logger.info("\n\n\n" + mm + bb + bb + bb + "processDocument .....");
        ExamLink examLink = sgelaFirestoreService.getExamLink(examLinkId);
        if (examLink == null) {
            throw new Exception("ExamLink not found");
        }
        String collectionName = ExamPageContent.class.getSimpleName();

        sgelaFirestoreService.deleteDocumentsByProperty(
                collectionName,"examLinkId", examLink.getId());
        logger.info(String.format("...extractPageContentFromExamLink: title: %s",
                G.toJson(examLink)));

        List<ExamPageContent> examPageContents = new ArrayList<>();
        int index = 0;
        File pdfFile = cloudStorageService.downloadFile(examLink.getLink());
        filesToBeZipped.clear();
        try {
            sgelaFirestoreService.deleteDocumentsByProperty(
                    collectionName,
                    "examLinkId", examLink.getId());
            examPageContents = extractPageContentFromPdf(examLink, pdfFile);
            List<String> list = sgelaFirestoreService.addExamPageContents(
                    examPageContents);
            var msg = "Firestore: ExamPageContent documents added: "
                    + list.size()
                    + " ... will zip file ...";
            logger.info(mm + msg);
            try {
                createZipFile(examLink);
            } catch (IOException e) {
                logger.error("\n\n\n" + mm + " ERROR: createZipFile failed," +
                        " or something!:  \uD83D\uDC7F" + e.getMessage());
                throw new RuntimeException(e);
            }

            var msg0 = bb + bb + bb + bb + bb + " Firestore: examLink zip updated " + examLink.getTitle();
            logger.info(mm + msg0);
        } catch (Exception e) {
            logger.error(mm + " ERROR: addMathExamPageContents failed," +
                    " or something!:  \uD83D\uDC7F" + e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info(mm + bb + bb + bb + bb + bb +
                " Returning " + examPageContents.size() + " examPageContents");
        return examPageContents;
    }


    private void getImageAndUpload(ExamLink examLink, PDFRenderer pdfRenderer, int index,
                                   ExamPageContent examPageContent) throws IOException {
        File imageFile = null;
        imageFile = convertPdfPageToImage(pdfRenderer, index, examLink.getId());
        if (imageFile != null && imageFile.exists()) {
            String url = cloudStorageService.uploadFile(
                    imageFile, examLink.getId(), ORG_IMAGE_FILE);
            examPageContent.setPageImageUrl(url);
            logger.info(mm + "getImageAndUpload completed!  ...... "
                    + vv + vv + imageFile.length() + " bytes uploaded");
        }

    }

    List<File> filesToBeZipped = new ArrayList<>();

    private File convertPdfPageToImage(PDFRenderer pdfRenderer, int pageIndex, Long id) {
        File dir = DirectoryUtils.createDirectoryIfNotExists(
                "f" + ORG_IMAGE_FILE + "_page_images");
        File imageFile = null;
        var byteArray = new byte[0];
        try {
            BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 100);
            imageFile = new File(dir, "page_" + id + "_" + (String.valueOf(pageIndex) + ".png"));
            ImageIO.write(image, "png", imageFile);
            logger.info(vv + vv + vv + vv + "examPage image file just created has: " + imageFile.length()
                    + " bytes, DPI: 100, path:  " + imageFile.getPath());
            filesToBeZipped.add(imageFile);
            logger.info(vv + vv + vv + vv + " image added to filesToBeZipped: " + filesToBeZipped.size());
            return imageFile;

        } catch (Exception e) {
            logger.error(mm + "\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F " +
                    "Unable to render pdf page as image: " + pageIndex);
        }
        return imageFile;
    }

    final String zx = " \uD83D\uDD35";

    private void createZipFile(ExamLink examLink) throws IOException {
        logger.info(mm + "createZipFile  ...... " + filesToBeZipped.size() + " filesToBeZipped - " + examLink.getTitle());
        File dir = DirectoryUtils.createDirectoryIfNotExists(
                "f" + ORG_IMAGE_FILE + "_page_images");
        File zipFile = new File(dir, "zipExamLink_" + examLink.getId() + ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File file : filesToBeZipped) {
                logger.info(mm + bb + "Zip this file: " + file.length() + " bytes - " + bb + file.getPath());
                zos.putNextEntry(new ZipEntry(file.getName()));
                Files.copy(file.toPath(), zos);
                zos.closeEntry();
            }
        }
        logger.info(mm + bb + bb + bb + "Zipped file created: " + zipFile.length() + " bytes");
        try {
            var map =  Util.objectToMap((examLink));
            logger.info(mm + bb + bb + bb + " map to update: " + G.toJson(map) + " ");

            var url = cloudStorageService.uploadFile(zipFile, examLink.getId(), ORG_ZIP_FILE);
            examLink.setZippedPaperUrl(url);
            sgelaFirestoreService.updateExamLink(examLink);
            logger.info(mm + bb + bb + bb + "ExamLink updated with zip url: "
                    + examLink.getTitle());
        } catch (Exception e) {
            logger.info(mm + "ERROR: Failed to update  \uD83D\uDC7F " + examLink.getZippedPaperUrl());
            throw new RuntimeException(e);
        }
    }

    private List<ExamPageContent> extractPageContentFromPdf(ExamLink examLink, File pdfFile) throws Exception {
        logger.info(mm + ".... extractPageContentFromPdf: title: " + examLink.getTitle());
//        Subject subject = examLink.getSubject();
        PdfReader reader = new PdfReader(pdfFile.getAbsolutePath());
        PdfDocument pdfDoc = new PdfDocument(reader);
        PDDocument pdDocument = Loader.loadPDF(pdfFile);
        PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
        List<ExamPageContent> examPageContents = new ArrayList<>();
        int index = 0;
        for (PDPage page : pdDocument.getPages()) {
            try {
                ExamPageContent examPageContent = new ExamPageContent();
                examPageContent.setTitle(examLink.getDocumentTitle()
                        + " - " + examLink.getTitle());
                examPageContent.setId(Util.generateUniqueLong());
                examPageContent.setExamLinkId(examLink.getId());
                examPageContent.setPageIndex(index);
                getImageAndUpload(examLink, pdfRenderer, index, examPageContent);
                examPageContents.add(examPageContent);
                index++;
            } catch (Exception e) {
                logger.error(mm + " ERROR: Failed to extract page content from pdf, IGNORED:  " + e.getMessage());
            }
        }

        logger.info(mm + "extractPageContentFromPdf: examPageContents created, subject: "
                + examPageContents.size() + " examPageContents \uD83C\uDF4E for examLink: "
                + examLink.getDocumentTitle()
                + " " + examLink.getTitle() + bb + bb);
        return examPageContents;
    }

    final String bb = " \uD83D\uDD35 \uD83D\uDD35";

}
