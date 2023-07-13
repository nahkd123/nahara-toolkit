package nahara.common.commands;

@FunctionalInterface
public interface ContextFactory<I, C extends AbstractCommandContext> {
	C create(I input, C parent, Command<C> command);
}
