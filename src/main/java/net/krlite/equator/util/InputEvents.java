package net.krlite.equator.util;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.*;

import java.io.File;

public class InputEvents {
	public interface InputCallbacks {
		interface Mouse {
			Event<Mouse> EVENT = EventFactory.createArrayBacked(Mouse.class, (listeners) -> (event, button, mods) -> {
				for (Mouse listener : listeners) {
					listener.onMouse(event, button, mods);
				}
			});

			void onMouse(InputEvent event, int button, int mods);
		}

		interface MouseScroll {
			Event<MouseScroll> EVENT = EventFactory.createArrayBacked(MouseScroll.class, (listeners) -> (xOffset, yOffset) -> {
				for (MouseScroll listener : listeners) {
					listener.onMouseScroll(xOffset, yOffset);
				}
			});

			void onMouseScroll(double xOffset, double yOffset);
		}

		interface MouseDrag {
			Event<MouseDrag> EVENT = EventFactory.createArrayBacked(MouseDrag.class, (listeners) -> (button, x, y) -> {
				for (MouseDrag listener : listeners) {
					listener.onMouseDrag(button, x, y);
				}
			});

			void onMouseDrag(int button, double x, double y);
		}

		interface CursorPosition {
			Event<CursorPosition> EVENT = EventFactory.createArrayBacked(CursorPosition.class, (listeners) -> (x, y) -> {
				for (CursorPosition listener : listeners) {
					listener.onCursorPosition(x, y);
				}
			});

			void onCursorPosition(double x, double y);
		}

		interface CursorEnter {
			Event<CursorEnter> EVENT = EventFactory.createArrayBacked(CursorEnter.class, (listeners) -> (entered) -> {
				for (CursorEnter listener : listeners) {
					listener.onCursorEnter(entered);
				}
			});

			void onCursorEnter(boolean entered);
		}

		interface Keyboard {
			Event<Keyboard> EVENT = EventFactory.createArrayBacked(Keyboard.class, (listeners) -> (event, keyCode, scanCode, mods) -> {
				for (Keyboard listener : listeners) {
					listener.onKeyboard(event, keyCode, scanCode, mods);
				}
			});

			void onKeyboard(InputEvent event, int keyCode, int scanCode, int mods);
		}

		interface Window {
			Event<Window> EVENT = EventFactory.createArrayBacked(Window.class, (listeners) -> (event) -> {
				for (Window listener : listeners) {
					listener.onWindow(event);
				}
			});

			void onWindow(InputEvent event);
		}

		interface WindowPosition {
			Event<WindowPosition> EVENT = EventFactory.createArrayBacked(WindowPosition.class, (listeners) -> (x, y) -> {
				for (WindowPosition listener : listeners) {
					listener.onWindowPosition(x, y);
				}
			});

			void onWindowPosition(int x, int y);
		}

		interface WindowSize {
			Event<WindowSize> EVENT = EventFactory.createArrayBacked(WindowSize.class, (listeners) -> (width, height) -> {
				for (WindowSize listener : listeners) {
					listener.onWindowSize(width, height);
				}
			});

			void onWindowSize(int width, int height);
		}
	}

	public enum InputEvent {
		// Mouse
		MOUSE_PRESSED,
		MOUSE_RELEASED,
		MOUSE_SCROLLED,

		// Keyboard
		KEY_PRESSED,
		KEY_RELEASED,
		KEY_TYPED,

		// Window
		WINDOW_RESIZED,
		WINDOW_CLOSED,
		WINDOW_ICONIFIED,
		WINDOW_DEICONIFIED,
		WINDOW_MAXIMIZED,
		WINDOW_DEMAXIMIZED,
		WINDOW_GAINED_FOCUS,
		WINDOW_LOST_FOCUS,
		WINDOW_STATE_CHANGED;

		private final String type, alias;

		InputEvent() {
			this.type = name().substring(0, name().indexOf('_'));
			this.alias = name().substring(name().indexOf('_') + 1);
		}

		public String type() {
			return type;
		}

		public String alias() {
			return alias;
		}
	}

