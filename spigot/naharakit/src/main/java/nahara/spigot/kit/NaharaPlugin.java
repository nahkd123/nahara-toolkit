package nahara.spigot.kit;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class NaharaPlugin extends JavaPlugin {
	protected abstract void onPluginStart() throws UserErrorException;
	protected abstract void onPluginStop() throws UserErrorException;

	/**
	 * <p>Print a message if given plugin name is not installed.</p>
	 * @param pluginName
	 */
	protected void softDependOn(String pluginName) {
		var plugin = getServer().getPluginManager().getPlugin(pluginName);
		if (plugin == null) getLogger().info("You can install " + pluginName + " for full functionality!");
	}

	/**
	 * <p>Stop plugin if the given plugin name is not installed.</p>
	 * @param pluginName
	 * @throws UserErrorException
	 */
	protected void hardDependOn(String pluginName) throws UserErrorException {
		var plugin = getServer().getPluginManager().getPlugin(pluginName);
		if (plugin == null) {
			getLogger().severe("");
			getLogger().severe("--- ERROR: Missing plugin ---");
			getLogger().severe("");
			getLogger().severe(getName() + " depends on plugin '" + pluginName + "', which is not installed!");
			getLogger().severe("Therefore, " + getName() + " will be stopped.");
			getLogger().severe("");
			getLogger().severe("To fix this error, find the plugin '" + pluginName + "' and install it inside");
			getLogger().severe("your plugins/ folder.");
			getLogger().severe("");
			throw new UserErrorException("Missing plugin '" + pluginName + "' which is required by plugin '" + getName() + "'");
		}
	}

	/**
	 * <p>A good way to ask your user to install a plugin is by adding name of required plugins inside
	 * {@code softdepend} list and stop plugin if any of those is missing. To make your life easier, you
	 * can now call this method inside {@link NaharaPlugin#onPluginStart()} and all soft dependencies
	 * suddenly becomes "hard dependencies".</p>
	 * @throws UserErrorException
	 */
	protected void hardDependOnSoftDependedPlugins() throws UserErrorException {
		for (var name : getDescription().getSoftDepend()) hardDependOn(name);
	}

	@Override
	public void onEnable() {
		try {
			onPluginStart();
		} catch (UserErrorException e) {
			getLogger().severe("An user error has been captured: " + e.getMessage());
			getLogger().severe("Please resolve your error. Plugin '" + getName() + "' will be stopped.");
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		try {
			onPluginStop();
		} catch (UserErrorException e) {
			getLogger().severe("An user error has been captured: " + e.getMessage());
		}
	}

	// Register methods
	protected void register(Listener listener) { getServer().getPluginManager().registerEvents(listener, this); }
}
