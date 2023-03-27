package net.krlite.equator.util;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.krlite.equator.visual.texture.Texture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Arrays;

public class InputEvents {
	public interface InputCallbacks {
		interface Mouse {
			Event<Mouse> EVENT = EventFactory.createArrayBacked(Mouse.class, (listeners) -> (event, button) -> {
				for (Mouse listener : listeners)
					listener.onMouse(event, button);
			});

			void onMouse(InputEvent event, int button);
		}

		interface MouseScroll {
			Event<MouseScroll> EVENT = EventFactory.createArrayBacked(MouseScroll.class, (listeners) -> (xOffset, yOffset) -> {
				for (MouseScroll listener : listeners)
					listener.onMouseScroll(xOffset, yOffset);
			});

			void onMouseScroll(double xOffset, double yOffset);
		}

		interface MouseDrag {
			Event<MouseDrag> EVENT = EventFactory.createArrayBacked(MouseDrag.class, (listeners) -> (button, x, y) -> {
				for (MouseDrag listener : listeners)
					listener.onMouseDrag(button, x, y);
			});

			void onMouseDrag(int button, double x, double y);
		}

		interface CursorPosition {
			Event<CursorPosition> EVENT = EventFactory.createArrayBacked(CursorPosition.class, (listeners) -> (x, y) -> {
				for (CursorPosition listener : listeners)
					listener.onCursorPosition(x, y);
			});

			void onCursorPosition(double x, double y);
		}

		interface CursorEnter {
			Event<CursorEnter> EVENT = EventFactory.createArrayBacked(CursorEnter.class, (listeners) -> (entered) -> {
				for (CursorEnter listener : listeners)
					listener.onCursorEnter(entered);
			});

			void onCursorEnter(boolean entered);
		}

		interface Keyboard {
			Event<Keyboard> EVENT = EventFactory.createArrayBacked(Keyboard.class, (listeners) -> (event, keyCode) -> {
				for (Keyboard listener : listeners)
					listener.onKeyboard(event, keyCode);
			});

			void onKeyboard(InputEvent event, int keyCode);
		}

		interface Window {
			Event<Window> EVENT = EventFactory.createArrayBacked(Window.class, (listeners) -> (event) -> {
				for (Window listener : listeners)
					listener.onWindow(event);
			});

			void onWindow(InputEvent event);
		}

		interface WindowPosition {
			Event<WindowPosition> EVENT = EventFactory.createArrayBacked(WindowPosition.class, (listeners) -> (x, y) -> {
				for (WindowPosition listener : listeners)
					listener.onWindowPosition(x, y);
			});

			void onWindowPosition(int x, int y);
		}

		interface WindowSize {
			Event<WindowSize> EVENT = EventFactory.createArrayBacked(WindowSize.class, (listeners) -> (width, height) -> {
				for (WindowSize listener : listeners)
					listener.onWindowSize(width, height);
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
		public enum MouseState {
			DOWN, UP;

			public boolean isDown() {
				return this == DOWN;
			}

			public static MouseState fromBoolean(boolean down) {
				return down ? DOWN : UP;
			}
		}

		private static final MouseState[] mouseStates = new MouseState[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];

		static {
			Arrays.fill(mouseStates, MouseState.UP);
			InputCallbacks.Mouse.EVENT.register((event, button) -> {
				if (event == InputEvent.MOUSE_PRESSED)
					mouseStates[button] = MouseState.fromBoolean(false);
				else if (event == InputEvent.MOUSE_RELEASED)
					mouseStates[button] = MouseState.fromBoolean(true);
			});
		}

		public static MouseState getMouseState(int button) {
			return mouseStates[button];
		}

		public static boolean isMouseDown(int button) {
			return mouseStates[button].isDown();
		}

		public static boolean isMousePressed(int button) {
			return GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), button) == GLFW.GLFW_PRESS;
		}

		public static boolean isMouseReleased(int button) {
			return GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), button) == GLFW.GLFW_RELEASE;
		}

