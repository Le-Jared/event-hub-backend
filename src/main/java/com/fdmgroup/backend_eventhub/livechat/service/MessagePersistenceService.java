package com.fdmgroup.backend_eventhub.livechat.service;

import com.fdmgroup.backend_eventhub.livechat.constant.KafkaConstants;
import com.fdmgroup.backend_eventhub.livechat.models.Message;
import com.fdmgroup.backend_eventhub.livechat.repository.IMessageRepository;

import java.util.List;
import java.util.Optional;

import com.fdmgroup.backend_eventhub.livechat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessagePersistenceService {
//  @Autowired private IMessageRepository messageRepository;

    @Autowired
    private MessageRepository messageRepository;

    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC, groupId = "chat-persistence")
    public void persistMessage(Message message) {
        System.out.println(message);
        // save message to database
        messageRepository.save(message);
    }

    public Optional<List<Message>> findMessagesBySession(String sessionId) {
        List<Message> messages;
        if ( sessionId == null || sessionId.isEmpty() ) {
            return Optional.empty();
        }
        try {
            messages = messageRepository.findBySessionId(sessionId);
        } catch ( Exception e ) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }

        return Optional.of(messages);
    }

    public long deleteMessagesBySession(String sessionId) {
        // return count of messages deleted
//    return messageRepository.deleteMessagesBySessionId(sessionId);
        List<Message> messages = messageRepository.findBySessionId(sessionId);
        messages.forEach(messageRepository::delete);
        return messages.size();
    }
}
