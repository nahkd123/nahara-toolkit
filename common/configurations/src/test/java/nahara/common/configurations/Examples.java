package nahara.common.configurations;

public class Examples {
	public static void configExample() {
		var config = new Config();

		var childKey = config.firstChild("childKey")
				.flatMap(v -> v.getValue(Integer::parseInt))
				.orElse(420); // Get child value with default value
	}
}
