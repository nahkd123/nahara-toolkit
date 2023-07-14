package nahara.common.localize;

public class Message {
	private static final String[] EMPTY = new String[0];
	private LocalizerProvider provider;
	private String key;
	private String def;

	public Message(LocalizerProvider provider, String key, String def) {
		this.provider = provider;
		this.key = key;
		this.def = def == null? key : def;
	}

	public Message(Localizer localizer, String key, String def) {
		this(() -> localizer, key, def);
	}

	public String of(String... replaces) {
		return Localizer.of(provider.getLocalizer(), key, def, replaces);
	}

	@Override
	public String toString() {
		return Localizer.of(provider.getLocalizer(), key, def, EMPTY);
	}
}
