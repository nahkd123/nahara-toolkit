package nahara.common.pipeline.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import nahara.common.pipeline.Pipeline;

public class FilterPipelineImpl<I, O> implements Pipeline<I, O> {
	private Pipeline<I, O> previous;
	private Predicate<O> predicate;

	public FilterPipelineImpl(Pipeline<I, O> previous, Predicate<O> predicate) {
		this.previous = previous;
		this.predicate = predicate;
	}

	@Override
	public Iterator<O> iteratorOf(Iterator<I> input) {
		return new Iterator<O>() {
			private O nextObj = null;
			private boolean holdingNextObj = false, isEnded = false;
			private Iterator<O> prevIter = previous.iteratorOf(input);

			@Override
			public boolean hasNext() {
				if (isEnded) return false;
				if (!holdingNextObj) {
					while (prevIter.hasNext()) {
						O obj = prevIter.next();
						if (!predicate.test(obj)) continue;

						holdingNextObj = true;
						nextObj = obj;
						return true;
					}

					isEnded = true;
					return false;
				}

				return true;
			}

			@Override
			public O next() {
				if (!holdingNextObj) {
					if (isEnded || !hasNext()) throw new NoSuchElementException();
				}

				holdingNextObj = false;
				return nextObj;
			}
		};
	}
}
