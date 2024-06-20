package com.boha.skunk.services;

import com.boha.skunk.data.UploadResponse;
import com.boha.skunk.util.DirectoryUtils;
import com.boha.skunk.util.DurationTypeAdapter;
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
import java.time.Duration;
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
    static final Gson G = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .setPrettyPrinting().create();

    /**
     * Initialize Firebase Admin SDK and get the storage instance using default credentials
     *
     * @throws IOException
     */
    public CloudStorageService() throws IOException {
        var myInstance = StorageOptions.getDefaultInstance();
        storage = myInstance.getService();

        try {
            logger.info(mm + "CloudStorageService: getDefaultProjectId:" + myInstance.getDefaultProjectId());
            var proj = storage.getOptions().getProjectId();
            var credentials = storage.getOptions().getCredentials();

            logger.info(mm + "CloudStorageService: projectId:" + proj);
            if (credentials != null) {
                logger.info(mm + "CloudStorageService: credentials: auth type: "
                        + credentials.getAuthenticationType()
                + " metadata: " + credentials.toString());
            }
            if (storage.getOptions() != null) {
                logger.info(mm + "CloudStorageService: getApplicationName:"
                        + storage.getOptions().getApplicationName());
            }

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

    public CloudStorageService(Storage storage) {
        this.storage = storage;
    }


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
    public File downloadPdfFileByUri(String gsUri) throws IOException {
        File dir = DirectoryUtils.createDirectoryIfNotExists("pdfs");
        String path = dir.getPath()
                + "/pdf_" + System.currentTimeMillis() + ".pdf";
        File file = new File(path);

        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.get(gsUri);
        blob.downloadTo(Paths.get(path));

        // Get the file size after the download is complete
        long fileSizeInBytes = file.length();
        logger.info(mm + "PDF File downloaded from Cloud Storage: "
                + (fileSizeInBytes / 1024) + "K bytes");
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
     * @param mFile - File to be uploaded to Cloud Storage
     * @param id    - id of the ExamLink or AnswerLink object
     * @param type  1, 2,3 or 4
     * @return @UploadResponse
     * @throws IOException
     */
    public UploadResponse uploadFile(File mFile, Long id, int type) throws IOException {
        File dir = DirectoryUtils.createDirectoryIfNotExists(directory);
        String name = cloudStorageDirectory
                + "/examLink_" + id + "_" + System.currentTimeMillis() + "."
                + getFileExtension(mFile.getName());
        String gsUri = "gs://" + bucketName + "/" + name;

        logger.info(mm +
                " ............. uploadFile to cloud storage: " + mFile.getName() +
                " bucket: " + bucketName + " - fileName: " + name);

        String contentType = Files.probeContentType(mFile.toPath());
        BlobId blobId = BlobId.of(bucketName, name);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();

        try {
            Blob blob = storage.createFrom(blobInfo, Paths.get(mFile.getPath()));
            // Generate a signed URL for the blob with no permissions required
            logger.info(mm +
                    " ............. Generate a signed URL for the blob with no permissions required, ie, public read ");
            String downloadUrl = null;
            try {
                downloadUrl = String.valueOf(blob.signUrl(
                        3650, TimeUnit.DAYS, Storage.SignUrlOption.withV2Signature()));
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(mm +
                        " ............. Get a media link ");
                downloadUrl = blob.getMediaLink();
                return new UploadResponse(downloadUrl, gsUri);
            }
            logger.info(mm +
                    " file uploaded to cloud storage, path: " + mFile.getAbsolutePath() + " size: " + mFile.length());
            logger.info(mm +
                    " file uploaded to cloud storage, type: " + type + " \n" + downloadUrl);
            return new UploadResponse(downloadUrl, gsUri);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cloud Storage File upload failed: " + e.getMessage());
        }
    }


}