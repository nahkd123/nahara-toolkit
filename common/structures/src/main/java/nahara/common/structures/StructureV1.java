package nahara.common.structures;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureV1<S> implements Structure<S> {
	public static final int VERSION = 0;
	public static final int VERSION_HEADER = (Structure.RAW_HEADER << 8) | VERSION;

	private int width, height, depth;
	private int[] shape;
	private Map<String, Integer> stateToId = new HashMap<>();
	private List<String> idToState = new ArrayList<>();
	private StatesFactory<S> factory;

	public StructureV1(int width, int height, int depth, StatesFactory<S> factory) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.shape = new int[width * height * depth];
		this.factory = factory;
		getId(null);
	}

	@Override public int getWidth() { return width; }
	@Override public int getHeight() { return height; }
	@Override public int getDepth() { return depth; }

	private int getId(String state) {
		var id = stateToId.get(state);
		if (id == null) {
			stateToId.put(state, id = idToState.size());
			idToState.add(state);
		}
		return id;
	}

	private int index(int x, int y, int z) {
		return (y * width * depth) + (z * width) + x;
	}

	@Override
	public void set(int x, int y, int z, S state) {
		var ss = state == null? null : factory.stateToString(state);
		var id = getId(ss);
		shape[index(x, y, z)] = id;
	}

	@Override
	public S get(int x, int y, int z) {
		var id = shape[index(x, y, z)];
		var ss = id < idToState.size()? idToState.get(id) : null;
		return ss == null? null : factory.stringToState(ss);
	}

	@Override
	public void serialize(OutputStream stream) throws IOException {
		var out = new DataOutputStream(stream);
		out.writeInt(VERSION_HEADER);

		// Shape size
		writeVarInt(width, out);
		writeVarInt(height, out);
		writeVarInt(depth, out);

		// Mapping table
		writeVarInt(idToState.size(), out);
		for (var ss : idToState) {
			if (ss == null) ss = "";
			out.writeUTF(ss);
		}

		// Shape
		for (int i = 0; i < shape.length; i++) writeVarInt(shape[i], out);
	}

	public static <S> StructureV1<S> deserialize(InputStream stream, StatesFactory<S> factory) throws IOException {
		var in = new DataInputStream(stream);

		// Shape size
		var width = readVarInt(in);
		var height = readVarInt(in);
		var depth = readVarInt(in);
		var struct = new StructureV1<>(width, height, depth, factory);

		// Mapping table
		var idsCount = readVarInt(in);
		for (int i = 0; i < idsCount; i++) {
			var ss = in.readUTF();
			if (ss.length() == 0 || i == 0) continue;
			struct.getId(ss);
		}

		// Shape
		for (int i = 0; i < struct.shape.length; i++) struct.shape[i] = readVarInt(in);
		return struct;
	}

	public static void writeVarInt(int val, DataOutput out) throws IOException {
		int v;
		do {
			v = val & 0x7F;
			val >>= 7;
			out.write(v | ((val != 0)? 0x80 : 0));
		} while (val != 0);
	}

	public static int readVarInt(DataInput in) throws IOException {
		int val = 0, v, shift = 0;
		do {
			v = Byte.toUnsignedInt(in.readByte());
			val |= (v & 0x7F) << shift;
			shift += 7;
		} while ((v & 0x80) != 0);
		return val;
	}
}
