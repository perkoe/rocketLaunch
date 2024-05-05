package com.em.rocketlaunch.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Bean
    public FirebaseApp initializeFirebaseApp() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://rocketlaunch-68dac-default-rtdb.europe-west1.firebasedatabase.app")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                logger.info("Initializing new Firebase app.");
                return FirebaseApp.initializeApp(options);
            } else {
                logger.info("Using existing Firebase app.");
                return FirebaseApp.getInstance();
            }

        } catch (IOException e) {
            logger.error("Failed to initialize Firebase: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }

    @Bean
    public FirebaseDatabase firebaseDatabase(FirebaseApp firebaseApp) {
        return FirebaseDatabase.getInstance(firebaseApp);
    }
}
