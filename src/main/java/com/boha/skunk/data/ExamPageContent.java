package com.boha.skunk.data;

public class ExamPageContent {
    private Long examLinkId;
    private Long id;
    private boolean hasImages;
    private String text;
    private int pageIndex;
    private String pageImageUrl;
    private String title;

    public ExamPageContent() {
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

    public boolean isHasImages() {
        return hasImages;
    }

    public void setHasImages(boolean hasImages) {
        this.hasImages = hasImages;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
