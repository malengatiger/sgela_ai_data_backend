package com.boha.skunk.data;

public abstract class Exam {
    public Long id;
    public Long examDocumentId;
    public Long subjectId;
    public  String subject;
    public  String link;
    public  String title;
    public  String documentTitle;
    public  String zippedPaperUrl;
    public  boolean isMemo;
    public  int year;
    public  String examPdfUrl;
    public  String cloudStorageUri;

    public String getCloudStorageUri() {
        return cloudStorageUri;
    }

    public void setCloudStorageUri(String cloudStorageUri) {
        this.cloudStorageUri = cloudStorageUri;
    }

    public Long getId() {
        return id;
    }

    public String getExamPdfUrl() {
        return examPdfUrl;
    }

    public void setExamPdfUrl(String examPdfUrl) {
        this.examPdfUrl = examPdfUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExamDocumentId() {
        return examDocumentId;
    }

    public void setExamDocumentId(Long examDocumentId) {
        this.examDocumentId = examDocumentId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getZippedPaperUrl() {
        return zippedPaperUrl;
    }

    public void setZippedPaperUrl(String zippedPaperUrl) {
        this.zippedPaperUrl = zippedPaperUrl;
    }

    public boolean isMemo() {
        return isMemo;
    }

    public void setMemo(boolean memo) {
        isMemo = memo;
    }

}
