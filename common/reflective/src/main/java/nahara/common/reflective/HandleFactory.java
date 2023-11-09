package nahara.common.reflective;

/**
 * <p>The factory for getting the handle from given object.</p>
 * @param <S> The object type.
 * @param <T> The type for handle.
 */
public interface HandleFactory<S, T> {
	public Handle<T> of(S obj);

	default Handle<T> ofStatic() {
		return of(null);
	}
}
