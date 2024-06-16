package com.boha.skunk.data;

import java.util.ArrayList;
import java.util.List;

public class ExamBag {
    List<ExamDocument> docLinks = new ArrayList<>();
    List<ExamInterface> links = new ArrayList<>();

    public ExamBag(List<ExamDocument> docLinks, List<ExamInterface> links) {
        this.docLinks = docLinks;
        this.links = links;
    }

    public List<ExamDocument> getDocLinks() {
        return docLinks;
    }

    public void setDocLinks(List<ExamDocument> docLinks) {
        this.docLinks = docLinks;
    }

    public List<ExamInterface> getLinks() {
        return links;
    }

    public void setLinks(List<ExamInterface> links) {
        this.links = links;
    }
}
