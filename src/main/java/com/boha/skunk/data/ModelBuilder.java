package com.boha.skunk.data;

import com.boha.skunk.services.VertexService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class ModelBuilder {
    private static final String mm = " \uD83D\uDC37  \uD83D\uDC37  \uD83D\uDC37 VertexService";
    static final Logger logger = Logger.getLogger(VertexService.class.getSimpleName());
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        logger.info(mm);
        List<Object> list = new ArrayList<>();
        list.add(buildOrganization());
        list.add(buildUser());
        list.add(buildExamDocument());
        list.add(buildExamLink());
        list.add(buildExamPageContent());
        logger.info(mm + G.toJson(list));

        var mJson = G.toJson(list);
        // Write mJson to file models.json
        try (FileWriter fileWriter = new FileWriter("models.json")) {
            fileWriter.write(mJson);
            logger.info("mJson written to models.json");
        } catch (IOException e) {
            logger.severe("An IO exception occurred while writing to file: " + e.getMessage());
            throw new RuntimeException(e);
        }
        // Parse the file
        File mJsonFile = new File("models.json");
        parseFile(mJsonFile);
        logger.info(mm);
    }
    public static void parseFile(File mJsonFile) {
        try (FileReader fileReader = new FileReader(mJsonFile)) {
            // Read the contents of the file
            StringBuilder stringBuilder = new StringBuilder();
            int character;
            while ((character = fileReader.read()) != -1) {
                stringBuilder.append((char) character);
            }
            String fileContents = stringBuilder.toString();

            logger.info("File parsed successfully: " + fileContents);
        } catch (IOException e) {
            logger.severe("An IO exception occurred while parsing the file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public static ExamPageContent buildExamPageContent() {
        ExamPageContent epc = new ExamPageContent();
        epc.setTitle("November 2022");
        epc.setId(9863L);
        epc.setExamLinkId(75L);
        epc.setPageImageUrl("https://");

        return epc;
    }

    public static ExamLink buildExamLink() {
        ExamLink el = new ExamLink();
        el.setTitle("November 2022");
        el.setId(9863L);
        el.setZippedPaperUrl("url");
        el.setDocumentTitle("docT");
        el.setLink("htpps://...");
        return el;
    }

    public static ExamDocument buildExamDocument() {
        ExamDocument ed = new ExamDocument();
        ed.setTitle("November 2022");
        ed.setId(9863L);
        ed.setLink("htpps://...");
        return ed;
    }

    public static Organization buildOrganization() {
        logger.info(mm);
        Organization org = new Organization();
        org.setName("Boha");
        org.setDate("2024-12-12");
        org.setMaxUsers(20000);
        org.setSplashUrl("https://");
        org.setCoverageRadiusInKM(200);
        org.setTagLine("We are a Tag");
        return org;
    }

    public static User buildUser() {
        User user = new User();
        user.setFirstName("Boha");
        user.setLastName("Boha");
        user.setActiveFlag(true);
        user.setId(12345L);
        user.setDate("2024-12-12");
        user.setEmail("email.com");
        user.setCellphone("78478748747");
        user.setOrganizationId(786476L);
        return user;
    }
}
