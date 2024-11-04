package com.fdmgroup.backend_eventhub.eventsession.service;

import com.fdmgroup.backend_eventhub.authenticate.model.Account;
import com.fdmgroup.backend_eventhub.authenticate.repository.AccountRepository;
import com.fdmgroup.backend_eventhub.modules.service.VideoService;
import com.fdmgroup.backend_eventhub.eventsession.model.Event;
import com.fdmgroup.backend_eventhub.eventsession.repository.IEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    IEventRepository eventRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    VideoService videoService;

    public Event createEvent(String eventName, String password, Long accountID, String scheduledDate, String scheduledTime) {

        Optional<Account> account = accountRepository.findById(accountID);
        if (account.isPresent()) {
            Event event = new Event();

            event.setEventName(eventName);
            event.setAccount(account.get());
            event.setPassword(password);

            event.setScheduledDate(scheduledDate);
            event.setScheduledTime(scheduledTime);

            return eventRepository.save(event);
        } else {
            throw new RuntimeException("Account not found");
        }
    }

    public Optional<Event> findByCode(String code) {
        return eventRepository.findByCode(code);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getEventsByUserId(Long userId) {
        Optional<Account> account = accountRepository.findById(userId);
        if (account.isPresent()) {
            return eventRepository.findByAccount(account.get());
        } else {
            throw new RuntimeException("Account not found");
        }
    }

    public List<Event> getAllEventsWithPoll() {
        return eventRepository.findEventsWithPoll();
    }

    public List<Event> getAllEventsWithoutPoll() {
        return eventRepository.findEventsWithoutPoll();
    }

    public Event updateEvent(String eventName, Long accountID, String scheduledDate, String scheduledTime, Long eventId) {

        Optional<Account> account = accountRepository.findById(accountID);
        if (account.isPresent()) {
            Optional<Event> event = eventRepository.findById(eventId);
            if (event.isPresent()) {
                Event updatedevent = event.get();
                updatedevent.setEventName(eventName);
                updatedevent.setScheduledDate(scheduledDate);
                updatedevent.setScheduledTime(scheduledTime);
                return eventRepository.save(updatedevent);

            } else {
                throw new RuntimeException("event not found");
            }

        } else {
            throw new RuntimeException("Account not found");
        }
    }

    public Event updateEventPassword(String password, Long accountID, Long eventId) {

        Optional<Account> account = accountRepository.findById(accountID);
        if (account.isPresent()) {
            Optional<Event> event = eventRepository.findById(eventId);
            if (event.isPresent()) {
                Event updatedEvent = event.get();
                updatedEvent.setPassword(password);
                return eventRepository.save(updatedEvent);

            } else {
                throw new RuntimeException("Event not found");
            }

        } else {
            throw new RuntimeException("Account not found");
        }
    }
}

