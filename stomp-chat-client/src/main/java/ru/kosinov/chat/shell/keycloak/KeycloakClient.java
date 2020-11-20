package ru.kosinov.chat.shell.keycloak;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.kosinov.chat.props.KeycloakProperties;
import ru.kosinov.chat.shell.handler.GlobalHandler;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


@Slf4j
@Component
public class KeycloakClient extends RestTemplate {

    private final KeycloakProperties config;
    private final GlobalHandler globalHandler;

    @Autowired
    public KeycloakClient(KeycloakProperties config, GlobalHandler globalHandler) {
        this.config = config;
        this.globalHandler = globalHandler;
     }

    public String getAccessToken(final String login, final String password) {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF8"));
        HttpHeaders headers = buildHeaders();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.valueOf("application/json;charset=UTF-8")));
        this.setMessageConverters(Arrays.asList(new FormHttpMessageConverter(), converter));
        MultiValueMap<String, String> variablesMap = new LinkedMultiValueMap<String, String>() {{
            add("username", login);
            add("password", password);
            add("grant_type", "password");
            add("client_id", config.getClientId());
            add("client_secret", config.getClientSecret());
        }};
        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(variablesMap, headers);
        log.info("<URL> " + getRealmPostUrl());
        String response = this.postForObject(getRealmPostUrl(), entity, String.class);
        String token = new Gson().fromJson(response, Map.class).get("access_token").toString();
        globalHandler.setToken(token);
        globalHandler.setLogin(login);
        return token;
    }

    String getRealmUrl() {
        return String.format("%s/realms/%s", config.getAuthServerUrl(), config.getRealm());
    }

    String getRealmPostUrl() {
        return getRealmUrl() + "/protocol/openid-connect/token";
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setCacheControl("no-cache");
        headers.set("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)");
        return headers;
    }


}
