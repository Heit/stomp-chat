package ru.kosinov;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Slf4j
public class AppWebXml extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.profiles(addDefaultProfile())
                .sources(Application.class);
    }

    /**
     * Set a default profile if it has not been set.
     * <p>
     * Please use -Dspring.profiles.active=dev
     * </p>
     */
    private String addDefaultProfile() {
        String profile = System.getProperty("spring.profiles.active");
        if (profile != null) {
            log.info("Running with Spring profile(s) : {}", profile);
            return profile;
        }

        log.warn("No Spring profile configured, running with default configuration");
        return "dev";
    }

}
