package nahara.common.commands;

import java.io.Serial;

public class CommandExecException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = -7072913923344707575L;

	public CommandExecException(String message) {
		super(message);
	}
}
