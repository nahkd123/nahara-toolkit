package nahara.common.structures;

public interface StatesFactory<S> {
	public String stateToString(S state);
	public S stringToState(String str);
}
