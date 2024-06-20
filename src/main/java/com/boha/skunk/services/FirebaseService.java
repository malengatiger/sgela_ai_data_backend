package com.boha.skunk.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

import static com.boha.skunk.util.E.*;

/**
 * Initializes Firebase
 */
@RequiredArgsConstructor
@Component
public class FirebaseService {

    private static final Logger logger = Logger.getLogger(FirebaseService.class.getSimpleName());

    @Value("${storageBucket}")
    private String storageBucket;
    @Value("${projectId}")
    private String projectId;

    @PostConstruct
    public void init() {
        logger.info(ALIEN + ALIEN + ALIEN + ALIEN
                + " FirebaseService: PostConstruct - \uD83C\uDF4E \uD83C\uDF4E" +
                " initializeFirebase ...... ");
        try {
            initializeFirebase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        logger.info(ALIEN + ALIEN + ALIEN + ALIEN
                + " FirebaseService initialized! \uD83E\uDD66\uD83E\uDD66");

    }
    public void initializeFirebase() throws Exception {
        logger.info(AMP+ AMP+ AMP+ "FirebaseService: .... initializing Firebase ....");
        FirebaseOptions options;
        logger.info(AMP+ AMP+ AMP+
                " Project Id from Properties: "+ RED_APPLE + " " + projectId + " bucket: " + storageBucket);
        try {
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .setDatabaseUrl("https://" + projectId + ".firebaseio.com/")
//                    .setStorageBucket(storageBucket)
                    .build();
        } catch (IOException e) {
            throw new Exception("Firebase initialization failed!  " + e.getMessage());
        }

        FirebaseApp app = FirebaseApp.initializeApp(options);
        logger.info(AMP+ AMP+ AMP+ AMP+ AMP+
                " Firebase has been initialized: "
                + app.getOptions().getDatabaseUrl()
                + " " + RED_APPLE);
    }

}
