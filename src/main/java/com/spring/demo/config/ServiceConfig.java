package com.spring.demo.config;

import com.spring.demo.service.GreetingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {
    @Bean
    public GreetingService greetingService() {
        return new GreetingService();
    }
}
