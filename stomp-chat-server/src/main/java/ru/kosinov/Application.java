package ru.kosinov;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.kosinov.chat.props.KeycloakProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({KeycloakProperties.class})
@EnableScheduling
public class Application {

    @Autowired
    private Environment env;

    public static void main( String[] args )  throws UnknownHostException {
        SpringApplication app = new SpringApplication(Application.class);
        Environment env = app.run(args).getEnvironment();
        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        addDefaultProfile(app, source);
        log.info("Access URLs:\n----------------------------------------------------------\n\t" +
                        "Local: \t\thttp://127.0.0.1:{}\n\t" +
                        "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));

    }

    private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
        if (!source.containsProperty("spring.profiles.active") &&
                !System.getenv().containsKey("SPRING_PROFILES_ACTIVE")) {
            app.setAdditionalProfiles("dev");
        }
    }

}
