package ru.kosinov.chat.shell.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kosinov.chat.shell.handler.GlobalHandler;
import ru.kosinov.chat.shell.keycloak.KeycloakClient;

@Service
public class AuthorizationService {

    @Autowired
    private KeycloakClient client;

    @Autowired
    private GlobalHandler global;

    public String authorize(String login, String password) {
        return client.getAccessToken(login, password);
    }

}
