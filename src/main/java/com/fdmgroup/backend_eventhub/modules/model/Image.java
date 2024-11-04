package com.fdmgroup.backend_eventhub.modules.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fdmgroup.backend_eventhub.eventsession.model.Content;
import com.fdmgroup.backend_eventhub.eventsession.model.Event;
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
public class Image extends Module{
    private String name;

    private String filePath;

}