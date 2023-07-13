package nahara.common.nbtstring;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NbtString extends Nbt {
	private String text;

	public NbtString(String text) {
		this.text = text;
	}

	public NbtString() {
		this.text = null;
	}

	public String getText() {
		return text != null? text : "";
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public NbtString string() {
		return this;
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public String serializeAsString() {
		return '"' + getText().replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"") + '"';
	}

	@Override
	public NbtString copy() {
		return new NbtString(getText());
	}

	private static final Pattern STRING_START = Pattern.compile("^[\"']");
	private static final Pattern QUOTELESS_STRING = Pattern.compile("^[^}\\],]+");

	public static NbtString deserialize(DeserializeContext ctx) {
		ctx.skipWhitespaces();
		Matcher match;

		if ((match = ctx.next(STRING_START)) != null) {
			var str = "";
			var escaping = false;
			var ending = match.group().charAt(0);

			while (true) {
				if (escaping) {
					escaping = false;
					str += ctx.nextChar();
					continue;
				}

				if (ctx.next('\\')) {
					if (!escaping) {
						escaping = true;
						continue;
					}
				}

				if (ctx.next(ending)) return new NbtString(str);
				str += ctx.nextChar();
			}
		} else if ((match = ctx.next(QUOTELESS_STRING)) != null) {
			return new NbtString(match.group());
		}

		return null;
	}
}
