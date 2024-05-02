package com.boha.skunk.services;

import com.boha.skunk.util.DirectoryUtils;
import com.google.cloud.storage.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@SuppressWarnings({"all"})

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


    public File downloadFile(String url) throws IOException {
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
    public String uploadFile(File mFile, Long id, int type) throws IOException {
        File dir = DirectoryUtils.createDirectoryIfNotExists(directory);
        logger.info(mm +
                " ............. uploadFile to cloud storage: " + mFile.getName());
        String contentType = Files.probeContentType(mFile.toPath());
        BlobId blobId = BlobId.of(bucketName, cloudStorageDirectory
                + mFile.getPath() + id + "_" + System.currentTimeMillis() + "."
                + getFileExtension(mFile.getName()));
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
        return downloadUrl;
    }


}