		private static final GLFWMouseButtonCallback mouseCallback = GLFW.glfwSetMouseButtonCallback(
				GLFW.glfwGetCurrentContext(),
				(window, button, action, mods) -> {
					if (action == GLFW.GLFW_PRESS)
						InputCallbacks.Mouse.EVENT.invoker().onMouse(InputEvent.MOUSE_PRESSED, button);
					else if (action == GLFW.GLFW_RELEASE)
						InputCallbacks.Mouse.EVENT.invoker().onMouse(InputEvent.MOUSE_RELEASED, button);
				}
		);

		private static final GLFWScrollCallback mouseScrollCallback = GLFW.glfwSetScrollCallback(
				GLFW.glfwGetCurrentContext(),
				(window, xOffset, yOffset) -> {
					InputCallbacks.Mouse.EVENT.invoker().onMouse(InputEvent.MOUSE_SCROLLED, GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
					InputCallbacks.MouseScroll.EVENT.invoker().onMouseScroll(xOffset, yOffset);
				}
		);

		private static final GLFWCursorPosCallback cursorPositionCallback = GLFW.glfwSetCursorPosCallback(
				GLFW.glfwGetCurrentContext(),
				(window, x, y) -> {
					InputCallbacks.CursorPosition.EVENT.invoker().onCursorPosition(x, y);
					for (int button = 0; button < mouseStates.length; button++)
						if (mouseStates[button] == MouseState.DOWN)
							InputCallbacks.MouseDrag.EVENT.invoker().onMouseDrag(button, x, y);
				}
		);

		public static final GLFWCursorEnterCallback cursorEnterCallback = GLFW.glfwSetCursorEnterCallback(
				GLFW.glfwGetCurrentContext(),
				(window, entered) -> InputCallbacks.CursorEnter.EVENT.invoker().onCursorEnter(entered)
		);

		public static long createCursor(int width, int height, File file, int xHot, int yHot) {
			// TODO: Implement
			return 0;
		}

		public static void destroyCursor(long cursor) {
			//TODO: Implement
			//GLFW.glfwDestroyCursor(cursor);
		}
	}

	public static class KeyBoard {
		public enum KeyState {
			DOWN, UP;

			public boolean isDown() {
				return this == DOWN;
			}

			public static KeyState fromBoolean(boolean down) {
				return down ? DOWN : UP;
			}
		}

		private static final KeyState[] keyStates = new KeyState[GLFW.GLFW_KEY_LAST + 1];

		static {
			Arrays.fill(keyStates, KeyState.UP);
			InputCallbacks.Keyboard.EVENT.register((event, key) -> {
				if (event == InputEvent.KEY_PRESSED)
					keyStates[key] = KeyState.fromBoolean(true);
				else if (event == InputEvent.KEY_RELEASED)
					keyStates[key] = KeyState.fromBoolean(false);
			});
		}

		public static KeyState getKeyState(int key) {
			return keyStates[key];
		}

		public static boolean isKeyDown(int key) {
			return keyStates[key].isDown();
		}

