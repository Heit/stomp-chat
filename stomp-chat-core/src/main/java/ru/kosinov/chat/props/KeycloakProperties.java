package ru.kosinov.chat.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chat.keycloak", ignoreUnknownFields = true)
public class KeycloakProperties {

    private String realm;

    private String authServerUrl;

    private String clientSecret;

    private String clientId;

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getAuthServerUrl() {
        return authServerUrl;
    }

    public void setAuthServerUrl(String authServerUrl) {
        this.authServerUrl = authServerUrl;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
