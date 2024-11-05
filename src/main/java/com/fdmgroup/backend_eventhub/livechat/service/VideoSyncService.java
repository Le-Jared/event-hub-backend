package com.fdmgroup.backend_eventhub.livechat.service;

import com.fdmgroup.backend_eventhub.livechat.constant.KafkaConstants;
import com.fdmgroup.backend_eventhub.livechat.models.VideoAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class VideoSyncService {
    // this class will receive a message from the Kafka VideoModule Topic
    // Upon receiving this message, it will send it to all clients connected to the relevant watchparty
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = KafkaConstants.KAFKA_VIDEO_TOPIC,
            groupId = "video-sync-service"
    )
    public void sendVideoSyncMessage(VideoAction action) {
        System.out.println("Sending video sync message to clients");
        messagingTemplate.convertAndSend("/topic/video/" + action.getSessionId(), action);
        System.out.println("VideoModule Sync Message sent to clients. " +
                "Session Id: " + action.getSessionId() +
                " Action: " + action.getActionType());
    }
}
