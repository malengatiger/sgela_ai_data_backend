package com.boha.skunk.controllers;

import com.boha.skunk.data.UrlData;
import com.boha.skunk.services.EduScraperService;
import com.boha.skunk.util.ScraperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/scraper")
public class EduScraperController {

    private final EduScraperService scraperService;

    @Autowired
    public EduScraperController(EduScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @GetMapping("/scrape")
    public ResponseEntity<List<UrlData>> scrapeUrls(@RequestParam String url) throws ScraperException {
        try {
            List<UrlData> urlDataList = scraperService.scrapeUrls(url);
            return ResponseEntity.ok(urlDataList);
        } catch (IOException e) {
            throw new ScraperException(e);
        }
    }

}
