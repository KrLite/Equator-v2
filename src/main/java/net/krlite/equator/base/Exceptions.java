package net.krlite.equator.base;

import net.krlite.equator.visual.color.Colorspace;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class Exceptions {
	public static class Visual {
		public static IllegalArgumentException colorArrayLength(String colorspaceName, int expectedLength, int length, Throwable cause) {
			return new IllegalArgumentException("Color array in colorspace " + colorspaceName + " must be of length " + expectedLength + ", but was" + length, cause);
		}

		public static IOException colorspaceNotSupported(Colorspace colorspace, Throwable cause) {
			return new IOException("Colorspace %s not supported" + colorspace.getName(), cause);
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
