package com.em.rocketlaunch.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Update {
    private String id;
    private String comment;
    private String infoUrl;
    private Launch launch;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public Launch getLaunch() {
        return launch;
    }

    public void setLaunch(Launch launch) {
        this.launch = launch;
    }

    @Override
    public String toString() {
        return "Update{" +
                "id='" + id + '\'' +
                ", comment='" + comment + '\'' +
                ", infoUrl='" + infoUrl + '\'' +
                ", launch=" + launch +
                '}';
    }
}
