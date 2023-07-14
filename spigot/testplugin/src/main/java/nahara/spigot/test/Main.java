package nahara.spigot.test;

import nahara.common.localize.Localizer;
import nahara.spigot.kit.NaharaPlugin;
import nahara.spigot.kit.UserErrorException;
import nahara.spigot.test.commands.TestCommand;
import nahara.spigot.test.demo.storage.StorageCommand;

public class Main extends NaharaPlugin {
	@Override
	protected void onPluginStart() throws UserErrorException {
		getResources().save("en-us.json", false);

		setLocalizer(Localizer.merge(
				getData().jsonTranslation("en-us.json"), // User's defined translations
				getResources().jsonTranslation("en-us.json")
				));

		register(TestCommand.COMMAND);
		register(StorageCommand.COMMAND);
	}

	@Override
	protected void onPluginStop() throws UserErrorException {
	}
}
