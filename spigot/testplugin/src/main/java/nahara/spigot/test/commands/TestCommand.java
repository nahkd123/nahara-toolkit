package nahara.spigot.test.commands;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import nahara.common.commands.Command;
import nahara.common.commands.CommandExecException;
import nahara.common.localize.LocalizerProvider;
import nahara.spigot.commands.SpigotCommand;
import nahara.spigot.commands.SpigotCommandContext;
import nahara.spigot.items.NaharaStack;
import nahara.spigot.test.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class TestCommand {
	private static final LocalizerProvider LOCALIZER = () -> JavaPlugin.getPlugin(Main.class).getLocalizer();

	public static final SpigotCommand COMMAND = new SpigotCommand(new Command<SpigotCommandContext>(LOCALIZER, "test")
			.require(SpigotCommand.hasPermission("nahara.commands.test"))
			.option("switch", true)
			.option("value", false)
			.onExec(ctx -> {
				ctx.println("&aHi!");
				ctx.option("switch").ifPresent(v -> ctx.println("-switch is true! yay!"));
				ctx.option("value").ifPresent(v -> ctx.println("-value is present: " + v));
			})
			.child(new Command<SpigotCommandContext>(LOCALIZER, "item")
					.onExec(ctx -> {
						ctx.getPlayer().ifPresentOrElse(player -> {
							var item = new NaharaStack(Material.PAPER);
							item.setDisplayName(new ComponentBuilder("Red Paper").italic(false).color(ChatColor.RED));
							item.buildLore(appender -> {
								appender.accept(new ComponentBuilder("Very cool!").italic(false).color(ChatColor.GREEN).font("minecraft:alt"));
							}, false);

							player.getInventory().addItem(item.toItemStack());
							ctx.println("&aGave you the item ;)");
						}, () -> { throw new CommandExecException("Not a player"); });
					}))
			);
}
