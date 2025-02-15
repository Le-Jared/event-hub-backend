package com.fdmgroup.backend_eventhub.poll.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fdmgroup.backend_eventhub.authenticate.model.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "poll_option_id")
    @JsonIgnore
    private PollOption pollOption;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "poll_id")
    @JsonIgnore
    private Poll poll;

    private String userDisplayName;
}
