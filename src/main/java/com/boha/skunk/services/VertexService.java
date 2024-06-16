package com.boha.skunk.services;

import com.boha.skunk.services.vertex.MyResponseHandler;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

@SuppressWarnings("all")
@Service
public class VertexService {
    private static final String PROJECT_ID = "sgela-ai-33";
    private static final String LOCATION = "us-east4";
    private static final String mm = " \uD83D\uDC37  \uD83D\uDC37  \uD83D\uDC37 VertexService";
    static final Logger logger = Logger.getLogger(VertexService.class.getSimpleName());
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    // Map to store chat sessions based on session ID
    private Map<String, ChatSession> chatSessions = new HashMap<>();

    // Ask interrelated questions in a row using a ChatSession object.
    public GenerateContentResponse chat(String sessionId, String prompt) {
        GenerateContentResponse response = null;
        try (VertexAI vertexAi = new VertexAI(PROJECT_ID, LOCATION)) {
            // Instantiate a GenerationConfig
            GenerationConfig config = GenerationConfig.newBuilder().setMaxOutputTokens(2048).build();
            // Instantiate a model
            GenerativeModel model = new GenerativeModel.Builder()
                    .setModelName("gemini-pro")
                    .setVertexAi(vertexAi)
                    .setGenerationConfig(config)
                    .build();

            // Retrieve the existing chat session for the given session ID or create a new one if it doesn't exist
            ChatSession chat = getOrCreateChatSession(sessionId, model);
            // Send the first message.
            // ChatSession also has two versions of sendMessage, stream and non-stream
            ResponseStream<GenerateContentResponse> responseStream = chat.sendMessageStream(prompt);

            // Create a MyResponseHandler to consume the stream and print the results
            MyResponseHandler myResponseHandler = new MyResponseHandler(responseStream);
            myResponseHandler.run();
            new Thread(myResponseHandler).start();
            Callable<String> callable = () -> {
                MyResponseHandler responseHandler = new MyResponseHandler(responseStream);

                new Thread(responseHandler).start();
                return prompt;

                // return responseHandler.run();
            };
            // Now send another message. The history will be remembered by the ChatSession.
            // Note: the stream needs to be consumed before you send another message
            // or fetch the history.
            ResponseStream<GenerateContentResponse> anotherResponse =
                    chat.sendMessageStream("Can you tell me more?");

            // Do something with the second response

            // See the whole history. Make sure you have consumed the stream.
            List<Content> history = chat.getHistory();

            logger.info("\n\n" + mm + "Response incoming ... history: " + history.size());
            logger.info(G.toJson(response));
        } catch (IOException e) {
            logger.severe("chat: An IO exception occurred: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.severe("chat: An exception occurred: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return response;
    }
    private ChatSession getOrCreateChatSession(String sessionId, GenerativeModel model) {
        logger.info("\n\n" + mm
                + " Check if the chat session already exists for the given session ID");
        if (chatSessions.containsKey(sessionId)) {
            logger.info(mm
                    + " Checked out ChatSession exists.");
            return chatSessions.get(sessionId);
        }
        logger.info("\n\n" + mm + " chat session does not exist, creating a new one");
        ChatSession chatSession = model.startChat();
        chatSessions.put(sessionId, chatSession);
        return chatSession;
    }
}