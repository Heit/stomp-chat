package ru.kosinov.chat.shell.client;

import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketHandlerAdapter;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.adapter.standard.WebSocketToStandardExtensionAdapter;
import org.springframework.web.socket.client.AbstractWebSocketClient;
import org.springframework.web.socket.client.standard.WebSocketContainerFactoryBean;

import javax.websocket.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Callable;

public class AuthWebSocketClient extends AbstractWebSocketClient {

    private final WebSocketContainer webSocketContainer;

    private final Map<String,Object> userProperties = new HashMap<>();

    private String login;

    @Nullable
    private AsyncListenableTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();


    /**
     * Default constructor that calls {@code ContainerProvider.getWebSocketContainer()}
     * to obtain a (new) {@link WebSocketContainer} instance. Also see constructor
     * accepting existing {@code WebSocketContainer} instance.
     */
    public AuthWebSocketClient(String login) {
        this.webSocketContainer = ContainerProvider.getWebSocketContainer();
        this.login = login;
    }

    /**
     * Constructor accepting an existing {@link WebSocketContainer} instance.
     * <p>For XML configuration, see {@link WebSocketContainerFactoryBean}. For Java
     * configuration, use {@code ContainerProvider.getWebSocketContainer()} to obtain
     * the {@code WebSocketContainer} instance.
     */
    public AuthWebSocketClient(WebSocketContainer webSocketContainer) {
        Assert.notNull(webSocketContainer, "WebSocketContainer must not be null");
        this.webSocketContainer = webSocketContainer;
    }


    /**
     * The standard Java WebSocket API allows passing "user properties" to the
     * server via {@link ClientEndpointConfig#getUserProperties() userProperties}.
     * Use this property to configure one or more properties to be passed on
     * every handshake.
     */
    public void setUserProperties(@Nullable Map<String, Object> userProperties) {
        if (userProperties != null) {
            this.userProperties.putAll(userProperties);
        }
    }

    /**
     * The configured user properties.
     */
    public Map<String, Object> getUserProperties() {
        return this.userProperties;
    }

    /**
     * Set an {@link AsyncListenableTaskExecutor} to use when opening connections.
     * If this property is set to {@code null}, calls to any of the
     * {@code doHandshake} methods will block until the connection is established.
     * <p>By default, an instance of {@code SimpleAsyncTaskExecutor} is used.
     */
    public void setTaskExecutor(@Nullable AsyncListenableTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * Return the configured {@link TaskExecutor}.
     */
    @Nullable
    public AsyncListenableTaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }


    @Override
    protected ListenableFuture<WebSocketSession> doHandshakeInternal(WebSocketHandler webSocketHandler,
                                                                     HttpHeaders headers, final URI uri, List<String> protocols,
                                                                     List<WebSocketExtension> extensions, Map<String, Object> attributes) {

        int port = getPort(uri);
        InetSocketAddress localAddress = new InetSocketAddress(getLocalHost(), port);
        InetSocketAddress remoteAddress = new InetSocketAddress(uri.getHost(), port);

        headers.add("Authorization",login);

        final StandardWebSocketSession session = new StandardWebSocketSession(headers,
                attributes, localAddress, remoteAddress);

        final ClientEndpointConfig endpointConfig = ClientEndpointConfig.Builder.create()
                .configurator(new AuthWebSocketClient.StandardWebSocketClientConfigurator(headers))
                .preferredSubprotocols(protocols)
                .extensions(adaptExtensions(extensions)).build();

        endpointConfig.getUserProperties().putAll(getUserProperties());

        final Endpoint endpoint = new StandardWebSocketHandlerAdapter(webSocketHandler, session);

        Callable<WebSocketSession> connectTask = () -> {
            this.webSocketContainer.connectToServer(endpoint, endpointConfig, uri);
            return session;
        };

        if (this.taskExecutor != null) {
            return this.taskExecutor.submitListenable(connectTask);
        }
        else {
            ListenableFutureTask<WebSocketSession> task = new ListenableFutureTask<>(connectTask);
            task.run();
            return task;
        }
    }

    private static List<Extension> adaptExtensions(List<WebSocketExtension> extensions) {
        List<Extension> result = new ArrayList<>();
        for (WebSocketExtension extension : extensions) {
            result.add(new WebSocketToStandardExtensionAdapter(extension));
        }
        return result;
    }

    private InetAddress getLocalHost() {
        try {
            return InetAddress.getLocalHost();
        }
        catch (UnknownHostException ex) {
            return InetAddress.getLoopbackAddress();
        }
    }

    private int getPort(URI uri) {
        if (uri.getPort() == -1) {
            String scheme = uri.getScheme().toLowerCase(Locale.ENGLISH);
            return ("wss".equals(scheme) ? 443 : 80);
        }
        return uri.getPort();
    }


    private class StandardWebSocketClientConfigurator extends ClientEndpointConfig.Configurator {

        private final HttpHeaders headers;

        public StandardWebSocketClientConfigurator(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override
        public void beforeRequest(Map<String, List<String>> requestHeaders) {
            List<String> val = new ArrayList<>();
            val.add("preved");
            requestHeaders.put("AUTHORIZATION", val);
            requestHeaders.putAll(this.headers);
            if (logger.isTraceEnabled()) {
                logger.trace("Handshake request headers: " + requestHeaders);
            }
        }
        @Override
        public void afterResponse(HandshakeResponse response) {
            if (logger.isTraceEnabled()) {
                logger.trace("Handshake response headers: " + response.getHeaders());
            }
        }
    }
}
