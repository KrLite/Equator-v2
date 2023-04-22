package net.krlite.equator.base;

import java.io.IOException;

public class Exceptions {
	public static class Visual {
		public static final IllegalArgumentException COLOR_ARRAY_LENGTH_EXCEPTION = new IllegalArgumentException("Color array must be of length 3 to 4");
	}

	public static class Render {
		public static final IOException IDENTIFIER_NOT_FOUND_EXCEPTION = new IOException("Resource %s not found");
		public static final IOException IDENTIFIER_FAILED_TO_LOAD_EXCEPTION = new IOException("Resource %s failed to load");
	}
}
