package nahara.common.nbtstring;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeserializeContext {
	public final String input;
	public int pos;

	public DeserializeContext(String input, int pos) {
		this.input = input;
		this.pos = pos;
	}

	public boolean endOfInput() {
		return pos >= input.length();
	}

	public Matcher next(Pattern pattern) {
		var matcher = pattern.matcher(input).region(pos, input.length());
		if (matcher.find()) {
			pos = matcher.end();
			return matcher;
		}
	
		return null;
	}

	public boolean next(char ch) {
		if (endOfInput()) throw new IllegalStateException("End of input reached");
		if (input.charAt(pos) == ch) {
			pos++;
			return true;
		}

		return false;
	}

	public char nextChar() {
		if (endOfInput()) throw new IllegalStateException("End of input reached");
		return input.charAt(pos++);
	}

	public void invaild() {
		System.out.println("'" + input.substring(pos));
		throw new IllegalArgumentException("Invaild input at position " + pos + ": " + input.substring(pos, Math.min(pos + 12, input.length())));
	}

	private static final Pattern WHITESPACES = Pattern.compile("^\\s*");

	public void skipWhitespaces() {
		next(WHITESPACES);
	}
}
