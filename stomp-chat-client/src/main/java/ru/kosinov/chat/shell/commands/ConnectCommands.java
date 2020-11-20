package ru.kosinov.chat.shell.commands;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.container.grizzly.client.GrizzlyClientContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.shell.Shell;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.kosinov.chat.props.ChatProperties;
import ru.kosinov.chat.shell.client.AuthWebSocketClient;
import ru.kosinov.chat.shell.handler.GlobalHandler;
import ru.kosinov.chat.shell.handler.STOMPHandler;
import ru.kosinov.chat.shell.util.ShellHelper;

import javax.websocket.ClientEndpointConfig;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@ShellComponent
public class ConnectCommands {

    @Autowired
    private TaskExecutor executor;

    @Autowired
    private GlobalHandler handler;

    @Autowired
    private ChatProperties properties;

    @Autowired
    private ShellHelper shellHelper;

    @ShellMethod("Connect to chat server")
    public void connect() {
        executor.execute(()->{

            List<Transport> transports = new ArrayList<>(2);

            transports.add(new WebSocketTransport(new StandardWebSocketClient()));
//            transports.add(new RestTemplaertteXhrTransport());


            SockJsClient client = new SockJsClient(transports);
            client.setHttpHeaderNames("Authorization","BOOYAKA");
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            stompClient.setMessageConverter(new StringMessageConverter());
//            stompClient.setMessageConverter(new MappingJackson2MessageConverter());
            stompClient.setAutoStartup(true);
            StompSessionHandler sessionHandler = new STOMPHandler(handler.getLogin(), properties, shellHelper, log);


            WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders(new HttpHeaders());
            webSocketHttpHeaders.add("Authorization","preved");
            webSocketHttpHeaders.add("BOOYAKA","preved");

            StompHeaders connectHeaders = new StompHeaders();
            connectHeaders.add("Authorization", handler.getToken());

            ListenableFuture<StompSession> sess = stompClient.connect(getChatUrl(),
                    webSocketHttpHeaders,
                    connectHeaders,
                    sessionHandler);
            sess.addCallback(stompSession -> {
                handler.setStompSession(stompSession);
            }, throwable -> {
                log.error("Can't connect to destination server");
                log.error("error",throwable);
            });

        });
    }

    String getChatUrl() {
        return properties.getUrl();
    }
}
