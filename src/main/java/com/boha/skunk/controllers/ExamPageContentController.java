package com.boha.skunk.controllers;


import com.boha.skunk.data.ExamPageContent;
import com.boha.skunk.services.CloudStorageService;
import com.boha.skunk.services.DocumentProcessor;
import com.boha.skunk.services.ExamPageContentService;
import com.boha.skunk.services.SgelaFirestoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RequestMapping("/examPageContents")
@RestController
@RequiredArgsConstructor
public class ExamPageContentController {

    final ExamPageContentService examPageContentService;
    final SgelaFirestoreService sgelaFirestoreService;
    final CloudStorageService cloudStorageService;
    final DocumentProcessor documentProcessor;

    @GetMapping("/ping")
    public String ping() {
        return "\uD83D\uDC9C\uD83D\uDC9C ExamPageContentController pinged. at " + new Date().toInstant().toString();
    }
    @GetMapping("/extractPageContentForExam")
    public ResponseEntity<List<ExamPageContent>> extractPageContentForExam(
            @RequestParam Long examLinkId) throws Exception {
        List<ExamPageContent> mList = documentProcessor.extractPageContentForExam(examLinkId);
        return ResponseEntity.ok(mList);
    }
    @GetMapping("/extractMathPageContent")
    public List<ExamPageContent> extractMathPageContent() throws Exception {
        return examPageContentService.extractMathPageContent();
    }

    @GetMapping("/extractPageContent")
    public ResponseEntity<List<ExamPageContent>> extractPageContent(@RequestParam Long subjectId) throws Exception {
        List<ExamPageContent> mList = examPageContentService.extractPageContentForSubject(subjectId);
        return ResponseEntity.ok(mList);
    }
    @GetMapping("/extractAllPageContent")
    public ResponseEntity<List<ExamPageContent>> extractAllPageContent() throws Exception {
        List<ExamPageContent> mList = examPageContentService.extractAllPageContent();
        return ResponseEntity.ok(mList);
    }
    @GetMapping("/fixSubjectExamLinks")
    public int fixSubjectExamLinks() throws Exception {
        return sgelaFirestoreService.fixSubjectExamLinks();

    }
}
