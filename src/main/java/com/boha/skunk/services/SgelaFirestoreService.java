package com.boha.skunk.services;

import com.boha.skunk.data.*;
import com.boha.skunk.util.Util;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Service
@SuppressWarnings("all")
public class SgelaFirestoreService {
    static final String mm = "\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35" +
            " SgelaFirestoreService \uD83C\uDF4E";
    static final Logger logger = Logger.getLogger(SgelaFirestoreService.class.getSimpleName());
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    private final Firestore firestore;
    private final FirebaseService firebaseService;

    public SgelaFirestoreService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
        this.firestore = FirestoreClient.getFirestore();
    }

    public List<String> addUsers(List<User> users) throws ExecutionException, InterruptedException {
        List<String> results = new ArrayList<>();
        for (User user : users) {
            user.setId(user.getId());
            String result = addDocument(user);
            results.add(result);
        }
        return results;
    }

    public List<String> addExamDocuments(List<ExamDocument> users) throws ExecutionException, InterruptedException {
        List<String> results = new ArrayList<>();
        for (ExamDocument examDocument : users) {
            examDocument.setId(examDocument.getId());
            String result = addDocument(examDocument);
            results.add(result);
        }
        return results;
    }

    public List<String> addGeminiResponseRatings(List<GeminiResponseRating> responseRatings)
            throws ExecutionException, InterruptedException {
        List<String> results = new ArrayList<>();
        for (GeminiResponseRating subscription : responseRatings) {
            subscription.setId(subscription.getId());
            String result = addDocument(subscription);
            results.add(result);
        }
        return results;
    }

    public List<String> addSubscriptions(List<SgelaSubscription> sgelaSubscriptions)
            throws ExecutionException, InterruptedException {
        List<String> results = new ArrayList<>();
        for (SgelaSubscription sgelaSubscription : sgelaSubscriptions) {
            sgelaSubscription.setId(sgelaSubscription.getId());
            String result = addDocument(sgelaSubscription);
            results.add(result);
        }
        return results;
    }

    public List<String> addOrganizations(List<Organization> organizations)
            throws ExecutionException, InterruptedException {
        List<String> results = new ArrayList<>();
        for (Organization organization : organizations) {
            organization.setId(organization.getId());
            String result = addDocument(organization);
            results.add(result);
        }
        return results;
    }

    public String addSubject(Subject subject) throws ExecutionException, InterruptedException {
        String result = addDocument(subject);
        logger.info(mm + " .... Subject added to Firestore: " + result);
        return result;
    }

    public String addSummarizedExam(SummarizedExam summarizedExam) throws ExecutionException, InterruptedException {
        String result = addDocument(summarizedExam);
        logger.info(mm + ".... SummarizedExam added to Firestore: " + result);
        return result;
    }

    public String addExamDocument(ExamDocument doc) throws ExecutionException, InterruptedException {
        String result = addDocument(doc);
        logger.info(mm + ".... ExamDocument added to Firestore: " + result);
        return result;
    }


    public List<String> addMathExamPageContents(List<ExamPageContent> examPageContents)
            throws ExecutionException, InterruptedException {
        List<String> results = new ArrayList<>();
        CollectionReference collectionReference = firestore.collection("MathExamPageContent");

        for (ExamPageContent examLink : examPageContents) {
            ApiFuture<DocumentReference> result = collectionReference.add(examLink);
            DocumentReference documentReference = result.get();
            results.add(documentReference.getPath());
            logger.info("MathExamPage added to Firestore: " + documentReference.getPath());
        }
        return results;
    }

    public String addDocument(Object data) throws ExecutionException, InterruptedException {
        String name = data.getClass().getSimpleName();
        CollectionReference collectionReference = firestore.collection(name);
        ApiFuture<DocumentReference> result = collectionReference.add(data);
        DocumentReference documentReference = result.get();
        return documentReference.getId();
    }

    public Long generateUniqueLong() {
        UUID uuid = UUID.randomUUID();
        long mostSignificantBits = uuid.getMostSignificantBits();
        return Math.abs(mostSignificantBits);
    }

    public void updateDocument(String collectionName, String documentId, Map<String, Object> data)
            throws ExecutionException, InterruptedException {
        DocumentReference documentReference = firestore.collection(collectionName).document(documentId);
        documentReference.update(data);
    }

    public void updateDocument(String collectionName,
                               Long id, Map<String, Object> data) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(collectionName).whereEqualTo("id", id);
        ApiFuture<QuerySnapshot> m = query.get();
        QuerySnapshot snap = m.get();
        for (QueryDocumentSnapshot document : snap.getDocuments()) {
            document.getReference().update(data);
        }

    }

    public void deleteDocument(String collectionName, String documentId)
            throws ExecutionException, InterruptedException {
        DocumentReference documentReference = firestore.collection(collectionName).document(documentId);
        ApiFuture<WriteResult> result = documentReference.delete();
        result.get();
    }

    public <T> T getDocument(String collectionName, String documentId, Class<T> valueType)
            throws ExecutionException, InterruptedException {
        DocumentReference documentReference = firestore.collection(collectionName).document(documentId);
        ApiFuture<DocumentSnapshot> result = documentReference.get();
        DocumentSnapshot document = result.get();
        if (document.exists()) {
            return document.toObject(valueType);
        }
        return null;
    }

    public <T> T getDocument(String collectionName, Long id, Class<T> valueType) throws Exception {
        logger.info(mm + "getDocument: " + collectionName + " id:: " + id);
        Query query = firestore.collection(collectionName).whereEqualTo("id", id);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();

        List<T> list = new ArrayList<>();
        for (QueryDocumentSnapshot document : snapshot) {
            var obj = document.toObject(valueType);
            list.add(obj);
        }

        if (list.isEmpty()) {
            logger.info(mm + "getDocument: nothing found. Sorry!");
            return null;
        }
        return list.get(0);
    }

    public void updateExamLink(ExamLink examLink) throws Exception {
        CollectionReference collectionRef = firestore.collection(examLink.getClass().getSimpleName());
        Query query = collectionRef.whereEqualTo("id", examLink.getId());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();

        for (QueryDocumentSnapshot document : snapshot) {
            DocumentReference documentRef = document.getReference();
            ApiFuture<WriteResult> updateFuture = documentRef.set(examLink, SetOptions.merge());
            updateFuture.get(); // Wait for the update to complete
            logger.info(mm + "updateExamLink: ExamLink " + " -  updated : "
                    + G.toJson(examLink));
        }
    }

    public void deleteDocumentsByProperty(String collectionName,
                                          String propertyName,
                                          Object propertyValue) throws Exception {
        CollectionReference collectionRef = firestore.collection(collectionName);
        Query query = collectionRef.whereEqualTo(propertyName, propertyValue);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();
        logger.info(mm + collectionName + ": documents to be deleted: " + snapshot.getDocuments().size());

        for (QueryDocumentSnapshot document : snapshot) {
            DocumentReference documentRef = collectionRef.document(document.getId());
            ApiFuture<WriteResult> apiFuture = documentRef.delete();
            apiFuture.get(); // Wait for the update to complete
        }
    }

    public int updateSubscription(
            Long organizationId,
            boolean isActive) throws Exception {
        CollectionReference collectionRef = firestore.collection(SgelaSubscription.class.getSimpleName());
        Query query = collectionRef.whereEqualTo("organizationId", organizationId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();

        Map<String, Object> map = new HashMap<>();
        map.put("activeFlag", isActive);
        for (QueryDocumentSnapshot document : snapshot) {
            DocumentReference documentRef = collectionRef.document(document.getId());
            ApiFuture<WriteResult> updateFuture = documentRef.update(map);
            updateFuture.get(); // Wait for the update to complete
        }
        return 0;
    }

    public int addUserToSubscription(Long userId, Long subscriptionId) throws Exception {
        CollectionReference collectionRef = firestore.collection(User.class.getSimpleName());
        Query query = collectionRef.whereEqualTo("userId", userId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();

        for (QueryDocumentSnapshot document : snapshot) {
            Map<String, Object> map = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            // Format the date to UTC string
            String utcDate = sdf.format(new Date());
            map.put("subscriptionId", subscriptionId);
            map.put("subscriptionDate", utcDate);
            DocumentReference documentRef = collectionRef.document(document.getId());
            ApiFuture<WriteResult> updateFuture = documentRef.update(map);
            updateFuture.get();
            int ok = increaseSubscriptionUsers(subscriptionId); // Wait for the update to complete
            if (ok == 0) {
                logger.info(mm + " user enrolled in subscription");
            } else {
                return 9;
            }
        }
        return 0;
    }

    public String addSponsorPaymentType(SponsorPaymentType type) throws Exception {
        CollectionReference collectionRef = firestore.collection(SponsorPaymentType.class.getSimpleName());
        var docRef = collectionRef.add(type);
        return docRef.get().getPath();
    }

    public String addOrganizationSponsorPaymentType(OrganizationSponsorPaymentType type) throws Exception {
        CollectionReference collectionRef = firestore.collection(OrganizationSponsorPaymentType.class.getSimpleName());
        var docRef = collectionRef.add(type);
        return docRef.get().getPath();
    }

    private int increaseSubscriptionUsers(Long subscriptionId) throws Exception {
        CollectionReference collectionRef = firestore.collection(SgelaSubscription.class.getSimpleName());
        Query query = collectionRef.whereEqualTo("subscriptionId", subscriptionId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();

        for (QueryDocumentSnapshot document : snapshot) {
            Object numberOfUsersObj = document.get("numberOfUsers");
            if (numberOfUsersObj != null) {
                int numberOfUsers = ((Integer) numberOfUsersObj) + 1;
                Map<String, Object> map = new HashMap<>();
                map.put("numberOfUsers", numberOfUsers);
                DocumentReference documentRef = collectionRef.document(document.getId());
                ApiFuture<WriteResult> updateFuture = documentRef.update(map);
                updateFuture.get(); // Wait for the update to complete
            }
        }
        return 0;
    }

    public List<String> addAnswerLinks(List<AnswerLink> answerLinks) throws Exception {
        logger.info(mm + " Firestore batchWrite addAnswerLinks " + answerLinks.size());

        CollectionReference collectionRef = firestore.collection(AnswerLink.class.getSimpleName());
        var list = batchWriteAnswerLinks(answerLinks);
        logger.info(mm + " Firestore batchWrite complete!. answerLinks rows added: " + list.size());
        return list;
    }

    public List<String> addExamLinks(List<ExamLink> examLinks) throws Exception {
        logger.info(mm + " Firestore batchWrite addExamLinks " + examLinks.size());
        CollectionReference collectionRef = firestore.collection(AnswerLink.class.getSimpleName());
        var list = batchWriteExamLinks(examLinks);
        logger.info(mm + " Firestore batchWrite complete!. examLinks rows added: " + list.size());
        return list;
    }

    public List<String> batchWriteExamLinks(List<ExamLink> examLinks) throws ExecutionException, InterruptedException {
        String className = examLinks.get(0).getClass().getSimpleName();
        List<String> list = new ArrayList<>();
        WriteBatch batch = firestore.batch();
        CollectionReference collection = firestore.collection(className);

        for (ExamLink item : examLinks) {
            DocumentReference document = collection.document();
            batch.set(document, item);
            list.add(document.getId());
        }

        batch.commit().get();

        return list;
    }

    public List<String> addExamPageContents(List<ExamPageContent> examPageContents) throws Exception {
        logger.info(mm + " Firestore batchWrite addExamLinks " + examPageContents.size());
        CollectionReference collectionRef = firestore.collection(AnswerLink.class.getSimpleName());
        var list = batchWriteExamPageContent(examPageContents);
        logger.info(mm + " Firestore batchWrite complete!. examPageContents rows added: " + list.size());
        return list;
    }

    public List<String> batchWriteExamPageContent(List<ExamPageContent> examPageContents) throws ExecutionException, InterruptedException {
        String className = examPageContents.get(0).getClass().getSimpleName();
        List<String> list = new ArrayList<>();
        WriteBatch batch = firestore.batch();
        CollectionReference collection = firestore.collection(className);

        for (ExamPageContent item : examPageContents) {
            DocumentReference document = collection.document();
            batch.set(document, item);
            list.add(document.getId());
        }

        batch.commit().get();

        return list;
    }

    public List<String> batchWriteAnswerLinks(List<AnswerLink> answerLinks) throws ExecutionException, InterruptedException {
        String className = answerLinks.get(0).getClass().getSimpleName();
        List<String> list = new ArrayList<>();
        WriteBatch batch = firestore.batch();
        CollectionReference collection = firestore.collection(className);

        for (AnswerLink item : answerLinks) {
            DocumentReference document = collection.document();
            batch.set(document, item);
            list.add(document.getId());
        }

        batch.commit().get();
        return list;
    }

    public <T> List<T> getAllDocuments(Class<T> valueType) throws ExecutionException, InterruptedException {

        CollectionReference collectionReference = firestore.collection(valueType.getSimpleName());
        ApiFuture<QuerySnapshot> result = collectionReference.get();
        QuerySnapshot querySnapshot = result.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        List<T> objects = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : documents) {
            objects.add(snapshot.toObject(valueType));
        }
        logger.info(mm + " getAllDocuments: found " + objects.size()
                + "  \uD83D\uDE0E valueType: "
                + valueType.getSimpleName());

        return objects;
    }

    public List<Subject> getSubjects() throws ExecutionException, InterruptedException {
        return getAllDocuments(Subject.class);
    }

    public Subject getSubjectById(Long subjectId) throws Exception {
        var list = getDocumentsByLongProperty(Subject.class.getSimpleName(),
                "id", subjectId, null);
        if (!list.isEmpty()) {
            return list.get(0).toObject(Subject.class);
        }
        throw new Exception("Subject not found");
    }

    public Subject getSubjectByTitle(String title) throws Exception {
        var list = getDocumentsByStringProperty(Subject.class.getSimpleName(),
                "title", title, null);
        if (!list.isEmpty()) {
            return list.get(0).toObject(Subject.class);
        }
        return null;
    }

    public ExamDocument getExamDocumentByTitle(String title) throws Exception {
        var list = getDocumentsByStringProperty(ExamDocument.class.getSimpleName(),
                "title", title, null);
        if (!list.isEmpty()) {
            return list.get(0).toObject(ExamDocument.class);
        }
        return null;
    }

    public Country getCountryByName(String name) throws Exception {

        var list = getDocumentsByStringProperty(Country.class.getSimpleName(),
                "name", name, null);
        if (!list.isEmpty()) {
            return list.get(0).toObject(Country.class);
        }
        throw new Exception("Country not found");
    }

    public Organization getOrganizationByName(String name) throws Exception {

        var list = getDocumentsByStringProperty(Organization.class.getSimpleName(),
                "name", name, null);
        if (!list.isEmpty()) {
            return list.get(0).toObject(Organization.class);
        }
        throw new Exception("Organization not found");
    }

    public Organization getSgelaOrganization() throws Exception {

        var list = getDocumentsByStringProperty(Organization.class.getSimpleName(),
                "name", "SgelaAI Inc.", null);
        if (!list.isEmpty()) {
            return list.get(0).toObject(Organization.class);
        }
        throw new Exception("Organization not found");
    }

    public User getUserByFirebaseId(String firebaseUserId) throws Exception {
        var list = getDocumentsByStringProperty("SgelaUser",
                "firebaseUserId", firebaseUserId, null);
        if (!list.isEmpty()) {
            return list.get(0).toObject(User.class);
        }
        throw new Exception("SgelaUser not found in Firestore: " + firebaseUserId);
    }

    public User updateUser(User user) throws Exception {
        logger.info(mm + "updateUser: " + G.toJson(user));
        Filter filter = Filter.equalTo("firebaseUserId", user.getFirebaseUserId());
        QuerySnapshot querySnapshot1 = firestore.collection(User.class.getSimpleName()).where(filter).get().get();
        User result = null;
        for (QueryDocumentSnapshot document : querySnapshot1.getDocuments()) {
            document.getReference().update(Util.objectToMap(user));
            logger.info(mm + "updateUser completed ");
            result = user;
        }

        if (result == null) {
            throw new Exception("User not found in Firestore");
        } else {
            return result;
        }
    }

    public City getCityByName(String name) throws Exception {
        var list = getDocumentsByStringProperty(City.class.getSimpleName(),
                "name", name, null);
        if (!list.isEmpty()) {
            return list.get(0).toObject(City.class);
        }
        throw new Exception("City not found");
    }

    public List<Subject> getSubjectsSorted() throws ExecutionException, InterruptedException {
        CollectionReference collectionReference = firestore.collection("Subject");
        Query query = collectionReference.orderBy("title");
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();
        var m = snapshot.getDocuments();
        List<Subject> subjects = new ArrayList<>();
        for (QueryDocumentSnapshot snap : m) {
            subjects.add(snap.toObject(Subject.class));
        }
        logger.info(mm + "getSubjectsSorted: documents found: " + subjects.size());
        return subjects;
    }

    public int fixSubjectExamLinks() throws Exception {

        var subjects = getSubjectsSorted();
        try {
            for (Subject subject : subjects) {
                List<QueryDocumentSnapshot> queryDocumentSnapshotList = getDocumentsByNestedLongProperty(
                        ExamLink.class.getSimpleName(),
                        "subject.id", subject.getId());
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshotList) {
                    ExamLink examLink = snapshot.toObject(ExamLink.class);
                    // Update the document in Firestore
                    firestore.collection(ExamLink.class.getSimpleName())
                            .document(snapshot.getId())
                            .set(examLink)
                            .get();
                    logger.info(mm + "Subject:  \uD83D\uDC9C \uD83D\uDC9C \uD83D\uDC9C"
                            + subject.getTitle() + " \uD83C\uDF4E fix completed!  \uD83D\uDC9C ");
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    public List<ExamLink> getSubjectExamLinks(Long subjectId) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> queryDocumentSnapshotList = getDocumentsByNestedLongProperty(
                ExamLink.class.getSimpleName(),
                "subject.id", subjectId);
        List<ExamLink> examLinks = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshotList) {
            ExamLink examLink = snapshot.toObject(ExamLink.class);
            examLinks.add(examLink);
        }

        return examLinks;
    }

    public List<ExamPageContent> getExamLinkPageContents(Long examLinkId)
            throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> queryDocumentSnapshotList = getDocumentsByNestedLongProperty(
                ExamPageContent.class.getSimpleName(),
                "examLinkId", examLinkId);
        List<ExamPageContent> examLinks = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshotList) {
            ExamPageContent examLink = snapshot.toObject(ExamPageContent.class);
            examLinks.add(examLink);
        }

        return examLinks;
    }

    public List<QueryDocumentSnapshot> getDocumentsByNestedLongProperty(String collectionName,
                                                                        String nestedPropertyName, Long propertyValue) throws ExecutionException, InterruptedException {
        CollectionReference collectionReference = firestore.collection(collectionName);
        Query query = collectionReference.whereEqualTo(nestedPropertyName, propertyValue);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();
        return snapshot.getDocuments();
    }

    public List<GeminiResponseRating> getResponseRatings(Long examLinkId)
            throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> queryDocumentSnapshotList = getDocumentsByLongProperty(
                GeminiResponseRating.class.getSimpleName(),
                "examLinkId", examLinkId, "date");
        List<GeminiResponseRating> geminiResponseRatings = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshotList) {
            GeminiResponseRating s = snapshot.toObject(GeminiResponseRating.class);
            geminiResponseRatings.add(s);
        }

        return geminiResponseRatings;
    }

    public List<SgelaSubscription> getSubscriptions(Long organizationId)
            throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> queryDocumentSnapshotList = getDocumentsByLongProperty(
                SgelaSubscription.class.getSimpleName(),
                "organizationId", organizationId, "date");
        List<SgelaSubscription> sgelaSubscriptions = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshotList) {
            SgelaSubscription s = snapshot.toObject(SgelaSubscription.class);
            sgelaSubscriptions.add(s);
        }

        return sgelaSubscriptions;
    }

    public List<User> getOrganizationUsers(Long organizationId) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> queryDocumentSnapshotList = getDocumentsByLongProperty(
                SgelaSubscription.class.getSimpleName(),
                "organizationId", organizationId, "date");
        List<User> users = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshotList) {
            User s = snapshot.toObject(User.class);
            users.add(s);
        }

        return users;
    }

    public List<Organization> getOrganizations() throws ExecutionException, InterruptedException {
        CollectionReference collectionReference = firestore.collection(Organization.class.getSimpleName());
        var queryDocumentSnapshotList = collectionReference.get().get();

        List<Organization> organizations = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshotList) {
            Organization s = snapshot.toObject(Organization.class);
            organizations.add(s);
        }

        return organizations;
    }

    public SgelaSubscription getSubscription(Long subscriptionId) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> queryDocumentSnapshotList = getDocumentsByLongProperty(
                SgelaSubscription.class.getSimpleName(),
                "id", subscriptionId, null);
        List<SgelaSubscription> sgelaSubscriptions = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshotList) {
            SgelaSubscription s = snapshot.toObject(SgelaSubscription.class);
            sgelaSubscriptions.add(s);
        }

        if (sgelaSubscriptions.isEmpty()) {
            return null;
        }
        return sgelaSubscriptions.get(0);
    }

    public Organization getOrganization(Long organizationId) throws ExecutionException, InterruptedException {

        CollectionReference collectionReference = firestore.collection("Organization");
        Query query = collectionReference.whereEqualTo("id", organizationId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();
        var m = snapshot.getDocuments();
        Organization organization = null;
        for (QueryDocumentSnapshot snap : m) {
            organization = snap.toObject(Organization.class);
        }
        logger.info(mm + "getOrganization: documents found: " + m.size());
        return organization;
    }

    public ExamLink getExamLink(Long examLinkId) throws ExecutionException, InterruptedException {

        CollectionReference collectionReference = firestore.collection("ExamLink");
        Query query = collectionReference.whereEqualTo("id", examLinkId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();
        var m = snapshot.getDocuments();
        ExamLink examLink = null;
        for (QueryDocumentSnapshot snap : m) {
            examLink = snap.toObject(ExamLink.class);
        }
        logger.info(mm + "getExamLink: documents found: " + m.size());
        logger.info(mm + "examLink found: \uD83C\uDF4E\uD83C\uDF4E " + G.toJson(examLink));
        return examLink;
    }

    public List<Pricing> getPricings(Long countryId) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> queryDocumentSnapshotList = getDocumentsByLongProperty(
                SgelaSubscription.class.getSimpleName(),
                "countryId", countryId, "date");
        List<Pricing> pricings = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshotList) {
            Pricing s = snapshot.toObject(Pricing.class);
            pricings.add(s);
        }

        return pricings;
    }

    public void updateDocumentProperty(
            String collectionName,
            String documentId,
            String propertyName,
            Object propertyValue) throws Exception {
        DocumentReference documentRef = firestore.collection(collectionName).document(documentId);
        ApiFuture<WriteResult> updateFuture = documentRef.update(propertyName, propertyValue);
        updateFuture.get(); // Wait for the update to complete
    }

    public List<QueryDocumentSnapshot> getDocumentsByLongProperty(String collectionName,
                                                                  String propertyName,
                                                                  Long propertyValue,
                                                                  String orderBy) throws ExecutionException, InterruptedException {
//        logger.info(mm + "getDocumentsByLongProperty: propertyName: "
//                + propertyName + " propertyValue: " + propertyValue
//                + " collectionName: " + collectionName);
        CollectionReference collectionReference = firestore.collection(collectionName);
        Query query = collectionReference.whereEqualTo(propertyName, propertyValue);
        if (orderBy != null) {
            query.orderBy(orderBy);
        }
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();
        var m = snapshot.getDocuments();
        logger.info(mm + "getDocumentsByLongProperty documents found: " + m.size());
        return m;
    }

    private List<QueryDocumentSnapshot> getDocumentsByStringProperty(String collectionName,
                                                                     String propertyName,
                                                                     String propertyValue,
                                                                     String orderBy) throws ExecutionException, InterruptedException {
//        logger.info(mm + "getDocumentsByStringProperty: propertyName: "
//                + propertyName + " propertyValue: " + propertyValue
//                + " collectionName: " + collectionName);
        CollectionReference collectionReference = firestore.collection(collectionName);
        Query query = collectionReference.whereEqualTo(propertyName, propertyValue);
        if (orderBy != null) {
            query.orderBy(orderBy);
        }
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();
        return snapshot.getDocuments();
    }

}
