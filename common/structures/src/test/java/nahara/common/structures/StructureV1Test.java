package nahara.common.structures;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class StructureV1Test {
	@Test
	void testVarInt() throws IOException {
		var ba = new ByteArrayOutputStream();
		var out = new DataOutputStream(ba);
		StructureV1.writeVarInt(42069, out);
		var in = new DataInputStream(new ByteArrayInputStream(ba.toByteArray()));
		assertEquals(42069, StructureV1.readVarInt(in));
	}

	@Test
	void testSerialization() throws IOException {
		var ba = new ByteArrayOutputStream();
		var out = new DataOutputStream(ba);
		var factory = new StatesFactory<String>() {
			@Override
			public String stateToString(String state) {
				return state;
			}

			@Override
			public String stringToState(String str) {
				return str;
			}
		};
		var struct = new StructureV1<>(32, 32, 32, factory);
		struct.set(8, 16, 24, "minecraft:diamond_block");
		struct.serialize(out);

		var in = new DataInputStream(new ByteArrayInputStream(ba.toByteArray()));
		assertEquals(StructureV1.VERSION_HEADER, in.readInt());
		struct = StructureV1.deserialize(in, factory);
		assertNull(struct.get(7, 16, 24));
		assertNull(struct.get(8, 15, 24));
		assertNull(struct.get(8, 16, 23));
		assertEquals("minecraft:diamond_block", struct.get(8, 16, 24));
	}
}
