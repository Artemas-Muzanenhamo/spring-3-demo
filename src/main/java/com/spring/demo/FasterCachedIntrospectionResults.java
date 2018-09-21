package com.spring.demo;

import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.FatalBeanException;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class FasterCachedIntrospectionResults {
	private static boolean patched = false;

	public static void patchCachedIntrospectionResults() {
		if (!patched) {
			Class cirClass = CachedIntrospectionResults.class;
			try {
				Field classCache = cirClass.getDeclaredField("classCache");
				makeAccessibleAndSettable(classCache);
				classCache.set(null, createProxiedMap());
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			patched = true;
		}
	}

	private static void makeAccessibleAndSettable(Field classCache) throws NoSuchFieldException, IllegalAccessException {
		classCache.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(classCache, classCache.getModifiers() & ~Modifier.FINAL);
	}

	private static Map createProxiedMap() {
		return (Map) Proxy.newProxyInstance(
				Map.class.getClassLoader(),
				new Class[]{Map.class},
				new MapProxy(new WeakHashMap()));
	}

	private static class MapProxy implements InvocationHandler {
		private Map proxied;
		private ObjectInstantiator cachedInvocationResultsInstantiator;

		public MapProxy(Map proxied) {
			this.proxied = proxied;
			Objenesis objenesis = new ObjenesisStd();
			cachedInvocationResultsInstantiator = objenesis.getInstantiatorOf(CachedIntrospectionResults.class);
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object object = method.invoke(proxied, args);
			if (method.getName().equals("get") && object == null) {
				object = createCachedIntrospectionResults((Class) args[0]);
				proxied.put(args[0], object);
			}

			return object;
		}

		private CachedIntrospectionResults createCachedIntrospectionResults(Class beanClass) {
			try {
				// This is the line that speeds everything up. The default CachedIntrospectionResults
				// is too slow because it doesn't use Introspector.IGNORE_ALL_BEANINFO
				BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, Introspector.IGNORE_ALL_BEANINFO);

				// Immediately remove class from Introspector cache, to allow for proper
				// garbage collection on class loader shutdown - we cache it here anyway,
				// in a GC-friendly manner. In contrast to CachedIntrospectionResults,
				// Introspector does not use WeakReferences as values of its WeakHashMap!
				Class classToFlush = beanClass;
				do {
					Introspector.flushFromCaches(classToFlush);
					classToFlush = classToFlush.getSuperclass();
				}
				while (classToFlush != null);

				Map propertyDescriptorCache = new HashMap();

				// This call is slow so we do it once.
				PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
				for (PropertyDescriptor pd : pds) {
					propertyDescriptorCache.put(pd.getName(), pd);
				}

				return createCachedIntrospectionResults(beanInfo, propertyDescriptorCache);
			}
			catch (IntrospectionException ex) {
				throw new FatalBeanException("Cannot get BeanInfo for object of class [" + beanClass.getName() + "]", ex);
			}
		}

		private CachedIntrospectionResults createCachedIntrospectionResults(BeanInfo beanInfo, Map propertyDescriptorCache) {
			try {
				CachedIntrospectionResults results = (CachedIntrospectionResults) cachedInvocationResultsInstantiator.newInstance();
				Field beanInfoField = CachedIntrospectionResults.class.getDeclaredField("beanInfo");
				Field propertyDescriptorCacheField = CachedIntrospectionResults.class.getDeclaredField("propertyDescriptorCache");

				beanInfoField.setAccessible(true);
				beanInfoField.set(results, beanInfo);
				propertyDescriptorCacheField.setAccessible(true);
				propertyDescriptorCacheField.set(results, propertyDescriptorCache);

				return results;
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
