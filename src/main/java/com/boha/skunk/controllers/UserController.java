package com.boha.skunk.controllers;

import com.boha.skunk.data.Organization;
import com.boha.skunk.data.User;
import com.boha.skunk.services.UserBatchService;
import com.boha.skunk.services.UserService;
import com.boha.skunk.services.WebCrawlerService;
import com.boha.skunk.util.DirectoryUtils;
import com.boha.skunk.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/users")
//@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserBatchService userBatchService;
    private final WebCrawlerService webCrawlerService;
    static final String mm = "\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D " +
            "UserController \uD83D\uDD35";
    static final Logger logger = Logger.getLogger(UserController.class.getSimpleName());
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public UserController(UserService userService, UserBatchService userBatchService, WebCrawlerService webCrawlerService) {
        this.userService = userService;
        this.userBatchService = userBatchService;
        this.webCrawlerService = webCrawlerService;
    }
    @PostMapping("/updateUserProfile")
    public ResponseEntity<Object> updateUserProfilePicture(@RequestParam("firebaseUserId") String firebaseUserId,
                                                     @RequestParam("file") MultipartFile file) {
        try {
            // Convert MultipartFile to File
            File profileFile = Util.convertMultipartFileToFile(file);
            if(Util.isFileTooBig(profileFile,1024*1024*2L)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Profile picture too large. Should be below 2MB ");
            }
            User mUser = userService.updateUserProfilePicture(firebaseUserId, profileFile);

            return ResponseEntity.ok(mUser);
        } catch (Exception e) {
            e.printStackTrace();
            // Return an error response
            return ResponseEntity.status(
                    HttpStatus.BAD_REQUEST).body("Failed to update user: " + e.getMessage());
        }
    }
    @PostMapping("/registerUser")
    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        try {
            User result = userService.createUser(user);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/updateUser")
    public ResponseEntity<Object> updateUser(@RequestBody User user) {
        try {
            User result = userService.updateUser(user);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getUsers")
    public ResponseEntity<Object> getOrganizationUsers(@RequestParam Long organizationId) {
        try {
            List<User> organizationUsers = userService.getOrganizationUsers(organizationId);
            return ResponseEntity.ok(organizationUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
