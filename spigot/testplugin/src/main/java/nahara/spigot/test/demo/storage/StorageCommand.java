package nahara.spigot.test.demo.storage;

import nahara.common.commands.CommandExecException;
import nahara.spigot.commands.SpigotCommand;
import nahara.spigot.commands.SpigotCommandContext;
import nahara.spigot.test.Main;

/**
 * <p>Storage system. Created as Nahara's Toolkit demonstration.</p>
 * @author nahkd
 *
 */
public class StorageCommand {
	public static final SpigotCommand COMMAND = new SpigotCommand(SpigotCommand.withName("storage")
			.require(v -> v.getSender().hasPermission("nahara.demo.storage"))
			.onExec(StorageCommand::exec));

	private static void exec(SpigotCommandContext ctx) {
		ctx.getPlayer().ifPresentOrElse(p -> {
			var menu = new StorageMenu((Main) ctx.getPlugin(), p);
			menu.openMenu(p);
		}, () -> { throw new CommandExecException("Not a player"); });
	}
}
