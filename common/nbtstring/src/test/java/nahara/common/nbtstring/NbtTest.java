package nahara.common.nbtstring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NbtTest {
	@Test
	void testCreateChain() {
		var root = new NbtCompound();
		var displayName = root
				.getOrCreate("display", NbtCompound::new).compound()
				.getOrCreate("Name", NbtString::new).string();
		displayName.setText("SUS!");
		assertEquals("{display:{Name:\"SUS!\"}}", root.serializeAsString());
	}
}
