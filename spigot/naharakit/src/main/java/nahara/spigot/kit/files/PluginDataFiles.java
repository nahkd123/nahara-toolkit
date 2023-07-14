package nahara.spigot.kit.files;

import java.io.File;
import java.nio.file.Path;

import org.bukkit.plugin.Plugin;

public class PluginDataFiles implements PluginFiles {
	private Plugin plugin;
	private File root;

	public PluginDataFiles(Plugin plugin, File root) {
		this.plugin = plugin;
		this.root = root;
	}

	@Override
	public Plugin getPlugin() {
		return plugin;
	}

	@Override
	public Path path(String path) {
		return root.toPath().resolve(path);
	}
}
