package com.em.rocketlaunch.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateResponse {
    private List<Update> results;

    // Getters and setters
    public List<Update> getResults() {
        return results;
    }

    public void setResults(List<Update> results) {
        this.results = results;
    }
}