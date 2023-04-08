package net.krlite.equator.render.frame;

import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.math.geometry.Vector;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class FrameInfo {
	public static Box scaled() {
		return new Box(Vector.fromCartesian(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight()));
	}

	public static Box screen() {
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(MinecraftClient.getInstance().getWindow().getHandle(), width, height);
		return new Box(Vector.fromCartesian(width[0], height[0]));
	}

	public static Box openGL() {
		return scaled().fitToOpenGL();
	}

	public static class Convertor {
		public static Vector scaledToScreen(Vector vector) {
			return vector.multiply(screen().size().magnitude() / scaled().size().magnitude());
		}

		public static Vector screenToScaled(Vector vector) {
			return vector.multiply(scaled().size().magnitude() / screen().size().magnitude());
		}

		public static Vector scaledToOpenGL(Vector vector) {
			return screenToOpenGL(scaledToScreen(vector));
		}

		public static Vector openGLToScaled(Vector vector) {
			return screenToScaled(openGLToScreen(vector));
		}

		public static Vector screenToOpenGL(Vector vector) {
			return vector.y(screen().h() - vector.y()).multiply(2);
		}

		public static Vector openGLToScreen(Vector vector) {
			return vector.y(screen().h() - vector.y()).divide(2);
		}

		public static Box scaledToScreen(Box box) {
			return new Box(scaledToScreen(box.origin()), scaledToScreen(box.size()));
		}

		public static Box screenToScaled(Box box) {
			return new Box(screenToScaled(box.origin()), screenToScaled(box.size()));
		}

		public static Box scaledToOpenGL(Box box) {
			return screenToOpenGL(scaledToScreen(box));
		}

		public static Box openGLToScaled(Box box) {
			return screenToScaled(openGLToScreen(box));
		}

		public static Box screenToOpenGL(Box box) {
			return new Box(box.origin().y(screen().h() - box.origin().y() - box.height().magnitude()).multiply(2), box.size().multiply(2));
		}

		public static Box openGLToScreen(Box box) {
			return new Box(box.origin().y(screen().h() - box.origin().y() - box.height().magnitude()).divide(2), box.size().divide(2));
		}
	}
}
