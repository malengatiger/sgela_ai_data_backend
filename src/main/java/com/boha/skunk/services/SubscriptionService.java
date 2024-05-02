package com.boha.skunk.services;


import com.boha.skunk.data.OrganizationSponsorPaymentType;
import com.boha.skunk.data.Pricing;
import com.boha.skunk.data.SponsorPaymentType;
import com.boha.skunk.data.SgelaSubscription;
import com.boha.skunk.util.Util;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class SubscriptionService {

    private final SgelaFirestoreService sgelaFirestoreService;

    public SubscriptionService(SgelaFirestoreService sgelaFirestoreService) {
        this.sgelaFirestoreService = sgelaFirestoreService;
    }

    public String addPricing(Pricing pricing) throws Exception {
        return sgelaFirestoreService.addDocument(pricing);
    }

    public List<Pricing> getPricings(Long countryId) throws Exception {
        return sgelaFirestoreService.getPricings(countryId);
    }

    public String addSubscription(SgelaSubscription sgelaSubscription) throws Exception {
        return sgelaFirestoreService.addDocument(sgelaSubscription);
    }
    public String addSponsorPaymentType(SponsorPaymentType type) throws Exception {
        type.setDate(new Date().toInstant().toString());
        type.setId(Util.generateUniqueLong());
        return sgelaFirestoreService.addDocument(type);
    }
    public String addOrganizationSponsorPaymentType(OrganizationSponsorPaymentType type) throws Exception {
        type.setDate(new Date().toInstant().toString());
        return sgelaFirestoreService.addDocument(type);
    }
    public List<SgelaSubscription> getSubscriptions(Long organizationId) throws Exception {
        return sgelaFirestoreService.getSubscriptions(organizationId);
    }

    public int updateSubscription(Long organizationId, boolean isActive) throws Exception {
        return sgelaFirestoreService.updateSubscription(organizationId, isActive);
    }

    public SgelaSubscription addUserToSubscription(Long userId, Long subscriptionId) throws Exception {
        int ok = sgelaFirestoreService.addUserToSubscription(userId, subscriptionId);
        SgelaSubscription sub = null;
        if (ok == 0) {
            sub = sgelaFirestoreService.getSubscription(subscriptionId);
        }
        return sub;
    }

    public boolean checkOrganizationSubscription(Long organizationId) throws Exception {
        List<SgelaSubscription> list = sgelaFirestoreService.getSubscriptions(organizationId);
        if (list.isEmpty()) {
            return false;
        }
        SgelaSubscription sub = list.get(list.size() - 1);
        return sub.isActiveFlag();
    }

    public int updateUserSubscription(Long userId, Long subscriptionId) {

        return 9;
    }

}
