package com.boha.skunk.controllers;


import com.boha.skunk.data.SummarizedPdf;
import com.boha.skunk.services.PDFService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RequestMapping("/pdf")
@RestController
//@RequiredArgsConstructor
public class PdfServiceController {

    final PDFService pdfService;

    public PdfServiceController(PDFService pdfService) {
        this.pdfService = pdfService;
    }


    @GetMapping("/ping")
    public String ping() {
        return "\uD83D\uDC9C\uD83D\uDC9C PdfServiceController pinged. at " + new Date().toInstant().toString();
    }


    @GetMapping("/summarizePdf")
    public ResponseEntity<SummarizedPdf> summarizePdf(
            @RequestParam Long examLinkId, @RequestParam String instruction, @RequestParam int weeks) throws Exception {

        var response = pdfService.summarizePdf( instruction, examLinkId, weeks);
        return ResponseEntity.ok(response);
    }

}
