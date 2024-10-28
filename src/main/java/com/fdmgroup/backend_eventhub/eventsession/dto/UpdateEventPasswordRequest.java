package com.fdmgroup.backend_eventhub.eventsession.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateEventPasswordRequest {
    private String password;
    private Long accountId;
    private Long eventId;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
