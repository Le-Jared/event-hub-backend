package com.fdmgroup.backend_eventhub.modules.model;

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
public class VideoModule extends Module {
    private String videoTitle;
    private LocalDate uploadedDate;
    private long durationSecond;
    private String thumbnailURL;
    private String videoURL;

}
