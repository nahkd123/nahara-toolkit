package nahara.common.reflective.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nahara.common.reflective.Handle;
import nahara.common.reflective.HandleFactory;

public class MethodHandleFactoryImpl<S, T> implements HandleFactory<S, T> {
	private Method getter;
	private Method setter;

	public MethodHandleFactoryImpl(Method getter, Method setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public Handle<T> of(S obj) {
		return new MethodHandleImpl<>(this, obj);
	}

	public static class MethodHandleImpl<S, T> implements Handle<T> {
		private MethodHandleFactoryImpl<S, T> factory;
		private S obj;

		public MethodHandleImpl(MethodHandleFactoryImpl<S, T> factory, S obj) {
			this.factory = factory;
			this.obj = obj;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T get() {
			if (factory.getter == null) throw new RuntimeException("Handle does not link to a getter");
			try {
				factory.getter.setAccessible(true);
				T t = (T) factory.getter.invoke(obj);
				factory.getter.setAccessible(false);
				return t;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void set(T obj) {
			if (factory.getter == null) throw new RuntimeException("Handle does not link to a setter");
			try {
				factory.getter.setAccessible(true);
				factory.setter.invoke(this.obj, obj);
				factory.getter.setAccessible(false);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
