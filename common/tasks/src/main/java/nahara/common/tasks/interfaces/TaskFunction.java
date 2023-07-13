package nahara.common.tasks.interfaces;

@FunctionalInterface
public interface TaskFunction<T, U> {
	public U apply(T obj) throws Throwable;
}
