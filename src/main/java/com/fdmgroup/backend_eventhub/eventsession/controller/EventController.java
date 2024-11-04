package com.fdmgroup.backend_eventhub.eventsession.controller;

import com.fdmgroup.backend_eventhub.authenticate.service.TokenService;
import com.fdmgroup.backend_eventhub.eventsession.dto.*;
import com.fdmgroup.backend_eventhub.eventsession.model.Event;
import com.fdmgroup.backend_eventhub.eventsession.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;


@RestController
@RequestMapping("/api/event")
public class EventController {

  @Autowired
  EventService eventService;

  @Autowired
  TokenService tokenService;

  private final String VIDEO_BASE_URL = "http://localhost:8080/encoded/";

  @PostMapping("/create")
  public ResponseEntity<CreateEventResponse> createEvent(
          @RequestBody CreateEventRequest createEventRequest) {

    Event event = eventService.createEvent(
            createEventRequest.getEventName(),
            createEventRequest.getPassword(),
            createEventRequest.getAccountID(),
            createEventRequest.getScheduledDate(),
            createEventRequest.getScheduledTime()
    );

    // generate a token with host privileges to send to the client
    String token = tokenService.generateToken(event.getCode(), "host");

    CreateEventResponse response = new CreateEventResponse();
    response.setHost(true);
    response.setToken(token);
    response.setCode(event.getCode());

    System.out.println(response.toString());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/join")
  public ResponseEntity<?> joinEvent(
          @RequestBody JoinEventRequest joinRequest
  ){
    String code = joinRequest.getCode();
    String password = joinRequest.getPassword();

    Optional<Event> eventOptional = eventService.findByCode(code);

    if ( eventOptional.isEmpty() ) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid party code");
    }

    Event event = eventOptional.get();

    String actualPassword = event.getPassword();

    if ( !password.equals(actualPassword) ) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password for party code");
    }

    // generate a token which contains the watch party information and return it to the user
    String token = tokenService.generateToken(code, "guest");

    JoinEventResponse response = new JoinEventResponse();
    response.setToken(token);
    response.setHost(false);
    response.setRoomId(code);
    System.out.println(response.toString());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/get/{userId}")
  public ResponseEntity<List<Event>> getEventsByUserId(@PathVariable Long userId) {
    List<Event> events = eventService.getEventsByUserId(userId);
    return ResponseEntity.ok(events);
  }

  @GetMapping("/get")
  public ResponseEntity<List<Event>> getAllEvents() {
    List<Event> events = eventService.getAllEvents();
    return ResponseEntity.ok(events);
  }

  @GetMapping("/get/with-poll")
  public ResponseEntity<List<Event>> getAllEventsWithPoll() {
    List<Event> events = eventService.getAllEventsWithPoll();
    return ResponseEntity.ok(events);
  }

  @GetMapping("/get/without-poll")
  public ResponseEntity<List<Event>> getAllEventsWithoutPoll() {
    List<Event> events = eventService.getAllEventsWithoutPoll();
    return ResponseEntity.ok(events);
  }

  @PostMapping("/update")
  public ResponseEntity<Event> updateWatchParty(
          @RequestBody UpdateEventRequest updateEventRequest) {

    Event event = eventService.updateEvent(
            updateEventRequest.getEventName(),
            updateEventRequest.getAccountId(),
            updateEventRequest.getScheduledDate(),
            updateEventRequest.getScheduledTime(),
            updateEventRequest.getEventId()
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(event);
  }

  @PostMapping("/update-password")
  public ResponseEntity<Event> updateEventPassword(
          @RequestBody UpdateEventPasswordRequest updateEventPasswordRequest) {

    Event event = eventService.updateEventPassword(
            updateEventPasswordRequest.getPassword(),
            updateEventPasswordRequest.getAccountId(),
            updateEventPasswordRequest.getEventId()
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(event);
  }

}
