package ru.kosinov.chat.shell.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.kosinov.chat.shell.handler.GlobalHandler;
import ru.kosinov.chat.shell.util.ShellHelper;

import java.lang.reflect.Type;

@Slf4j
@ShellComponent
public class JoinRoomCommands {

    @Autowired
    private GlobalHandler globalHandler;

    @Autowired
    private ShellHelper shellHelper;


    @ShellMethod("Join to room")
    public void join(
            @ShellOption(defaultValue = "") String room) {
        StompSession stompSession = globalHandler.getStompSession();
        globalHandler.setRoomId(room);
        StompSession.Subscription subs = stompSession.subscribe("/topic/replay."+room, new StompFrameHandler() {


            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                String output = shellHelper.getSuccessMessage(String.format("Hello %s!", (String) payload));
                shellHelper.print(output);
            }

        });
        globalHandler.getSubscriptions().add(subs);
    }
}
