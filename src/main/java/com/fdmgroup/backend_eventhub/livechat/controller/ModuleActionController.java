package com.fdmgroup.backend_eventhub.livechat.controller;

import com.fdmgroup.backend_eventhub.livechat.models.ModuleAction;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModuleActionController {

    SimpMessagingTemplate template;

    public ModuleActionController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/moduleAction")
    public void triggerModuleAction(ModuleAction action) {
        System.out.println(action);
        template.convertAndSend("/topic/moduleAction/" + action.SESSION_ID(), action);

    }
}
