package com.boha.skunk.controllers;

import com.boha.skunk.data.OrganizationSponsorPaymentType;
import com.boha.skunk.data.Pricing;
import com.boha.skunk.data.SgelaSubscription;
import com.boha.skunk.data.SponsorPaymentType;
import com.boha.skunk.services.GooglePlayAPIService;
import com.boha.skunk.services.SubscriptionService;
import org.apache.http.HttpException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/subs")
//@RequiredArgsConstructor
public class SubscriptionController {

    private final String policy = "https://www.google.com/policies/privacy/";
    private final SubscriptionService subscriptionService;
    private final GooglePlayAPIService googlePlayAPIService;

    public SubscriptionController(SubscriptionService subscriptionService, GooglePlayAPIService googlePlayAPIService) {
        this.subscriptionService = subscriptionService;
        this.googlePlayAPIService = googlePlayAPIService;
    }

    @PostMapping("/addPricing")
    public ResponseEntity<String> addPricing(@RequestBody Pricing pricing) {
        try {
            String result = subscriptionService.addPricing(pricing);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getPricings")
    public ResponseEntity<List<Pricing>> getPricings(@RequestParam Long countryId) {
        try {
            List<Pricing> pricings = subscriptionService.getPricings(countryId);
            return ResponseEntity.ok(pricings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/privacyPolicy")
    public ResponseEntity<FileSystemResource> privacyPolicy() {
        try {
            File file = new ClassPathResource("privacy.txt").getFile();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "privacy.txt");

            FileSystemResource resource = new FileSystemResource(file);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/addSgelaSubscription")
    public ResponseEntity<String> addSubscription(@RequestBody SgelaSubscription sgelaSubscription) {
        try {
            String result = subscriptionService.addSubscription(sgelaSubscription);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/addSponsorPaymentType")
    public ResponseEntity<String> addSponsorPaymentType(@RequestBody SponsorPaymentType type) throws HttpException {
        try {
            String result = subscriptionService.addSponsorPaymentType(type);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }

    @PostMapping("/addOrganizationSponsorPaymentType")
    public ResponseEntity<String> addOrganizationSponsorPaymentType(@RequestBody OrganizationSponsorPaymentType type) throws HttpException {
        try {
            String result = subscriptionService.addOrganizationSponsorPaymentType(type);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }

    @GetMapping("/getSubscriptions")
    public ResponseEntity<List<SgelaSubscription>> getSubscriptions(@RequestParam Long organizationId) {
        try {
            List<SgelaSubscription> sgelaSubscriptions = subscriptionService.getSubscriptions(organizationId);
            return ResponseEntity.ok(sgelaSubscriptions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/createSubscription")
    public ResponseEntity<Object> createSubscription() {
        try {
            var result = googlePlayAPIService.createSubscription();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/addUserToSubscription")
    public ResponseEntity<SgelaSubscription> addUserToSubscription(@RequestParam Long userId,
                                                                   @RequestParam Long subscriptionId) {
        try {
            SgelaSubscription result = subscriptionService.addUserToSubscription(userId, subscriptionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/checkOrganizationSubscription")
    public ResponseEntity<Boolean> checkOrganizationSubscription(@RequestParam Long organizationId) {
        try {
            boolean result = subscriptionService.checkOrganizationSubscription(organizationId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/updateUserSubscription")
    public ResponseEntity<Integer> updateUserSubscription(@RequestParam Long userId, @RequestParam Long subscriptionId) {
        int result = subscriptionService.updateUserSubscription(userId, subscriptionId);
        return ResponseEntity.ok(result);
    }
}
