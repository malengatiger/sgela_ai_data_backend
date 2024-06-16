package com.boha.skunk.services;

import com.boha.skunk.data.UploadResponse;
import com.boha.skunk.util.DirectoryUtils;
import com.google.cloud.storage.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@SuppressWarnings({"all"})
/**
 *  Manages file uploads and downloads from Cloud Storage
 */
public class CloudStorageService {

    private final Storage storage;
    static final String mm = "\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D CloudStorageService \uD83D\uDD35";
    static final Logger logger = Logger.getLogger(CloudStorageService.class.getSimpleName());
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public CloudStorageService() throws IOException {
        // Initialize Firebase Admin SDK and get the storage instance using default credentials
        storage = StorageOptions.getDefaultInstance().getService();
        var proj = storage.getOptions().getProjectId();
        var app = storage.getOptions().getCredentials();
        try {
            logger.info(mm + "CloudStorageService: projectId:" + proj);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Value("${projectId}")
    private String projectId;
    @Value("${storageBucket}")
    private String bucketName;
    @Value("${cloudStorageDirectory}")
    private String cloudStorageDirectory;


    public File downloadZipFile(String url) throws IOException {
        File dir = DirectoryUtils.createDirectoryIfNotExists("pdfs");
        String path = dir.getPath() + "/images_"
                + System.currentTimeMillis() + ".zip";
        File file = new File(path);
        var mUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
        FileUtils.copyURLToFile(new URL(url),
                file);

        logger.info(mm + "File downloaded from Cloud Storage: "
                + (file.length() / 1024) + "K bytes");
        return file;
    }
    public File downloadPdfFile(String url, Long examLinkId) throws IOException {
        File dir = DirectoryUtils.createDirectoryIfNotExists("pdfs");
        String path = dir.getPath()
                + "/pdf_" + examLinkId + "_"
                + System.currentTimeMillis() + ".pdf";
        File file = new File(path);
        var mUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
        FileUtils.copyURLToFile(new URL(url),
                file);

        logger.info(mm + "PDF File downloaded from Web"
                + (file.length() / 1024) + "K bytes");
        return file;
    }

    static final int EXAM_FILE = 0;
    static final int ANSWER_FILE = 1;
    static final int ORG_IMAGE_FILE = 2;
    static final int ORG_ZIP_FILE = 3;



    private String getFileExtension(String fileName) {
        logger.info(mm + "getFileExtension: fileName: " + fileName);
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            var ext = fileName.substring(dotIndex + 1).toLowerCase();
            logger.info(mm + "getFileExtension: ext: " + ext);
            return ext;
        }
        return "";
    }

    public static String directory = "cloudstorage";

    /**
     *
     * @param mFile - File to be uploaded to Cloud Storage
     * @param id - id of the ExamLink or AnswerLink object
     * @param type 1, 2,3 or 4
     * @return @UploadResponse
     * @throws IOException
     */
    public UploadResponse uploadFile(File mFile, Long id, int type) throws IOException {
        File dir = DirectoryUtils.createDirectoryIfNotExists(directory);
        logger.info(mm +
                " ............. uploadFile to cloud storage: " + mFile.getName());
        String contentType = Files.probeContentType(mFile.toPath());
        String name = cloudStorageDirectory
                + "/examLink_" + id + "_" + System.currentTimeMillis()  +  "."
                + getFileExtension(mFile.getName());
        BlobId blobId = BlobId.of(bucketName,  name);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();

        Blob blob = storage.createFrom(blobInfo, Paths.get(mFile.getPath()));
        // Generate a signed URL for the blob with no permissions required
        String downloadUrl = String.valueOf(blob.signUrl(3650, TimeUnit.DAYS, Storage.SignUrlOption.withV2Signature()));
        logger.info(mm +
                " file uploaded to cloud storage, path: " + mFile.getAbsolutePath() + " size: " + mFile.length());
        logger.info(mm +
                " file uploaded to cloud storage, type: " + type + " \n" + downloadUrl);
        //gs://busha-2024.appspot.com/sgelaMedia/examLink_1718568522833_1718570225499.png
        String gsUri = "gs://" + bucketName + "/" + name;
        return new UploadResponse(downloadUrl, gsUri);
    }


//gs://busha-2024.appspot.com/sgelaMedia/examLink_1718568898273_1718575914689.pdf

}