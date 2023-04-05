package net.krlite.equator.frame;

import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.math.geometry.Vector;
import net.krlite.label.For;
import net.krlite.label.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

@Module("Math")
@For("2.1.2")
public class FrameInfo {
	public static class Scaled {
		public static Vector width() {
			return Vector.fromCartesian(MinecraftClient.getInstance().getWindow().getScaledWidth(), 0);
		}

		public static Vector height() {
			return Vector.fromCartesian(0, MinecraftClient.getInstance().getWindow().getScaledHeight());
		}

		public static Vector size() {
			return width().add(height());
		}

		public static Vector center() {
			return size().divide(2);
		}

		public static Vector cursor() {
			return Convertor.screenToScaled(Screen.cursor());
		}

		public static Box fullScreen() {
			return new Box(size());
		}

		public static class Number {
			public static double width() {
				return Scaled.width().magnitude();
			}

			public static double height() {
				return Scaled.height().magnitude();
			}

			public static double size() {
				return Scaled.size().magnitude();
			}

			public static double xCenter() {
				return Scaled.center().x();
			}

			public static double yCenter() {
				return Scaled.center().y();
			}

			public static double xCursor() {
				return Scaled.cursor().x();
			}

			public static double yCursor() {
				return Scaled.cursor().y();
			}
		}
	}

	public static class Screen {
		public static Vector width() {
			int[] width = new int[1];
			GLFW.glfwGetWindowSize(MinecraftClient.getInstance().getWindow().getHandle(), width, null);
			return Vector.fromCartesian(width[0], 0);
		}

		public static Vector height() {
			int[] height = new int[1];
			GLFW.glfwGetWindowSize(MinecraftClient.getInstance().getWindow().getHandle(), null, height);
			return Vector.fromCartesian(0, height[0]);
		}

		public static Vector size() {
			return width().add(height());
		}

		public static Vector center() {
			return size().divide(2);
		}

		public static Vector cursor() {
			double[] x = new double[1], y = new double[1];
			GLFW.glfwGetCursorPos(MinecraftClient.getInstance().getWindow().getHandle(), x, y);
			return Vector.fromCartesian(x[0], y[0]);
		}

		public static Box fullScreen() {
			return new Box(size());
		}

		public static class Number {
			public static double width() {
				return Screen.width().magnitude();
			}

			public static double height() {
				return Screen.height().magnitude();
			}

			public static double size() {
				return Screen.size().magnitude();
			}

			public static double xCenter() {
				return Screen.center().x();
			}

			public static double yCenter() {
				return Screen.center().y();
			}

			public static double xCursor() {
				return Screen.cursor().x();
			}

			public static double yCursor() {
				return Screen.cursor().y();
			}
		}
	}

	public static class Convertor {
		public static Vector scaledToScreen(Vector vector) {
			return vector.multiply(Screen.size().magnitude() / Scaled.size().magnitude());
		}

		public static Vector screenToScaled(Vector vector) {
			return vector.multiply(Scaled.size().magnitude() / Screen.size().magnitude());
		}

		public static Vector scaledToOpenGL(Vector vector) {
			return screenToOpenGL(scaledToScreen(vector));
		}

		public static Vector openGLToScaled(Vector vector) {
			return screenToScaled(openGLToScreen(vector));
		}

		public static Vector screenToOpenGL(Vector vector) {
			return vector.y(Screen.height().magnitude() - vector.y()).multiply(2);
		}

		public static Vector openGLToScreen(Vector vector) {
			return vector.y(Screen.height().magnitude() - vector.y()).divide(2);
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
			return new Box(box.origin().y(Screen.height().magnitude() - box.origin().y() - box.height().magnitude()).multiply(2), box.size().multiply(2));
		}

		public static Box openGLToScreen(Box box) {
			return new Box(box.origin().y(Screen.height().magnitude() - box.origin().y() - box.height().magnitude()).divide(2), box.size().divide(2));
		}
	}
}
