package com.boha.skunk.controllers;

import com.boha.skunk.data.*;
import com.boha.skunk.services.ExamLinkService;
import com.boha.skunk.services.LinkExtractorService;
import com.boha.skunk.services.SgelaFirestoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/links")
@SuppressWarnings("all")
public class ExamLinkController {
    static final String mm = " \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E " +
            "ExamLinkController  \uD83C\uDF4E";
    static final Logger logger = Logger.getLogger(ExamLinkController.class.getSimpleName());

    private final ExamLinkService examLinkService;
    private final SgelaFirestoreService sgelaFirestoreService;
    private final LinkExtractorService linkExtractorService;


    public ExamLinkController(ExamLinkService examLinkService,
                              SgelaFirestoreService sgelaFirestoreService, LinkExtractorService linkExtractorService) {
        this.examLinkService = examLinkService;
        this.sgelaFirestoreService = sgelaFirestoreService;
        this.linkExtractorService = linkExtractorService;
    }

    @GetMapping("/")
    public ResponseEntity<String> hello() {
        logger.info(mm + "say hello! ..... ");
        return ResponseEntity.ok(
                "<h1>\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E Sgela AI Backend</h1><p>The SgelaAI Backend to manage data</p> <p> Date:  <b>"+new Date().toString()+"</b></p>");
    }

    @GetMapping("/getSubjects")
    public ResponseEntity<List<Subject>> getSubjects() throws Exception {
        logger.info(mm + "find all subjects ..... ");

//        var list = examLinkService.getSubjects();
        var list = sgelaFirestoreService.getSubjects();
        logger.info(mm + "subjects found: " + list.size());
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/downloadExamDocuments")
    public ResponseEntity<List<SubjectExamInterfaceBag>> downloadExamDocuments() throws Exception {
        logger.info(mm + "downloadExamDocuments ..... ");
        var bag = linkExtractorService.downloadExamDocuments();

        logger.info(mm + "downloadExamDocuments SubjectExamInterfaceBags found: " + bag.size());
        return ResponseEntity.ok().body(bag);
    }

    @PostMapping("/addResponseRating")
    public ResponseEntity<List<String>> addResponseRating(@RequestBody GeminiResponseRating rating) throws ExecutionException, InterruptedException {
        logger.info(mm + "add response rating ..... ");

//        var mRating = examLinkService.addResponseRating(rating);
        List<GeminiResponseRating> list = new ArrayList<>();
        list.add(rating);
        var mRating = sgelaFirestoreService.addGeminiResponseRatings(list);
        return ResponseEntity.ok().body(mRating);
    }

    @GetMapping("/getResponseRatings")
    public ResponseEntity<List<GeminiResponseRating>> getResponseRatings(
            @RequestParam Long examLinkId) throws Exception {
        logger.info(mm + "find response ratings ..... ");

//        var list = examLinkService.getExamRatings(examLinkId);
        var list = sgelaFirestoreService.getResponseRatings(examLinkId);
        logger.info(mm + "response ratings found: " + list.size());
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/getExamDocuments")
    public ResponseEntity<List<ExamDocument>> getExamDocuments() throws Exception {

//        var list = examLinkService.getExamDocuments();
        var list = sgelaFirestoreService
                .getAllDocuments(ExamDocument.class);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/getSubjectExamLinks")
    public ResponseEntity<List<ExamLink>> getSubjectExamLinks(@RequestParam Long subjectId) throws Exception {

        var list = examLinkService.getSubjectExamLinks(subjectId);
        logger.info(mm + "exam links found: " + list.size());

        return ResponseEntity.ok().body(list);
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        }
        return file;
    }

}
