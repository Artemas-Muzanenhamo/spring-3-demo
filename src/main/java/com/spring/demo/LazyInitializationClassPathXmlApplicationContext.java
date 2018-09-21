package com.spring.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class LazyInitializationClassPathXmlApplicationContext extends ClassPathXmlApplicationContext {
	public LazyInitializationClassPathXmlApplicationContext(String configLocation) throws BeansException {
		super(configLocation);
	}

	public LazyInitializationClassPathXmlApplicationContext(String[] configLocations) throws BeansException {
		super(configLocations);
	}

	public LazyInitializationClassPathXmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
		super(configLocations, parent);
	}

	public LazyInitializationClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
		super(configLocations, refresh);
	}

	public LazyInitializationClassPathXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent) throws BeansException {
		super(configLocations, refresh, parent);
	}

	public LazyInitializationClassPathXmlApplicationContext(String path, Class clazz) throws BeansException {
		super(path, clazz);
	}

	public LazyInitializationClassPathXmlApplicationContext(String[] paths, Class clazz) throws BeansException {
		super(paths, clazz);
	}

	public LazyInitializationClassPathXmlApplicationContext(String[] paths, Class clazz, ApplicationContext parent) throws BeansException {
		super(paths, clazz, parent);
	}

	protected DefaultListableBeanFactory createBeanFactory() {
		return new LazyInitializationListableBeanFactory(getParentBeanFactory());
	}
}
