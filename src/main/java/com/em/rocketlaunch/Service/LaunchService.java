package com.em.rocketlaunch.Service;

import com.em.rocketlaunch.Model.Launch;
import com.em.rocketlaunch.Model.LaunchResponse;
import com.em.rocketlaunch.Model.Update;
import com.em.rocketlaunch.Model.UpdateResponse;
import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class LaunchService {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    @Autowired
    private NotificationService notificationService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "https://ll.thespacedevs.com/2.2.0/";

    @Scheduled(cron = "0 0 0 * * *") // Run once a day at midnight
    public void checkForUpcomingLaunches() {
        List<Launch> upcomingLaunches = getUpcomingLaunches();
        processLaunches(upcomingLaunches);
    }
    private Set<String> notifiedLaunches = new HashSet<>();
    private void processLaunches(List<Launch> launches) {
        for (Launch launch : launches) {
            DatabaseReference launchRef = firebaseDatabase
                    .getReference("launches")
                    .child(launch.getId());
            launchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Launch existingLaunch = dataSnapshot.getValue(Launch.class);
                    if (existingLaunch == null || !existingLaunch.equals(launch)) {
                        if (!notifiedLaunches.contains(launch.getId())) {
                        // New launch or launch details changed
                        notificationService.sendNotificationForLaunch(launch, existingLaunch == null ? "New launch" : "Launch details changed");
                        saveLaunch(launchRef, launch);
                            notifiedLaunches.add(launch.getId());
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("Database error: " + databaseError.getMessage());
                }
            });
        }
    }

    private void saveLaunch(DatabaseReference launchRef, Launch launch) {
        launchRef.setValue(launch, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                System.err.println("Failed to save launch: " + databaseError.getMessage());
            } else {
                System.out.println("Successfully saved launch: " + launch.getName());
            }
        });
    }


    @Scheduled(cron = "0 0 0 * * *") // Run once a day at midnight
    public void checkForLaunchUpdates() {
        List<Update> updates = getLaunchUpdates();
        processUpdates(updates);
    }
    private Set<String> notifiedUpdates = new HashSet<>();
    private void processUpdates(List<Update> updates) {
        for (Update update : updates) {
            if (update.getLaunch() != null && update.getLaunch().getId() != null) {
                DatabaseReference updateRef = firebaseDatabase
                        .getReference("updates")
                        .child(update.getLaunch().getId());

                updateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Update existingUpdate = dataSnapshot.getValue(Update.class);
                        if (existingUpdate == null) {
                            if (!notifiedUpdates.contains(update.getId())) {
                            // New launch update
                            notificationService.sendNotificationForUpdate(update, "New launch update");
                            saveUpdate(updateRef, update);
                                notifiedUpdates.add(update.getId());
                            }
                        } else if (!existingUpdate.equals(update)) {
                            if (!notifiedUpdates.contains(update.getId())) {
                            // Launch update changed
                            notificationService.sendNotificationForUpdate(update, "Launch update changed");
                            saveUpdate(updateRef, update);
                            notifiedUpdates.add(update.getId());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("Database error: " + databaseError.getMessage());
                    }
                });
            }
        }
    }

    private void saveUpdate(DatabaseReference updateRef, Update update) {
        updateRef.setValue(update, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                System.err.println("Failed to save update: " + databaseError.getMessage());
            } else {
                System.out.println("Successfully saved update for launch ID: " + update.getLaunch().getId());
            }
        });
    }

    private List<Launch> getUpcomingLaunches() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String currentTime = ZonedDateTime.now().format(formatter);
        String oneWeekLater = ZonedDateTime.now().plusWeeks(1).format(formatter);


        String url = UriComponentsBuilder
                .fromUriString(BASE_URL + "launch/upcoming/")
                .queryParam("window_start__gte", currentTime)
                .queryParam("window_start__lte", oneWeekLater)
                .queryParam("mode", "list")
                .toUriString();

        LaunchResponse response = restTemplate.getForObject(url, LaunchResponse.class);
        return response.getResults();
    }

    private List<Update> getLaunchUpdates() {
        String url = UriComponentsBuilder
                .fromUriString(BASE_URL + "updates/")
                .toUriString();

        UpdateResponse response = restTemplate.getForObject(url, UpdateResponse.class);
        return response.getResults();
    }
}
