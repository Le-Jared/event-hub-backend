package com.fdmgroup.backend_eventhub.eventsession.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fdmgroup.backend_eventhub.eventsession.enums.ContentType;
import com.fdmgroup.backend_eventhub.modules.model.Module;
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
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private ContentType type;

    private long orderNumber;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    @JsonIgnore
    private Event event;

    @OneToOne
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private Module module; // This will store the reference to either an ImageModule or a VideoModule

}
