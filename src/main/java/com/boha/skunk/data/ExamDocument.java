package com.boha.skunk.data;



//@Table(name = "exam_documents")

public class ExamDocument {
    
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

   //@Column(name = "title")
    String title;

   //@Column(name = "link", columnDefinition = "TEXT")
    String link;

    int year;

    public ExamDocument() {
    }

    public ExamDocument(Long id, String title, String link, int year) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
