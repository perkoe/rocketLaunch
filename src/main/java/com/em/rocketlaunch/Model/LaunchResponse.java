package com.em.rocketlaunch.Model;

import java.util.List;

public class LaunchResponse {
    private List<Launch> results;

    // Getters and setters

    public List<Launch> getResults() {
        return results;
    }

    public void setResults(List<Launch> results) {
        this.results = results;
    }
}
