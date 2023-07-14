package nahara.common.structures;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Structure<S> {
	public static final int RAW_HEADER = 0x00_52_41_57;

	public int getWidth();
	public int getHeight();
	public int getDepth();

	public void set(int x, int y, int z, S state);
	public S get(int x, int y, int z);

	public void serialize(OutputStream stream) throws IOException;

	public static <S> Structure<S> deserialize(InputStream stream, StatesFactory<S> factory) throws IOException {
		var in = new DataInputStream(stream);
		var header = in.readInt();

		if (header == StructureV1.VERSION_HEADER) return StructureV1.deserialize(stream, factory);
		else throw new IOException("Invaild header: " + header);
	}
}
