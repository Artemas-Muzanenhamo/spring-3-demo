package com.spring.demo;

import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DummyDAOTest {

    public DummyDAO fakeDummyDAOImpl;
    protected static ApplicationContext applicationContext;

    @BeforeSuite
    public static void createApplicationContext(){

        System.out.println();
        applicationContext = new LazyInitializationClassPathXmlApplicationContext(
                new String[]{
                        "applicationContext.xml",
                        "applicationTestContext.xml"
                }
        );
    }

    @BeforeClass
    public void setUpClass(){
        fakeDummyDAOImpl = applicationContext.getBean(DummyDAO.class);
    }

    @Test
    public void shouldReturnDummyDAOOverriden(){
        assertEquals(fakeDummyDAOImpl.message(), "Calling Fake DAO");
    }
}
