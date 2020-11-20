package ru.kosinov.chat.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.kosinov.chat.shell.handler.GlobalHandler;
import ru.kosinov.chat.shell.service.AuthorizationService;

@ShellComponent
public class MessageCommands {

    @Autowired
    private AuthorizationService service;

    @Autowired
    private GlobalHandler globalHandler;

    @ShellMethod("Send message to current room")
    public void msg(@ShellOption(defaultValue = "") String message ) {
        StompSession session = globalHandler.getStompSession();
        session.send("/app/message/"+globalHandler.getRoomId(), message);
    }

    @ShellMethod("Direct message to user")
    public void to(@ShellOption(defaultValue = "") String user,
                   @ShellOption(defaultValue = "") String message ){
        StompSession session = globalHandler.getStompSession();
        session.send(String.format("/app/direct/%s",user), message);
    }

}
