package ru.kosinov.chat.shell.handler;

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GlobalHandler {

    private String login;

    private String token;

    private String roomId;

    private StompSession stompSession;

    private List<StompSession.Subscription> subscriptions = new ArrayList<>();

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public StompSession getStompSession() {
        return stompSession;
    }

    public List<StompSession.Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<StompSession.Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setStompSession(StompSession stompSession) {
        this.stompSession = stompSession;
    }
}
