package com.boha.skunk.services;

import com.boha.skunk.data.*;
import com.boha.skunk.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
//@RequiredArgsConstructor
public class OrganizationService {
    static final String mm = "\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35 " +
            "OrganizationService \uD83D\uDC9C";
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    static final Logger logger = Logger.getLogger(OrganizationService.class.getSimpleName());
    private final SgelaFirestoreService sgelaFirestoreService;

    private final MailService mailService;
    private final UserService userService;
    private final CloudStorageService cloudStorageService;

    public OrganizationService(SgelaFirestoreService sgelaFirestoreService, MailService mailService, UserService userService, CloudStorageService cloudStorageService) {
        this.sgelaFirestoreService = sgelaFirestoreService;
        this.mailService = mailService;
        this.userService = userService;
        this.cloudStorageService = cloudStorageService;
    }

    //
    public List<Organization> getOrganizations() throws Exception {
        return sgelaFirestoreService.getAllDocuments(Organization.class);
    }


    public Organization createSgelaOrganization(File logoFile, File splashFile) throws Exception {
        logger.info(mm + "createSgelaOrganization starting ...logoFile: " + logoFile.length() + " splashFile: " + splashFile.length());

        Long id = Util.generateUniqueLong();
        String logoUrl = cloudStorageService.uploadFile(logoFile, id, CloudStorageService.ORG_IMAGE_FILE);
        String splashUrl = cloudStorageService.uploadFile(splashFile, id, CloudStorageService.ORG_IMAGE_FILE);
        Country country = sgelaFirestoreService.getCountryByName("South Africa");
        City city = sgelaFirestoreService.getCityByName("Pretoria");

        logger.info(mm + "createSgelaOrganization: country ..." + country.getName());
        logger.info(mm + "createSgelaOrganization: city ..." + city.getName());

        Pricing ps = new Pricing();
        ps.setCurrency(country.getCurrencyName());
        ps.setAnnualPrice(200.00);
        ps.setMonthlyPrice(25.00);
        ps.setCountryId(country.getId());
        ps.setDate(new Date().toInstant().toString());
        ps.setId(Util.generateUniqueLong());
        sgelaFirestoreService.addDocument(ps);

        Subscription subscription = new Subscription();
        subscription.setActiveFlag(true);
        subscription.setCountryId(country.getId());
        subscription.setDate(new Date().toInstant().toString());
        subscription.setNumberOfUsers(0);
        subscription.setOrganizationId(id);
        subscription.setOrganizationName("SgelaAI Inc.");
        subscription.setPricing(ps);
        subscription.setId(Util.generateUniqueLong());


        User user = new User();
        user.setId(Util.generateUniqueLong());
        user.setFirstName("Aubrey");
        user.setLastName("Malabie");
        user.setCellphone("+27655917675");
        user.setEmail("malengatiger@gmail.com");
        user.setPassword("pass123_");
        user.setActiveFlag(true);
        user.setDate(new Date().toInstant().toString());
        user.setSubscriptionDate(user.getDate());
        user.setSubscriptionId(subscription.getId());
        user.setOrganizationId(id);


        Organization organization = new Organization();
        organization.setName("SgelaAI Inc.");
        organization.setId(id);
        organization.setAdminUser(user);
        organization.setCountry(country);
        organization.setCity(city);
        organization.setLogoUrl(logoUrl);
        organization.setSplashUrl(splashUrl);
        organization.setId(id);
        organization.setCoverageRadiusInKM(1500);
        organization.setDate(new Date().toInstant().toString());
        organization.setExclusiveCoverage(false);
        organization.setTagLine("AI for African People!");
        organization.setMaxUsers(10000);

        logger.info(mm + "createSgelaOrganization: add org to database");

        var mUser = userService.createUser(organization.getAdminUser());
        organization.setAdminUser(mUser);
        sgelaFirestoreService.addDocument(organization);

        subscription.setUser(mUser);
        sgelaFirestoreService.addDocument(subscription);

        var m = sgelaFirestoreService.getOrganization(organization.getId());
        logger.info(mm + "Organization: " + m.getName() + " added to database");
        return m;
    }

    public Organization addOrganization(Organization organization) throws Exception {
        organization.setId(Util.generateUniqueLong());
        organization.setDate(new Date().toInstant().toString());

        logger.info(mm + "addOrganization: org to be added: " + G.toJson(organization));
        if (organization.getAdminUser() == null) {
            throw new Exception("Admin user not found");
        }
        organization.getAdminUser().setId(Util.generateUniqueLong());
        organization.getAdminUser().setOrganizationId(organization.getId());
        organization.getAdminUser().setDate(new Date().toInstant().toString());
        organization.getAdminUser().setActiveFlag(true);

        try {
            var mUser = userService.createUser(organization.getAdminUser());
            organization.setAdminUser(mUser);
            sgelaFirestoreService.addDocument(organization);

            var m = sgelaFirestoreService.getOrganization(organization.getId());
            logger.info(mm + "Organization: " + m.getName() + " added to database");
            return m;
        } catch (Exception e) {
            logger.info(mm + "Fucking error, does not percolate! " + e.getMessage());
            throw e;
        }
    }

    public Branding addBranding(Long organizationId, String organizationName,
                                String tagLine, String orgUrl,
                                File logoFile, File splashFile, int splashTimeInSeconds) throws Exception {

        Branding branding = new Branding();
        branding.setOrganizationId(organizationId);
        branding.setOrganizationName(organizationName);
        branding.setId(Util.generateUniqueLong());
        branding.setTagLine(tagLine);
        branding.setOrganizationUrl(orgUrl);
        branding.setSplashTimeInSeconds(splashTimeInSeconds);
        String logoUrl = null;
        if (logoFile != null) {
            logoUrl = cloudStorageService.uploadFile(logoFile, branding.getId(), CloudStorageService.ORG_IMAGE_FILE);
        }
        String splashUrl = cloudStorageService.uploadFile(splashFile, branding.getId(), CloudStorageService.ORG_IMAGE_FILE);

        branding.setLogoUrl(logoUrl);
        branding.setSplashUrl(splashUrl);
        branding.setActiveFlag(true);
        branding.setDate(new Date().toInstant().toString());

        sgelaFirestoreService.addDocument(branding);

        logger.info(mm + "Branding: " + G.toJson(branding) + " added to database");
        return branding;
    }

    public Organization updateOrganization(Organization organization) throws Exception {
        sgelaFirestoreService.updateDocument(Organization.class.getSimpleName(),
                organization.getId(), Util.objectToMap(organization));
        return sgelaFirestoreService.getDocument(Organization.class.getSimpleName(),
                organization.getId(), Organization.class);
    }
}