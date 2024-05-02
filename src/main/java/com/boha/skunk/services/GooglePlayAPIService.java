package com.boha.skunk.services;


import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class GooglePlayAPIService {
    //5065353737403989591/
    //GET https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{packageName}/inappproducts/{sku}Base

    static final String mm = "\uD83E\uDD66\uD83E\uDD66\uD83E\uDD66 GooglePlayAPIService  \uD83D\uDC9B";
    static final Logger logger = Logger.getLogger(GooglePlayAPIService.class.getSimpleName());

    HttpTransport transport = new ApacheHttpTransport();
    JsonFactory jsonFactory = new GsonFactory();
    HttpRequestInitializer requestInitializer = new HttpRequestInitializer() {
        @Override
        public void initialize(HttpRequest httpRequest) throws IOException {

        }
    };
    AndroidPublisher androidPublisher = new AndroidPublisher(transport, jsonFactory, requestInitializer);

    public AndroidPublisher.Monetization.Subscriptions.Create createSubscription() throws IOException {

        logger.info(mm+"... createSubscription ....");
        var pkgName = "com.boha.sgela_sponsor_app";
        var prodId = "productId1";

        List<String> benefits = new ArrayList<>();
        benefits.add("Students on AI");
        benefits.add("Teachers on AI");
        benefits.add("Tutor/Assistant");

        SubscriptionListing subscriptionListing = new SubscriptionListing();
        subscriptionListing.setBenefits(benefits);
        subscriptionListing.setLanguageCode("en-US");
        subscriptionListing.setDescription("SgelaAI Subscription enables you to support students and teachers");
        subscriptionListing.setTitle("SgelaAI Subscription");
//        subscriptionListing.set("myField","myValue");

        List<SubscriptionListing> subscriptionListings = new ArrayList<>();
        subscriptionListings.add(subscriptionListing);

        List<OfferTag> offerTags = new ArrayList<>();
        OfferTag offerTag = new OfferTag();
        offerTag.setTag("AI");
        offerTags.add(offerTag);
        OfferTag offerTag2 = new OfferTag();
        offerTag2.setTag("Tutor");
        offerTags.add(offerTag2);
        OfferTag offerTag3 = new OfferTag();
        offerTag3.setTag("Assistant");
        offerTags.add(offerTag3);

        List<RegionalBasePlanConfig> regionalBasePlanConfigs = new ArrayList<>();
        RegionalBasePlanConfig basePlanConfig1 = new RegionalBasePlanConfig();
        basePlanConfig1.setNewSubscriberAvailability(true);
        Money money = new Money();
        money.setCurrencyCode("ZAR");
        money.setUnits(15000L);
        basePlanConfig1.setPrice(money);
        //
        RegionalBasePlanConfig basePlanConfig2 = new RegionalBasePlanConfig();
        basePlanConfig2.setNewSubscriberAvailability(true);
        Money money2 = new Money();
        money2.setCurrencyCode("USD");
        money2.setUnits(1000L);
        basePlanConfig1.setPrice(money2);

        regionalBasePlanConfigs.add(basePlanConfig1);
        regionalBasePlanConfigs.add(basePlanConfig2);

        AutoRenewingBasePlanType typeMonthly = new AutoRenewingBasePlanType();
        typeMonthly.setAccountHoldDuration("30 days");
        typeMonthly.setBillingPeriodDuration("1 Month");
        typeMonthly.setGracePeriodDuration("7 days");
        typeMonthly.setProrationMode("");
        typeMonthly.setResubscribeState("");

        BasePlan basePlanMonthly = new BasePlan();
        basePlanMonthly.setBasePlanId("basePlanMonthlyId1");
        basePlanMonthly.setOfferTags(offerTags);
        basePlanMonthly.setRegionalConfigs(regionalBasePlanConfigs);
        basePlanMonthly.setAutoRenewingBasePlanType(typeMonthly);
        //

        AutoRenewingBasePlanType typeYearly = new AutoRenewingBasePlanType();
        typeYearly.setAccountHoldDuration("30 days");
        typeYearly.setBillingPeriodDuration("1 Year");
        typeYearly.setGracePeriodDuration("7 days");
        typeYearly.setProrationMode("");
        typeYearly.setResubscribeState("");

        BasePlan basePlanYearly = new BasePlan();
        basePlanYearly.setBasePlanId("basePlanYearlyId1");
        basePlanYearly.setOfferTags(offerTags);
        basePlanYearly.setRegionalConfigs(regionalBasePlanConfigs);
        basePlanYearly.setAutoRenewingBasePlanType(typeYearly);

        List<BasePlan> basePlans = new ArrayList<>();
        basePlans.add(basePlanMonthly);
        basePlans.add(basePlanYearly);

        Map<String, RegionalTaxRateInfo> taxRateInfoByRegionCode = new HashMap<>();
        RegionalTaxRateInfo regionalTaxRateInfo = new RegionalTaxRateInfo();
        regionalTaxRateInfo.setTaxTier("TAX_TIER_UNSPECIFIED");

        taxRateInfoByRegionCode.put("ZA",regionalTaxRateInfo);
        taxRateInfoByRegionCode.put("BW",regionalTaxRateInfo);
        taxRateInfoByRegionCode.put("CN",regionalTaxRateInfo);
        taxRateInfoByRegionCode.put("US",regionalTaxRateInfo);
        taxRateInfoByRegionCode.put("EU",regionalTaxRateInfo);



        SubscriptionTaxAndComplianceSettings settings = new SubscriptionTaxAndComplianceSettings();
        settings.setEeaWithdrawalRightType("WITHDRAWAL_RIGHT_DIGITAL_CONTENT");
        settings.setIsTokenizedDigitalAsset(true);
        settings.setTaxRateInfoByRegionCode(taxRateInfoByRegionCode);

        Subscription subscription = new Subscription();
        subscription.setProductId(prodId);
        subscription.setPackageName(pkgName);
        subscription.setBasePlans(basePlans);
        subscription.setListings(subscriptionListings);
        subscription.setTaxAndComplianceSettings(settings);

        logger.info(mm+"... createSubscription .... call androidPublisher.monetization().subscriptions().create");
        var res = androidPublisher.monetization().subscriptions().create(pkgName, subscription);
        logger.info(mm+"create sub result: " + res.getAccessToken()
                + " status" + res.getLastStatusCode() + " " + res.getLastStatusMessage());
        //activate
        var plan1 = activateBasePlan(basePlanMonthly.getBasePlanId(), pkgName, prodId);
        var plan2 = activateBasePlan(basePlanYearly.getBasePlanId(), pkgName, prodId);

        logger.info(mm+" plan1 activated: " + plan1.getLastStatusCode() + " " + plan1.getLastStatusMessage());
        logger.info(mm+" plan2 activated: " + plan2.getLastStatusCode() + " " + plan2.getLastStatusMessage());

        return res;
    }

    public AndroidPublisher.Monetization.Subscriptions.BasePlans.Activate activateBasePlan(String basePlanId, String packageName,
                                                                                         String productId) throws IOException {

        ActivateBasePlanRequest planRequest = new ActivateBasePlanRequest();
        planRequest.setBasePlanId(basePlanId);
        planRequest.setPackageName(packageName);
        planRequest.setProductId(productId);

        AndroidPublisher.Monetization.Subscriptions.BasePlans.Activate abp =
                androidPublisher.monetization().subscriptions().basePlans().activate(packageName,
                productId, basePlanId, planRequest);
        logger.info(mm+" activate BasePlan: " + abp.getAccessToken() + " statusCode: "
                + abp.getLastStatusCode() + " " + abp.getLastStatusMessage());
        return abp;
    }


}
