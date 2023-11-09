package nahara.common.reflective.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import nahara.common.reflective.HandleFactory;
import nahara.common.reflective.MethodFactory;
import nahara.common.reflective.Reflective;

public class ReflectiveImpl<S> implements Reflective<S> {
	private Class<S> clazz;

	public ReflectiveImpl(Class<S> clazz) {
		this.clazz = clazz;
	}

	@Override
	public <T> HandleFactory<S, T> getterSetter(Class<T> type, String[] getter, String[] setter) {
		Method mGetter = null, mSetter = null;
		for (Method method : clazz.getDeclaredMethods()) {
			if (!type.isAssignableFrom(method.getReturnType())) continue;

			if (mGetter == null && getter != null && getter.length > 0) {
				for (String n : getter) {
					if (method.getName().equals(n)) {
						mGetter = method;
						break;
					}
				}
			}

			if (mSetter == null && setter != null && setter.length > 0) {
				for (String n : setter) {
					if (method.getName().equals(n)) {
						mSetter = method;
						break;
					}
				}
			}

			if (
					(getter == null || getter.length == 0 || mGetter != null) &&
					(setter == null || setter.length == 0 || mSetter != null)) break;
		}

		if (getter != null && getter.length > 0 && mGetter == null) {
			throw new RuntimeException("Unable to find matching getter method in " + clazz + " (possible methods are " + String.join(", ", getter) + ")");
		}

		if (setter != null && setter.length > 0 && mSetter == null) {
			throw new RuntimeException("Unable to find matching setter method in " + clazz + " (possible methods are " + String.join(", ", setter) + ")");
		}

		return new MethodHandleFactoryImpl<>(mGetter, mSetter);
	}

	@Override
	public <T> MethodFactory<S, T> method(Class<T> returnType, Class<?>[] arguments, String... names) {
		outer: for (Method method : clazz.getDeclaredMethods()) {
			if (!returnType.isAssignableFrom(method.getReturnType())) continue;

			for (String n : names) {
				if (method.getName().equals(n)) {
					if (arguments != null) {
						Class<?>[] params = method.getParameterTypes();
						if (params.length != arguments.length) continue outer;

						for (int i = 0; i < arguments.length; i++) {
							Class<?> param = params[i];
							Class<?> arg = arguments[i];
							if (!param.isAssignableFrom(arg)) continue outer;
						}
					}

					return new MethodFactoryImpl<>(method, null);
				}
			}
		}

		throw new RuntimeException("Unable to find matching field in " + clazz + " (possible fields are " + String.join(", ", names) + ")");
	}

	@SuppressWarnings("unchecked")
	@Override
	public nahara.common.reflective.Method<S> constructor(Class<?>... arguments) {
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			Class<?>[] params = constructor.getParameterTypes();
			if (params.length != arguments.length) continue;

			for (int i = 0; i < arguments.length; i++) {
				Class<?> param = params[i];
				Class<?> arg = arguments[i];
				if (!param.isAssignableFrom(arg)) continue;
			}

			return new MethodFactoryImpl<S, S>(null, (Constructor<S>) constructor).ofStatic();
		}

		throw new RuntimeException("Unable to find matching constructor in " + clazz);
	}

	@Override
	public <T> HandleFactory<S, T> field(Class<T> type, String... names) {
		for (Field field : clazz.getDeclaredFields()) {
			if (!type.isAssignableFrom(field.getType())) continue;

			for (String n : names) {
				if (field.getName().equals(n)) return new FieldHandleFactoryImpl<>(field);
			}
		}

		throw new RuntimeException("Unable to find matching field in " + clazz + " (possible fields are " + String.join(", ", names) + ")");
	}
}
