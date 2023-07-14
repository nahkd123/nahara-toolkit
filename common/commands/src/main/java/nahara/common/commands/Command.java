package nahara.common.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import nahara.common.localize.Localizer;
import nahara.common.localize.LocalizerProvider;
import nahara.common.localize.Message;

public class Command<C extends AbstractCommandContext> {
	public Message COMMAND_HELP;
	public Message COMMAND_HELP_MAIN;
	public Message COMMAND_OPTIONS;
	public Message COMMAND_OPTIONS_SWITCH;
	public Message COMMAND_OPTIONS_VALUE;
	public Message COMMAND_SUBCOMMANDS;
	public Message COMMAND_SUBCOMMANDS_ENTRY;
	public Message COMMAND_SUBCOMMANDS_ARG;
	public Message COMMAND_NO_PERMISSION;

	public final LocalizerProvider provider;
	public final String name;
	public final Map<String, Command<C>> children = new HashMap<>();
	public final Map<String, Boolean> options = new HashMap<>();
	public final List<String> arguments = new ArrayList<>();

	public Executor<C> executor;
	public Predicate<C> requirement;

	public Command(LocalizerProvider provider, String name) {
		this.provider = provider;
		this.name = name;
		COMMAND_HELP = new Message(provider, "general.command.help", "&7Help message for &f{}&7:");
		COMMAND_HELP_MAIN = new Message(provider, "general.command.main", "  &f{} {}");
		COMMAND_OPTIONS = new Message(provider, "general.command.options", "&7Options:");
		COMMAND_OPTIONS_SWITCH = new Message(provider, "general.command.options.entry", "  &7-&f{}");
		COMMAND_OPTIONS_VALUE = new Message(provider, "general.command.options.entry", "  &7-&f{} &3<&bValue&3>");
		COMMAND_SUBCOMMANDS = new Message(provider, "general.command.subcommands", "&7Subcommands:");
		COMMAND_SUBCOMMANDS_ENTRY = new Message(provider, "general.command.subcommands.entry", "  &e{} {}");
		COMMAND_SUBCOMMANDS_ARG = new Message(provider, "general.command.subcommands.arg", "&6<&e{}&6>");
		COMMAND_NO_PERMISSION = new Message(provider, "general.command.nopermission", "&cYou don't have permission to use this command");
	}

	public Command(Localizer localizer, String name) {
		this(() -> localizer, name);
	}

	public Command(String name) {
		this(Localizer::getGlobal, name);
	}

	public Command<C> option(String optionName, boolean isSwitch) {
		options.put(optionName, isSwitch);
		return this;
	}

	public Command<C> argument(String name) {
		arguments.add(name);
		return this;
	}

	public Command<C> onExec(Executor<C> executor) {
		this.executor = executor;
		return this;
	}

	public Command<C> child(Command<C> child) {
		children.put(child.name, child);
		return this;
	}

	public Command<C> require(Predicate<C> pred) {
		requirement = pred;
		return this;
	}

	public <I> void exec(I input, Iterator<String> argsIter, ContextFactory<I, C> factory) {
		var ctx = factory.create(input, null, this);
		var current = this;

		if (current.requirement != null && !current.requirement.test(ctx)) { ctx.println(COMMAND_NO_PERMISSION); return; }

		while (argsIter.hasNext()) {
			var s = argsIter.next();

			if (s.startsWith("-")) {
				var key = s.substring(1);

				if (s.equalsIgnoreCase("-h") || s.equalsIgnoreCase("--help")) {
					current.printHelp(ctx, current == this);
					return;
				}

				var isSwitch = current.options.get(key);
				if (isSwitch == null) throw new CommandExecException("Unknown option: " + s);
				if (isSwitch) {
					ctx.options.put(key, "true");
					continue;
				}

				if (!argsIter.hasNext()) throw new CommandExecException("Missing value for option: " + s);
				var val = argsIter.next();
				ctx.options.put(key, val);
				continue;
			}

			if (current.executor != null && ctx.arguments.size() < current.arguments.size()) {
				ctx.arguments.add(s);
				continue;
			}

			var nextChild = current.children.get(s);
			if (nextChild == null) throw new CommandExecException("Unknown subcommand: " + s);

			ctx = factory.create(input, ctx, nextChild);
			current = nextChild;
			if (current.requirement != null && !current.requirement.test(ctx)) { ctx.println(COMMAND_NO_PERMISSION); return; }

			continue;
		}

		if (current.executor != null) {
			current.executor.onExecute(ctx);
		} else if (current.children.size() > 0) {
			printHelp(ctx, current == this);
		} else {
			throw new RuntimeException("Missing command executor or subcommand");
		}
	}

	public <I> List<String> tabComplete(I input, Iterator<String> argsIter, String prompt, ContextFactory<I, C> factory) {
		var ctx = factory.create(input, null, this);
		var current = this;
		if (current.requirement != null && !current.requirement.test(ctx)) return Collections.emptyList();

		while (argsIter.hasNext()) {
			var s = argsIter.next();

			if (s.startsWith("-")) {
				if (s.equalsIgnoreCase("-h") || s.equalsIgnoreCase("--help")) return Collections.emptyList();

				var isSwitch = current.options.get(s.substring(1));
				if (isSwitch == null || isSwitch) continue;

				if (!argsIter.hasNext()) return Arrays.asList("<" + s + ">");
				argsIter.next();
				continue;
			}

			if (current.executor != null && ctx.arguments.size() < current.arguments.size()) {
				ctx.arguments.add(s);
				continue;
			}

			var nextChild = current.children.get(s);
			if (nextChild == null) return Collections.emptyList();

			ctx = factory.create(input, ctx, nextChild);
			current = nextChild;
			if (current.requirement != null && !current.requirement.test(ctx)) return Collections.emptyList();
			continue;
		}

		var result = new ArrayList<String>();
		if (!prompt.startsWith("-")) {
			if (ctx.arguments.size() < current.arguments.size()) return Arrays.asList("<argument value>");
			result.addAll(current.children.keySet().stream().filter(v -> v.startsWith(prompt)).toList());
		}

		result.addAll(current.options.keySet().stream().map(v -> "-" + v).filter(v -> v.startsWith(prompt)).toList());
		return result;
	}

	public void printHelp(AbstractCommandContext ctx, boolean isRoot) {
		ctx.println(COMMAND_HELP.of((isRoot? "&8/&f" : "") + name));
		var args = String.join(" ", arguments.stream().map(COMMAND_SUBCOMMANDS_ARG::of).toList());
		ctx.println(COMMAND_HELP_MAIN.of(name, args));

		if (options.size() > 0) {
			ctx.println(COMMAND_OPTIONS);
			for (var e : options.entrySet()) {
				var msg = e.getValue()? COMMAND_OPTIONS_SWITCH : COMMAND_OPTIONS_VALUE;
				ctx.println(msg.of(e.getKey()));
			}
		}

		if (children.size() > 0) {
			ctx.println(COMMAND_SUBCOMMANDS);
			for (var e : children.entrySet()) {
				var subCmdArgs = String.join(" ", e.getValue().arguments.stream().map(COMMAND_SUBCOMMANDS_ARG::of).toList());
				ctx.println(COMMAND_SUBCOMMANDS_ENTRY.of(e.getKey(), subCmdArgs));
			}
		}
	}
}
