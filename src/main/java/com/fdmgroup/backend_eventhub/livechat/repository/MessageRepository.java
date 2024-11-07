package com.fdmgroup.backend_eventhub.livechat.repository;

import com.fdmgroup.backend_eventhub.livechat.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySessionId(String sessionId);

}
