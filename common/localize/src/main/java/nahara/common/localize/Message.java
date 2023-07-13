package nahara.common.localize;

public class Message {
	private static final String[] EMPTY = new String[0];

	private String key;
	private String def;

	public Message(String key, String def) {
		this.key = key;
		this.def = def == null? key : def;
	}

	public String of(String... replaces) {
		return Localizer.of(key, def, replaces);
	}

	@Override
	public String toString() {
		return Localizer.of(key, def, EMPTY);
	}
}