		public static boolean isKeyPressed(int key) {
			return GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), key) == GLFW.GLFW_PRESS;
		}

		public static boolean isKeyTyped(int key) {
			return GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), key) == GLFW.GLFW_REPEAT;
		}

		public static boolean isKeyReleased(int key) {
			return GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), key) == GLFW.GLFW_RELEASE;
		}

		private static final GLFWKeyCallback keyBoardCallback = GLFW.glfwSetKeyCallback(
				GLFW.glfwGetCurrentContext(),
				(window, key, scancode, action, mods) -> {
					if (action == GLFW.GLFW_PRESS)
						InputCallbacks.Keyboard.EVENT.invoker().onKeyboard(InputEvent.KEY_PRESSED, key);
					else if (action == GLFW.GLFW_RELEASE)
						InputCallbacks.Keyboard.EVENT.invoker().onKeyboard(InputEvent.KEY_RELEASED, key);
					else if (action == GLFW.GLFW_REPEAT)
						InputCallbacks.Keyboard.EVENT.invoker().onKeyboard(InputEvent.KEY_TYPED, key);
				}
		);
	}

	public static class Window {
		public enum WindowStateVisible {
			VISIBLE, UNVISIBLE;

			public boolean isOpen() {
				return this == VISIBLE;
			}

			public static WindowStateVisible fromBoolean(boolean visible) {
				return visible ? VISIBLE : UNVISIBLE;
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
			ICONIFIED, UNICONIFIED;

			public boolean isIconified() {
				return this == ICONIFIED;
			}

			public static WindowStateIconify fromBoolean(boolean iconified) {
				return iconified ? ICONIFIED : UNICONIFIED;
			}
		}

		public enum WindowStateMaximize {
			MAXIMIZED, UNMAXIMIZED;

			public boolean isMaximized() {
				return this == MAXIMIZED;
			}

			public static WindowStateMaximize fromBoolean(boolean maximized) {
				return maximized ? MAXIMIZED : UNMAXIMIZED;
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
				if (event == InputEvent.WINDOW_CLOSED)
					windowStateVisible = WindowStateVisible.fromBoolean(false);
				else if (event == InputEvent.WINDOW_GAINED_FOCUS)
					windowStateFocus = WindowStateFocus.fromBoolean(true);
				else if (event == InputEvent.WINDOW_LOST_FOCUS)
					windowStateFocus = WindowStateFocus.fromBoolean(false);
				else if (event == InputEvent.WINDOW_ICONIFIED)
					windowStateIconify = WindowStateIconify.fromBoolean(true);
				else if (event == InputEvent.WINDOW_DEICONIFIED)
					windowStateIconify = WindowStateIconify.fromBoolean(false);
				else if (event == InputEvent.WINDOW_MAXIMIZED)
					windowStateMaximize = WindowStateMaximize.fromBoolean(true);
				else if (event == InputEvent.WINDOW_DEMAXIMIZED)
					windowStateMaximize = WindowStateMaximize.fromBoolean(false);
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

		private static final GLFWWindowCloseCallback windowCloseCallback = GLFW.glfwSetWindowCloseCallback(
			GLFW.glfwGetCurrentContext(),
			(window) -> InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_CLOSED)
		);

		private static final GLFWWindowFocusCallback windowFocusCallback = GLFW.glfwSetWindowFocusCallback(
				GLFW.glfwGetCurrentContext(),
				(window, focused) -> {
					if (focused)
						InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_GAINED_FOCUS);
					else
						InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_LOST_FOCUS);
				}
		);

		private static final GLFWWindowIconifyCallback windowIconifyCallback = GLFW.glfwSetWindowIconifyCallback(
				GLFW.glfwGetCurrentContext(),
				(window, iconified) -> {
					if (iconified)
						InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_ICONIFIED);
					else
						InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_DEICONIFIED);
				}
		);

		private static final GLFWWindowMaximizeCallback windowMaximizeCallback = GLFW.glfwSetWindowMaximizeCallback(
				GLFW.glfwGetCurrentContext(),
				(window, maximized) -> {
					if (maximized)
						InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_MAXIMIZED);
					else
						InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_DEMAXIMIZED);
				}
		);

		private static final GLFWWindowPosCallback windowPosCallback = GLFW.glfwSetWindowPosCallback(
				GLFW.glfwGetCurrentContext(),
				(window, x, y) -> {
					InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_STATE_CHANGED);
					InputCallbacks.WindowPosition.EVENT.invoker().onWindowPosition(x, y);
				}
		);

		private static final GLFWWindowSizeCallback windowSizeCallback = GLFW.glfwSetWindowSizeCallback(
				GLFW.glfwGetCurrentContext(),
				(window, width, height) -> {
					InputCallbacks.Window.EVENT.invoker().onWindow(InputEvent.WINDOW_RESIZED);
					InputCallbacks.WindowSize.EVENT.invoker().onWindowSize(width, height);
				}
		);
	}
}
