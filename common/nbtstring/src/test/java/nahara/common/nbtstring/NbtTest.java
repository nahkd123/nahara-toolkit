package nahara.common.nbtstring;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class NbtTest {
	@Test
	void testCreateChain() {
		var root = new NbtCompound();
		var displayName = root
				.getOrCreate("display", NbtCompound::new).compound()
				.getOrCreate("Name", NbtString::new).string();
		displayName.setText("SUS!");
		assertEquals("{display:{Name:\"SUS!\"}}", root.serializeAsString());
	}

	@Test
	void testFromSampleFile() throws URISyntaxException, IOException {
		var path = Path.of(getClass().getClassLoader().getResource("sample_nbt.txt").toURI());
		var text = Files.readString(path, StandardCharsets.UTF_8);
		System.out.println(Nbt.fromString(text).serializeAsString());
	}
}
