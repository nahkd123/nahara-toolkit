package nahara.common.nbtstring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NbtCompoundTest {
	@Test
	void testDeserialize() {
		assertEquals(0, NbtCompound.deserialize(new DeserializeContext("{}", 0)).size());
		assertEquals(1, NbtCompound.deserialize(new DeserializeContext("{Key:'value'}", 0)).size());
		assertEquals(2, NbtCompound.deserialize(new DeserializeContext("{K1:'value1',K2:'value2'}", 0)).size());

		var nested = NbtCompound.deserialize(new DeserializeContext("{1K:'value1',2K:{Key:'value'}}", 0));
		assertNotNull(nested.get("2K").compound());

		assertEquals("{Key:\"value\"}", Nbt.fromString("{Key:\"value\",}").serializeAsString()); // Trailing comma
		assertEquals("{Key:\"value:value\"}", Nbt.fromString("{Key:value:value,}").serializeAsString());
		assertEquals("{Damage:1i}", Nbt.fromString("{Damage:1}").serializeAsString());
		assertEquals("{Damage:1b}", Nbt.fromString("{Damage:1b}").serializeAsString());
	}

	@Test
	void testSerialize() {
		assertEquals("{}", new NbtCompound().serializeAsString());

		var root = new NbtCompound();
		root
				.getOrCreate("Key", NbtCompound::new).compound()
				.getOrCreate("Value", NbtString::new).string()
				.setText("Very sus!");
		assertEquals("{Key:{Value:\"Very sus!\"}}", root.serializeAsString());
	}
}
