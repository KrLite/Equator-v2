package net.krlite.equator.base;

import net.minecraft.util.Identifier;

import java.io.IOException;

public class Exceptions {
	public static class ModeUnexpectedException extends IllegalStateException {
		public ModeUnexpectedException(String mode) {
			super("Unexpected mode '" + mode + "'");
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
