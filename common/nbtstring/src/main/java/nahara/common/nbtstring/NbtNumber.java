package nahara.common.nbtstring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class NbtNumber extends Nbt {
	private Number number;

	public NbtNumber(Number number) {
		this.number = number;
	}

	public NbtNumber() {
		number = null;
	}

	public Number getNumber() {
		return number;
	}

	public void setNumber(Number number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return serializeAsString();
	}

	private static final Map<Class<?>, Character> CLASS_TO_SUFFIX = Map.of(
			Byte.class, 'b',
			Short.class, 's',
			Integer.class, 'i',
			Long.class, 'l',
			Float.class, 'f',
			Double.class, 'd'
			);
	private static final Map<Character, Class<?>> SUFFIX_TO_CLASS = ((Supplier<Map<Character, Class<?>>>) () -> {
		var map = new HashMap<Character, Class<?>>();
		CLASS_TO_SUFFIX.forEach((cls, ch) -> {
			map.put(ch, cls);
			map.put(Character.toUpperCase(ch), cls);
		});
		return Collections.unmodifiableMap(map);
	}).get();
	private static final Map<Class<?>, Function<String, Number>> CLASS_TO_NUMBER = Map.of(
			Byte.class, Byte::parseByte,
			Short.class, Short::parseShort,
			Integer.class, Integer::parseInt,
			Long.class, Long::parseLong,
			Float.class, Float::parseFloat,
			Double.class, Double::parseDouble
			);

	@Override
	public String serializeAsString() {
		return getNumber().toString() + CLASS_TO_SUFFIX.get(getNumber().getClass());
	}

	@Override
	public NbtNumber copy() {
		return new NbtNumber(getNumber());
	}

	private static final Pattern NUMBER = Pattern.compile("^(?<integer>\\d+)(\\.(?<floating>\\d+))?(?<suffix>[bsildfBSILDF])?");

	public static NbtNumber deserialize(DeserializeContext ctx) {
		ctx.skipWhitespaces();
		var match = ctx.next(NUMBER);
		if (match != null) {
			var intStr = match.group("integer");
			var floatingStr = match.group("floating");
			var suffix = match.group("suffix");

			var possibleClasses = new ArrayList<Class<?>>();
			possibleClasses.add(Integer.class);
			possibleClasses.add(Byte.class);
			possibleClasses.add(Short.class);
			possibleClasses.add(Long.class);
			possibleClasses.add(Float.class);
			possibleClasses.add(Double.class);

			if (floatingStr == null) {
				var parsed = Long.parseLong(intStr);

				if (parsed > Integer.MAX_VALUE || parsed < Integer.MIN_VALUE) possibleClasses.remove(Integer.class);
				if (parsed > Short.MAX_VALUE || parsed < Short.MIN_VALUE) possibleClasses.remove(Short.class);
				if (parsed > Byte.MAX_VALUE || parsed < Byte.MIN_VALUE) possibleClasses.remove(Byte.class);
			} else {
				possibleClasses.remove(Byte.class);
				possibleClasses.remove(Short.class);
				possibleClasses.remove(Integer.class);
				possibleClasses.remove(Long.class);
			}

			var targetType = suffix == null? possibleClasses.get(0) : SUFFIX_TO_CLASS.get(suffix.charAt(0));
			if (!possibleClasses.contains(targetType)) throw new IllegalArgumentException("Invaild input at position " + ctx.pos + ": Type is not suitable");
			var val = intStr + (floatingStr != null? ("." + floatingStr) : "");
			return new NbtNumber(CLASS_TO_NUMBER.get(targetType).apply(val));
		}

		return null;
	}
}
