package com.fdmgroup.backend_eventhub.eventsession.service;

import com.fdmgroup.backend_eventhub.authenticate.model.Account;
import com.fdmgroup.backend_eventhub.authenticate.repository.AccountRepository;
import com.fdmgroup.backend_eventhub.eventsession.exceptions.EventNotFoundException;
import com.fdmgroup.backend_eventhub.modules.service.VideoService;
import com.fdmgroup.backend_eventhub.eventsession.model.Event;
import com.fdmgroup.backend_eventhub.eventsession.repository.IEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final IEventRepository eventRepository;
    private final AccountRepository accountRepository;
    private final VideoService videoService;

    @Autowired
    public EventService(IEventRepository eventRepository, AccountRepository accountRepository, VideoService videoService) {
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
        this.videoService = videoService;
    }

    @Transactional
    public Event createEvent(String eventName, String password, Long accountID, LocalDate scheduledDate, LocalTime scheduledTime)
            throws
            AccountNotFoundException,
            IllegalArgumentException
    {
        if (eventName == null || eventName.isEmpty() || password == null || password.isEmpty() || accountID == null || scheduledDate == null || scheduledTime == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
        Account account = getAccountOrThrow(accountID);
        Event event = new Event();
        event.setEventName(eventName);
        event.setAccount(account);
        event.setPassword(password);
        event.setScheduledDate(scheduledDate);
        event.setScheduledTime(scheduledTime);
        return eventRepository.save(event);
    }

    public Optional<Event> findByCode(String code) {
        return eventRepository.findByCode(code);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getEventsByUserId(Long userId) throws AccountNotFoundException {
        Account account = getAccountOrThrow(userId);
        return eventRepository.findByAccount(account);
    }

    public List<Event> getAllEventsWithPoll() {
        return eventRepository.findEventsWithPoll();
    }

    public List<Event> getAllEventsWithoutPoll() {
        return eventRepository.findEventsWithoutPoll();
    }

    @Transactional
    public Event updateEvent(String eventName, Long accountID, LocalDate scheduledDate, LocalTime scheduledTime, Long eventId)
            throws
                AccountNotFoundException,
                EventNotFoundException,
                IllegalArgumentException
    {
        if (eventName == null || eventName.isEmpty() || accountID == null || scheduledDate == null || scheduledTime == null || eventId == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
        Account account = getAccountOrThrow(accountID);
        Event event = getEventOrThrow(eventId);
        event.setEventName(eventName);
        event.setScheduledDate(scheduledDate);
        event.setScheduledTime(scheduledTime);
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEventPassword(String password, Long accountID, Long eventId)
            throws
            AccountNotFoundException,
            EventNotFoundException,
            IllegalArgumentException
    {
        if (password == null || password.isEmpty() || accountID == null || eventId == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
        Account account = getAccountOrThrow(accountID);
        Event event = getEventOrThrow(eventId);
        event.setPassword(password);
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEventByCode(String code) throws EventNotFoundException {
        Optional<Event> eventOptional = findByCode(code);
        if ( eventOptional.isEmpty() ) {
            System.out.println("event not found : " + code);
            throw new EventNotFoundException("Event not found");
        } else {
            eventRepository.deleteByCode(code);
        }
    }

    private Account getAccountOrThrow(Long accountID) throws AccountNotFoundException {
        return accountRepository.findById(accountID)
            .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    private Event getEventOrThrow(Long eventId) throws EventNotFoundException {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Event not found"));
    }
}

