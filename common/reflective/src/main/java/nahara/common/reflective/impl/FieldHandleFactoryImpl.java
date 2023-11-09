package nahara.common.reflective.impl;

import java.lang.reflect.Field;

import nahara.common.reflective.Handle;
import nahara.common.reflective.HandleFactory;

public class FieldHandleFactoryImpl<S, T> implements HandleFactory<S, T> {
	private Field field;

	public FieldHandleFactoryImpl(Field field) {
		this.field = field;
	}

	@Override
	public Handle<T> of(S obj) {
		return new FieldHandleImpl<>(this, obj);
	}

	public static class FieldHandleImpl<S, T> implements Handle<T> {
		private FieldHandleFactoryImpl<S, T> factory;
		private S obj;

		public FieldHandleImpl(FieldHandleFactoryImpl<S, T> factory, S obj) {
			this.factory = factory;
			this.obj = obj;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T get() {
			try {
				factory.field.setAccessible(true);
				T t = (T) factory.field.get(obj);
				factory.field.setAccessible(false);
				return t;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void set(T obj) {
			try {
				factory.field.setAccessible(true);
				factory.field.set(this.obj, obj);
				factory.field.setAccessible(false);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
