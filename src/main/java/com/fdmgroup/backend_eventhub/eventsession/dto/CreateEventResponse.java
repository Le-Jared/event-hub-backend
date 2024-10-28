package com.fdmgroup.backend_eventhub.eventsession.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEventResponse {
    private String token;
    private String videoSource;
    private String code;
    private boolean isHost;

    public CreateEventResponse(String token, String videoSource, String code, boolean isHost) {
        this.token = token;
        this.videoSource = videoSource;
        this.code = code;
        this.isHost = isHost;
    }

    public CreateEventResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVideoSource() {
        return videoSource;
    }

    public void setVideoSource(String videoSource) {
        this.videoSource = videoSource;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }
}
