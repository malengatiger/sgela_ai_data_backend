package com.boha.skunk.data;

public class SummarizedExam {
    private String concepts;
    private String lessonPlan;
    private String pdfUri;
    private String agentResponseUri;
    private String agentResponseUrl;
    private Long examLinkId;
    private String date;
    private String firebaseUserId;
    private String answers;
    private int totalTokens;
    private int promptTokens;
    private int candidatesTokens;

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

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }

    public int getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(int promptTokens) {
        this.promptTokens = promptTokens;
    }

    public int getCandidatesTokens() {
        return candidatesTokens;
    }

    public void setCandidatesTokens(int candidatesTokens) {
        this.candidatesTokens = candidatesTokens;
    }
}
