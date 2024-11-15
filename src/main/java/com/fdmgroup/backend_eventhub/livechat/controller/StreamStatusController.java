package com.fdmgroup.backend_eventhub.livechat.controller;

import com.fdmgroup.backend_eventhub.livechat.models.StreamStatus;
import com.fdmgroup.backend_eventhub.livechat.models.StreamStatusNotification;
import com.fdmgroup.backend_eventhub.livechat.models.StreamStatusRecord;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class StreamStatusController {

    SimpMessagingTemplate template;

    Map<String, StreamStatus> streamMap = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> sessionTracker = new ConcurrentHashMap<>();

    public StreamStatusController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/streamStatus")
    public void statusCheck(StreamStatusNotification notification, SimpMessageHeaderAccessor headerAccessor) {
        String sessionID = notification.SESSION_ID();
        String type = notification.TYPE();
        String wsSessionId = headerAccessor.getSessionId();

        if ( !streamMap.containsKey(sessionID) ) {
            streamMap.put(sessionID, new StreamStatus(sessionID, -1, false));
            sessionTracker.put(sessionID, new HashSet<>());
        }

        StreamStatus stream = streamMap.get(sessionID);
        Set<String> activeSessions = sessionTracker.get(sessionID);

        handleMessage(type, activeSessions, wsSessionId, stream, sessionID);

        // Send update to all clients
        template.convertAndSend("/topic/streamStatus/" + sessionID,
                new StreamStatusRecord(type, UUID.randomUUID().toString(),
                        sessionID, stream.getViewerCount(), stream.isLive())
        );


    }

    private void handleMessage(String type, Set<String> activeSessions, String wsSessionId, StreamStatus stream, String sessionID) {
        switch ( type ) {
            case "VIEWER_JOIN": {
                if ( !activeSessions.contains(wsSessionId) ) {
                    activeSessions.add(wsSessionId);
                    stream.setViewerCount(activeSessions.size());
                    sessionTracker.put(sessionID, activeSessions);
                }
                break;
            }
            case "VIEWER_LEAVE": {
                if ( activeSessions.remove(wsSessionId) ) {
                    stream.setViewerCount(activeSessions.size());
                    sessionTracker.put(sessionID, activeSessions);
                }
                break;
            }
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

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String wsSessionId = event.getSessionId();

        // Find and remove the disconnected session from all rooms
        for ( Map.Entry<String, Set<String>> entry : sessionTracker.entrySet() ) {
            String roomId = entry.getKey();
            Set<String> sessions = entry.getValue();

            if ( sessions.remove(wsSessionId) ) {
                StreamStatus stream = streamMap.get(roomId);
                if ( stream != null ) {
                    stream.setViewerCount(sessions.size());
                    streamMap.put(roomId, stream);

                    // Notify remaining clients about the viewer count update
                    template.convertAndSend("/topic/streamStatus/" + roomId,
                            new StreamStatusRecord("VIEWER_LEAVE",
                                    UUID.randomUUID().toString(),
                                    roomId,
                                    stream.getViewerCount(),
                                    stream.isLive())
                    );
                }
            }
        }
    }


}
