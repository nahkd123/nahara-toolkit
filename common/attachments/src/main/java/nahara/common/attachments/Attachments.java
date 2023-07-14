package nahara.common.attachments;

import java.util.HashMap;
import java.util.Map;

public class Attachments {
	private Map<Object, Map<Class<?>, Object>> attachments = new HashMap<>();

	public Map<Class<?>, Object> select(Object holder) {
		var map = attachments.get(holder);
		if (map == null) attachments.put(holder, map = new HashMap<>());
		return map;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Object holder, Class<T> type) {
		var map = select(holder);
		return (T) map.get(type);
	}

	public <T> void set(Object holder, Class<T> type, T value) {
		var map = select(holder);
		if (value != null) map.put(type, value);
		else map.remove(type);
	}

	public boolean has(Object holder, Class<?> type) {
		var map = attachments.get(holder);
		if (map == null) return false;
		return map.containsKey(type);
	}

	public void remove(Object holder, Class<?> type) {
		var map = select(holder);
		map.remove(type);
	}

	private static Attachments globalInstance;

	public static Attachments getGlobal() {
		if (globalInstance == null) globalInstance = new Attachments();
		return globalInstance;
	}

	/**
	 * <p>Obtain the attachments manager from given "runtime manager" object. Runtime manager can be an
	 * instance of running server, a thread or {@code null} for global attachments manager.</p>
	 * @param obj
	 * @return
	 */
	public static Attachments of(Object obj) {
		if (obj == null) return getGlobal();
		if (obj instanceof Attachments attachments) return attachments;
		if (obj instanceof AttachmentsHolder holder) return holder.getAttachments();
		return getGlobal();
	}
}
