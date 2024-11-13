package com.fdmgroup.backend_eventhub.poll.repository;

import com.fdmgroup.backend_eventhub.poll.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IPollRepository extends JpaRepository<Poll, Long> {
    @Query(value = "SELECT * FROM poll p WHERE p.event_id IN (SELECT e.id FROM event e WHERE e.code = ?1)", nativeQuery = true)
    Optional<Poll> getPollIdByEventCode(String code);
}
