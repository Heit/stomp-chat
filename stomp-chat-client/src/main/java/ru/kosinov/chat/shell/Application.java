package ru.kosinov.chat.shell;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.shell.jline.PromptProvider;
import ru.kosinov.chat.props.ChatProperties;
import ru.kosinov.chat.props.KeycloakProperties;
import ru.kosinov.chat.shell.util.ShellHelper;

@SpringBootApplication
@EnableConfigurationProperties({KeycloakProperties.class, ChatProperties.class})
public class Application {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    }

    @Bean
    public PromptProvider myPromptProvider() {
        return () -> new AttributedString("stomp-chat:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

    @Bean
    public ShellHelper shellHelper(@Lazy Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

}
