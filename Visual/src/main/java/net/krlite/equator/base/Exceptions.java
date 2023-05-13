package net.krlite.equator.base;

import net.minecraft.util.Identifier;

import java.io.IOException;

@Visual("2.3.0")
public class Exceptions {
	public static class Visual {
		public static IllegalArgumentException colorArrayLength(String colorspaceName, int expectedLength, int length, Throwable cause) {
			return new IllegalArgumentException("Color array in colorspace " + colorspaceName + " must be of length " + expectedLength + ", but was" + length, cause);
		}

		public static IllegalArgumentException colorspaceNotSame(Throwable cause) {
			return new IllegalArgumentException("Colorspaces must be the same", cause);
		}

		public static IllegalStateException modeUnexpected(String mode, Throwable cause) {
			return new IllegalStateException("Unexpected mode '" + mode + "'", cause);
		}

		public static IndexOutOfBoundsException colorIndexOutOfBounds(int index, int length) {
			return new IndexOutOfBoundsException("Color index " + index + " out of bounds " + length);
		}
	}

	public static class Render {
		public static IOException identifierNotFound(Identifier identifier, Throwable cause) {
			return new IOException("Resource %s not found" + identifier.toString(), cause);
		}

		public static IOException identifierFailedToLoad(Identifier identifier, Throwable cause) {
			return new IOException("Resource %s failed to load" + identifier.toString(), cause);
		}
	}
}
