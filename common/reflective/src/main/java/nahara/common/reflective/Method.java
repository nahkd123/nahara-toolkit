package nahara.common.reflective;

public interface Method<T> {
	public T invoke(Object... args);
}
