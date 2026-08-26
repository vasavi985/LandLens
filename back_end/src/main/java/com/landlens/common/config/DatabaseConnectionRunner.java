package com.landlens.common.config;

import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Validates and tests the Firebase Firestore connection on Spring Boot startup.
 */
@Component
public class DatabaseConnectionRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConnectionRunner.class);
    private static final String SEPARATOR = "========================================================================";

    @Autowired
    private Firestore firestore;

    @Override
    public void run(String... args) {
        log.info("Testing Firebase Firestore connection...");
        try {
            // Check connectivity by trying to read the roles collection (lightweight check)
            firestore.collection("roles").limit(1).get().get();
            log.info(SEPARATOR);
            log.info("FIREBASE FIRESTORE CONNECTION SUCCESSFUL!");
            log.info("Firestore client initialized successfully.");
            log.info(SEPARATOR);
        } catch (Exception e) {
            log.error(SEPARATOR);
            log.error("FIREBASE FIRESTORE CONNECTION FAILED! Please check credentials, Project ID, and network permissions.", e);
            log.error(SEPARATOR);
        }
    }
}
