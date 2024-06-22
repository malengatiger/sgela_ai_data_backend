package com.boha.skunk.controllers;


import com.boha.skunk.data.SummarizedExam;
import com.boha.skunk.services.PDFSummarizerAgent;
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

    final PDFSummarizerAgent pdfSummarizerAgent;

    public PdfServiceController(PDFSummarizerAgent pdfSummarizerAgent) {
        this.pdfSummarizerAgent = pdfSummarizerAgent;
    }


    @GetMapping("/ping")
    public String ping() {
        return "\uD83D\uDC9C\uD83D\uDC9C PdfServiceController pinged. at " + new Date().toInstant().toString();
    }


    @GetMapping("/summarizePdf")
    public ResponseEntity<SummarizedExam> summarizePdf(
            @RequestParam Long examLinkId,  @RequestParam int weeks) throws Exception {

        var response = pdfSummarizerAgent.summarizePdf(  examLinkId, weeks);
        return ResponseEntity.ok(response);
    }

}
