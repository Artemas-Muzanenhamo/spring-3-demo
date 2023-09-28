package com.spring.demo;

import com.spring.demo.controller.GreetingController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        System.out.println("Hello Spring App!");

        GreetingController greetingController = context.getBean("greetingController", GreetingController.class);
        System.out.println(greetingController.greet("Artemas"));
    }
}
