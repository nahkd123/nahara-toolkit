package nahara.spigot.test;

import nahara.spigot.kit.NaharaPlugin;
import nahara.spigot.kit.UserErrorException;
import nahara.spigot.test.commands.TestCommand;
import nahara.spigot.test.demo.storage.StorageCommand;

public class Main extends NaharaPlugin {
	@Override
	protected void onPluginStart() throws UserErrorException {
		TestCommand.COMMAND.registerTo(this);
		StorageCommand.COMMAND.registerTo(this);
	}

	@Override
	protected void onPluginStop() throws UserErrorException {
	}
}
