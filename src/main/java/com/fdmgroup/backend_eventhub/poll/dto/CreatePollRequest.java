package com.fdmgroup.backend_eventhub.poll.dto;

public class CreatePollRequest {
    private String question;
    private String eventCode;
    private PollOptionRequest[] pollOptionRequests;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public PollOptionRequest[] getPollOptionRequests() {
        return pollOptionRequests;
    }

    public void setPollOptionRequests(PollOptionRequest[] pollOptionRequests) {
        this.pollOptionRequests = pollOptionRequests;
    }
}
