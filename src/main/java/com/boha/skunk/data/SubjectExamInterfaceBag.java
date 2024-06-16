package com.boha.skunk.data;

import java.util.ArrayList;
import java.util.List;

public class SubjectExamInterfaceBag {

    ExamDocument examDocument;
    Subject subject;
    List<ExamInterface> links = new ArrayList<>();

    public SubjectExamInterfaceBag(ExamDocument examDocument, Subject subject, List<ExamInterface> links) {
        this.examDocument = examDocument;
        this.subject = subject;
        this.links = links;
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

    public List<ExamInterface> getLinks() {
        return links;
    }

    public void setLinks(List<ExamInterface> links) {
        this.links = links;
    }
}
