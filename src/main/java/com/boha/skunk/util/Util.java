package com.boha.skunk.util;
import com.boha.skunk.controllers.OrganizationController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class Util {
    static final String mm = "\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D " +
            "OrganizationController \uD83D\uDD35";
    static final Logger logger = Logger.getLogger(OrganizationController.class.getSimpleName());
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public static Map<String, Object> objectToMap(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();

        // Get all fields of the User class
        Field[] fields = object.getClass().getDeclaredFields();

        // Iterate over the fields and add them to the map
        for (Field field : fields) {
            field.setAccessible(true); // Set the field accessible
            String fieldName = field.getName();
            Object fieldValue = field.get(object);
            map.put(fieldName, fieldValue);
        }

        return map;
    }
    public static File convertMultipartFileToFile(MultipartFile multipartFile) throws Exception {
        File dir = DirectoryUtils.createDirectoryIfNotExists("files");
        String ext = getFileExtension(multipartFile);
        File file = new File(dir, "file"+ System.currentTimeMillis() + "." + ext );
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        }
        logger.info(mm+"convertMultipartFileToFile file: "+file.length()+" bytes");
        return file;
    }
    public static boolean isFileTooBig(File file, long size) {
        return file.length() > size;
    }
    public static String getFileExtension(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
                String ext =
                 originalFilename.substring(dotIndex + 1).toLowerCase();
                logger.info(mm+" file extension: " + ext);
                return ext;
            }
        }
        throw new Exception("Cannot find file extension");
    }

    public static Long generateUniqueLong() {
        UUID uuid = UUID.randomUUID();
        long mostSignificantBits = uuid.getMostSignificantBits();
        return Math.abs(mostSignificantBits);
    }
}