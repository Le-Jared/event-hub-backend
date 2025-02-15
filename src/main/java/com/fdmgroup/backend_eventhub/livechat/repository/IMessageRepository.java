package com.fdmgroup.backend_eventhub.livechat.repository;

import com.fdmgroup.backend_eventhub.livechat.models.Message;
//import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IMessageRepository {
    List<Message> findBySessionId(String sessionId);
    Long deleteMessagesBySessionId(String sessionId);
}
