package net.krlite.equator.input;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.krlite.equator.Equator;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

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

	public interface Callbacks {
		interface Mouse {
			Event<Mouse> EVENT = EventFactory.createArrayBacked(Mouse.class, (listeners) -> (event, button, modifiers) -> {
				for (Mouse listener : listeners) {
					listener.onMouse(event, button, modifiers);
				}
			});

			void onMouse(InputEvent event, int button, net.krlite.equator.input.Keyboard.Modifier[] modifiers);
		}

		interface MouseScroll {
			Event<MouseScroll> EVENT = EventFactory.createArrayBacked(MouseScroll.class, (listeners) -> (horizontal, vertical) -> {
				for (MouseScroll listener : listeners) {
					listener.onMouseScroll(horizontal, vertical);
				}
			});

			void onMouseScroll(double horizontal, double vertical);
		}

		interface MouseDrag {
			Event<MouseDrag> EVENT = EventFactory.createArrayBacked(MouseDrag.class, (listeners) -> (button, x, y) -> {
				for (MouseDrag listener : listeners) {
					listener.onMouseDrag(button, x, y);
				}
			});

			void onMouseDrag(int button, double x, double y);
		}

		interface MouseDrop {
			Event<MouseDrop> EVENT = EventFactory.createArrayBacked(MouseDrop.class, (listeners) -> (count, paths) -> {
				for (MouseDrop listener : listeners) {
					listener.onMouseDrop(count, paths);
				}
			});

			void onMouseDrop(int count, Path[] paths);
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
			Event<Keyboard> EVENT = EventFactory.createArrayBacked(Keyboard.class, (listeners) -> (event, keyCode, scanCode, modifiers) -> {
				for (Keyboard listener : listeners) {
					listener.onKeyboard(event, keyCode, scanCode, modifiers);
				}
			});

			void onKeyboard(InputEvent event, int keyCode, int scanCode, net.krlite.equator.input.Keyboard.Modifier[] modifiers);
		}

		interface Char {
			Event<Char> EVENT = EventFactory.createArrayBacked(Char.class, (listeners) -> (codePoint, modifiers) -> {
				for (Char listener : listeners) {
					listener.onChar(codePoint, modifiers);
				}
			});

			void onChar(int codePoint, net.krlite.equator.input.Keyboard.Modifier[] modifiers);
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

	private static final AtomicBoolean initialized = new AtomicBoolean(false);
	
	public static void initCallbacks(long window) {
		if (!initialized.getAndSet(true)) {
			// Mouse
			Mouse.initMouseCallback(window);
			Mouse.initMouseScrollCallback(window);
			Mouse.initMouseDropCallback(window);

			Mouse.initCursorPositionCallback(window);
			Mouse.initCursorEnterCallback(window);

			// Keyboard
			Keyboard.initCallback(window);
			Keyboard.initCharCallback(window);

			// Window
			Window.initCloseCallback(window);
			Window.initFocusCallback(window);
			Window.initIconifyCallback(window);
			Window.initMaximizeCallback(window);
			Window.initPositionCallback(window);
			Window.initSizeCallback(window);
		}
		else {
			Equator.LOGGER.warn("Input callbacks have already been initialized! Reinitializing callbacks is forbidden.");
		}
	}
}