	public static class Mouse {
		public static boolean isMouseDown(int button) {
			return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), button) == GLFW.GLFW_PRESS;
		}

		public static boolean isMouseUp(int button) {
			return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), button) == GLFW.GLFW_RELEASE;
		}

		public static void initMouseCallback(long window) {
			GLFWMouseButtonCallback mouseCallback = new GLFWMouseButtonCallback() {
				@Override
				public void invoke(long window, int button, int action, int mods) {
					System.out.println("Mouse button " + button + " " + (action == GLFW.GLFW_PRESS ? "pressed" : "released") + " at glfw time " + GLFW.glfwGetTime() + " at system time " + System.currentTimeMillis());
					if (action == GLFW.GLFW_PRESS) {
						InputCallbacks.Mouse.EVENT.invoker().onMouse(InputEvent.MOUSE_PRESSED, button, mods);
					}
					else if (action == GLFW.GLFW_RELEASE) {
						InputCallbacks.Mouse.EVENT.invoker().onMouse(InputEvent.MOUSE_RELEASED, button, mods);
					}
				}
			};

			GLFW.glfwSetMouseButtonCallback(window, mouseCallback);
		}

		public static void initMouseScrollCallback(long window) {
			GLFWScrollCallback mouseScrollCallback = new GLFWScrollCallback() {
				@Override
				public void invoke(long window, double xOffset, double yOffset) {
					InputCallbacks.Mouse.EVENT.invoker().onMouse(InputEvent.MOUSE_SCROLLED, GLFW.GLFW_MOUSE_BUTTON_MIDDLE, 0);
					InputCallbacks.MouseScroll.EVENT.invoker().onMouseScroll(xOffset, yOffset);
				}
			};

			GLFW.glfwSetScrollCallback(window, mouseScrollCallback);
		}

		public static void initCursorPositionCallback(long window) {
			GLFWCursorPosCallback cursorPositionCallback = new GLFWCursorPosCallback() {
				@Override
				public void invoke(long window, double x, double y) {
					InputCallbacks.CursorPosition.EVENT.invoker().onCursorPosition(x, y);
					for (int button = 0; button < GLFW.GLFW_MOUSE_BUTTON_LAST; button++) {
						if (isMouseDown(button)) {
							InputCallbacks.MouseDrag.EVENT.invoker().onMouseDrag(button, x, y);
						}
					}
				}
			};

			GLFW.glfwSetCursorPosCallback(window, cursorPositionCallback);
		}

		public static void initCursorEnterCallback(long window) {
			GLFWCursorEnterCallback cursorEnterCallback = new GLFWCursorEnterCallback() {
				@Override
				public void invoke(long window, boolean entered) {
					InputCallbacks.CursorEnter.EVENT.invoker().onCursorEnter(entered);
				}
			};

			GLFW.glfwSetCursorEnterCallback(window, cursorEnterCallback);
		}

		public static long createCursor(int width, int height, File file, int xHot, int yHot) {
			// TODO: Implement
			return 0;
		}

		public static void destroyCursor(long cursor) {
			//TODO: Implement
			//GLFW.glfwDestroyCursor(cursor);
		}
	}

	public static class Keyboard {
		public static boolean isKeyDown(int key) {
			return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
		}

		public static boolean isKeyTyped(int key) {
			return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) == GLFW.GLFW_REPEAT;
		}

		public static boolean isKeyUp(int key) {
			return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) == GLFW.GLFW_RELEASE;
		}

		public static void initKeyboardCallback(long window) {
			GLFWKeyCallback keyboardCallback = new GLFWKeyCallback() {
				@Override
				public void invoke(long window, int key, int scancode, int action, int mods) {
					if (action == GLFW.GLFW_PRESS) {
						InputCallbacks.Keyboard.EVENT.invoker().onKeyboard(InputEvent.KEY_PRESSED, key, scancode, mods);
					}
					else if (action == GLFW.GLFW_RELEASE) {
						InputCallbacks.Keyboard.EVENT.invoker().onKeyboard(InputEvent.KEY_RELEASED, key, scancode, mods);
					}
					else if (action == GLFW.GLFW_REPEAT) {
						InputCallbacks.Keyboard.EVENT.invoker().onKeyboard(InputEvent.KEY_TYPED, key, scancode, mods);
					}
				}
			};

			GLFW.glfwSetKeyCallback(window, keyboardCallback);
		}
	}

	public static class Window {
		public enum WindowStateVisible {
			VISIBLE, INVISIBLE;

			public boolean isOpen() {
				return this == VISIBLE;
			}

			public static WindowStateVisible fromBoolean(boolean visible) {
				return visible ? VISIBLE : INVISIBLE;
			}
		}

		public enum WindowStateFocus {
			FOCUSED, UNFOCUSED;

			public boolean isFocused() {
				return this == FOCUSED;
			}

			public static WindowStateFocus fromBoolean(boolean focused) {
				return focused ? FOCUSED : UNFOCUSED;
			}
		}

		public enum WindowStateIconify {
			ICONIFIED, RESTORED;

			public boolean isIconified() {
				return this == ICONIFIED;
			}

			public static WindowStateIconify fromBoolean(boolean iconified) {
				return iconified ? ICONIFIED : RESTORED;
			}
		}

		public enum WindowStateMaximize {
			MAXIMIZED, RESTORED;

			public boolean isMaximized() {
				return this == MAXIMIZED;
			}

			public static WindowStateMaximize fromBoolean(boolean maximized) {
				return maximized ? MAXIMIZED : RESTORED;
			}
		}

		private static WindowStateVisible windowStateVisible;

		private static WindowStateFocus windowStateFocus;

		private static WindowStateIconify windowStateIconify;

		private static WindowStateMaximize windowStateMaximize;

		static {
			windowStateVisible = WindowStateVisible.fromBoolean(GLFW.glfwGetWindowAttrib(GLFW.glfwGetCurrentContext(), GLFW.GLFW_VISIBLE) == GLFW.GLFW_TRUE);
			windowStateFocus = WindowStateFocus.fromBoolean(GLFW.glfwGetWindowAttrib(GLFW.glfwGetCurrentContext(), GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE);
			windowStateIconify = WindowStateIconify.fromBoolean(GLFW.glfwGetWindowAttrib(GLFW.glfwGetCurrentContext(), GLFW.GLFW_ICONIFIED) == GLFW.GLFW_TRUE);
			windowStateMaximize = WindowStateMaximize.fromBoolean(GLFW.glfwGetWindowAttrib(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MAXIMIZED) == GLFW.GLFW_TRUE);

			InputCallbacks.Window.EVENT.register((event) -> {
				if (event == InputEvent.WINDOW_CLOSED) {
					windowStateVisible = WindowStateVisible.fromBoolean(false);
				}
				else if (event == InputEvent.WINDOW_GAINED_FOCUS) {
					windowStateFocus = WindowStateFocus.fromBoolean(true);
				}
				else if (event == InputEvent.WINDOW_LOST_FOCUS) {
					windowStateFocus = WindowStateFocus.fromBoolean(false);
				}
				else if (event == InputEvent.WINDOW_ICONIFIED) {
					windowStateIconify = WindowStateIconify.fromBoolean(true);
				}
				else if (event == InputEvent.WINDOW_DEICONIFIED) {
					windowStateIconify = WindowStateIconify.fromBoolean(false);
				}
				else if (event == InputEvent.WINDOW_MAXIMIZED) {
					windowStateMaximize = WindowStateMaximize.fromBoolean(true);
				}
				else if (event == InputEvent.WINDOW_DEMAXIMIZED) {
					windowStateMaximize = WindowStateMaximize.fromBoolean(false);
				}
			});
		}

		public static WindowStateVisible getWindowStateGeneral() {
			return windowStateVisible;
		}

		public static WindowStateFocus getWindowStateFocus() {
			return windowStateFocus;
		}

		public static WindowStateIconify getWindowStateIconify() {
			return windowStateIconify;
		}

		public static WindowStateMaximize getWindowStateMaximize() {
			return windowStateMaximize;
		}

		public static boolean isWindowOpen() {
			return windowStateVisible.isOpen();
		}

		public static boolean isWindowFocused() {
			return windowStateFocus.isFocused();
		}

		public static boolean isWindowIconified() {
			return windowStateIconify.isIconified();
		}

		public static boolean isWindowMaximized() {
			return windowStateMaximize.isMaximized();
		}

		public static void initWindowCloseCallback(long window) {
			GLFWWindowCloseCallback windowCloseCallback = new GLFWWindowCloseCallback() {
				@Override
				public void invoke(long window) {
					InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_CLOSED);
				}
			};

			GLFW.glfwSetWindowCloseCallback(window, windowCloseCallback);
		}

		public static void initWindowFocusCallback(long window) {
			GLFWWindowFocusCallback windowFocusCallback = new GLFWWindowFocusCallback() {
				@Override
				public void invoke(long window, boolean focused) {
					if (focused) {
						InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_GAINED_FOCUS);
					}
					else {
						InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_LOST_FOCUS);
					}
				}
			};

			GLFW.glfwSetWindowFocusCallback(window, windowFocusCallback);
		}

		public static void initWindowIconifyCallback(long window) {
			GLFWWindowIconifyCallback windowIconifyCallback = new GLFWWindowIconifyCallback() {
				@Override
				public void invoke(long window, boolean iconified) {
					if (iconified) {
						InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_ICONIFIED);
					}
					else {
						InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_DEICONIFIED);
					}
				}
			};

			GLFW.glfwSetWindowIconifyCallback(window, windowIconifyCallback);
		}

		public static void initWindowMaximizeCallback(long window) {
			GLFWWindowMaximizeCallback windowMaximizeCallback = new GLFWWindowMaximizeCallback() {
				@Override
				public void invoke(long window, boolean maximized) {
					if (maximized)
						InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_MAXIMIZED);
					else
						InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_DEMAXIMIZED);
				}
			};

			GLFW.glfwSetWindowMaximizeCallback(window, windowMaximizeCallback);
		}

		public static void initWindowPositionCallback(long window) {
			GLFWWindowPosCallback windowPositionCallback = new GLFWWindowPosCallback() {
				@Override
				public void invoke(long window, int x, int y) {
					InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_STATE_CHANGED);
					InputCallbacks.WindowPosition.EVENT.invoker().onWindowPosition(x, y);
				}
			};

			GLFW.glfwSetWindowPosCallback(window, windowPositionCallback);
		}

		public static void initWindowSizeCallback(long window) {
			GLFWWindowSizeCallback windowSizeCallback = new GLFWWindowSizeCallback() {
				@Override
				public void invoke(long window, int width, int height) {
					InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_RESIZED);
					InputCallbacks.WindowSize.EVENT.invoker().onWindowSize(width, height);
				}
			};

			GLFW.glfwSetWindowSizeCallback(window, windowSizeCallback);
		}
	}
}
