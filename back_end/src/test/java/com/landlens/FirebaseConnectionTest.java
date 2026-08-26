package com.landlens;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FirebaseConnectionTest {

    @Autowired
    private Firestore firestore;

    @BeforeAll
    public static void setup() throws Exception {
        // Automatically inject local credentials file for JUnit running environment
        String credentialsPath = "firebase-service-account.json";
        if (Files.exists(Paths.get(credentialsPath))) {
            String json = Files.readString(Paths.get(credentialsPath));
            System.setProperty("FIREBASE_CREDENTIALS_JSON", json);
            System.setProperty("FIREBASE_PROJECT_ID", "landlens-c0007");
        }
        if (System.getProperty("FIREBASE_API_KEY") == null && System.getenv("FIREBASE_API_KEY") == null) {
            System.setProperty("FIREBASE_API_KEY", "PLACEHOLDER_KEY");
        }
        if (System.getProperty("JWT_SECRET") == null && System.getenv("JWT_SECRET") == null) {
            System.setProperty("JWT_SECRET", "9a2f3f4e5d6c7b8a9f0e1d2c3b4a5f6e7d8c9b0a1f2e3d4c5b6a7f8e9d0c1b2a3");
        }
    }

    @Test
    public void testFirestoreConnection() throws Exception {
        assertNotNull(firestore, "Firestore client should be initialized");

        // Phase 3 Tests: Write, Read, Update, Delete
        String testId = "test-" + UUID.randomUUID().toString();
        DocumentReference docRef = firestore.collection("test_connection").document(testId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "LandLens Firebase Live Connection Test");
        data.put("status", "VERIFIED");
        data.put("timestamp", System.currentTimeMillis());

        // Write
        docRef.set(data).get();

        // Read
        DocumentSnapshot docSnapshot = docRef.get().get();
        assertTrue(docSnapshot.exists(), "Test document should exist in Firestore");
        assertEquals("VERIFIED", docSnapshot.getString("status"));

        // Update
        docRef.update("status", "UPDATED").get();
        docSnapshot = docRef.get().get();
        assertEquals("UPDATED", docSnapshot.getString("status"));

        // Delete
        docRef.delete().get();
        docSnapshot = docRef.get().get();
        assertFalse(docSnapshot.exists(), "Test document should be deleted from Firestore");
    }

    @Autowired
    private com.landlens.auth.repository.RoleRepository roleRepository;

    @Autowired
    private com.landlens.auth.service.AuthService authService;

    @Autowired
    private com.landlens.user.repository.UserRepository userRepository;

    @Test
    public void testSeededRolesAndUserRegistration() throws Exception {
        // 1. Verify that roles are seeded on application startup
        assertTrue(roleRepository.findByName("BUYER").isPresent(), "Role BUYER should be seeded");
        assertTrue(roleRepository.findByName("ADMIN").isPresent(), "Role ADMIN should be seeded");
        assertTrue(roleRepository.findByName("PROVIDER").isPresent(), "Role PROVIDER should be seeded");
        assertTrue(roleRepository.findByName("GOVERNMENT_OFFICER").isPresent(), "Role GOVERNMENT_OFFICER should be seeded");

        // 2. Perform registration
        String testEmail = "test-user-" + UUID.randomUUID().toString().substring(0, 8) + "@landlens.com";
        com.landlens.auth.dto.RegisterRequest request = new com.landlens.auth.dto.RegisterRequest();
        request.setEmail(testEmail);
        request.setPassword("SecurePassword123");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPhoneNumber("+15551234567");
        request.setRole("BUYER");

        com.landlens.user.model.User registeredUser = authService.register(request);
        assertNotNull(registeredUser);
        assertNotNull(registeredUser.getId());
        assertEquals(testEmail, registeredUser.getEmail());

        // 3. Confirm Firebase Authentication user is created
        com.google.firebase.auth.UserRecord firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance()
                .getUser(registeredUser.getId().toString());
        assertNotNull(firebaseUser);
        assertEquals(testEmail, firebaseUser.getEmail());

        // 4. Confirm corresponding Firestore user document exists
        assertTrue(userRepository.findById(registeredUser.getId()).isPresent(), "User should exist in Firestore");

        // 5. Clean up test user in Firebase Auth and Firestore
        com.google.firebase.auth.FirebaseAuth.getInstance().deleteUser(registeredUser.getId().toString());
        userRepository.delete(registeredUser);

        assertFalse(userRepository.findById(registeredUser.getId()).isPresent(), "User should be cleaned up from Firestore");
    }

    @Test
    @org.junit.jupiter.api.Disabled("GCP APIs are not used for Render/Firebase Hosting deployment")
    public void testEnableGcpApis() throws Exception {
        // Generate GCP OAuth2 token using the service account
        java.io.InputStream credentialsStream = new java.io.ByteArrayInputStream(
                System.getProperty("FIREBASE_CREDENTIALS_JSON").getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
        com.google.auth.oauth2.GoogleCredentials credentials = com.google.auth.oauth2.GoogleCredentials.fromStream(credentialsStream)
                .createScoped(java.util.Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
        credentials.refreshIfExpired();
        String token = credentials.getAccessToken().getTokenValue();

        String[] services = {"run.googleapis.com", "artifactregistry.googleapis.com", "cloudbuild.googleapis.com"};
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

        for (String service : services) {
            System.out.println("[GCP API ACTIVATOR] Enabling service: " + service);
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("https://serviceusage.googleapis.com/v1/projects/landlens-c0007/services/" + service + ":enable"))
                    .header("Authorization", "Bearer " + token)
                    .POST(java.net.http.HttpRequest.BodyPublishers.noBody())
                    .build();
            
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            System.out.println("[GCP API ACTIVATOR] Response code: " + response.statusCode());
            System.out.println("[GCP API ACTIVATOR] Response body: " + response.body());
        }
    }
}
