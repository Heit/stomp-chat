package ru.kosinov.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ru.kosinov.auth.JWSAuthenticationToken;

import javax.servlet.Filter;
import java.util.Optional;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer  {

    @Autowired
    @Qualifier("websocket")
    private AuthenticationManager authenticationManager;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/user", "/topic", "/queue");
        config.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("127.0.0.1")
                .setRelayPort(61613)
                .setSystemLogin("guest")
                .setSystemPasscode("guest");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        return loggingFilter;
    }

    @Bean
    public FilterRegistrationBean requestDumperFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter requestDumperFilter = new RequestDumperFilter();
        registration.setFilter(requestDumperFilter);
        registration.addUrlPatterns("/*");
        return registration;
    }



    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws-main")
                .setAllowedOrigins("*")
                .withSockJS()
                .setClientLibraryUrl("https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.1/sockjs.min.js");
    }





    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(new ChannelInterceptor(){

            private static final String AUTHORIZATION = "Authorization";

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Optional.ofNullable(accessor.getNativeHeader(AUTHORIZATION)).ifPresent(ah -> {
                        String bearerToken = ah.get(0).replace("Bearer ", "");
                        log.debug("Received bearer token {}", bearerToken);
                        JWSAuthenticationToken token = (JWSAuthenticationToken) authenticationManager
                                .authenticate(new JWSAuthenticationToken(bearerToken));
                        token.setAuthenticated(true);
                        accessor.setUser(token);
                    });
                }
                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
                    log.debug("Want to subscribe "+accessor.getUser());
                }
                return message;
            }
        });
    }


}
