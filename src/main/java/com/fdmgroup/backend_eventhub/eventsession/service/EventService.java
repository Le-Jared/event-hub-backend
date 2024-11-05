package com.fdmgroup.backend_eventhub.eventsession.service;

import com.fdmgroup.backend_eventhub.authenticate.model.Account;
import com.fdmgroup.backend_eventhub.authenticate.repository.AccountRepository;
import com.fdmgroup.backend_eventhub.eventsession.exceptions.EventNotFoundException;
import com.fdmgroup.backend_eventhub.modules.service.VideoService;
import com.fdmgroup.backend_eventhub.eventsession.model.Event;
import com.fdmgroup.backend_eventhub.eventsession.repository.IEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final IEventRepository eventRepository;
    private final AccountRepository accountRepository;
    private final VideoService videoService;

    public EventService(IEventRepository eventRepository, AccountRepository accountRepository, VideoService videoService) {
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
        this.videoService = videoService;
    }

    public Event createEvent(String eventName, String password, Long accountID, String scheduledDate, String scheduledTime) throws AccountNotFoundException {
        if (eventName == null || eventName.isEmpty() || password == null || password.isEmpty() || accountID == null || scheduledDate == null || scheduledTime == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
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
            throw new AccountNotFoundException("Account with ID " + accountID + " not found");
        }
    }

    public Optional<Event> findByCode(String code) {
        return eventRepository.findByCode(code);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getEventsByUserId(Long userId) throws AccountNotFoundException {
        Optional<Account> account = accountRepository.findById(userId);
        if (account.isPresent()) {
            return eventRepository.findByAccount(account.get());
        } else {
            throw new AccountNotFoundException("Account not found");
        }
    }

    public List<Event> getAllEventsWithPoll() {
        return eventRepository.findEventsWithPoll();
    }

    public List<Event> getAllEventsWithoutPoll() {
        return eventRepository.findEventsWithoutPoll();
    }

    public Event updateEvent(String eventName, Long accountID, String scheduledDate, String scheduledTime, Long eventId) throws AccountNotFoundException, EventNotFoundException {
        if (eventName == null || eventName.isEmpty() || accountID == null || scheduledDate == null || scheduledTime == null || eventId == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
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
                throw new EventNotFoundException("Event not found");
            }
        } else {
            throw new AccountNotFoundException("Account not found");
        }
    }

    public Event updateEventPassword(String password, Long accountID, Long eventId) throws AccountNotFoundException, EventNotFoundException {
        if (password == null || password.isEmpty() || accountID == null || eventId == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
        Optional<Account> account = accountRepository.findById(accountID);
        if (account.isPresent()) {
            Optional<Event> event = eventRepository.findById(eventId);
            if (event.isPresent()) {
                Event updatedEvent = event.get();
                updatedEvent.setPassword(password);
                return eventRepository.save(updatedEvent);
            } else {
                throw new EventNotFoundException("Event not found");
            }
        } else {
            throw new AccountNotFoundException("Account not found");
        }
    }
}

