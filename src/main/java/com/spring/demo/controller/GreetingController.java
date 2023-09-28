package com.spring.demo.controller;

import com.spring.demo.service.GreetingService;

public class GreetingController {
    private GreetingService greetingService;

    public void setGreetingService(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    public String greet(String name) {
        return greetingService.greet(name);
    }
}
