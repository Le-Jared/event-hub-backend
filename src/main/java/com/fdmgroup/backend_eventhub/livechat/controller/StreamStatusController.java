package com.fdmgroup.backend_eventhub.livechat.controller;

import com.fdmgroup.backend_eventhub.livechat.models.StreamStatus;
import com.fdmgroup.backend_eventhub.livechat.models.StreamStatusNotification;
import com.fdmgroup.backend_eventhub.livechat.models.StreamStatusRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class StreamStatusController {

    SimpMessagingTemplate template;

    Map<String, StreamStatus> streamMap = new ConcurrentHashMap<>();

    public StreamStatusController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/streamStatus")
    public void statusCheck(StreamStatusNotification notification) {
        System.out.println(notification);
        String sessionID = notification.SESSION_ID();
        String type = notification.TYPE();

        if ( !streamMap.containsKey(sessionID) ) {
            streamMap.put(sessionID, new StreamStatus(sessionID, 0, false));
        }
        StreamStatus stream = streamMap.get(sessionID);

        switch ( type ) {
            case "START_STREAM": {
                stream.setLive(true);

                template.convertAndSend("/topic/streamStatus/" + sessionID,
                        new StreamStatusRecord("START_STREAM", UUID.randomUUID().toString(), sessionID,
                                stream.getViewerCount(), stream.isLive())
                );
                break;
            }
            case "STOP_STREAM": {
                stream.setLive(false);
                template.convertAndSend("/topic/streamStatus/" + sessionID,
                        new StreamStatusRecord("STOP_STREAM", UUID.randomUUID().toString(), sessionID,
                                stream.getViewerCount(), stream.isLive())
                );
                break;
            }
            case "VIEWER_JOIN": {
                stream.setViewerCount(stream.getViewerCount() + 1);
                template.convertAndSend("/topic/streamStatus/" + sessionID,
                        new StreamStatusRecord("VIEWER_JOIN", UUID.randomUUID().toString(), sessionID,
                                stream.getViewerCount(), stream.isLive())
                );
                break;
            }
            case "VIEWER_LEAVE": {
                int count = Math.max(stream.getViewerCount() - 1, 0);
                stream.setViewerCount(count);
                template.convertAndSend("/topic/streamStatus/" + sessionID,
                        new StreamStatusRecord("VIEWER_LEAVE", UUID.randomUUID().toString(), sessionID,
                                stream.getViewerCount(), stream.isLive())
                );
                break;
            }
            default:
                System.out.println("command not found: " + type);
                break;
        }
        streamMap.put(sessionID, stream);
    }

    @GetMapping("/api/streamStatus/{sessionID}")
    public ResponseEntity<StreamStatus> getStreamStatus(@PathVariable String sessionID) {
        return ResponseEntity.ok(streamMap.get(sessionID));

    }


}
