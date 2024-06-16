package com.boha.skunk.services;


import com.boha.skunk.data.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.tls.OkHostnameVerifier;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
@Service
/**
 *  Scrapes the Web to download exam paper links
 */
public class LinkExtractorService {
    private final OkHttpClient client;
    static final String xx = "\uD83E\uDD43\uD83E\uDD43\uD83E\uDD43 LinkExtractorService \uD83E\uDD43";

    static final String mm = "\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35 LinkExtractorService \uD83D\uDC9C";
    static final Logger logger = LoggerFactory.getLogger(LinkExtractorService.class);
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Value("${educUrl}")
    private String educUrl;

    final SgelaFirestoreService firestoreService;

    public LinkExtractorService(SgelaFirestoreService firestoreService) {
        this.firestoreService = firestoreService;
        this.client = createHttpClient();
    }

    private OkHttpClient createHttpClient() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            HostnameVerifier hostnameVerifier = OkHostnameVerifier.INSTANCE;

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .connectTimeout(120, TimeUnit.SECONDS) // Set the connect timeout to 10 seconds
                    .readTimeout(120, TimeUnit.SECONDS) // Set the read timeout to 10 seconds
                    .hostnameVerifier(hostnameVerifier)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create OkHttpClient with custom SSL configuration", e);
        }
    }

    /**
     *  Download the main page of the website and save all exam paper links found
     * @return List<SubjectExamInterfaceBag>
     */
    public List<SubjectExamInterfaceBag> downloadExamDocuments() {
        List<SubjectExamInterfaceBag> bags = new ArrayList<>();

        logger.info(mm + " ..... download South African Matric Exam Documents: " + educUrl);
        try {
            Request request = new Request.Builder()
                    .url(educUrl)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String html = response.body().string();
                    // Process the HTML response
                    Document document = Jsoup.parse(html);
                    Element table = document.selectFirst("#dnn_ctr1741_Links_lstLinks");

                    if (table != null) {
                        Elements linkElements = table.select("a[href]");
                        for (Element linkElement : linkElements) {
                            String link = linkElement.attr("href");
                            String title = linkElement.text();
                            var year = extractYearFromText(title);
                            if (year > -1) {
                                var examDoc = firestoreService.getExamDocumentByTitle(title);
                                if (examDoc == null) {
                                    examDoc = new ExamDocument(System.currentTimeMillis(), title, convertToAbsoluteLink(link), year);
                                    firestoreService.addExamDocument(examDoc);
                                }
                                var s = xx + " ... ExamDocument created: " + G.toJson(examDoc);
                                logger.info(s);
                                var subjectExamInterfaceBag = getExamPaperLinks(examDoc, link, title);
                                if (subjectExamInterfaceBag != null) {
                                    subjectExamInterfaceBag.setExamDocument(examDoc);
                                    bags.add(subjectExamInterfaceBag);
                                }
                            }
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to download exam documents: " + e.getMessage());
        }
        return bags;
    }

    public int extractYearFromText(String text) {
        // Define the regular expression pattern to match the year
        Pattern pattern = Pattern.compile("\\b\\d{4}\\b");

        // Create a matcher object to find the pattern in the text
        Matcher matcher = pattern.matcher(text);

        // Check if a match is found
        if (matcher.find()) {
            // Extract the matched year
            String yearString = matcher.group();
            // Parse the year string to an integer
            int year = Integer.parseInt(yearString);
            return year;
        }

        // Return a default value if no year is found
        return -1;
    }

    private SubjectExamInterfaceBag getExamPaperLinks(ExamDocument examDocument, String url, String title) {
        SubjectExamInterfaceBag bag = null;
        logger.info(mm + "download exam paper links: " + title + "  url: " + url);
        try {
            Request mRequest = new Request.Builder()
                    .url(convertToAbsoluteLink(url))
                    .build();
            try (Response mResponse = client.newCall(mRequest).execute()) {
                if (mResponse.isSuccessful() && mResponse.body() != null) {
                    String html2 = mResponse.body().string();
                    // Process the HTML response
                    Document destinationDocument = Jsoup.parse(html2);
                    bag = extractLinksFromPage(examDocument, destinationDocument);
                    var ss = xx + "\n\nEXAM PROCESSED: " +
                            " ... ExamDocument:" + examDocument.getTitle() + "  "
                            + title + " - "
                            + convertToAbsoluteLink(url)
                            + "  \uD83E\uDD66 links extracted: "
                            + bag.getLinks().size();
                    logger.info(ss);
                } else {
                    var ms = xx + "Error: " + mResponse.code() + " - " + mResponse.message();
                    logger.error(ms);
//                    throw new RuntimeException("Failed to download exam paper links");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
//            throw new RuntimeException("Failed to download exam paper links", e);
        }
        return bag;
    }

    private SubjectExamInterfaceBag extractLinksFromPage(ExamDocument examDocument, Document examWebPage) {
        List<ExamInterface> links = new ArrayList<>();
        SubjectExamInterfaceBag bag = null;
        String docTitle = examWebPage.title();
        var ss = "\n" + mm + " extractLinksFromPage: examWebPage: Document Title: " + docTitle;
        logger.info(ss);

        Set<String> uniqueSubjects = new HashSet<>();
        Subject subject = null;
        // Select elements by class
        Elements docs = examWebPage.select(".DnnModule-DNN_Documents");
        List<ExamLink> examLinks = new ArrayList<>();
        List<AnswerLink> answerLinks = new ArrayList<>();
        examLinks = new ArrayList<>();
        for (Element container : docs) {
            Elements titleElements = container.select(".eds_containerTitle");
            Element titleElement = titleElements.first();
            String title = titleElement.text();
            switch (title) {
                case "Agricultural Mangement Practices":
                    title = "Agricultural Management Practices";
                    break;
                case "Accounting (Senior Certificate)":
                    title = "Accounting";
                    break;
                case "Business Studies (Senior Certificate)":
                    title = "Business Studies";
                    break;
                case "Computer Aplication Technology":
                    title = "Computer Applications Technology";
                    break;
                case "Dance studies":
                    title = "Dance Studies";
                    break;
                case "Engineering Graphic and Design":
                    title = "Engineering Graphics and Design";
                    break;
                case "Engineering Graphics Design":
                    title = "Engineering Graphics and Design";
                    break;
                case "English First Additional Language: 2016-2018":
                    title = "English";
                    break;
                case "English HL":
                    title = "English";
                    break;
                case "GEOGRAPHY":
                    title = "Geography";
                    break;
                case "Life Sciences version 1":
                    title = "Life Sciences";
                    break;
                case "life sciences":
                    title = "Life Sciences";
                    break;
                case "Mathematics: 2015":
                    title = "Mathematics";
                    break;
                case "Mathematics: 2016":
                    title = "Mathematics";
                    break;
                case "Mathematics: 2017":
                    title = "Mathematics";
                    break;
                case "Mathematics: 2018":
                    title = "Mathematics";
                    break;
                case "Physical Sciences: 2015":
                    title = "Physical Sciences";
                    break;
                case "Physical Sciences: 2016":
                    title = "Physical Sciences";
                    break;
                case "Physical Sciences: 2017":
                    title = "Physical Sciences";
                    break;
                case "Physical Sciences: 2018":
                    title = "Physical Sciences";
                    break;
                case "Physical Science":
                    title = "Physical Sciences";
                    break;
                case "Religion Studies":
                    title = "Religious Studies";
                    break;

                case "Relgious Studies":
                    title = "Religious Studies";
                    break;
                case "Religion Studies (Senior Certificate)":
                    title = "Religious Studies";
                    break;
                case "South African Sign Language (Final)":
                    title = "South African Sign Language";
                    break;
                case "South African Sign Language (Preparatory)":
                    title = "South African Sign Language";
                    break;
                case "sepedi":
                    title = "Sepedi";
                    break;
                case "Sepedi HL":
                    title = "Sepedi";
                    break;
                case "setswana":
                    title = "Setswana";
                    break;
                case "Setswana HL":
                    title = "Setswana";
                    break;
                case "Xitsonga HL":
                    title = "Xitsonga";
                    break;
                case "isiZulu HL":
                    title = "IsiZulu";
                    break;
                case "isizulu":
                    title = "IsiZulu";
                    break;
                case "isindebele":
                    title = "IsiNdebele";
                    break;
                case "isindebele HL":
                    title = "IsiZulu";
                    break;
                case "Civil technology":
                    title = "Civil Technology";
                    break;
                case "Computer Applicaitons Technology":
                    title = "Computer Applications Technology";
                    break;
                case "Computer Application Technology":
                    title = "Computer Applications Technology";
                    break;
                case "Afrikaans HL":
                    title = "Afrikaans";
                    break;
            }

            try {
                subject = firestoreService.getSubjectByTitle(title);
                if (subject == null) {
                    subject = new Subject();
                    subject.setId(System.currentTimeMillis());
                    subject.setTitle(title);
                    firestoreService.addSubject(subject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Elements rows = container.select(".TitleCell");
            // Iterate over the rows and extract the link and text
            for (Element row : rows) {
                Element linkElement = row.selectFirst("td a");
                if (linkElement != null) {
                    String link = linkElement.attr("href");
                    String text = linkElement.text();
                    String textUpperCase = text.toUpperCase();
                    //ignore languages except English
                    if (textUpperCase.contains("SESOTHO")
                            || textUpperCase.contains("SETSWANA")
                            || textUpperCase.contains("ISIXHOSA")
                            || textUpperCase.contains("ISINDEBELE")
                            || textUpperCase.contains("ISIZULU")
                            || textUpperCase.contains("XITSONGA")
                            || textUpperCase.contains("SISWATI")
                            || textUpperCase.contains("TSHIVENDA")
                            || textUpperCase.contains("SEPEDI")) {
                        // Ignore
                    } else {
                        uniqueSubjects.add(titleElements.first().text());
                        var m = text.toLowerCase();
                        if (m.contains("memo") || m.contains("addendum")) {
                            var memo = new AnswerLink();
                            memo.setId(System.currentTimeMillis());
                            memo.setMemo(true);
                            memo.setLink(convertToAbsoluteLink(link));
                            memo.setExamDocumentId(examDocument.getId());
                            memo.setDocumentTitle(examDocument.getTitle());
                            memo.setTitle(text);
                            memo.setYear(examDocument.getYear());
                            memo.setSubject(subject.getTitle());
                            memo.setSubjectId(subject.getId());
                            answerLinks.add(memo);
                            links.add(memo);
                        } else {
                            var examLink = new ExamLink();
                            examLink.setId(System.currentTimeMillis());
                            examLink.setMemo(false);
                            examLink.setLink(convertToAbsoluteLink(link));
                            examLink.setExamDocumentId(examDocument.getId());
                            examLink.setDocumentTitle(examDocument.getTitle());
                            examLink.setTitle(text);
                            examLink.setYear(examDocument.getYear());
                            examLink.setSubject(subject.getTitle());
                            examLink.setSubjectId(subject.getId());

                            examLinks.add(examLink);
                            links.add(examLink);
                        }
                    }
                }
            }
        }

        var s = mm + docTitle + " - links from examWebPage: " + links.size() + "\n\n\n";
        logger.info(s);
        try {
            firestoreService.addAnswerLinks(answerLinks);
            firestoreService.addExamLinks(examLinks);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Firestore batch write failed: " + e.getMessage());
        }
        logger.info("\n\n\n");
        bag = new SubjectExamInterfaceBag(examDocument, subject, links);
        return bag;
    }

    public String convertToAbsoluteLink(String relativeLink) {
        HttpUrl baseHttpUrl = HttpUrl.parse(educUrl);
        assert baseHttpUrl != null;
        HttpUrl absoluteHttpUrl = baseHttpUrl.resolve(relativeLink);
        if (absoluteHttpUrl != null) {
            return absoluteHttpUrl.toString();
        }
        return null;
    }

}