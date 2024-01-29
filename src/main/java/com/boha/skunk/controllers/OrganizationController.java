package com.boha.skunk.controllers;

import com.boha.skunk.data.Branding;
import com.boha.skunk.data.Organization;
import com.boha.skunk.data.User;
import com.boha.skunk.services.OrganizationService;
import com.boha.skunk.services.SgelaFirestoreService;
import com.boha.skunk.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/organizations")
//@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;
    final SgelaFirestoreService sgelaFirestoreService;
    static final String mm = "\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D " +
            "OrganizationController \uD83D\uDD35";
    static final Logger logger = Logger.getLogger(OrganizationController.class.getSimpleName());
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public OrganizationController(OrganizationService organizationService, SgelaFirestoreService sgelaFirestoreService) {
        this.organizationService = organizationService;
        this.sgelaFirestoreService = sgelaFirestoreService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "\uD83C\uDF50\uD83C\uDF50\uD83C\uDF50\uD83C\uDF50" +
                " SgelaAI Backend Pinged on " + new Date().toInstant().toString();
    }

    @GetMapping("/getSgelaOrganization")
    public Organization getSgelaOrganization() throws Exception {
        return sgelaFirestoreService.getSgelaOrganization();
    }

    @GetMapping("/finishSignUp")
    public User finishSignUp(@RequestParam Long userId) {
        //todo - Complete the code to deal with user who had email sign in link sent to them
        logger.info(mm + "shit from Firebase email link user ....");

        return new User();
    }

    @PostMapping("/updateOrganization")
    public ResponseEntity<Object> updateOrganization(@RequestBody Organization organization) {
        try {
            Organization result = organizationService.updateOrganization(organization);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getOrganizations")
    public ResponseEntity<Object> getOrganizations(@RequestParam Long countryId) {
        try {
            List<Organization> pricings = organizationService.getOrganizations();
            return ResponseEntity.ok(pricings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/addOrganization")
    public ResponseEntity<Object> addOrganization(@RequestBody Organization organization) {
        try {
            // Call the service method to create the organization
            Organization createdOrganization =
                    organizationService.addOrganization(organization);

            // Return a success response
            return ResponseEntity.ok(createdOrganization);
        } catch (Exception e) {
            // Return an error response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("\uD83D\uDD34\uD83D\uDD34\uD83D\uDD34\uD83D\uDD34 Failed to create organization: "
                            + e.getMessage());
        }
    }


    @PostMapping("/uploadBrandingWithoutLogo")
    public ResponseEntity<Object> uploadBrandingWithoutLogo(@RequestParam("organizationId") Long organizationId,
                                                            @RequestParam("organizationName") String organizationName,
                                                            @RequestParam("tagLine") String tagLine,
                                                            @RequestParam("orgUrl") String orgUrl,
                                                            @RequestParam("splashTimeInSeconds") int splashTimeInSeconds,
                                                            @RequestParam("splashFile") MultipartFile splashFile) {
        try {
            logger.info(mm + "Branding: " + organizationId + " to be processed .....");

            File convertedSplashFile = Util.convertMultipartFileToFile(splashFile);
            if (Util.isFileTooBig(convertedSplashFile, 1024 * 1024 * 4L)) {
                return ResponseEntity.badRequest().body("Splash image too large. Should be below 4MB.");
            }

            // Call the service method to addBranding
            Branding createdBranding = organizationService.addBranding(
                    organizationId, organizationName, tagLine, orgUrl,
                    null, convertedSplashFile, splashTimeInSeconds);

            // Return a success response
            return ResponseEntity.ok(createdBranding);
        } catch (IOException e) {
            logger.severe("Failed to convert multipart file to file.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload branding: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("Failed to upload branding.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload branding: " + e.getMessage());
        }
    }

    @PostMapping("/uploadBrandingWithLogo")
    public ResponseEntity<Object> uploadBrandingWithLogo(@RequestParam("organizationId") Long organizationId,
                                                         @RequestParam("organizationName") String organizationName,
                                                         @RequestParam("tagLine") String tagLine,
                                                         @RequestParam("orgUrl") String orgUrl,
                                                         @RequestParam("splashTimeInSeconds") int splashTimeInSeconds,

                                                         @RequestParam("logoFile") MultipartFile logoFile,
                                                         @RequestParam("splashFile") MultipartFile splashFile) {
        try {
            logger.info(mm + "Branding: " + organizationId + " to be processed .....");
            // Convert MultipartFile to File
            File convertedLogoFile = Util.convertMultipartFileToFile(logoFile);
            if (Util.isFileTooBig(convertedLogoFile, 1024 * 1024 * 2L)) {
                return ResponseEntity.badRequest().body("Logo image too large. Should be below 2MB.");
            }

            File convertedSplashFile = Util.convertMultipartFileToFile(splashFile);
            if (Util.isFileTooBig(convertedSplashFile, 1024 * 1024 * 4L)) {
                return ResponseEntity.badRequest().body("Splash image too large. Should be below 4MB.");
            }

            // Call the service method to addBranding
            Branding createdBranding = organizationService.addBranding(
                    organizationId, organizationName, tagLine, orgUrl,
                    convertedLogoFile, convertedSplashFile, splashTimeInSeconds);

            // Return a success response
            return ResponseEntity.ok(createdBranding);
        } catch (IOException e) {
            logger.severe("Failed to convert multipart file to file.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload branding: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("Failed to upload branding.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload branding: " + e.getMessage());
        }
    }

    @PostMapping("/createSgelaOrganization")
    public ResponseEntity<Object> createSgelaOrganization(
            @RequestParam("logoFile") MultipartFile logoFile,
            @RequestParam("splashFile") MultipartFile splashFile) {
        try {
            logger.info(mm + "createSgelaOrganization starting ...");

            // Convert MultipartFile to File
            File convertedLogoFile = Util.convertMultipartFileToFile(logoFile);
            if (Util.isFileTooBig(convertedLogoFile, 1024 * 1024 * 2L)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Logo file too large. Should be below 2MB ");
            }
            File convertedSplashFile = Util.convertMultipartFileToFile(splashFile);
            if (Util.isFileTooBig(convertedSplashFile, 1024 * 1024 * 4L)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Splash file too large. Should be below 4MB ");
            }
            logger.info(mm + "createSgelaOrganization: multipart files converted ...");

            // Call the service method to create the organization
            Organization createdOrganization =
                    organizationService.createSgelaOrganization(convertedLogoFile, convertedSplashFile);

            // Return a success response
            return ResponseEntity.ok(createdOrganization);
        } catch (Exception e) {
            // Return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create organization: " + e.getMessage());
        }
    }


}
