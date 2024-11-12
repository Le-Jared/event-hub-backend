package com.fdmgroup.backend_eventhub.livechat.models;

import java.time.LocalDateTime;

public record ModuleAction(String ID, String TYPE, String SESSION_ID, String SENDER, LocalDateTime TIMESTAMP,
                           String TITLE, String CONTENT, String IMAGE_URL) {
}
