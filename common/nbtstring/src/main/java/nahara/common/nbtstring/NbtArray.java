package nahara.common.nbtstring;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class NbtArray extends Nbt {
	public final List<Nbt> children = new ArrayList<>();

	public Nbt get(int index) {
		if (index < children.size()) return children.get(index);
		return null;
	}

	public Nbt getOrCreate(int index, Supplier<Nbt> creator) {
		if (index >= children.size()) {
			// We assume this array contain elements of same type
			while (index >= children.size()) children.add(null);
		}

		var out = children.get(index);
		if (out == null) children.set(index, out = creator.get());
		return out;
	}

	public void set(int index, Nbt child) {
		if (index >= children.size()) {
			// We assume this array contain elements of same type
			while (index >= children.size()) children.add(null);
		}

		children.set(index, child);
	}

	public void clear() {
		children.clear();
	}

	public void add(Nbt child) {
		children.add(child);
	}

	public int size() {
		return children.size();
	}

	@Override
	public NbtArray array() {
		return this;
	}

	@Override
	public String serializeAsString() {
		var builder = new StringBuilder("[");
		boolean first = true;

		for (int i = 0; i < children.size(); i++) {
			var val = children.get(i);
			if (!first) builder.append(',');
			else first = false;

			if (val == null) {
				builder.append("null");
				continue;
			} else {
				builder.append(val.serializeAsString());
			}
		}

		return builder.append(']').toString();
	}

	@Override
	public NbtArray copy() {
		var out = new NbtArray();
		for (int i = 0; i < size(); i++) out.set(i, get(i));
		return out;
	}

	private static final Pattern INDEX = Pattern.compile("^(?<index>\\d+)\\s*:\\s*");

	public static NbtArray deserialize(DeserializeContext ctx) {
		ctx.skipWhitespaces();
		if (ctx.next('[')) {
			ctx.skipWhitespaces();

			var out = new NbtArray();
			var index = 0;

			while (!ctx.next(']')) {
				ctx.skipWhitespaces();

				var match = ctx.next(INDEX);
				if (match != null) index = Integer.parseInt(match.group("index"));

				var value = Nbt.deserialize(ctx);
				if (value == null) ctx.invaild();
				out.set(index, value);
				index++;

				ctx.skipWhitespaces();
				if (ctx.next(',')) {
					ctx.skipWhitespaces();
					continue;
				} else if (ctx.next(']')) {
					ctx.skipWhitespaces();
					return out;
				} else {
					ctx.invaild();
				}
			}

			return out;
		}

		return null;
	}
}
