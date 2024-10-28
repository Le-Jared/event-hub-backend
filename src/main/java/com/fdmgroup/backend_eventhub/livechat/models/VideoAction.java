package com.fdmgroup.backend_eventhub.livechat.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VideoAction {
    private String actionType;
    private long actionTime;
    private double videoTime;
    private String sessionId;
    private String sender;
}
