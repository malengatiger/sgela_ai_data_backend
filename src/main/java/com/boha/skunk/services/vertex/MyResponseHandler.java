package com.boha.skunk.services.vertex;

import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ResponseStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Optional;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class MyResponseHandler implements Runnable {
    private ResponseStream<GenerateContentResponse> responseStream;

    public MyResponseHandler(ResponseStream<GenerateContentResponse> responseStream) {
        this.responseStream = responseStream;
    }
    static final Logger logger = Logger.getLogger(MyResponseHandler.class.getSimpleName());
    static final Gson G =  new GsonBuilder().setPrettyPrinting().create();
    static final String mm = "\uD83C\uDF38\uD83C\uDF38\uD83C\uDF38\uD83C\uDF38 " +
            " MyResponseHandler  \uD83C\uDF38";
    @Override
    public void run() {
        try {
            // Consume the stream and print the results
            while (true) {
                Optional<GenerateContentResponse> response =
                        responseStream.stream().findFirst();
                if (response == null) {
                    break;
                }
                logger.info(mm+"Response incoming..... ");
               logger.info(G.toJson(  response ));
            //    return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // return null;
    }
}
