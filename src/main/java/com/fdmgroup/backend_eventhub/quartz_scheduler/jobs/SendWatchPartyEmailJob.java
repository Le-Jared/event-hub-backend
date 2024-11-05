package com.fdmgroup.backend_eventhub.quartz_scheduler.jobs;

import com.fdmgroup.backend_eventhub.eventsession.model.Event;
import com.fdmgroup.backend_eventhub.eventsession.repository.IEventRepository;
import externalServices.email_service.service.EmailServiceImpl;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class SendWatchPartyEmailJob extends QuartzJobBean {

    @Autowired
    private IEventRepository eventRepository;

    @Autowired
    private EmailServiceImpl emailService;

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime tenMinutesLater = now.plusMinutes(10);
//
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//
//        LocalDate currentDate = now.format(dateFormatter);
//        LocalTime startTime = now.format(timeFormatter);
//        LocalTime endTime = tenMinutesLater.format(timeFormatter);
//
//        List<Event> upcomingEvents = eventRepository.findByScheduledDateAndScheduledTimeBetweenAndReminderEmailSentFalse(
//                currentDate,
//                startTime,
//                endTime
//        );
        // after type migration
        List<Event> upcomingEvents = eventRepository.findByScheduledDateAndScheduledTimeBetweenAndReminderEmailSentFalse(
                LocalDate.now(),
                LocalTime.now(),
                LocalTime.now().plusMinutes(10)
        );

        for (Event event : upcomingEvents) {
            String to = event.getAccount().getEmail();
            String subject = "Reminder: Watch Party Starting Soon - " + event.getEventName();
            String text = String.format("Your watch party '%s' is starting in 10 minutes! Don't forget to join at %s.",
                    event.getEventName(), event.getScheduledTime());

            emailService.sendSimpleMessage(to, subject, text);

            event.setReminderEmailSent(true);
            eventRepository.save(event);
        }
    }
}



