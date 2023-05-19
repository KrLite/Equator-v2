package net.krlite.equator.base;

import net.minecraft.util.Identifier;

import java.io.IOException;

@net.krlite.equator.base.Math("2.4.0")
public class Exceptions {
	public static class ColorArrayLengthException extends IllegalArgumentException {
		public ColorArrayLengthException(String colorspaceName, int expectedLength, int length, Throwable cause) {
			super("Color array in colorspace " + colorspaceName + " must be of length " + expectedLength + ", but was" + length, cause);
		}

		public ColorArrayLengthException(String colorspaceName, int expectedLength, int length) {
			this(colorspaceName, expectedLength, length, null);
		}
	}

	public static class ColorIndexOutOfBoundsException extends IndexOutOfBoundsException {
		public ColorIndexOutOfBoundsException(int index, int length) {
			super("Color index " + index + " out of bounds " + length);
		}
	}

	public static class IdentifierNotFoundException extends IOException {
		public IdentifierNotFoundException(Identifier identifier, Throwable cause) {
			super("Resource " + identifier.toString() + " not found", cause);
		}

		public IdentifierNotFoundException(Identifier identifier) {
			this(identifier, null);
		}
	}

	public static class IdentifierFailedToLoadException extends IOException {
		public IdentifierFailedToLoadException(Identifier identifier, Throwable cause) {
			super("Resource " + identifier.toString() + " failed to load", cause);
		}

		public IdentifierFailedToLoadException(Identifier identifier) {
			this(identifier, null);
		}
	}

	public static class StepMustBePositiveException extends IllegalArgumentException {
		public StepMustBePositiveException(long xStep, long yStep) {
			super("Step must be positive, but was: xStep=" + xStep + ", yStep=" + yStep);
		}

		public StepMustBePositiveException(long step) {
			super("Step must be positive, but was " + step);
		}
	}
}
