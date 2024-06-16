package com.boha;

import com.boha.skunk.data.Branding;
import com.boha.skunk.data.Organization;
import com.boha.skunk.data.User;
import com.boha.skunk.services.CloudStorageService;
import com.boha.skunk.services.OrganizationService;
import com.boha.skunk.services.SgelaFirestoreService;
import com.boha.skunk.services.UserService;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private SgelaFirestoreService sgelaFirestoreService;

    @Mock
    private UserService userService;

    @Mock
    private CloudStorageService cloudStorageService;

    @Mock
    private Storage storage;

    @Mock
    private Firestore firestore;

    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        organizationService =
                new OrganizationService(sgelaFirestoreService,
                        userService, cloudStorageService);
    }

    @Test
    void createSgelaOrganization() throws Exception {
        // Given
        File logoFile = new File("logo.png");
        File splashFile = new File("splash.png");
        Organization organization = organizationService.createSgelaOrganization(logoFile, splashFile);
        User user = new User();
        user.setEmail("<EMAIL>");
        user.setPassword("<PASSWORD>");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setDate(new Date().toInstant().toString());
        user.setOrganizationId(1L);
//        when(cloudStorageService.uploadFile(any(), any(), any())).thenReturn("logoUrl", "splashUrl");
        when(sgelaFirestoreService.getCountryByName("South Africa")).thenReturn(new com.boha.skunk.data.Country());
        when(sgelaFirestoreService.getCityByName("Cape Town")).thenReturn(new com.boha.skunk.data.City());
        when(userService.createUser(any())).thenReturn(user);

        // When

        // Then
        assertNotNull(organization);
        assertEquals("SgelaAI Inc.", organization.getName());
        assertEquals("logoUrl", organization.getLogoUrl());
        assertEquals("splashUrl", organization.getSplashUrl());
    }

    @Test
    void addOrganization() throws Exception {
        // Given
        Organization organization = new Organization();
        User adminUser = new User();
        organization.setAdminUser(adminUser);
        when(userService.createUser(any())).thenReturn(new User());
        when(sgelaFirestoreService.addDocument(any())).thenReturn(null);

        // When
        Organization addedOrganization = organizationService.addOrganization(organization);

        // Then
        assertNotNull(addedOrganization);
        assertEquals(organization.getName(), addedOrganization.getName());
        assertEquals(organization.getAdminUser().getId(), addedOrganization.getAdminUser().getId());
    }

    @Test
    void addBrandingFromPrevious() throws Exception {
        // Given
        Branding branding = new Branding();
        when(sgelaFirestoreService.addDocument(any())).thenReturn(null);

        // When
        Branding addedBranding = organizationService.addBrandingFromPrevious(branding);

        // Then
        assertNotNull(addedBranding);
        assertEquals(branding.getOrganizationId(), addedBranding.getOrganizationId());
        assertEquals(branding.getTagLine(), addedBranding.getTagLine());
    }

    @Test
    void addBranding() throws Exception {
        // Given
        File logoFile = new File("logo.png");
        File splashFile = new File("splash.png");
//        when(cloudStorageService.uploadFile(any(), any(), any())).thenReturn("logoUrl", "splashUrl");
        when(sgelaFirestoreService.addDocument(any())).thenReturn(null);

        // When
        Branding addedBranding = organizationService.addBranding(1L, "Org Name", "Tag Line", "orgUrl", logoFile, splashFile, 10, 1);

        // Then
        assertNotNull(addedBranding);
        assertEquals(1L, addedBranding.getOrganizationId());
        assertEquals("Tag Line", addedBranding.getTagLine());
        assertEquals("logoUrl", addedBranding.getLogoUrl());
        assertEquals("splashUrl", addedBranding.getSplashUrl());
    }

    @Test
    void updateOrganization(String id) throws Exception {
//        // Given
//        Organization organization = new Organization();
//        when(sgelaFirestoreService.updateDocument(id, organization));
//
//        // When
//        Organization updatedOrganization = organizationService.updateOrganization(organization);
//
//        // Then
//        assertNotNull(updatedOrganization);
//        assertEquals(organization.getId(), updatedOrganization.getId());
    }
}
