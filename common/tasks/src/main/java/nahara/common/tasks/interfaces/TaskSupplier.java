package nahara.common.tasks.interfaces;

@FunctionalInterface
public interface TaskSupplier<T> {
	public T get() throws Throwable;
}
