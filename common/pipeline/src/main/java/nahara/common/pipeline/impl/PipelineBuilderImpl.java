package nahara.common.pipeline.impl;

import java.util.function.Function;
import java.util.function.Predicate;

import nahara.common.pipeline.Pipeline;
import nahara.common.pipeline.PipelineBuilder;

public class PipelineBuilderImpl<I, O> implements PipelineBuilder<I, O> {
	private Pipeline<I, O> current;

	public PipelineBuilderImpl(Pipeline<I, O> current) {
		this.current = current;
	}

	@Override
	public PipelineBuilder<I, O> filter(Predicate<O> predicate) {
		current = new FilterPipelineImpl<>(current, predicate);
		return this;
	}

	@Override
	public <M> PipelineBuilder<I, M> map(Function<O, M> mapper) {
		return new PipelineBuilderImpl<>(new MapPipelineImpl<>(current, mapper));
	}

	@Override
	public Pipeline<I, O> build() {
		return current;
	}
}
