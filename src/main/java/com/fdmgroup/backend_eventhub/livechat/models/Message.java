package com.fdmgroup.backend_eventhub.livechat.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
//import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long messageID;
  private MessageType type;
  private String content;
  private String sender;
  private String sessionId;
  private LocalDateTime timeStamp;

  @Override
  public String toString() {
    return "Message{"
        + "messageID="
        + messageID
        + ", type="
        + type
        + ", content='"
        + content
        + '\''
        + ", sender='"
        + sender
        + '\''
        + ", sessionId='"
        + sessionId
        + '\''
        + ", timeStamp="
        + timeStamp
        + '}';
  }
}
