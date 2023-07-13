package nahara.common.tasks;

import java.io.Serial;

public class AsyncException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 6393463802898128426L;

	public AsyncException(Throwable cause) {
		super(cause);
	}

	public AsyncException(String message, Throwable cause) {
		super(message, cause);
	}
}
