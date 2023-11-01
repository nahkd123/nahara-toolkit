package nahara.common.pipeline;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PipelineTest {
	@Test
	void testMapping() {
		var pipeline = PipelineBuilder.<String>create()
				.map(v -> Integer.parseInt(v))
				.map(v -> v + 1)
				.filter(v -> v != 0)
				.build();
		assertEquals(124, pipeline.firstNonnullOf("123").orElseThrow());
		assertTrue(pipeline.firstNonnullOf("-1").isEmpty());
	}
}
