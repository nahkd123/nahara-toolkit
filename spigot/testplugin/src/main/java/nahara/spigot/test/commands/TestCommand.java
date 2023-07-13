package nahara.spigot.test.commands;

import org.bukkit.Material;

import nahara.common.commands.CommandExecException;
import nahara.spigot.commands.SpigotCommand;
import nahara.spigot.items.NaharaStack;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class TestCommand {
	public static final SpigotCommand COMMAND = new SpigotCommand(SpigotCommand.withName("test")
			.require(SpigotCommand.hasPermission("nahara.commands.test"))
			.option("switch", true)
			.option("value", false)
			.onExec(ctx -> {
				ctx.println("&aHi!");
				ctx.option("switch").ifPresent(v -> ctx.println("-switch is true! yay!"));
				ctx.option("value").ifPresent(v -> ctx.println("-value is present: " + v));
			})
			.child(SpigotCommand.withName("item")
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
