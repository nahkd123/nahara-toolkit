package nahara.common.nbtstring;

public abstract class Nbt {
	public abstract String serializeAsString();
	public abstract Nbt copy();

	public NbtString string() { return new NbtString(toString()); }
	public NbtCompound compound() { return new NbtCompound(); }
	public NbtArray array() { return new NbtArray(); }
	public NbtNumber number() { return new NbtNumber(0); }

	public static Nbt deserialize(DeserializeContext ctx) {
		Nbt out;
		if ((out = NbtCompound.deserialize(ctx)) != null) return out;
		if ((out = NbtNumber.deserialize(ctx)) != null) return out;
		if ((out = NbtArray.deserialize(ctx)) != null) return out;
		if ((out = NbtString.deserialize(ctx)) != null) return out;
		return null;
	}

	public static Nbt fromString(String str) {
		return deserialize(new DeserializeContext(str, 0));
	}
}
