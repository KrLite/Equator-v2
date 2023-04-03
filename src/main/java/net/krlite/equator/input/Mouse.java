package net.krlite.equator.input;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Mouse {
	public static boolean isDown(int button) {
		return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), button) == GLFW.GLFW_PRESS;
	}

	public static boolean isUp(int button) {
		return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), button) == GLFW.GLFW_RELEASE;
	}

	public static long createCursor(int width, int height, File file, int xHot, int yHot) {
		// TODO: Implement
		return 0;
	}

	public static void destroyCursor(long cursor) {
		//TODO: Implement
		//GLFW.glfwDestroyCursor(cursor);
	}

	static void initMouseCallback(long window) {
		GLFWMouseButtonCallback mouseCallback = new GLFWMouseButtonCallback() {
			private final GLFWMouseButtonCallbackI delegate = GLFW.glfwSetMouseButtonCallback(window, null);

			@Override
			public void invoke(long window, int button, int action, int modifiers) {
				if (action == GLFW.GLFW_PRESS) {
					InputEvent.Callbacks.Mouse.EVENT.invoker().onMouse(InputEvent.MOUSE_PRESSED, button, Keyboard.Modifier.getModifiers(modifiers));
				} else if (action == GLFW.GLFW_RELEASE) {
					InputEvent.Callbacks.Mouse.EVENT.invoker().onMouse(InputEvent.MOUSE_RELEASED, button, Keyboard.Modifier.getModifiers(modifiers));
				}

				if (delegate != null) {
					delegate.invoke(window, button, action, modifiers);
				}
			}
		};

		GLFW.glfwSetMouseButtonCallback(window, mouseCallback);
	}

	static void initMouseScrollCallback(long window) {
		GLFWScrollCallback mouseScrollCallback = new GLFWScrollCallback() {
			private final GLFWScrollCallbackI delegate = GLFW.glfwSetScrollCallback(window, null);

			@Override
			public void invoke(long window, double horizontal, double vertical) {
				InputEvent.Callbacks.Mouse.EVENT.invoker().onMouse(InputEvent.MOUSE_SCROLLED, GLFW.GLFW_MOUSE_BUTTON_MIDDLE, new Keyboard.Modifier[0]);
				InputEvent.Callbacks.MouseScroll.EVENT.invoker().onMouseScroll(horizontal, vertical);

				if (delegate != null) {
					delegate.invoke(window, horizontal, vertical);
				}
			}
		};

		GLFW.glfwSetScrollCallback(window, mouseScrollCallback);
	}

	static void initMouseDropCallback(long window) {
		GLFWDropCallback mouseDropCallback = new GLFWDropCallback() {
			private final GLFWDropCallbackI delegate = GLFW.glfwSetDropCallback(window, null);

			@Override
			public void invoke(long window, int count, long names) {
				Path[] paths = new Path[count];
				for (int i = 0; i < count; i++) {
					paths[i] = Paths.get(GLFWDropCallback.getName(names, i));
				}
				InputEvent.Callbacks.MouseDrop.EVENT.invoker().onMouseDrop(count, paths);

				if (delegate != null) {
					delegate.invoke(window, count, names);
				}
			}
		};

		GLFW.glfwSetDropCallback(window, mouseDropCallback);
	}

	static void initCursorPositionCallback(long window) {
		GLFWCursorPosCallback cursorPositionCallback = new GLFWCursorPosCallback() {
			private final GLFWCursorPosCallbackI delegate = GLFW.glfwSetCursorPosCallback(window, null);

			@Override
			public void invoke(long window, double x, double y) {
				InputEvent.Callbacks.CursorPosition.EVENT.invoker().onCursorPosition(x, y);
				for (int button = 0; button < GLFW.GLFW_MOUSE_BUTTON_LAST; button++) {
					if (isDown(button)) {
						InputEvent.Callbacks.MouseDrag.EVENT.invoker().onMouseDrag(button, x, y);
					}
				}

				if (delegate != null) {
					delegate.invoke(window, x, y);
				}
			}
		};

		GLFW.glfwSetCursorPosCallback(window, cursorPositionCallback);
	}

	static void initCursorEnterCallback(long window) {
		GLFWCursorEnterCallback cursorEnterCallback = new GLFWCursorEnterCallback() {
			private final GLFWCursorEnterCallbackI delegate = GLFW.glfwSetCursorEnterCallback(window, null);

			@Override
			public void invoke(long window, boolean entered) {
				InputEvent.Callbacks.CursorEnter.EVENT.invoker().onCursorEnter(entered);

				if (delegate != null) {
					delegate.invoke(window, entered);
				}
			}
		};

		GLFW.glfwSetCursorEnterCallback(window, cursorEnterCallback);
	}
}
