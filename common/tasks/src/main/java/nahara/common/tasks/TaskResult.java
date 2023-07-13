package nahara.common.tasks;

public class TaskResult<T> {
	private T success;
	private Throwable failed;

	private TaskResult(T success, Throwable failed) {
		this.success = success;
		this.failed = failed;
	}

	public static <R> TaskResult<R> success(R val) {
		return new TaskResult<>(val, null);
	}

	public static <R> TaskResult<R> failed(Throwable t) {
		return new TaskResult<R>(null, t);
	}

	public boolean isSuccess() {
		return failed == null;
	}

	public T getSuccess() {
		return success;
	}

	public Throwable getFailure() {
		return failed;
	}
}
