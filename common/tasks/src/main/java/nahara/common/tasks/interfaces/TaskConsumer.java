package nahara.common.tasks.interfaces;

@FunctionalInterface
public interface TaskConsumer<T> {
	public void consume(T obj) throws Throwable;
}
