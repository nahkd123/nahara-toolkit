package nahara.spigot.kit;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import nahara.common.attachments.Attachments;
import nahara.common.commands.Command;
import nahara.common.localize.Localizer;
import nahara.common.localize.LocalizerProvider;
import nahara.common.tasks.ManualTask;
import nahara.common.tasks.Task;
import nahara.spigot.commands.SpigotCommand;
import nahara.spigot.commands.SpigotCommandContext;
import nahara.spigot.kit.files.PluginDataFiles;
import nahara.spigot.kit.files.PluginResources;
import nahara.spigot.menus.Menu;

/**
 * <p>An enhanced version of {@link JavaPlugin}.</p>
 * <p>Unlike {@link JavaPlugin}, some methods have {@code protected} modifier.</p>
 * @author nahkd
 * @see #onPluginStart()
 * @see #onPluginStop()
 *
 */
public abstract class NaharaPlugin extends JavaPlugin implements LocalizerProvider {
	private Localizer localizer = Localizer.getGlobal();
	@Override public Localizer getLocalizer() { return localizer; }
	protected void setLocalizer(Localizer localizer) { this.localizer = localizer != null? localizer : Localizer.getGlobal(); }

	private PluginResources resources;
	public PluginResources getResources() { return resources; }

	private PluginDataFiles data;
	public PluginDataFiles getData() { return data; }

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
			Menu.ensureListenerRegistered(this);
			resources = new PluginResources(this, getClassLoader());
			data = new PluginDataFiles(this, getDataFolder());
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
			for (var p : getServer().getOnlinePlayers()) {
				var menu = Attachments.getGlobal().get(p, Menu.class);
				if (menu != null) p.closeInventory();
			}
		} catch (UserErrorException e) {
			getLogger().severe("An user error has been captured: " + e.getMessage());
		}
	}

	// Register methods
	protected void register(Listener listener) { getServer().getPluginManager().registerEvents(listener, this); }
	protected void register(SpigotCommand command) { command.registerTo(this); }
	protected void register(Command<SpigotCommandContext> command) { new SpigotCommand(command).registerTo(this); }

	/**
	 * <p>Synchronize task. The task will be completed inside main server thread. If you are performing
	 * asynchronous task, you have to use this method before you can interact with the server.</p>
	 * @param <T>
	 * @param task Task to synchronize.
	 * @return New task that will be completed at the start of server tick in main thread.
	 */
	public <T> Task<T> sync(Task<T> task) {
		var syncTask = new ManualTask<T>();
		task.onCompleted(result -> Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> syncTask.resolve(result)));
		return syncTask;
	}

	// Scheduling
	public BukkitTask scheduleRepeating(long delay, long interval, Runnable run) { return Bukkit.getScheduler().runTaskTimer(this, run, delay, interval); }
	public BukkitTask scheduleRepeating(long interval, Runnable run) { return scheduleRepeating(0, interval, run); }
	public BukkitTask schedule(long delay, Runnable run) { return Bukkit.getScheduler().runTaskLater(this, run, delay); }
	public BukkitTask schedule(Runnable run) { return Bukkit.getScheduler().runTask(this, run); }
}
