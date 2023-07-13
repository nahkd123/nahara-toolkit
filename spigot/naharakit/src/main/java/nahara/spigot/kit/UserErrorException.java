package nahara.spigot.kit;

import java.io.Serial;

/**
 * <p>This exception is thrown if the error is confirmed to have caused by user.</p>
 * @author nahkd
 *
 */
public class UserErrorException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = -1956309948226272611L;

	public UserErrorException(String message) {
		super(message);
	}
}
