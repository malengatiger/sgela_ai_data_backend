package com.boha.skunk.data;


//@Table(name = "exam_links")
public class ExamLink {
    
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //
    //(name = "document_id")
    private ExamDocument examDocument;

    //
    //(name = "subject_id")
    private Subject subject;

   //@Column(name = "link", columnDefinition = "TEXT")
    private String link;

   //@Column(name = "title")
    private String title;

   //@Column(name = "document_title")
    private String documentTitle;
    private String zippedPaperUrl;

    public String getZippedPaperUrl() {
        return zippedPaperUrl;
    }

    public void setZippedPaperUrl(String zippedPaperUrl) {
        this.zippedPaperUrl = zippedPaperUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExamLink() {
    }
// Constructors, getters, and setters...

    public ExamLink(Long id, ExamDocument examDocument, Subject subject, String link, String title, String documentTitle, String zippedPaperUrl) {
        this.id = id;
        this.examDocument = examDocument;
        this.subject = subject;
        this.link = link;
        this.title = title;
        this.documentTitle = documentTitle;
        this.zippedPaperUrl = zippedPaperUrl;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public Long getId() {
        return id;
    }

    public ExamDocument getExamDocument() {
        return examDocument;
    }

    public void setExamDocument(ExamDocument examDocument) {
        this.examDocument = examDocument;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }
}
