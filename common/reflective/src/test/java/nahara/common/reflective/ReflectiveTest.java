package nahara.common.reflective;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ReflectiveTest {
	@Test
	void test() {
		Reflective<SampleClass> r = Reflective.of(SampleClass.class);
		var ctorMethod = r.constructor(String.class, int.class);
		var nameHandle = r.field(String.class, "name");
		var ageHandle = r.field(int.class, "age");
		var describeHandle = r.getterSetter(String.class, Reflective.names("describe"), null);
		var describeMethod = r.method(String.class, Reflective.args(), "describe");

		SampleClass john = new SampleClass("John", 42);
		assertEquals("John is now 42 years old!", john.describe());

		ageHandle.of(john).map(age -> age - 10);
		assertEquals("John is now 32 years old!", john.describe());

		nameHandle.of(john).set("Annie");
		assertEquals("Annie is now 32 years old!", john.describe());
		assertEquals("Annie is now 32 years old!", describeHandle.of(john).get());
		assertEquals("Annie is now 32 years old!", describeMethod.of(john).invoke());

		assertEquals("Albert is now 69 years old!", ctorMethod.invoke("Albert", 69).describe());
	}
}
