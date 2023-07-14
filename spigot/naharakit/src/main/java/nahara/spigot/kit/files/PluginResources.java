package nahara.spigot.kit.files;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

import org.bukkit.plugin.Plugin;

public class PluginResources implements PluginFiles {
	private Plugin plugin;
	private ClassLoader loader;

	public PluginResources(Plugin plugin, ClassLoader loader) {
		this.plugin = plugin;
		this.loader = loader;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	@Override
	public Path path(String resPath) {
		try {
			var uri = loader.getResource(resPath).toURI();

			if (uri.getScheme().equals("jar")) {
				// We are loading resources from JAR file
				var provider = FileSystemProvider.installedProviders().stream().filter(v -> v.getScheme().equalsIgnoreCase("jar")).findFirst();
				if (provider.isPresent()) {
					try {
						provider.get().getFileSystem(uri);
					} catch (FileSystemNotFoundException e) {
						try {
							provider.get().newFileSystem(uri, Collections.emptyMap());
						} catch (IOException e2) {
							throw new RuntimeException(e2);
						}
					}
				}
			}

			return Paths.get(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void save(String resPath, boolean override) {
		var from = path(resPath);
		var target = new File(plugin.getDataFolder(), resPath).toPath();

		if (!override && Files.exists(target)) return;
		if (Files.notExists(target.resolve(".."))) {
			try {
				Files.createDirectories(target.resolve(".."));
			} catch (IOException e) {
				e.printStackTrace();
				plugin.getLogger().warning("Failed to create parent folder: '" + resPath + "': IOException");
			}
		}

		try {
			Files.copy(from, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			plugin.getLogger().warning("Failed to copy resource to plugin data folder: '" + resPath + "': IOException");
		}
	}
}
