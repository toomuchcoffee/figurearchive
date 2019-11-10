package de.toomuchcoffee.figurearchive.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "figurearchive")
public class ConfigProperties {
    private Figures figures;
    private Photos photos;

    @Getter
    @Setter
    public static class Figures {
        private int pageSize;
    }

    @Getter
    @Setter
    public static class Photos {
        private int pageSize;
    }
}
