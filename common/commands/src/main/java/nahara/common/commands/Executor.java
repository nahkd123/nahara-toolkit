package nahara.common.commands;

@FunctionalInterface
public interface Executor<T extends AbstractCommandContext> {
	public void onExecute(T ctx);
}
