package com.boha.skunk.services;


import com.boha.skunk.data.ParseResult;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.ArrayList;
import java.util.List;

public class ParseMarkdown {

    public static List<ParseResult> parse(String markdownText) {
        List<ParseResult> results = new ArrayList<>();

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownText);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);

        String[] lines = html.split("\n");
        for (String line : lines) {
            boolean isHeader = line.startsWith("<h1>") || line.startsWith("<h2>") || line.startsWith("<h3>") ||
                    line.startsWith("<h4>") || line.startsWith("<h5>") || line.startsWith("<h6>");
            boolean isBlankLine = line.trim().isEmpty(); // Check for blank lines
            String text = line.replaceAll("<[^>]*>", ""); // Remove HTML tags
            results.add(new ParseResult(isHeader, text, isBlankLine)); // Pass blankLine to ParseResult
        }

        return results;
    }
}
