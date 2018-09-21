package com.spring.demo;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.BeansException;

public class LazyInitializationListableBeanFactory extends DefaultListableBeanFactory {
	private Object lock = new Object();
	public LazyInitializationListableBeanFactory() {
		if ("true".equals(System.getProperty("quick.startup"))) {
			FasterCachedIntrospectionResults.patchCachedIntrospectionResults();
		}
	}

	public LazyInitializationListableBeanFactory(BeanFactory parentBeanFactory) {
		super(parentBeanFactory);
	}

	public void preInstantiateSingletons() throws BeansException {
	}

	@Override
	protected <T> T doGetBean(String name, Class<T> requiredType, Object[] args, boolean typeCheckOnly) throws BeansException {
		synchronized (lock) {
			// Synchronized to prevent deadlocks
			return super.doGetBean(name, requiredType, args, typeCheckOnly);    //To change body of overridden methods use File | Settings | File Templates.
		}
	}

	@Override
	public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException {
		synchronized (lock) {
			super.autowireBeanProperties(existingBean, autowireMode, dependencyCheck);    //To change body of overridden methods use File | Settings | File Templates.
		}
	}

	@Override
	public Object initializeBean(Object existingBean, String beanName) {
		synchronized (lock) {
			return super.initializeBean(existingBean, beanName);    //To change body of overridden methods use File | Settings | File Templates.
		}
	}
}