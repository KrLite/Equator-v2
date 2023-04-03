package net.krlite.equator.input;

import org.lwjgl.glfw.*;

public class Window {
	public enum VisibleState {
		VISIBLE, INVISIBLE;

		public boolean isVisible() {
			return this == VISIBLE;
		}

		public static VisibleState fromBoolean(boolean visible) {
			return visible ? VISIBLE : INVISIBLE;
		}
	}

	public enum FocusState {
		FOCUSED, UNFOCUSED;

		public boolean isFocused() {
			return this == FOCUSED;
		}

		public static FocusState fromBoolean(boolean focused) {
			return focused ? FOCUSED : UNFOCUSED;
		}
	}

	public enum IconifyState {
		ICONIFIED, RESTORED;

		public boolean isIconified() {
			return this == ICONIFIED;
		}

		public static IconifyState fromBoolean(boolean iconified) {
			return iconified ? ICONIFIED : RESTORED;
		}
	}

	public enum MaximizeState {
		MAXIMIZED, RESTORED;

		public boolean isMaximized() {
			return this == MAXIMIZED;
		}

		public static MaximizeState fromBoolean(boolean maximized) {
			return maximized ? MAXIMIZED : RESTORED;
		}
	}

	private static VisibleState visibleState;

	private static FocusState focusState;

	private static IconifyState iconifyState;

	private static MaximizeState maximizeState;

	static {
		visibleState = VisibleState.fromBoolean(GLFW.glfwGetWindowAttrib(GLFW.glfwGetCurrentContext(), GLFW.GLFW_VISIBLE) == GLFW.GLFW_TRUE);
		focusState = FocusState.fromBoolean(GLFW.glfwGetWindowAttrib(GLFW.glfwGetCurrentContext(), GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE);
		iconifyState = IconifyState.fromBoolean(GLFW.glfwGetWindowAttrib(GLFW.glfwGetCurrentContext(), GLFW.GLFW_ICONIFIED) == GLFW.GLFW_TRUE);
		maximizeState = MaximizeState.fromBoolean(GLFW.glfwGetWindowAttrib(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MAXIMIZED) == GLFW.GLFW_TRUE);

		InputEvent.Callbacks.Window.EVENT.register((event) -> {
			if (event == InputEvent.WINDOW_CLOSED) {
				visibleState = VisibleState.fromBoolean(false);
			} else if (event == InputEvent.WINDOW_GAINED_FOCUS) {
				focusState = FocusState.fromBoolean(true);
			} else if (event == InputEvent.WINDOW_LOST_FOCUS) {
				focusState = FocusState.fromBoolean(false);
			} else if (event == InputEvent.WINDOW_ICONIFIED) {
				iconifyState = IconifyState.fromBoolean(true);
			} else if (event == InputEvent.WINDOW_DEICONIFIED) {
				iconifyState = IconifyState.fromBoolean(false);
			} else if (event == InputEvent.WINDOW_MAXIMIZED) {
				maximizeState = MaximizeState.fromBoolean(true);
			} else if (event == InputEvent.WINDOW_DEMAXIMIZED) {
				maximizeState = MaximizeState.fromBoolean(false);
			}
		});
	}

	public static VisibleState visibleState() {
		return visibleState;
	}

	public static FocusState focusState() {
		return focusState;
	}

	public static IconifyState iconifyState() {
		return iconifyState;
	}

	public static MaximizeState maximizeState() {
		return maximizeState;
	}

	public static boolean isVisible() {
		return visibleState.isVisible();
	}

	public static boolean isFocused() {
		return focusState.isFocused();
	}

	public static boolean isIconified() {
		return iconifyState.isIconified();
	}

	public static boolean isMaximized() {
		return maximizeState.isMaximized();
	}

	static void initCloseCallback(long window) {
		GLFWWindowCloseCallback closeCallback = new GLFWWindowCloseCallback() {
			private final GLFWWindowCloseCallbackI delegate = GLFW.glfwSetWindowCloseCallback(window, null);

			@Override
			public void invoke(long window) {
				InputEvent.Callbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_CLOSED);

				if (delegate != null) {
					delegate.invoke(window);
				}
			}
		};

		GLFW.glfwSetWindowCloseCallback(window, closeCallback);
	}

	static void initFocusCallback(long window) {
		GLFWWindowFocusCallback focusCallback = new GLFWWindowFocusCallback() {
			private final GLFWWindowFocusCallbackI delegate = GLFW.glfwSetWindowFocusCallback(window, null);

			@Override
			public void invoke(long window, boolean focused) {
				if (focused) {
					InputEvent.Callbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_GAINED_FOCUS);
				} else {
					InputEvent.Callbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_LOST_FOCUS);
				}

				if (delegate != null) {
					delegate.invoke(window, focused);
				}
			}
		};

		GLFW.glfwSetWindowFocusCallback(window, focusCallback);
	}

	static void initIconifyCallback(long window) {
		GLFWWindowIconifyCallback iconifyCallback = new GLFWWindowIconifyCallback() {
			private final GLFWWindowIconifyCallbackI delegate = GLFW.glfwSetWindowIconifyCallback(window, null);

			@Override
			public void invoke(long window, boolean iconified) {
				if (iconified) {
					InputEvent.Callbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_ICONIFIED);
				} else {
					InputEvent.Callbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_DEICONIFIED);
				}

				if (delegate != null) {
					delegate.invoke(window, iconified);
				}
			}
		};

		GLFW.glfwSetWindowIconifyCallback(window, iconifyCallback);
	}

	static void initMaximizeCallback(long window) {
		GLFWWindowMaximizeCallback maximizeCallback = new GLFWWindowMaximizeCallback() {
			private final GLFWWindowMaximizeCallbackI delegate = GLFW.glfwSetWindowMaximizeCallback(window, null);

			@Override
			public void invoke(long window, boolean maximized) {
				if (maximized) {
					InputEvent.Callbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_MAXIMIZED);
				} else {
					InputEvent.Callbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_DEMAXIMIZED);
				}

				if (delegate != null) {
					delegate.invoke(window, maximized);
				}
			}
		};

		GLFW.glfwSetWindowMaximizeCallback(window, maximizeCallback);
	}

	static void initPositionCallback(long window) {
		GLFWWindowPosCallback positionCallback = new GLFWWindowPosCallback() {
			private final GLFWWindowPosCallbackI delegate = GLFW.glfwSetWindowPosCallback(window, null);

			@Override
			public void invoke(long window, int x, int y) {
				InputEvent.Callbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_STATE_CHANGED);
				InputEvent.Callbacks.WindowPosition.EVENT.invoker().onWindowPosition(x, y);

				if (delegate != null) {
					delegate.invoke(window, x, y);
				}
			}
		};

		GLFW.glfwSetWindowPosCallback(window, positionCallback);
	}

	static void initSizeCallback(long window) {
		GLFWWindowSizeCallback sizeCallback = new GLFWWindowSizeCallback() {
			private final GLFWWindowSizeCallbackI delegate = GLFW.glfwSetWindowSizeCallback(window, null);

			@Override
			public void invoke(long window, int width, int height) {
				InputEvent.Callbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_RESIZED);
				InputEvent.Callbacks.WindowSize.EVENT.invoker().onWindowSize(width, height);

				if (delegate != null) {
					delegate.invoke(window, width, height);
				}
			}
		};

		GLFW.glfwSetWindowSizeCallback(window, sizeCallback);
	}
}
