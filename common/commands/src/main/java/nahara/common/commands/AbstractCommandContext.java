package nahara.common.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import nahara.common.localize.Message;

public abstract class AbstractCommandContext {
	public final AbstractCommandContext parent;
	public final Command<?> command;
	public final Map<String, String> options = new HashMap<>();
	public final List<String> arguments = new ArrayList<>();

	public AbstractCommandContext(AbstractCommandContext parent, Command<?> command) {
		this.parent = parent;
		this.command = command;
	}

	public abstract void println(String message);

	public void println(Message message) {
		println(message.toString());
	}

	public Optional<String> option(String key) {
		return Optional.ofNullable(options.get(key));
	}

	public Optional<String> argument(int index) {
		if (index < arguments.size()) return Optional.of(arguments.get(index));
		return Optional.empty();
	}

	public Optional<String> argument(String name) {
		if (command == null) throw new IllegalArgumentException("No command attached; please use argument(int)");
		return argument(command.arguments.indexOf(name));
	}
}
