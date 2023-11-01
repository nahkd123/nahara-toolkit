package nahara.common.pipeline;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * <p>Represent a pipeline that takes in inputs and yield out outputs.</p>
 * <p>Pipelines can be used to filter and transform data, similar to {@link Stream}. I do not know why I made
 * this (well ok to be fair I think reusing pipeline transformation could reduce memory usage), but it could
 * be useful for someone. Only time will tell I guess?</p>
 * @param <I> Input type.
 * @param <O> Output type.
 */
public interface Pipeline<I, O> {
	public Iterator<O> iteratorOf(Iterator<I> input);

	default Iterator<O> iteratorOf(I input) {
		return iteratorOf(Collections.singleton(input).iterator());
	}

	default Iterator<O> iteratorOf(Iterable<I> input) {
		return iteratorOf(input.iterator());
	}

	default Optional<O> firstNonnullOf(Iterator<I> input) {
		Iterator<O> iter = iteratorOf(input);

		while (iter.hasNext()) {
			O obj = iter.next();
			if (obj == null) continue;
			return Optional.of(obj);
		}

		return Optional.empty();
	}

	default Optional<O> firstNonnullOf(I input) {
		return firstNonnullOf(Collections.singleton(input).iterator());
	}
}
