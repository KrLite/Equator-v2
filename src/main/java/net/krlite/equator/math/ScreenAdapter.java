package net.krlite.equator.math;

import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.math.geometry.Vector;
import net.minecraft.client.MinecraftClient;

public class ScreenAdapter {
	public static Vector scaledWidth() {
		return Vector.fromCartesian(MinecraftClient.getInstance().getWindow().getScaledWidth(), 0);
	}

	public static Vector width() {
		return Vector.fromCartesian(MinecraftClient.getInstance().getWindow().getWidth() * 2, 0);
	}

	public static Vector scaledHeight() {
		return Vector.fromCartesian(0, MinecraftClient.getInstance().getWindow().getScaledHeight());
	}

	public static Vector height() {
		return Vector.fromCartesian(0, MinecraftClient.getInstance().getWindow().getHeight() * 2);
	}

	public static Vector scaledSize() {
		return scaledWidth().add(scaledHeight());
	}

	public static Vector size() {
		return width().add(height());
	}

	public static Box scaledScreen() {
		return new Box(scaledSize());
	}

	public static Box screen() {
		return new Box(size());
	}

	public static Vector scaledCenter() {
		return scaledSize().divide(2);
	}

	public static Vector center() {
		return size().divide(2);
	}

	public static Vector fitWithOpenGLScaled(Vector vector) {
		return vector.multiply(size().magnitude() / scaledSize().magnitude()).theta(vector.theta());
	}

	public static Vector fitWithOpenGL(Vector vector) {
		return vector.theta(-vector.theta());
	}

	public static Box fitWithOpenGLScaled(Box box) {
		return new Box(fitWithOpenGLScaled(box.origin()), fitWithOpenGLScaled(box.size()));
	}

	public static Box fitWithOpenGL(Box box) {
		return new Box(fitWithOpenGL(box.origin()), fitWithOpenGL(box.size()));
	}
}
