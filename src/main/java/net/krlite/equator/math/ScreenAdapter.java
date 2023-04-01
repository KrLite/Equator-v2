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

	public static Vector fitScaledToScreen(Vector vector) {
		return vector.multiply(size().magnitude() / scaledSize().magnitude());
	}

	public static Vector fitScaledToOpenGL(Vector vector) {
		return fitScreenToOpenGL(fitScaledToScreen(vector));
	}

	public static Vector fitScreenToOpenGL(Vector vector) {
		return vector.theta(-vector.theta());
	}

	public static Box fitScaledToScreen(Box box) {
		return new Box(fitScaledToScreen(box.origin()), fitScaledToScreen(box.size()));
	}

	public static Box fitScaledToOpenGL(Box box) {
		return new Box(fitScaledToOpenGL(box.origin()), fitScaledToOpenGL(box.size()));
	}

	public static Box fitScreenToOpenGL(Box box) {
		return new Box(fitScreenToOpenGL(box.origin()), fitScreenToOpenGL(box.size()));
	}
}
