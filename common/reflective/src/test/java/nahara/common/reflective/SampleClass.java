package nahara.common.reflective;

public class SampleClass {
	private String name;
	private int age;

	public SampleClass(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public String describe() {
		return name + " is now " + age + " years old!";
	}
}
