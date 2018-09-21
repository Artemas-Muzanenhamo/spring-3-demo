package com.spring.demo;


import org.springframework.stereotype.Component;

@Component
public class FakeDummyDAOImpl implements DummyDAO{
    public String message(){
        return "Calling Fake DAO";
    }
}
