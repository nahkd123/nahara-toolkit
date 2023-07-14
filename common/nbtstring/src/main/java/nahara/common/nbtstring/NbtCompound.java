package nahara.common.nbtstring;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NbtCompound extends Nbt {
	public final Map<String, Nbt> children = new HashMap<>();

	@Override
	public NbtCompound compound() {
		return this;
	}

	@Override
	public String serializeAsString() {
		var builder = new StringBuilder("{");
		boolean first = true;

		for (var e : children.entrySet()) {
			if (!first) builder.append(',');
			else first = false;
			builder.append(e.getKey()).append(':').append(e.getValue().serializeAsString());
		}

		return builder.append("}").toString();
	}

	@Override
	public NbtCompound copy() {
		var clone = new NbtCompound();
		keys().forEach(k -> clone.set(k, get(k).copy()));
		return clone;
	}

	public Nbt get(String key) {
		return children.get(key);
	}

	public void set(String key, Nbt child) {
		if (child == null) remove(key);
		else children.put(key, child);
	}

	public int size() {
		return children.size();
	}	

	public Set<String> keys() {
		return children.keySet();
	}

	public void clear() {
		children.clear();
	}

	public void remove(String key) {
		children.remove(key);
	}

	public Nbt getOrCreate(String key, Supplier<Nbt> creator) {
		var result = children.get(key);
		if (result == null) children.put(key, result = creator.get());
		return result;
	}

	private static final Pattern KEY_1 = Pattern.compile("^(?<key>\\w+)\\s*:\\s*");
	private static final Pattern KEY_2 = Pattern.compile("^([\\\"'])(?<key>\\w+)\\1\\s*:\\s*");

	public static NbtCompound deserialize(DeserializeContext ctx) {
		ctx.skipWhitespaces();
		if (ctx.next('{')) {
			ctx.skipWhitespaces();
			var out = new NbtCompound();

			while (!ctx.next('}')) {
				Matcher match;

				if ((match = ctx.next(KEY_1)) != null || (match = ctx.next(KEY_2)) != null) {
					var key = match.group("key");
					var value = Nbt.deserialize(ctx);
					if (value == null) ctx.invaild();

					out.set(key, value);
					ctx.skipWhitespaces();

					if (ctx.next(',')) {
						ctx.skipWhitespaces();
						continue;
					} else if (ctx.next('}')) {
						ctx.skipWhitespaces();
						return out;
					} else {
						ctx.invaild();
					}
				} else {
					ctx.invaild();
				}
			}

			return out;
		}

		return null;
	}
}
