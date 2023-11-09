package nahara.common.reflective;

public interface MethodFactory<S, T> {
	public Method<T> of(S obj);

	default Method<T> ofStatic() {
		return of(null);
	}
}
