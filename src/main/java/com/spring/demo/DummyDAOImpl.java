package com.spring.demo;

import org.springframework.stereotype.Repository;

@Repository
public class DummyDAOImpl implements DummyDAO{

    @Override
    public String message(){
        return "Calling REAL DAO";
    }
}
