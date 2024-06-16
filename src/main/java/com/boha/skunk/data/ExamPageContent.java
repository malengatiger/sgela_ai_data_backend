package com.boha.skunk.data;

public class ExamPageContent {
    private Long examLinkId;
    private Long answerLinkId;
    private Long id;
    private int pageIndex;
    private String pageImageUrl;
    private String title;

    public Long getAnswerLinkId() {
        return answerLinkId;
    }

    public void setAnswerLinkId(Long answerLinkId) {
        this.answerLinkId = answerLinkId;
    }

    public Long getExamLinkId() {
        return examLinkId;
    }

    public void setExamLinkId(Long examLinkId) {
        this.examLinkId = examLinkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getPageImageUrl() {
        return pageImageUrl;
    }

    public void setPageImageUrl(String pageImageUrl) {
        this.pageImageUrl = pageImageUrl;
    }
}
