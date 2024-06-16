package com.boha.skunk.services;

import com.boha.skunk.data.*;
import com.boha.skunk.util.DirectoryUtils;
import com.boha.skunk.util.TrustAllCertificates;
import com.boha.skunk.util.Util;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.boha.skunk.services.CloudStorageService.ORG_IMAGE_FILE;
import static com.boha.skunk.services.CloudStorageService.ORG_ZIP_FILE;

@Service
//@RequiredArgsConstructor
@SuppressWarnings("all")
public class ExamPageContentService {
    public ExamPageContentService(SgelaFirestoreService sgelaFirestoreService, CloudStorageService cloudStorageService, DocumentProcessor documentProcessor, OKHelper okHelper, PdfImageDetector pdfImageDetector) {
        this.sgelaFirestoreService = sgelaFirestoreService;
        this.cloudStorageService = cloudStorageService;
        this.documentProcessor = documentProcessor;
        this.okHelper = okHelper;
        this.pdfImageDetector = pdfImageDetector;
    }

    @PostConstruct
    public void initialize() {
        try {
            disableCerts();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
        logger.info(mm + " SSL certificates disabled at construction time");
    }

    private void disableCerts() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that accepts all certificates
        TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCertificates()};

        // Create an SSL context with the trust manager
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        // Disable SSL certificate validation
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        // Enable server hostname verification
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> {
            // Perform proper hostname verification here
            // Return true if the hostname is verified, false otherwise
            // You can use the default hostname verifier for standard verification:
            // return HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, session);
            //return true; // Disable hostname verification (not recommended for production)
            return true;
        });
        logger.info(mm + "SSL certificates disabled");
    }

    @NonNull
    final SgelaFirestoreService sgelaFirestoreService;
    @NonNull
    final CloudStorageService cloudStorageService;
    @NonNull
    final DocumentProcessor documentProcessor;

    @NonNull
    private final OKHelper okHelper;
    @NonNull
    private final PdfImageDetector pdfImageDetector;


    static final String mm = "\uD83E\uDD66\uD83E\uDD66\uD83E\uDD66 ExamPageContentService  \uD83D\uDC9B";
    static final Logger logger = LoggerFactory.getLogger(ExamPageContentService.class.getSimpleName());

    private byte[] convertFileToByteArray(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }

    private static final String ERROR_PREFIX = "ERROR: ";

    public List<ExamPageContent> extractMathPageContent() throws Exception {
        logger.info(mm + "extractSubjectPageContent");
        List<Subject> subjects = sgelaFirestoreService.getSubjects();
        List<ExamPageContent> examPageContents = new ArrayList<>();

        subjects.forEach(subject -> {
            try {
                if (subject.getTitle().contains("MATH")) {
                    examPageContents.addAll(extractSubjectPageContent(subject.getId()));
                }
            } catch (Exception e) {
                logger.error("extractSubjectPageContent " + e.getMessage());
            }
        });
        logger.info(String.format("extractMathPageContent: created %d ExamPageContents", examPageContents.size()));
        return examPageContents;
    }

    public List<ExamPageContent> extractSubjectPageContent(Long subjectId) throws Exception {
        List<ExamLink> examLinks = sgelaFirestoreService.getSubjectExamLinks(subjectId);
        List<ExamPageContent> examPageContents = new ArrayList<>();
        logger.info("\n\n\n" + mm + bb + bb + bb + "Total examLinks to be processed: " + examLinks.size());
        AtomicInteger count = new AtomicInteger();
        examLinks.forEach(examLink -> {
            try {
                if (examLink.getTitle().contains("Addendum")) {
                    logger.info("Addendum found and ignored: " + examLink.getTitle()
                            + " - " + examLink.getDocumentTitle());
                } else {
                    logger.info("\n\nprocessing paper: "
                            + examLink.getTitle() + " - " + examLink.getDocumentTitle());
                    documentProcessor.extractPageContentForExam(examLink.getId());
                    logger.info(mm + "examLink COMPLETE: #" + bb + bb + count.get() + " of " + examLinks.size()
                            + " - " + examLink.getTitle() + " - " + examLink.getDocumentTitle() + "\n\n");

                    count.getAndIncrement();
                }
            } catch (Exception e) {
                logger.error("extractSubjectPageContent" + e.getMessage());
            }
        });
        logger.info("\n\nprocessing MATH paper: "
                + examPageContents.size() + " - mathExamPageContents ");

        return examPageContents;
    }

    final String bb = " \uD83D\uDD35 \uD83D\uDD35";

    private List<ExamPageContent> extractPageContentFromPdf(ExamLink examLink, File pdfFile) throws Exception {
        logger.info(mm + "extractPageContentFromPdf: title: " + examLink.getTitle());
//        Subject subject = examLink.getSubject();
        PdfReader reader = new PdfReader(pdfFile.getAbsolutePath());
        PdfDocument pdfDoc = new PdfDocument(reader);
        PDDocument pdDocument = Loader.loadPDF(pdfFile);
        PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
        List<ExamPageContent> examPageContents = new ArrayList<>();
        int i = 0;
        for (PDPage page : pdDocument.getPages()) {
            try {
                ExamPageContent examPageContent = new ExamPageContent();
                examPageContent.setTitle(examLink.getDocumentTitle() + " - " + examLink.getTitle());
                examPageContent.setId(Util.generateUniqueLong());
                examPageContent.setExamLinkId(examLink.getId());
                examPageContent.setPageIndex(i);
                boolean hasImages = pdfImageDetector.hasImage(page);
                getImageAndUpload(examLink, pdfRenderer, i, examPageContent);
                examPageContents.add(examPageContent);
                i++;
            } catch (Exception e) {
                logger.error(mm + e.getMessage());
            }

        }
        var mList = sgelaFirestoreService.addMathExamPageContents(examPageContents);

        logger.info(mm + "extractPageContentFromPdf: Firestore MathExamPageContent documents added: " + mList.size() +
                " \uD83D\uDC99\uD83D\uDC99 ");
        logger.info(mm + "extractPageContentFromPdf: examPageContents created, \uD83C\uDF4E subject: "
                + examPageContents.size() + " examPageContents \uD83C\uDF4E for examLink: "
                + examLink.getDocumentTitle()
                + " " + examLink.getTitle());
        return examPageContents;
    }

    private void getImageAndUpload(Exam examLink, PDFRenderer pdfRenderer, int index,
                                   ExamPageContent examPageContent) throws IOException {
        File imageFile = null;
        imageFile = convertPdfPageToImage(pdfRenderer, index);
        if (imageFile != null && imageFile.exists()) {
            String url = cloudStorageService.uploadFile(
                    imageFile, examLink.getId(), ORG_IMAGE_FILE);
            examPageContent.setPageImageUrl(url);
            logger.info(mm + "getImageAndUpload completed!  ...... "
                    + vv + vv + imageFile.length() + " bytes uploaded");
        }
    }

    List<File> filesToBeZipped = new ArrayList<>();

    public void createZipFile(ExamLink examLink) throws IOException {
        logger.info(mm + "createZipFile  ...... " + filesToBeZipped.size()
                + " filesToBeZipped - " + examLink.getTitle());

        long total = 0;
        File zipFile = new File("zipExamLink_" + examLink.getId() + ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File file : filesToBeZipped) {
                zos.putNextEntry(new ZipEntry(file.getName()));
                Files.copy(file.toPath(), zos);
                zos.closeEntry();
            }
        }
        logger.info(mm + bb + bb + bb + "Zipped file created: " + zipFile.length() + " bytes");
        try {
            var url = cloudStorageService.uploadFile(zipFile, examLink.getId(), ORG_ZIP_FILE);
            examLink.setZippedPaperUrl(url);
            logger.info(mm + "ExamLink Zipped file uploaded: " + url);
            sgelaFirestoreService.updateExamLink(examLink);
        } catch (Exception e) {
            logger.info(mm + "ERROR: Failed to update  \uD83D\uDC7F " + examLink.getZippedPaperUrl());
            throw new RuntimeException(e);
        }
    }

    public List<ExamPageContent> extractAllPageContent() throws Exception {
        List<Subject> subjects = sgelaFirestoreService.getSubjectsSorted();

        logger.info("\n\n\n" + mm + "Subjects to process:  \uD83D\uDC9C \uD83D\uDC9C \uD83D\uDC9C"
                + subjects.size() + "  \uD83D\uDC9C \n\n\n");
        for (Subject subject : subjects) {
            logger.info("\n\n\n" + mm + "Subject:  \uD83D\uDC9C \uD83D\uDC9C \uD83D\uDC9C"
                    + subject.getTitle() + "  \uD83D\uDC9C \n");
        }
        List<ExamPageContent> examPageContentList = new ArrayList<>();
        for (Subject subject : subjects) {
            logger.info("\n\n\n" + mm + "Subject started!!  \uD83D\uDC9C \uD83D\uDC9C \uD83D\uDC9C"
                    + subject.getTitle() + "  \uD83D\uDC9C \n\n\n");
            try {
                var mList = extractPageContentForSubject(subject.getId());
                examPageContentList.addAll(mList);
                logger.info("\n\n\n" + mm + "Subject COMPLETED:  \uD83D\uDC9C \uD83D\uDC9C \uD83D\uDC9C"
                        + subject.getTitle() + "  \uD83D\uDC9C " + " " + mList.size() + " examPageContents created\n");
            } catch (Exception e) {
                logger.info(mm + "extractAllPageContent: " + e.getMessage());
            }
        }
        logger.info("\n\n\n" + mm + "TOTAL JOB DONE!!! \uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E" +
                "Completed!!  \uD83D\uDC9C \uD83D\uDC9C \uD83D\uDC9C"
                + examPageContentList.size()
                + " pageContents for all subjects:  \uD83D\uDC9C "
                + subjects.size() + "  \uD83D\uDC9C \n\n\n");

        return examPageContentList;
    }

    public List<ExamPageContent> extractPageContentForSubject(Long subjectId) throws Exception {
        List<ExamPageContent> examPageContentList = new ArrayList<>();
        Subject subject = sgelaFirestoreService.getSubjectById(subjectId);

        long start = System.currentTimeMillis();

        List<ExamLink> examLinks = getSubjectExamLinks(subjectId);
        for (ExamLink examLink : examLinks) {
            var eList = sgelaFirestoreService.getExamLinkPageContents(examLink.getId());
            if (!eList.isEmpty()) {
                logger.info("\n\n" + mm + "this exam link already processed for  \uD83D\uDC9C "
                        + subject.getTitle() + "  \uD83D\uDC9C \uD83D\uDC9C examLink: " +
                        examLink.getDocumentTitle() + " - " + examLink.getTitle() +
                        "\n \uD83D\uDC9C \uD83D\uDC9C \uD83D\uDC9C Found " + eList.size() + " examPageContents already created");
                return eList;
            }
            try {
                logger.info("\n\n\n\n" + mm + "ExamLink processing started ... ");
                File file = downloadPdf(examLink.getLink());
                List<ExamPageContent> mList = extractPageContentFromPdf(examLink, file);
                examPageContentList.addAll(mList);
                logger.info("\n" + mm + "ExamLink completed:  \uD83D\uDC9C \uD83D\uDC9C \uD83D\uDC9C"
                        + examLink.getDocumentTitle() + " " + examLink.getTitle() + "  \uD83D\uDC9C \n");
            } catch (Exception e) {
                logger.info(mm + "extractPageContentForSubject: " + e.getMessage());
            }
        }
        if (!examPageContentList.isEmpty()) {
            logger.info("\n\n\n" + mm
                    + " Subject: " + subject.getTitle() + " \uD83D\uDD35\uD83D\uDD35 " +
                    " ExamPageContents created:  \uD83D\uDC9C " + examPageContentList.size() + "\n\n\n");
        }
        long end = System.currentTimeMillis();
        printElapsed(end - start, " Subject: " + subject.getTitle());
        return examPageContentList;
    }

    private void printElapsed(long mSecs, String title) {
        var fmt = new DecimalFormat("###,###,###.00");
        var secs = mSecs / 1000;
        logger.info("\n" + mm + "Elapsed time for " + title + " = " + fmt.format(secs) + " seconds");
    }

    public List<ExamPageContent> extractPageContentForExam(Long examLinkId) throws Exception {
        ExamLink link = sgelaFirestoreService.getExamLink(examLinkId);
//        Subject subject = link.getSubject();

        List<ExamPageContent> mList = null;

        File file = downloadPdf(link.getLink());
        mList = extractPageContentFromPdf(link, file);

        return mList;
    }
