package com.fdmgroup.backend_eventhub.eventsession.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fdmgroup.backend_eventhub.authenticate.model.Account;
import com.fdmgroup.backend_eventhub.poll.model.Poll;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private boolean reminderEmailSent = false;
    private String eventName;
    private String code;

    // Add password to authenticate users joining a watchparty
    private String password;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate scheduledDate;

    @JsonFormat(pattern="HH:mm:ss")
    private LocalTime scheduledTime;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate createdDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

    @OneToOne(mappedBy = "event")
    @JsonIgnore
    private Poll poll;

    @OneToMany(mappedBy = "event")
    @JsonIgnore
    private List<Content> contents = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.code = generateCode();
        this.createdDate = LocalDate.now();
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

}