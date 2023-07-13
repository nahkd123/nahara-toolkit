package nahara.common.tasks;

import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;

import nahara.common.tasks.interfaces.TaskConsumer;
import nahara.common.tasks.interfaces.TaskFunction;
import nahara.common.tasks.interfaces.TaskSupplier;

/**
 * <p>Nahara's Task system. Please don't create infinite loop with this tasks system; it might prevent
 * other tasks from starting because your {@link ForkJoinPool} is full.</p>
 * @author nahkd
 *
 * @param <T>
 */
public interface Task<T> {
	public void onCompleted(Consumer<TaskResult<T>> callback);
	Optional<TaskResult<T>> get();

	/**
	 * <p>Block current thread to wait for the task to complete.</p>
	 * @return Value from this task.
	 * @throws InterruptedException If the thread is interrupted while waiting for result.
	 */
	default T await() throws InterruptedException {
		while (!Thread.currentThread().isInterrupted()) {
			var resultOptional = get();
			if (resultOptional.isPresent()) {
				var result = resultOptional.get();
				if (result.isSuccess()) return result.getSuccess();
				else throw new AsyncException(result.getFailure());
			} else {
				Thread.sleep(1); // Is this a good way to reduce CPU usage?
			}
		}

		throw new InterruptedException("Interrupted while waiting for task");
	}

	default <U> Task<U> afterThatDo(TaskFunction<T, U> func) {
		var newTask = new ManualTask<U>();
		onCompleted(result -> {
			try {
				if (result.isSuccess()) newTask.resolveSuccess(func.apply(result.getSuccess()));
				else newTask.resolveFailure(result.getFailure());
			} catch (Throwable t) {
				if (t instanceof AsyncException async) newTask.resolveFailure(async.getCause());
				else newTask.resolveFailure(t);
			}
		});
		return newTask;
	}

	default Task<Void> afterThatDo(TaskConsumer<T> consumer) {
		return afterThatDo(v -> {
			consumer.consume(v);
			return null;
		});
	}

	default <U> Task<U> andThen(Function<T, Task<U>> func) {
		var subtask = new ManualTask<U>();
		onCompleted(result -> {
			try {
				if (result.isSuccess()) {
					var newTask = func.apply(result.getSuccess());
					newTask.onCompleted(newResult -> subtask.resolve(newResult));
					return;
				} else {
					subtask.resolveFailure(result.getFailure());
				}
			} catch (Throwable t) {
				if (t instanceof AsyncException async) subtask.resolveFailure(async.getCause());
				else subtask.resolveFailure(t);
			}
		});
		return subtask;
	}

	public static <T> Task<T> resolved(T obj) {
		var task = new ManualTask<T>();
		task.resolveSuccess(obj);
		return task;
	}

	public static <T> Task<T> failed(Throwable t) {
		var task = new ManualTask<T>();
		task.resolveFailure(t);
		return task;
	}

	public static <T> Task<T> async(TaskSupplier<T> supplier, ForkJoinPool pool) {
		var task = new ManualTask<T>();
		pool.execute(() -> {
			try {
				task.resolveSuccess(supplier.get());
			} catch (Throwable t) {
				Task.failed(t);
			}
		});
		return task;
	}
}
