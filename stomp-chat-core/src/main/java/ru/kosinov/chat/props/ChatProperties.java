package ru.kosinov.chat.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chat.base", ignoreUnknownFields = true)
public class ChatProperties {

    private String url;

    private String baseMessages;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBaseMessages() {
        return baseMessages;
    }

    public void setBaseMessages(String baseMessages) {
        this.baseMessages = baseMessages;
    }
}
