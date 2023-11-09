package nahara.common.reflective;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * <p>Handles allows you to get/set objects using either field accessing or getter and setter.</p>
 * @param <T> The type.
 */
public interface Handle<T> {
	public T get();
	public void set(T obj);

	default void apply(Consumer<Handle<T>> applier) {
		applier.accept(this);
	}

	default void map(UnaryOperator<T> mapper) {
		set(mapper.apply(get()));
	}
}
