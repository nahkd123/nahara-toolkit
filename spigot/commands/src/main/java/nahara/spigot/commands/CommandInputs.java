package nahara.spigot.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public record CommandInputs(Plugin plugin, CommandSender sender, String[] args) {
}
