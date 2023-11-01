package nahara.common.pipeline;

import java.util.function.Function;
import java.util.function.Predicate;

import nahara.common.pipeline.impl.PipelineBuilderImpl;
import nahara.common.pipeline.impl.StartOfPipelineImpl;

public interface PipelineBuilder<I, O> {
	public PipelineBuilder<I, O> filter(Predicate<O> predicate);
	public <M> PipelineBuilder<I, M> map(Function<O, M> mapper);

	// Create and build
	public Pipeline<I, O> build();

	public static <T> PipelineBuilder<T, T> create() {
		return new PipelineBuilderImpl<>(new StartOfPipelineImpl<>());
	}
}
