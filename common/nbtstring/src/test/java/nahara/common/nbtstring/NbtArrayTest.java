package nahara.common.nbtstring;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

class NbtArrayTest {
	@Test
	void testSerialize() {
		assertEquals("[]", new NbtArray().serializeAsString());
		assertEquals("[\"abc\"]", ((Supplier<NbtArray>) () -> {
			var arr = new NbtArray();
			arr.getOrCreate(0, NbtString::new).string().setText("abc");
			return arr;
		}).get().serializeAsString());
		assertEquals("[\"abc\",\"the sus\"]", ((Supplier<NbtArray>) () -> {
			var arr = new NbtArray();
			arr.getOrCreate(0, NbtString::new).string().setText("abc");
			arr.getOrCreate(1, NbtString::new).string().setText("the sus");
			return arr;
		}).get().serializeAsString());
		assertEquals("[null,\"the sus\"]", ((Supplier<NbtArray>) () -> {
			var arr = new NbtArray();
			arr.getOrCreate(1, NbtString::new).string().setText("the sus");
			return arr;
		}).get().serializeAsString());
	}

	@Test
	void testDeserialize() {
		assertEquals(0, NbtArray.deserialize(new DeserializeContext("[]", 0)).size());
		assertEquals(1, NbtArray.deserialize(new DeserializeContext("[\"sus\"]", 0)).size());
		assertEquals(2, NbtArray.deserialize(new DeserializeContext("[\"sus\", \"sus2\"]", 0)).size());
		assertEquals(2, NbtArray.deserialize(new DeserializeContext("[\"sus\", \"sus2\",]", 0)).size());
		assertEquals(10, NbtArray.deserialize(new DeserializeContext("[\"sus\", \"sus2\", 9: \"sus10\"]", 0)).size());
		assertEquals(5, Nbt.fromString("[{}, [], \"\", [], {}]").array().size());
	}
}
