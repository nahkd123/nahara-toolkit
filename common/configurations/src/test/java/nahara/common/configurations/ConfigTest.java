package nahara.common.configurations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class ConfigTest {
	@Test
	void testToString() {
		assertEquals("root", new Config("root").toString());
		assertEquals("root (3 children)", new Config("root")
				.addChild(new Config("child"))
				.addChild(new Config("child"))
				.addChild(new Config("child"))
				.toString());
		assertEquals("root my value (3 children)", new Config("root", "my value")
				.addChild(new Config("child"))
				.addChild(new Config("child"))
				.addChild(new Config("child"))
				.toString());
	}

	@Test
	void testGetAsString() {
		var ref = String.join("\n",
				"root root value",
				"  child child value",
				"  child nested?",
				"    child nested!",
				"    child child value",
				"  child",
				"");
		var result = new Config("root", "root value")
				.addChild(new Config("child", "child value"))
				.addChild(new Config("child", "nested?")
						.addChild(new Config("child", "nested!"))
						.addChild(new Config("child", "child value")))
				.addChild(new Config("child"))
				.getAsString();
		assertEquals(ref, result);
	}

	@Test
	void testParse() {
		var input = Arrays.asList(
				"title My menu title!",
				"type GENERIC_9X6",
				"button static minecraft:diamond{ \\",
				"  display: { \\",
				"    Name: '\"Click me!\"' \\",
				"  } \\",
				"}",
				"  fill (0, 0) to (9, 1)",
				"  onEvent leftClick rightClick shiftLeftClick shiftRightClick middleClick",
				"    serverRun tellraw %player_name% \"You clicked me!\"",
				"button dynamic menuup:animated_icon",
				"  dynamic minecraft:stone",
				"    name <rainbow speed=5 saturation=100% lightness=50%>Rainbow Text!</rainbow>",
				"    lore You can have more than",
				"    lore 1 lore line!",
				"  // You can overlap the previous button",
				"  fill (0, 0)",
				"entryWithNoValue");

		var config = Config.parseConfig(input);
		assertEquals("My menu title!", config.firstChild("title").get().getValue().get());
		assertEquals("GENERIC_9X6", config.firstChild("type").get().getValue().get());
	}
}
