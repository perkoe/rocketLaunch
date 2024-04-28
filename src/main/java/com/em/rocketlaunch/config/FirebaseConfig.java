package com.em.rocketlaunch.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct  // This method will be invoked after the bean creation
    public void init() {
        initializeFirebaseApp();
        testFirebaseDatabase();
    }
    public void initializeFirebaseApp() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://rocketlaunch-68dac-default-rtdb.europe-west1.firebasedatabase.app")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) { //<-- Only initialize if not already initialized
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to initialize Firebase: " + e.getMessage());
        }
    }

    public void testFirebaseDatabase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("test");
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("items");

        CountDownLatch done = new CountDownLatch(3);  // Expecting three operations to complete

        // Generate a unique key and write a new item
        String uniqueKey = itemsRef.push().getKey();
        itemsRef.child(uniqueKey).setValue("New Item", (databaseError, databaseReference) -> {
            if (databaseError != null) {
                logger.error("Item could not be saved: {}", databaseError.getMessage());
            } else {
                logger.info("Item saved successfully at '/items/{}'", uniqueKey);
            }
            done.countDown();
        });

        // Write a timestamped message
        long timestamp = System.currentTimeMillis();
        ref.child(String.valueOf(timestamp)).setValue("Timestamped Message", (databaseError, databaseReference) -> {
            if (databaseError != null) {
                logger.error("Timestamped message could not be saved: {}", databaseError.getMessage());
            } else {
                logger.info("Timestamped message saved successfully at '/test/{}'", timestamp);
            }
            done.countDown();
        });

        // Write to '/test'
        ref.setValue("Hello, Firebase5!", (databaseError, databaseReference) -> {
            if (databaseError != null) {
                logger.error("Data could not be saved: {}", databaseError.getMessage());
            } else {
                logger.info("Data saved successfully at '/test'.");
            }
            done.countDown();
        });

        try {
            done.await();  // Wait until all operations complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Read data
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    logger.info("Received data: {}", dataSnapshot.getValue(String.class));
                } else {
                    logger.info("No data found at '/test'.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("The read failed: {}", databaseError.getCode());
            }
        });
    }
}
