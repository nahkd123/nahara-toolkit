package nahara.spigot.commands;

import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nahara.common.commands.AbstractCommandContext;
import nahara.common.commands.Command;
import net.md_5.bungee.api.ChatColor;

public class SpigotCommandContext extends AbstractCommandContext {
	private CommandInputs inputs;

	public SpigotCommandContext(CommandInputs inputs, SpigotCommandContext parent, Command<SpigotCommandContext> command) {
		super(parent, command);
		this.inputs = inputs;
	}

	@Override
	public void println(String message) {
		inputs.sender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public CommandInputs getInputs() {
		return inputs;
	}

	public CommandSender getSender() {
		return inputs.sender();
	}

	public Optional<Player> getPlayer() {
		return getSender() instanceof Player player? Optional.of(player) : Optional.empty();
	}
}