//
//    private List<ExamLink> getExamLinks() throws Exception {
//        List<ExamLink> examLinks = new ArrayList<>();
//        List<Subject> subjects = sgelaFirestoreService.getSubjects();
//        for (Subject subject : subjects) {
//            var subjectLinks = getSubjectExamLinks(subject.getId());
//            examLinks.addAll(subjectLinks);
//        }
//        return examLinks;
//    }

    private List<ExamLink> getSubjectExamLinks(Long subjectId) throws Exception {
        List<ExamLink> examLinks = sgelaFirestoreService.getSubjectExamLinks(subjectId);
        logger.info(mm + "Subject exam links found: " + examLinks.size());
        return examLinks;
    }

    @SuppressWarnings({"all"})
    String vv = "\uD83D\uDC9B";

    private File convertPdfPageToImage(PDFRenderer pdfRenderer, int pageIndex) {
        File dir = DirectoryUtils.createDirectoryIfNotExists(
                "f" + ORG_IMAGE_FILE + "_page_images");
        File imageFile = new File(dir, "page_" + System.currentTimeMillis() + ".png");
        var byteArray = new byte[0];
        try {
            BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300);
            ImageIO.write(image, "png", imageFile);
            logger.info("\n\n" + vv + vv + vv + vv + "file image file just created has: " + imageFile.length()
                    + " bytes, path:  " + imageFile.getPath());
            filesToBeZipped.add(imageFile);
            logger.info(vv + vv + vv + vv + "image added to filesToBeZipped: " + filesToBeZipped.size() + "\n\n");
            return imageFile;

        } catch (Exception e) {
            logger.error(mm + "\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F " +
                    "Unable to render pdf page as image: " + pageIndex);
        }
        return imageFile;
    }

    private List<File> convertPdfToImages(File pdfFile, Long id, String type) throws IOException {
        PDDocument document = Loader.loadPDF(pdfFile);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        File dir = DirectoryUtils.createDirectoryIfNotExists(
                type + "_page_images");
        List<File> imageFiles = new ArrayList<>();
        logger.info(mm + type + " Document has \uD83D\uDD35\uD83D\uDD35 "
                + document.getNumberOfPages() + " pages ...");
        for (int page = 0; page < document.getNumberOfPages(); page++) {
            try {

                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 400);
                File imageFile = new File(dir.getPath() + "/image_" + id + "_" + page + ".png");
                ImageIO.write(image, "png", imageFile);
                imageFiles.add(imageFile);
            } catch (Exception e) {
                logger.error(mm + "\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F " +
                        "Unable to render pdf page as image: " + page);
            }
        }

        document.close();
        logger.info(mm + "PDF images created: " + imageFiles.size());
        return imageFiles;
    }

    private File downloadPdf(String url) throws IOException {

        var dir = DirectoryUtils.createDirectoryIfNotExists("pdfPageContent");
        File outFile = new File(dir, "pdfContent_" + System.currentTimeMillis() + ".pdf");
        OkHttpClient client = okHelper.getClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to download PDF: " + response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                try (InputStream inputStream = responseBody.byteStream();
                     OutputStream outputStream = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
        }
        return outFile;
    }

}
