package com.fdmgroup.backend_eventhub.eventsession.controller;

import com.fdmgroup.backend_eventhub.authenticate.service.TokenService;
import com.fdmgroup.backend_eventhub.eventsession.dto.*;
import com.fdmgroup.backend_eventhub.eventsession.exceptions.EventNotFoundException;
import com.fdmgroup.backend_eventhub.eventsession.model.Event;
import com.fdmgroup.backend_eventhub.eventsession.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Optional;
import java.util.List;


@RestController
@RequestMapping("/api/event")
public class EventController {

  private final EventService eventService;
  private final TokenService tokenService;

  @Autowired
  public EventController(EventService eventService, TokenService tokenService) {
      this.eventService = eventService;
      this.tokenService = tokenService;
  }

  private final String VIDEO_BASE_URL = "http://localhost:8080/encoded/";

  private final String ACCOUNT_ID_NOT_FOUND_MESSAGE = "Account ID in request not found";
  private final String EVENT_ID_NOT_FOUND_MESSAGE = "Event ID in request not found";
  private final String EVENT_CODE_NOT_FOUND_MESSAGE = "Event code in request not found";
  private final String ILLEGAL_PARAMETERS_MESSAGE = "Invalid or missing parameters in request";

  @PostMapping("/create")
  public ResponseEntity<?> createEvent(
          @RequestBody CreateEventRequest createEventRequest) {

    Event event = null;
    System.out.println(createEventRequest.toString());
    try {
      event = eventService.createEvent(
              createEventRequest.getEventName(),
              createEventRequest.getPassword(),
              createEventRequest.getAccountID(),
              createEventRequest.getScheduledDate(),
              createEventRequest.getScheduledTime()
      );
    } catch ( AccountNotFoundException e ) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ACCOUNT_ID_NOT_FOUND_MESSAGE);
    } catch ( IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ILLEGAL_PARAMETERS_MESSAGE);
    }

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
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(EVENT_CODE_NOT_FOUND_MESSAGE);
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

  @GetMapping("/getByUserId/{userId}")
  public ResponseEntity<?> getEventsByUserId(@PathVariable Long userId) {
      List<Event> events = null;
      try {
          events = eventService.getEventsByUserId(userId);
      } catch ( AccountNotFoundException e ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ACCOUNT_ID_NOT_FOUND_MESSAGE);
      }
      return ResponseEntity.ok(events);
  }

  @GetMapping("/get/{code}")
  public ResponseEntity<?> getEventByCode(@PathVariable String code) {
    Optional<Event> eventOptional = eventService.findByCode(code);
    if (eventOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(EVENT_CODE_NOT_FOUND_MESSAGE);
    } else {
      return ResponseEntity.ok(eventOptional.get());
    }
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
  public ResponseEntity<?> updateWatchParty(
          @RequestBody UpdateEventRequest updateEventRequest) {

    Event event = null;
    try {
      event = eventService.updateEvent(
              updateEventRequest.getEventName(),
              updateEventRequest.getAccountId(),
              updateEventRequest.getScheduledDate(),
              updateEventRequest.getScheduledTime(),
              updateEventRequest.getEventId()
      );
    } catch ( AccountNotFoundException e ) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ACCOUNT_ID_NOT_FOUND_MESSAGE);
    } catch ( EventNotFoundException e ) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(EVENT_ID_NOT_FOUND_MESSAGE);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ILLEGAL_PARAMETERS_MESSAGE);
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(event);
  }

  @PostMapping("/update-password")
  public ResponseEntity<?> updateEventPassword(
          @RequestBody UpdateEventPasswordRequest updateEventPasswordRequest) {

    Event event;
    try {
      event = eventService.updateEventPassword(
              updateEventPasswordRequest.getPassword(),
              updateEventPasswordRequest.getAccountId(),
              updateEventPasswordRequest.getEventId()
      );
    } catch ( AccountNotFoundException e ) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ACCOUNT_ID_NOT_FOUND_MESSAGE);
    } catch ( EventNotFoundException e ) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(EVENT_ID_NOT_FOUND_MESSAGE);
    } catch ( IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ILLEGAL_PARAMETERS_MESSAGE);
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(event);
  }

  @DeleteMapping("/delete/{code}")
  public ResponseEntity<?> deleteEventByCode(@PathVariable String code) {
    try {
      eventService.deleteEventByCode(code);
      return ResponseEntity.status(HttpStatus.OK).body("Event (Code : " + code + ") deleted successfully");
    } catch ( EventNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(EVENT_CODE_NOT_FOUND_MESSAGE);
    }
  }

}
