package nahara.common.pipeline.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

import nahara.common.pipeline.Pipeline;

public class MapPipelineImpl<I, M, O> implements Pipeline<I, O> {
	private Pipeline<I, M> previous;
	private Function<M, O> mapper;

	public MapPipelineImpl(Pipeline<I, M> previous, Function<M, O> mapper) {
		this.previous = previous;
		this.mapper = mapper;
	}

	@Override
	public Iterator<O> iteratorOf(Iterator<I> input) {
		return new Iterator<O>() {
			private O nextObj = null;
			private boolean holdingNextObj = false, isEnded = false;
			private Iterator<M> prevIter = previous.iteratorOf(input);

			@Override
			public boolean hasNext() {
				if (isEnded) return false;
				if (!holdingNextObj) {
					if (!prevIter.hasNext()) {
						isEnded = true;
						return false;
					}

					holdingNextObj = true;
					nextObj = mapper.apply(prevIter.next());
					return true;
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
