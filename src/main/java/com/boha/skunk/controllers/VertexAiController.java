package com.boha.skunk.controllers;

import com.boha.skunk.services.SgelaFirestoreService;
import com.boha.skunk.services.VertexService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/vertex-ai")

//@RequiredArgsConstructor
public class VertexAiController {
    static final String mm = "\uD83C\uDF38\uD83C\uDF38\uD83C\uDF38\uD83C\uDF38 " +
            " YouTubeController  \uD83C\uDF38";
    static final Logger logger = Logger.getLogger(VertexAiController.class.getSimpleName());

    private final VertexService vertexService;
    @SuppressWarnings("unused")
    private final SgelaFirestoreService firestoreService;

    public VertexAiController(VertexService vertexService, SgelaFirestoreService firestoreService) {
        this.vertexService = vertexService;
        this.firestoreService = firestoreService;
    }


    @GetMapping("/chat")
    public ResponseEntity<Object> chat(@RequestParam("prompt") String prompt,
                                                 @RequestParam("sessionId") String sessionId) throws Exception {

        return ResponseEntity.ok().body(vertexService.chat(sessionId,prompt));
    }


}