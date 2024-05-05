package com.em.rocketlaunch.Controller;

import com.em.rocketlaunch.Service.LaunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private LaunchService launchService;

    @GetMapping("/check-launches")
    public String checkLaunches() {
        try {
            launchService.checkForUpcomingLaunches();
            return "Checked for upcoming launches.";
        } catch (Exception e) {
            return "Error checking for upcoming launches: " + e.getMessage();
        }
    }

    @GetMapping("/check-updates")
    public String checkUpdates() {
        try {
            launchService.checkForLaunchUpdates();
            return "Checked for launch updates.";
        } catch (Exception e) {
            return "Error checking for launch updates: " + e.getMessage();
        }
    }
}
