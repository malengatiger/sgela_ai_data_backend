package com.boha.skunk.services;

import com.boha.skunk.data.UrlData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EduScraperService {

    static final Logger logger = LoggerFactory.getLogger(EduScraperService.class);
    static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Value("${educUrl}")
    private String educUrl;
    //
    public List<UrlData> scrapeUrls(String url) throws IOException {
        List<UrlData> urlDataList = new ArrayList<>();
        logger.info(".... Scraping URL: {}", url);
        try {
            Document document = Jsoup.connect(url).get();
            Elements links = document.select("a[href]");

            for (Element link : links) {
                String href = link.attr("href");
                String text = link.text();
                if (isValidUrl(href)) {
                    urlDataList.add(new UrlData(href, text));
                }
            }
            logger.info("Scraped URLs: {}", gson.toJson(urlDataList));
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return urlDataList;
    }

    private boolean isValidUrl(String url) {
        // Add your validation logic here to determine if the URL is valid
        // Return true if the URL is valid, false otherwise
        return url.startsWith("http://") || url.startsWith("https://");
    }

}
