package nahara.common.configurations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * <p>Configuration thing, with syntax similar to YAML but without ':' symbols, comments must have double
 * slashes prefix and any element can have children.</p>
 */
public class Config {
	private static final String ROOT_KEY = "// root";
	private int indentLevel;
	private String key;
	private String value;
	private List<Config> children = new ArrayList<>();

	protected Config(int indentLevel, String key, String value) {
		this.indentLevel = indentLevel;
		this.key = key;
		this.value = value;
	}

	public Config(String key, String value) {
		this(-1, key, value);
	}

	public Config(String key) {
		this(-1, key, null);
	}

	/**
	 * <p>Create root configuration. Root configuration can't be added as a child to another
	 * configuration.</p>
	 */
	public Config() {
		this(-1, ROOT_KEY, null);
	}

	protected int getIndentLevel() {
		return indentLevel;
	}

	public String getKey() {
		return key;
	}

	public Optional<String> getValue() {
		return Optional.ofNullable(value);
	}

	/**
	 * <p>Get value with parsing function in the middle. If parser throws {@link IllegalArgumentException},
	 * the returned value will be empty.</p>
	 * @param <T> Return type.
	 * @param parser Parser to parse value.
	 * @return Value with parser applied.
	 */
	public <T> Optional<T> getValue(Function<String, T> parser) {
		return getValue().map(v -> {
			try {
				return parser.apply(v);
			} catch (IllegalArgumentException e) {
				return null;
			}
		});
	}

	public List<Config> getChildren() {
		return children;
	}

	public Config addChild(Config child) {
		if (child.getKey().equals(ROOT_KEY)) throw new IllegalArgumentException("Can't add root to config");
		children.add(child);
		return this;
	}

	public Config addChild(String key, String value) {
		return addChild(new Config(key, value));
	}

	public Config addChild(String key) {
		return addChild(key, null);
	}

	public Stream<Config> children(String key) {
		return children.stream().filter(v -> v.getKey().equals(key));
	}

	public Optional<Config> firstChild(String key) {
		return children(key).findFirst();
	}

	private void writeTo(Writer writer, String indent) throws IOException {
		if (key.equals(ROOT_KEY)) {
			for (var child : children) child.writeTo(writer, indent);
			return;
		}

		writer.append(indent).append(key);
		if (value != null) writer.append(' ').append(value);
		writer.append('\n');

		for (var child : children) child.writeTo(writer, indent + "  ");
	}

	public void writeTo(Writer writer) throws IOException {
		writeTo(writer, "");
	}

	public String getAsString() {
		try {
			var writer = new StringWriter();
			writeTo(writer);
			return writer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return toString();
		}
	}

	@Override
	public String toString() {
		return key + (value != null? (" " + value) : "") + (children.size() > 0? (" (" + children.size() + " children)") : "");
	}

	// Parsing
	private static final Pattern ELEMENT_HEAD = Pattern.compile("^(\\s*)([^\\s]+)\\s+(.+)$");
	private static final Pattern ELEMENT_HEAD_VALUELESS = Pattern.compile("^(\\s*)([^\\s]+)\\s*$");

	public static Config parseConfig(Supplier<String> nextLine) {
		var root = new Config();
		var stack = new Stack<Config>();
		Config element;
		stack.add(root);

		while ((element = nextElement(nextLine)) != null) {
			if (stack.lastElement().indentLevel < element.indentLevel) {
				stack.lastElement().children.add(element);
			} else if (stack.lastElement().indentLevel == element.indentLevel) {
				stack.pop();
				stack.lastElement().children.add(element);
			} else {
				while ((stack.lastElement().indentLevel >= element.indentLevel)) stack.pop();
				stack.lastElement().children.add(element);
			}

			stack.push(element);
		}

		return root;
	}

	private static Config nextElement(Supplier<String> nextLine) {
		String line;
		Matcher matcher;

		while ((line = nextLine.get()) != null) {
			if (line.trim().startsWith("//")) continue;

			if ((matcher = ELEMENT_HEAD.matcher(line)).matches()) {
				var indent = matcher.group(1);
				var key = matcher.group(2);
				var value = matcher.group(3);

				if (value.endsWith(" \\")) {
					value = value.substring(0, value.length() - 2);

					while ((line = nextLine.get()) != null && line.endsWith(" \\")) {
						value += line.substring(0, line.length() - 2);
					}

					value += line;
				}

				return new Config(indent.length(), key, value); // TODO calculate tab size?
			}

			if ((matcher = ELEMENT_HEAD_VALUELESS.matcher(line)).matches()) {
				var indent = matcher.group(1);
				var key = matcher.group(2);
				return new Config(indent.length(), key, null);
			}
		}

		return null;
	}

	public static Config parseConfig(Iterator<String> iterator) {
		return parseConfig(() -> iterator.hasNext()? iterator.next() : null);
	}

	public static Config parseConfig(Iterable<String> iterable) {
		return parseConfig(iterable.iterator());
	}

	public static Config parseConfig(Scanner scanner) {
		return parseConfig(() -> {
			if (scanner.hasNextLine()) return scanner.nextLine();
			return null;
		});
	}

	public static Config parseConfig(InputStream stream) throws IOException {
		return parseConfig(new Scanner(stream));
	}

	public static Config parseConfig(Path pathToFile) throws IOException {
		if (!Files.isRegularFile(pathToFile)) return new Config();
		return parseConfig(Files.newInputStream(pathToFile));
	}

	public static Config parseConfig(File file) throws IOException {
		if (!file.exists()) return new Config();
		return parseConfig(file.toPath());
	}
}
