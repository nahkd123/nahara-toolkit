package nahara.common.reflective.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import nahara.common.reflective.Method;
import nahara.common.reflective.MethodFactory;

public class MethodFactoryImpl<S, T> implements MethodFactory<S, T> {
	private java.lang.reflect.Method method;
	private Constructor<T> constructor;

	public MethodFactoryImpl(java.lang.reflect.Method method, Constructor<T> constructor) {
		this.method = method;
		this.constructor = constructor;
	}

	@Override
	public Method<T> of(S obj) {
		return new MethodImpl<>(this, obj);
	}

	public static class MethodImpl<S, T> implements Method<T> {
		private MethodFactoryImpl<S, T> factory;
		private S obj;

		public MethodImpl(MethodFactoryImpl<S, T> factory, S obj) {
			this.factory = factory;
			this.obj = obj;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T invoke(Object... args) {
			try {
				if (factory.constructor != null) {
					factory.constructor.setAccessible(true);
					T t = (T) factory.constructor.newInstance(args);
					factory.constructor.setAccessible(false);
					return t;
				} else {
					factory.method.setAccessible(true);
					T t = (T) factory.method.invoke(obj, args);
					factory.method.setAccessible(false);
					return t;
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
