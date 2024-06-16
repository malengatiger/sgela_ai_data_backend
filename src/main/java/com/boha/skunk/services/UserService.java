package com.boha.skunk.services;

import com.boha.skunk.data.UploadResponse;
import com.boha.skunk.data.User;
import com.boha.skunk.util.Util;
import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Service
//@RequiredArgsConstructor
public class UserService {

    static final String mm = "\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D " +
            "UserService \uD83D\uDD35";
    static final Logger logger = Logger.getLogger(UserService.class.getSimpleName());
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();
//     final MailService mailService;
        final SgelaFirestoreService sgelaFirestoreService;
    final CloudStorageService cloudStorageService;
    public UserService(SgelaFirestoreService sgelaFirestoreService, CloudStorageService cloudStorageService) {
        this.sgelaFirestoreService = sgelaFirestoreService;
        this.cloudStorageService = cloudStorageService;
    }

    public User createUser(User user) throws Exception {
        logger.info("\uD83E\uDDE1\uD83E\uDDE1 create user : " + G.toJson(user));
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        logger.info("\uD83E\uDDE1\uD83E\uDDE1 createRequest  .... ");
        try {
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest();
            createRequest.setPhoneNumber(user.getCellphone());
            createRequest.setDisplayName(user.getName());
            createRequest.setPassword(user.getPassword());
            createRequest.setEmail(user.getEmail());

            logger.info("\uD83E\uDDE1\uD83E\uDDE1 createUserAsync  .... ");

            ApiFuture<UserRecord> userRecord = firebaseAuth.createUserAsync(createRequest);
            logger.info("\uD83E\uDDE1\uD83E\uDDE1 userRecord : " + G.toJson(userRecord));
            String uid = userRecord.get().getUid();
            user.setFirebaseUserId(uid);
            user.setPassword(null);
            String message = "Dear " + user.getName() +
                    "      ,\n\nYou have been registered with SgelaAI and the team is happy to send you the first time login password. '\n" +
                    "      \nPlease login on the web with your email and the attached password but use your cellphone number to sign in on the phone.\n" +
                    "      \n\nThank you for working with GeoMonitor. \nWelcome aboard!!\nBest Regards,\nThe SgelaAI Team\ninfo@sgela-ai.io\n\n";

            logger.info("\uD83E\uDDE1\uD83E\uDDE1 sending email  .... ");

//            mailService.sendHtmlEmail(user.getEmail(), message, "Welcome to SgelaAI");
            logger.info("\uD83E\uDDE1\uD83E\uDDE1 create user completed. ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return addUser(user);
    }

    private User addUser(User user) throws ExecutionException, InterruptedException {
        String id = sgelaFirestoreService.addDocument(user);
        return sgelaFirestoreService.getDocument(User.class.getSimpleName(),id,User.class);
    }
    public User updateUser(User user) throws Exception {
        sgelaFirestoreService.updateDocument(User.class.getSimpleName(),
                user.getFirebaseUserId(), Util.objectToMap(user));
        return sgelaFirestoreService.getDocument(User.class.getSimpleName(),
                user.getFirebaseUserId(),User.class);
    }
    public User updateUserProfilePicture(String firebaseUserId, File file) throws Exception {
        User user = sgelaFirestoreService.getUserByFirebaseId(firebaseUserId);
        if (user == null) {
            logger.info(mm+"User not available");
            throw new Exception("No User aboard. Mutiny abounds!");
        }

        UploadResponse url = cloudStorageService.uploadFile(file, user.getId(),CloudStorageService.ORG_IMAGE_FILE);
        user.setProfileUrl(url.downloadUrl);
        logger.info(mm+"User found, to be updated with profileUrl: "+ G.toJson(user));
        return sgelaFirestoreService.updateUser(user);
    }

    public int updateUserAuth(User user) throws Exception {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(user.getFirebaseUserId())
                    .setEmail(user.getEmail())
                    .setPhoneNumber(user.getCellphone())
                    .setEmailVerified(false)
                    .setPassword(user.getPassword())
                    .setDisplayName(user.getName())
                    .setDisabled(false);

            FirebaseAuth.getInstance().updateUser(request);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("User auth update failed: " + e.getMessage());
        }

    }

    public List<User> getOrganizationUsers(Long organizationId) throws ExecutionException, InterruptedException {
        return sgelaFirestoreService.getOrganizationUsers(organizationId);
    }
}
