package com.fdmgroup.backend_eventhub.livechat.controller;

import com.fdmgroup.backend_eventhub.livechat.models.EmojiReaction;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmojiController {

  SimpMessagingTemplate template;

  //  int latestID = 0;

  public EmojiController(SimpMessagingTemplate template) {
    this.template = template;
  }

  @MessageMapping("/emoji")
  public void handleEmojiReactions(EmojiReaction emoji) {
    System.out.println(emoji);
    template.convertAndSend("/topic/emoji/" + emoji.SESSION_ID(), emoji);
    //    System.out.println("hehe");
  }
}
