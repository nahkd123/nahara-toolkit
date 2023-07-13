package nahara.spigot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import nahara.common.commands.CommandExecException;
import nahara.common.localize.Message;
import net.md_5.bungee.api.ChatColor;

public class SpigotCommand implements CommandExecutor, TabCompleter {
	private static final Message COMMAND_ERROR = new Message("general.command.error", "&cError while performing command: &f{}");
	private nahara.common.commands.Command<SpigotCommandContext> command;

	public SpigotCommand(nahara.common.commands.Command<SpigotCommandContext> command) {
		this.command = command;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		var argsIn = new ArrayList<String>();
		argsIn.addAll(Arrays.asList(args));
		var prompt = argsIn.remove(argsIn.size() - 1);
		return this.command.tabComplete(new CommandInputs(sender, args), argsIn.iterator(), prompt, SpigotCommandContext::new);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			this.command.exec(new CommandInputs(sender, args), Arrays.asList(args).iterator(), SpigotCommandContext::new);
		} catch (CommandExecException e) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', COMMAND_ERROR.of(e.getMessage())));
		}

		return true;
	}

	public void registerTo(JavaPlugin plugin) {
		plugin.getCommand(command.name).setExecutor(this);
	}

	public static Predicate<SpigotCommandContext> hasPermission(String permission) {
		return v -> v.getSender().hasPermission(permission);
	}

	public static nahara.common.commands.Command<SpigotCommandContext> withName(String name) {
		return new nahara.common.commands.Command<>(name);
	}
}
