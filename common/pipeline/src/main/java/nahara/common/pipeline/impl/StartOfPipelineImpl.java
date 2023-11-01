package nahara.common.pipeline.impl;

import java.util.Iterator;

import nahara.common.pipeline.Pipeline;

public class StartOfPipelineImpl<I> implements Pipeline<I, I> {
	@Override
	public Iterator<I> iteratorOf(Iterator<I> input) {
		return input;
	}
}
