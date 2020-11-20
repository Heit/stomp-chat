package ru.kosinov.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.text.SimpleDateFormat;

@Slf4j
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/message/{room}")
    @SendTo("/topic/{room}.room")
    public String processMessageFromClient(@Payload String message,
                                           @DestinationVariable String room,
                                           Principal principal) throws Exception {
        log.debug("<Principal> {}, <room> {}, <message> {}", principal, room, message);
        return message;
    }

    @MessageMapping("/direct/{user}")
    @SendTo("/queue/{user}.messages")
    public String processDirectMessage(@Payload String message,
                                       @DestinationVariable String user,
                                       Principal principal){
        log.debug("<Direct> from {}, <to> {}, <message> {}",principal, user,message);
        if (user.equals("anonimous")) {
            throw new IllegalArgumentException("Can't send direct message to anonimous");
        }
        return message;
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

//    @Scheduled(fixedRate = 10000)
    public void scheduleFixedRateTask() {
       SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
       log.debug("Timer task {}", formatter.format(formatter));
       simpMessagingTemplate.convertAndSend("/topic/booyaka.room", "Greetings from chat server!!!");
    }

}
