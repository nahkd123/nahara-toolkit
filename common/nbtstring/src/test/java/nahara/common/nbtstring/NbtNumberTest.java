package nahara.common.nbtstring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NbtNumberTest {
	@Test
	void testSerialize() {
		assertEquals("-128b", new NbtNumber((byte) -128).serializeAsString());
		assertEquals("127b", new NbtNumber((byte) 127).serializeAsString());
		assertEquals("127s", new NbtNumber((short) 127).serializeAsString());
		assertEquals("1337i", new NbtNumber((int) 1337).serializeAsString());
		assertEquals("1337l", new NbtNumber((long) 1337).serializeAsString());
		assertEquals("1337.0f", new NbtNumber((float) 1337).serializeAsString());
		assertEquals("1337.0d", new NbtNumber((double) 1337).serializeAsString());
	}

	@Test
	void testDeserialize() {
		assertEquals(Byte.class, NbtNumber.deserialize(new DeserializeContext("1b", 0)).getNumber().getClass());
		assertEquals(Short.class, NbtNumber.deserialize(new DeserializeContext("1s", 0)).getNumber().getClass());
		assertEquals(Integer.class, NbtNumber.deserialize(new DeserializeContext("1i", 0)).getNumber().getClass());
		assertEquals(Long.class, NbtNumber.deserialize(new DeserializeContext("1l", 0)).getNumber().getClass());
		assertEquals(Float.class, NbtNumber.deserialize(new DeserializeContext("1f", 0)).getNumber().getClass());
		assertEquals(Double.class, NbtNumber.deserialize(new DeserializeContext("1d", 0)).getNumber().getClass());

		assertEquals(Integer.class, NbtNumber.deserialize(new DeserializeContext("1", 0)).getNumber().getClass());
		assertEquals(Long.class, NbtNumber.deserialize(new DeserializeContext("" + Integer.MAX_VALUE + 1L, 0)).getNumber().getClass());
		assertEquals(Float.class, NbtNumber.deserialize(new DeserializeContext("1.2", 0)).getNumber().getClass());
	}
}
