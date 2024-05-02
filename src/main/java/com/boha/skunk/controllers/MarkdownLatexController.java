package com.boha.skunk.controllers;


import com.boha.skunk.services.CloudStorageService;
import com.boha.skunk.services.MarkdownLaTexConverterService;
import com.boha.skunk.services.SgelaFirestoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Date;
import java.util.logging.Logger;

@RequestMapping("/converter")
@RestController
@RequiredArgsConstructor
public class MarkdownLatexController {

    final MarkdownLaTexConverterService markdownLaTexConverterService;
    final SgelaFirestoreService sgelaFirestoreService;
    static final String mm = "\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D " +
            "MarkdownLatexController \uD83D\uDD35";
    static final Logger logger = Logger.getLogger(MarkdownLatexController.class.getSimpleName());

    @GetMapping("/ping")
    public String ping() {
        return "\uD83D\uDC9C\uD83D\uDC9C MarkdownLatexController pinged. at " + new Date().toInstant().toString();
    }

    @GetMapping("/convertMarkdownToPDF")
    public ResponseEntity<File> convertMarkdownToPDF(@RequestParam String markdownString) throws Exception {
        logger.info("convertMarkdownToPDF: " + markdownString);
        File file = markdownLaTexConverterService.convertMarkdownToPDF(markdownString);
        logger.info("return convertMarkdownToPDF: " + file.length() + " bytes");

        return ResponseEntity.ok(file);
    }

    @GetMapping("/convertLatexToPNG")
    public ResponseEntity<File> convertLatexToPNG(@RequestParam String laTexString) throws Exception {
        logger.info("convertLatexToPNG: " + laTexString);
        File file = markdownLaTexConverterService.convertLatexToPNG(laTexString);
        logger.info("return convertLatexToPNG: " + file.length() + " bytes");

        return ResponseEntity.ok(file);
    }

}
