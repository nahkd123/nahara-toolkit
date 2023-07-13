package nahara.common.nbtstring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NbtStringTest {
	@Test
	void testSerializeAsString() {
		var inpString = "{\"text\": \"Cool \\\\text!\", \"font\": \"minecraft:alt\"}";
		var nbt = new NbtString(inpString);
		assertEquals("\"{\\\"text\\\": \\\"Cool \\\\\\\\text!\\\", \\\"font\\\": \\\"minecraft:alt\\\"}\"", nbt.serializeAsString());
	}
}
