package nahara.common.localize;

import java.util.regex.Pattern;

import nahara.common.attachments.Attachments;

public interface Localizer {
	public String translate(String key, String defaultMessage);

	public static final Pattern PLACEHOLDER = Pattern.compile("\\{([0-9]*)\\}");

	public static Localizer getInstance() {
		return Attachments.getGlobal().get(Localizer.class, Localizer.class);
	}

	public static void setInstance(Localizer localizer) {
		Attachments.getGlobal().set(Localizer.class, Localizer.class, localizer);
	}

	public static String of(String key, String defaultMessage, String... replaces) {
		var i = getInstance();
		var text = i != null? i.translate(key, defaultMessage) : defaultMessage;

		var matcher = PLACEHOLDER.matcher(text);
		var current = new int[] { 0 };
		return matcher.replaceAll(t -> {
			var indexStr = t.group(1);

			if (indexStr == null || indexStr.length() == 0) {
				if (current[0] >= replaces.length) return "{}";
				return replaces[current[0]++].toString();
			} else {
				var index = Integer.parseInt(indexStr);
				if (index >= replaces.length) return "{" + index + "}";
				return replaces[index].toString();
			}
		});
	}

	public static String of(String key, String... replaces) {
		return of(key, key, replaces);
	}
}
