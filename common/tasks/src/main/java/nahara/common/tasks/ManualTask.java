package nahara.common.tasks;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class ManualTask<T> implements Task<T> {
	private TaskResult<T> result = null;
	private Queue<Consumer<TaskResult<T>>> callbacksQueue = new ConcurrentLinkedQueue<>();

	@Override
	public void onCompleted(Consumer<TaskResult<T>> callback) {
		if (callbacksQueue == null) {
			callback.accept(result);
			return;
		}

		callbacksQueue.add(callback);
	}

	@Override
	public Optional<TaskResult<T>> get() {
		return Optional.ofNullable(result);
	}

	/**
	 * <p>Set result for this task. If this task already have result, this method will do nothing.</p>
	 * @param result Result to set.
	 */
	public void resolve(TaskResult<T> result) {
		if (this.result != null) return;
		while (!callbacksQueue.isEmpty()) callbacksQueue.poll().accept(result);
		this.callbacksQueue = null;
		this.result = result;
	}

	public void resolveSuccess(T result) {
		resolve(TaskResult.success(result));
	}

	public void resolveFailure(Throwable failure) {
		resolve(TaskResult.failed(failure));
	}
}
