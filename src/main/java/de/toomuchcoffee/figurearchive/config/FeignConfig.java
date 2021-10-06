package de.toomuchcoffee.figurearchive.config;

import de.toomuchcoffee.figurearchive.tumblr.TumblrClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = TumblrClient.class)
public class FeignConfig {
}
