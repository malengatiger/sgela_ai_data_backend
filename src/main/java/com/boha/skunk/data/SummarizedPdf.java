package com.boha.skunk.data;

public class SummarizedPdf {
    private String concepts;
    private String lessonPlan;
    private String pdfUri;
    private String agentResponseUri;
    private String agentResponseUrl;
    private Long examLinkId;
    private String date;
    private String firebaseUserId;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFirebaseUserId() {
        return firebaseUserId;
    }

    public void setFirebaseUserId(String firebaseUserId) {
        this.firebaseUserId = firebaseUserId;
    }

    public String getAgentResponseUrl() {
        return agentResponseUrl;
    }

    public void setAgentResponseUrl(String agentResponseUrl) {
        this.agentResponseUrl = agentResponseUrl;
    }

    public String getAgentResponseUri() {
        return agentResponseUri;
    }

    public void setAgentResponseUri(String agentResponseUri) {
        this.agentResponseUri = agentResponseUri;
    }

    public Long getExamLinkId() {
        return examLinkId;
    }

    public void setExamLinkId(Long examLinkId) {
        this.examLinkId = examLinkId;
    }

    public String getConcepts() {
        return concepts;
    }

    public void setConcepts(String concepts) {
        this.concepts = concepts;
    }

    public String getLessonPlan() {
        return lessonPlan;
    }

    public void setLessonPlan(String lessonPlan) {
        this.lessonPlan = lessonPlan;
    }

    public String getPdfUri() {
        return pdfUri;
    }

    public void setPdfUri(String pdfUri) {
        this.pdfUri = pdfUri;
    }
}
