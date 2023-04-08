package net.krlite.equator.render.frame;

import net.krlite.equator.math.geometry.Vector;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class CursorInfo {
	public static Vector scaledCursor() {
		return FrameInfo.Convertor.screenToScaled(screenCursor());
	}

	public static Vector screenCursor() {
		double[] x = new double[1], y = new double[1];
		GLFW.glfwGetCursorPos(MinecraftClient.getInstance().getWindow().getHandle(), x, y);
		return Vector.fromCartesian(x[0], y[0]);
	}
}
