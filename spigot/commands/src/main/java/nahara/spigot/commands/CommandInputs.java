package nahara.spigot.commands;

import org.bukkit.command.CommandSender;

public record CommandInputs(CommandSender sender, String[] args) {
}
