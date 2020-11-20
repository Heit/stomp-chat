package ru.kosinov.chat.shell.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.kosinov.chat.shell.handler.GlobalHandler;

@Slf4j
@ShellComponent
public class SomeCommand {

    @Autowired
    GlobalHandler globalHandler;

    @ShellMethod("Send message to user")
    public void say(
            @ShellOption(defaultValue = "") String message  ) {
        StompSession session = globalHandler.getStompSession();
        session.send("/app/direct/creved", message);
    }
}
