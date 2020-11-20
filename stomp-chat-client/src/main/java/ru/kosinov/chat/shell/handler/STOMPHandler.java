package ru.kosinov.chat.shell.handler;

import org.slf4j.Logger;
import org.springframework.messaging.simp.stomp.*;
import ru.kosinov.chat.props.ChatProperties;
import ru.kosinov.chat.shell.util.ShellHelper;

import java.lang.reflect.Type;

public class STOMPHandler implements StompSessionHandler {

    private String login;

    private ChatProperties props;

    private ShellHelper shellHelper;

    private Logger log;


    public STOMPHandler(String login, ChatProperties props, ShellHelper shellHelper, Logger log) {
        this.login = login;
        this.props = props;
        this.shellHelper = shellHelper;
        this.log = log;
    }

    @Override
    public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
        System.out.println("connectedHeaders = " + stompHeaders);
        stompSession.subscribe(getReplyTopic(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object o) {
                shellHelper.print((String)o);
            }
        });
//        stompSession.subscribe(getErrorTopic(), new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders stompHeaders) {
//                return String.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders stompHeaders, Object o) {
//                shellHelper.print((String) o);
//            }
//        });
    }

    @Override
    public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
        log.error("Incoming", throwable);
    }

    @Override
    public void handleTransportError(StompSession stompSession, Throwable throwable) {

    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return null;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        System.out.println("Incoming " + o);
    }

    String getReplyTopic(){
        return String.format("/queue/%s.messages", login);
    }

    String getErrorTopic(){
        return String.format("/queue/%s.replay.error", login);
    }
}
