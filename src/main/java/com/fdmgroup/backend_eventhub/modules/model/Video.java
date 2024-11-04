package com.fdmgroup.backend_eventhub.modules.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fdmgroup.backend_eventhub.eventsession.model.Content;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String videoTitle;
    private LocalDate uploadedDate;
    private long durationSecond;
    private String thumbnailURL;
    private String videoURL;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "content_id")
    @JsonIgnore
    private Content content;
}
