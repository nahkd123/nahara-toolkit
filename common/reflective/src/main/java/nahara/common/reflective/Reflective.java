package nahara.common.reflective;

import java.lang.reflect.AccessibleObject;

import nahara.common.reflective.impl.ReflectiveImpl;

/**
 * <p><i>"Once upon a time, there was a small pond in a middle of the forest. The pond water is said to be
 * precious and shiny, which means it is highly <b>reflective</b>. Nahara saw the pond, got extremely
 * curious, then decided to get closer to it.</i></p>
 * <p><i>Nahara stands still, staring at her reflection on the watery surface. "What is this all about?",
 * "Why is the {@code PI} constant of universe becomes 4.0?", "Am I living in a simulation?", "Am I living
 * in an illusion?", "Is this a dream?". Nahara begins questioning her existence as she is about to perform
 * an act that no ones could have guessed: It's Java reflectin' time!"</i></p>
 *
 * <p>This is Nahara's Toolkit for Reflections, a simple tool for your toolbox so that you can perform
 * Java Reflection operations without dealing with nasty exceptions (I'm looking at you,
 * {@link ReflectiveOperationException}). While the main point of this is to avoid catching exceptions,
 * {@link Reflective} contains methods that helps you shorten the time it takes to use reflection.</p>
 * <p>First, some methods allows you to feed in multiple possible names for a field or method. This is
 * to ensure no magic will happens in the production environment, especially in Fabric modding, where dev
 * environment may have remapped names, but in prouction, it becomes "method_abcdef".</p>
 * <p>Second, some methods allows you to pick the only method with matching signature. There can be always
 * more than 1 constructor or method with same name, but method signatures can't be the same.</p>
 * <p>Third, all {@link Handle}s and {@link Method}s will calls {@link AccessibleObject#setAccessible(boolean)}
 * everytime you access the fields or methods. You don't have to set its accessibility everytime you need
 * to, well, access them.</p>
 *
 * <p>Now that I've done talkin' about {@link Reflective}, let's talk about "Where did the person named 'Nahara'
 * comes from?". It might seems weird that I'm talking to you using Javadocs, but I kinda want to make this
 * as a little "easter egg".</p>
 * <p>To say "it comes from my dream" wouldn't be entirely correct, but the whole idea is: every person should have
 * someone to help with their job. But because you can't see Nahara in person (she isn't real), she decided to mail
 * her toolbox to you, with the hope of improving your productivity. That's why "Nahara's Toolkit" was created: to
 * help me write Java code faster and more enjoyable. And what is the result of enjoying? Being productive!</p>
 * <p>I don't expect this little dumb repository gets some attentions, but if you are using Nahara's toolkit in your
 * project, consider making an "issue" in the Issues tab of my GitHub special repository for profile card. Nahara
 * would likes to know her toolkit is actually useful!</p>
 *
 * @see #constructor(Class...)
 * @see #field(Class, String...)
 * @see #method(Class, Class[], String...)
 * @see #getterSetter(Class, String[], String[])
 */
public interface Reflective<S> {
	/**
	 * <p>Combine getter and setter into a single handle.</p>
	 * @param type The output type.
	 * @param getter All possible names for getter. Nahara will choose the method that is appeared first
	 * in this array that also present in the class.
	 * @param setter All possible names for setter. Nahara will choose the method that is appeared first
	 * in this array that also present in the class.
	 * @return The handle.
	 */
	public <T> HandleFactory<S, T> getterSetter(Class<T> type, String[] getter, String[] setter);

	/**
	 * <p>Get a field as a handle.</p>
	 * @param type The output type.
	 * @param names All possible names for the field.
	 * @return The handle.
	 */
	public <T> HandleFactory<S, T> field(Class<T> type, String... names);

	/**
	 * <p>Find a method with specified name(s).</p>
	 * @param returnType The return type of method.
	 * @param arguments Arguments to check, or {@code null} to ignore checking arguments.
	 * @param names All possible names for the method.
	 * @return The method.
	 */
	public <T> MethodFactory<S, T> method(Class<T> returnType, Class<?>[] arguments, String... names);

	/**
	 * <p>Find a constructor with given types for all arguments.</p>
	 * @param arguments Arguments to check.
	 * @return The constructor described as a method.
	 */
	public Method<S> constructor(Class<?>... arguments);

	/**
	 * <p>Obtain a {@link Reflective} instance of a class.</p>
	 * @param clazz The class.
	 * @return The {@link Reflective} instance.
	 */
	public static <S> Reflective<S> of(Class<S> clazz) {
		return new ReflectiveImpl<>(clazz);
	}

	public static Reflective<?> of(String className) {
		try {
			return of(Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException();
		}
	}

	public static String[] names(String... names) {
		return names;
	}

	public static Class<?>[] args(Class<?>... args) {
		return args;
	}
}
