package nahara.spigot.kit.files;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bukkit.plugin.Plugin;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import nahara.common.localize.Localizer;

public interface PluginFiles {
	public Plugin getPlugin();
	public Path path(String path);

	default public Localizer jsonTranslation(String resPath) {
		var from = path(resPath);
		if (Files.notExists(from)) {
			getPlugin().getLogger().warning("No translations in plugin resources: " + resPath);
			return key -> null;
		}

		try {
			var json = JsonParser.parseString(Files.readString(from, StandardCharsets.UTF_8));
			if (!json.isJsonObject()) {
				getPlugin().getLogger().warning("Translations must be JSON object. Skipping...");
				return key -> null;
			}

			var obj = json.getAsJsonObject();
			getPlugin().getLogger().info("Loaded translations from " + from.toAbsolutePath());

			return key -> {
				if (obj.has(key)) {
					var a = obj.get(key);
					return a.isJsonPrimitive()? a.getAsString() : null;
				} else {
					return null;
				}
			};
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			getPlugin().getLogger().warning("Failed to parse " + resPath + ": Malformed JSON?");
		} catch (IOException e) {
			e.printStackTrace();
			getPlugin().getLogger().warning("Failed to obtain translations " + resPath + ": IOException");
		}

		getPlugin().getLogger().warning("Failed to get translations for " + resPath);
		return key -> null;
	}
}
