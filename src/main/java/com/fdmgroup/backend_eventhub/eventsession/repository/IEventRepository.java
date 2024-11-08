package com.fdmgroup.backend_eventhub.eventsession.repository;

import com.fdmgroup.backend_eventhub.authenticate.model.Account;
import com.fdmgroup.backend_eventhub.eventsession.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface IEventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByCode(String code);
    List<Event> findByAccount(Account account);
    List<Event> findByScheduledDateAndScheduledTimeBetweenAndReminderEmailSentFalse(
            LocalDate scheduledDate, LocalTime startTime, LocalTime endTime);
    @Query(value = "SELECT * FROM event e WHERE e.id IN (SELECT p.event_id FROM poll p)", nativeQuery = true)
    List<Event> findEventsWithPoll();

    @Query(value = "SELECT * FROM event e WHERE e.id NOT IN (SELECT p.event_id FROM poll p)", nativeQuery = true)
    List<Event> findEventsWithoutPoll();
    @Modifying
    @Query("delete from Event e where e.code=:code")
    void deleteByCode(@Param("code") String code);
}