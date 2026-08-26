package com.landlens.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials.json:#{null}}")
    private String firebaseCredentialsJson;

    @Value("${firebase.project.id:#{null}}")
    private String firebaseProjectId;

    @Bean
    public Firestore firestore() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions.Builder builder = FirebaseOptions.builder();
            
            if (firebaseCredentialsJson != null && !firebaseCredentialsJson.trim().isEmpty()) {
                InputStream credentialsStream = new ByteArrayInputStream(
                        firebaseCredentialsJson.getBytes(StandardCharsets.UTF_8)
                );
                builder.setCredentials(GoogleCredentials.fromStream(credentialsStream));
            } else {
                // Fallback to Application Default Credentials (useful in GCP environments)
                builder.setCredentials(GoogleCredentials.getApplicationDefault());
            }

            if (firebaseProjectId != null && !firebaseProjectId.trim().isEmpty()) {
                builder.setProjectId(firebaseProjectId);
            }

            FirebaseApp.initializeApp(builder.build());
        }
        return FirestoreClient.getFirestore();
    }
}
