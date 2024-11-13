package com.fdmgroup.backend_eventhub.livechat.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreamStatus {

    private String sessionID;
    private int viewerCount;
    private boolean isLive;

}
