package net.krlite.equator.input;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.krlite.equator.Equator;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public enum Keyboard {
	// Symbols
	/**
	 * The <code>space</code> key.
	 */
	SPACE(GLFW.GLFW_KEY_SPACE),
	/**
	 * <h1><code>"</code><br /><code>'</code></h1>
	 * The <code>apostrophe(quote)</code> key.
	 */
	APOSTROPHE(GLFW.GLFW_KEY_APOSTROPHE),
	/**
	 * <h1><code><</code><br /><code>,</code></h1>
	 * The <code>comma(less than)</code> key.
	 */
	COMMA(GLFW.GLFW_KEY_COMMA),
	/**
	 * <h1><code>_</code><br /><code>-</code></h1>
	 * The <code>minus(underscore)</code> key.
	 */
	MINUS(GLFW.GLFW_KEY_MINUS),
	/**
	 * <h1><code>></code><br /><code>.</code></h1>
	 * The <code>period(greater than)</code> key.
	 */
	PERIOD(GLFW.GLFW_KEY_PERIOD),
	/**
	 * <h1><code>?</code><br /><code>/</code></h1>
	 * The <code>slash(question mark)</code> key.
	 */
	SLASH(GLFW.GLFW_KEY_SLASH),
	/**
	 * <h1><code>|</code><br /><code>\</code></h1>
	 * The <code>backslash(pipe mark)</code> key.
	 */
	BACKSLASH(GLFW.GLFW_KEY_BACKSLASH),
	/**
	 * <h1><code>:</code><br /><code>;</code></h1>
	 * The <code>semicolon(colon)</code> key.
	 */
	SEMICOLON(GLFW.GLFW_KEY_SEMICOLON),
	/**
	 * <h1><code>+</code><br /><code>=</code></h1>
	 * The <code>equal(plus)</code> key.
	 */
	EQUAL(GLFW.GLFW_KEY_EQUAL),
	/**
	 * <h1><code>{</code><br /><code>[</code></h1>
	 * The <code>left bracket(left brace)</code> key.
	 */
	LEFT_BRACKET(GLFW.GLFW_KEY_LEFT_BRACKET),
	/**
	 * <h1><code>}</code><br /><code>]</code></h1>
	 * The <code>right bracket(right brace)</code> key.
	 */
	RIGHT_BRACKET(GLFW.GLFW_KEY_RIGHT_BRACKET),
	/**
	 * <h1><code>~</code><br /><code>`</code></h1>
	 * The <code>grave accent(tilde)</code> key.
	 */
	GRAVE_ACCENT(GLFW.GLFW_KEY_GRAVE_ACCENT),

	// Numbers
	/**
	 * <h1><code>)</code><br /><code>0</code></h1>
	 * The <code>zero(right parenthesis)</code> key.
	 */
	NUM_0(GLFW.GLFW_KEY_0),
	/**
	 * <h1><code>!</code><br /><code>1</code></h1>
	 * The <code>one(exclamation mark)</code> key.
	 */
	NUM_1(GLFW.GLFW_KEY_1),
	/**
	 * <h1><code>@</code><br /><code>2</code></h1>
	 * The <code>two(at sign)</code> key.
	 */
	NUM_2(GLFW.GLFW_KEY_2),
	/**
	 * <h1><code>#</code><br /><code>3</code></h1>
	 * The <code>three(hash)</code> key.
	 */
	NUM_3(GLFW.GLFW_KEY_3),
	/**
	 * <h1><code>$</code><br /><code>4</code></h1>
	 * The <code>four(dollar sign)</code> key.
	 */
	NUM_4(GLFW.GLFW_KEY_4),
	/**
	 * <h1><code>%</code><br /><code>5</code></h1>
	 * The <code>five(percent sign)</code> key.
	 */
	NUM_5(GLFW.GLFW_KEY_5),
	/**
	 * <h1><code>^</code><br /><code>6</code></h1>
	 * The <code>six(caret)</code> key.
	 */
	NUM_6(GLFW.GLFW_KEY_6),
	/**
	 * <h1><code>&</code><br /><code>7</code></h1>
	 * The <code>seven(ampersand)</code> key.
	 */
	NUM_7(GLFW.GLFW_KEY_7),
	/**
	 * <h1><code>*</code><br /><code>8</code></h1>
	 * The <code>eight(asterisk)</code> key.
	 */
	NUM_8(GLFW.GLFW_KEY_8),
	/**
	 * <h1><code>(</code><br /><code>9</code></h1>
	 * The <code>nine(left parenthesis)</code> key.
	 */
	NUM_9(GLFW.GLFW_KEY_9),

	// Alphanumeric Keys
	A(GLFW.GLFW_KEY_A), B(GLFW.GLFW_KEY_B), C(GLFW.GLFW_KEY_C), D(GLFW.GLFW_KEY_D), E(GLFW.GLFW_KEY_E), F(GLFW.GLFW_KEY_F),
	G(GLFW.GLFW_KEY_G), H(GLFW.GLFW_KEY_H), I(GLFW.GLFW_KEY_I), J(GLFW.GLFW_KEY_J), K(GLFW.GLFW_KEY_K), L(GLFW.GLFW_KEY_L),
	M(GLFW.GLFW_KEY_M), N(GLFW.GLFW_KEY_N), O(GLFW.GLFW_KEY_O), P(GLFW.GLFW_KEY_P), Q(GLFW.GLFW_KEY_Q), R(GLFW.GLFW_KEY_R),
	S(GLFW.GLFW_KEY_S), T(GLFW.GLFW_KEY_T), U(GLFW.GLFW_KEY_U), V(GLFW.GLFW_KEY_V), W(GLFW.GLFW_KEY_W), X(GLFW.GLFW_KEY_X),
	Y(GLFW.GLFW_KEY_Y), Z(GLFW.GLFW_KEY_Z),

	// Function Keys
	/**
	 * <h1><code>↩︎</code></h1>
	 * The <code>enter</code> key.
	 */
	ENTER(GLFW.GLFW_KEY_ENTER),
	/**
	 * <h1><code>⇥</code></h1>
	 * The <code>tab</code> key.
	 */
	TAB(GLFW.GLFW_KEY_TAB),
	/**
	 * <h1><code>⌫</code></h1>
	 * The <code>backspace</code> key.
	 */
	BACKSPACE(GLFW.GLFW_KEY_BACKSPACE),
	/**
	 * <h1><code>⌦</code></h1>
	 * The <code>delete</code> key.
	 */
	DELETE(GLFW.GLFW_KEY_DELETE),
	/**
	 * <h1><code>→</code></h1>
	 * The <code>right arrow</code> key.
	 */
	RIGHT(GLFW.GLFW_KEY_RIGHT),
	/**
	 * <h1><code>←</code></h1>
	 * The <code>left arrow</code> key.
	 */
	LEFT(GLFW.GLFW_KEY_LEFT),
	/**
	 * <h1><code>↓</code></h1>
	 * The <code>down arrow</code> key.
	 */
	DOWN(GLFW.GLFW_KEY_DOWN),
	/**
	 * <h1><code>↑</code></h1>
	 * The <code>up arrow</code> key.
	 */
	UP(GLFW.GLFW_KEY_UP),
	ESCAPE(GLFW.GLFW_KEY_ESCAPE), INSERT(GLFW.GLFW_KEY_INSERT), PAGE_DOWN(GLFW.GLFW_KEY_PAGE_DOWN), PAGE_UP(GLFW.GLFW_KEY_PAGE_UP),
	HOME(GLFW.GLFW_KEY_HOME), END(GLFW.GLFW_KEY_END), PRINT_SCREEN(GLFW.GLFW_KEY_PRINT_SCREEN), PAUSE(GLFW.GLFW_KEY_PAUSE),

	F1(GLFW.GLFW_KEY_F1), F2(GLFW.GLFW_KEY_F2), F3(GLFW.GLFW_KEY_F3), F4(GLFW.GLFW_KEY_F4), F5(GLFW.GLFW_KEY_F5),
	F6(GLFW.GLFW_KEY_F6), F7(GLFW.GLFW_KEY_F7), F8(GLFW.GLFW_KEY_F8), F9(GLFW.GLFW_KEY_F9), F10(GLFW.GLFW_KEY_F10),
	F11(GLFW.GLFW_KEY_F11), F12(GLFW.GLFW_KEY_F12), F13(GLFW.GLFW_KEY_F13), F14(GLFW.GLFW_KEY_F14), F15(GLFW.GLFW_KEY_F15),
	F16(GLFW.GLFW_KEY_F16), F17(GLFW.GLFW_KEY_F17), F18(GLFW.GLFW_KEY_F18), F19(GLFW.GLFW_KEY_F19), F20(GLFW.GLFW_KEY_F20),
	F21(GLFW.GLFW_KEY_F21), F22(GLFW.GLFW_KEY_F22), F23(GLFW.GLFW_KEY_F23), F24(GLFW.GLFW_KEY_F24), F25(GLFW.GLFW_KEY_F25),

	// Keypad Numbers & Symbols
	KP_0(GLFW.GLFW_KEY_KP_0), KP_1(GLFW.GLFW_KEY_KP_1), KP_2(GLFW.GLFW_KEY_KP_2), KP_3(GLFW.GLFW_KEY_KP_3), KP_4(GLFW.GLFW_KEY_KP_4),
	KP_5(GLFW.GLFW_KEY_KP_5), KP_6(GLFW.GLFW_KEY_KP_6), KP_7(GLFW.GLFW_KEY_KP_7), KP_8(GLFW.GLFW_KEY_KP_8), KP_9(GLFW.GLFW_KEY_KP_9),

	KP_DECIMAL(GLFW.GLFW_KEY_KP_DECIMAL), KP_DIVIDE(GLFW.GLFW_KEY_KP_DIVIDE), KP_MULTIPLY(GLFW.GLFW_KEY_KP_MULTIPLY),
	KP_SUBTRACT(GLFW.GLFW_KEY_KP_SUBTRACT), KP_ADD(GLFW.GLFW_KEY_KP_ADD), KP_ENTER(GLFW.GLFW_KEY_KP_ENTER), KP_EQUAL(GLFW.GLFW_KEY_KP_EQUAL),

	// Locks
	CAPS_LOCK(GLFW.GLFW_KEY_CAPS_LOCK), SCROLL_LOCK(GLFW.GLFW_KEY_SCROLL_LOCK), NUM_LOCK(GLFW.GLFW_KEY_NUM_LOCK),

	// Modifiers
	/**
	 * <h1><code>↑</code></h1>
	 * The <code>left shift</code> key.
	 */
	LEFT_SHIFT(GLFW.GLFW_KEY_LEFT_SHIFT),
	/**
	 * <h1><code>⌃</code></h1>
	 * The <code>left control</code> key.
	 */
	LEFT_CONTROL(GLFW.GLFW_KEY_LEFT_CONTROL),
	/**
	 * <h1><code>⌥</code></h1>
	 * The <code>left alt(left option)</code> key.
	 */
	LEFT_ALT(GLFW.GLFW_KEY_LEFT_ALT),
	/**
	 * <h1><code>⌘</code></h1>
	 * The <code>left super(left windows, left command)</code> key.
	 */
	LEFT_SUPER(GLFW.GLFW_KEY_LEFT_SUPER),

	/**
	 * <h1><code>↑</code></h1>
	 * The <code>right shift</code> key.
	 */
	RIGHT_SHIFT(GLFW.GLFW_KEY_RIGHT_SHIFT),
	/**
	 * <h1><code>⌃</code></h1>
	 * The <code>right control</code> key.
	 */
	RIGHT_CONTROL(GLFW.GLFW_KEY_RIGHT_CONTROL),
	/**
	 * <h1><code>⌥</code></h1>
	 * The <code>right alt(right option)</code> key.
	 */
	RIGHT_ALT(GLFW.GLFW_KEY_RIGHT_ALT),
	/**
	 * <h1><code>⌘</code></h1>
	 * The <code>right super(right windows, right command)</code> key.
	 */
	RIGHT_SUPER(GLFW.GLFW_KEY_RIGHT_SUPER),

	WORLD_1(GLFW.GLFW_KEY_WORLD_1),
	WORLD_2(GLFW.GLFW_KEY_WORLD_2),
	/**
	 * <h1><code>☰</code></h1>
	 * The <code>menu</code> key.
	 */
	MENU(GLFW.GLFW_KEY_MENU),

	// Unknown
	/**
	 * This key is unknown.
	 */
	UNKNOWN(GLFW.GLFW_KEY_UNKNOWN);

	private final int value;

	Keyboard(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public static Keyboard getKey(int key) {
		return Arrays.stream(values()).filter(k -> k.value() == key).findFirst().orElse(null);
	}

	public enum Action {
		RELEASE(GLFW.GLFW_RELEASE),
		PRESS(GLFW.GLFW_PRESS),
		REPEAT(GLFW.GLFW_REPEAT);

		private final int value;

		Action(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public boolean isPress() {
			return this == PRESS;
		}

		public boolean isRelease() {
			return this == RELEASE;
		}

		public boolean isRepeat() {
			return this == REPEAT;
		}

		public static Action getAction(int action) {
			return Arrays.stream(values()).filter(a -> a.value() == action).findFirst().orElse(null);
		}
	}

	public static class Callbacks {
		public interface Key {
			Event<Key> EVENT = EventFactory.createArrayBacked(Key.class, (listeners) -> (key, scanCode, action, modifiers) -> {
				for (Key listener : listeners) {
					listener.onKey(key, scanCode, action, modifiers);
				}
			});

			void onKey(Keyboard key, int scanCode, Action action, Modifier[] modifiers);
		}

		public interface Char {
			Event<Char> EVENT = EventFactory.createArrayBacked(Char.class, (listeners) -> (chars, modifiers) -> {
				for (Char listener : listeners) {
					listener.onChar(chars, modifiers);
				}
			});

			void onChar(char[] chars, Modifier[] modifiers);
		}
	}

	public enum Modifier {
		/**
		 * <h1><code>↑</code></h1>
		 * The <code>shift</code> modifier.
		 */
		SHIFT(GLFW.GLFW_MOD_SHIFT),
		/**
		 * <h1><code>⌃</code></h1>
		 * The <code>control</code> modifier.
		 */
		CONTROL(GLFW.GLFW_MOD_CONTROL),
		/**
		 * <h1><code>⌥</code></h1>
		 * The <code>alt(option)</code> modifier.
		 */
		ALT(GLFW.GLFW_MOD_ALT),
		/**
		 * <h1><code>⌘</code></h1>
		 * The <code>super(windows, command)</code> modifier.
		 */
		SUPER(GLFW.GLFW_MOD_SUPER),
		CAPS_LOCK(GLFW.GLFW_MOD_CAPS_LOCK),
		NUM_LOCK(GLFW.GLFW_MOD_NUM_LOCK);

		private final int value;

		Modifier(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static Modifier[] getModifiers(int modifiers) {
			List<Modifier> list = new ArrayList<>();
			for (Modifier modifier : values()) {
				if ((modifiers & modifier.value()) != 0) {
					list.add(modifier);
				}
			}
			return list.toArray(new Modifier[0]);
		}

		public static final Modifier[] NONE = new Modifier[0];
	}

	public static boolean isDown(Keyboard key) {
		return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key.value()) == GLFW.GLFW_PRESS;
	}

	public static boolean isTyped(Keyboard key) {
		return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key.value()) == GLFW.GLFW_REPEAT;
	}

	public static boolean isUp(Keyboard key) {
		return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key.value()) == GLFW.GLFW_RELEASE;
	}

	private static final AtomicBoolean initialized = new AtomicBoolean(false);

	public static void initCallbacks(long window) {
		if (!initialized.compareAndSet(false, true)) {
			Equator.LOGGER.warn("Keyboard callbacks have already been initialized!");
			return;
		}
		initKeyCallback(window);
		initCharCallback(window);
	}

	static void initKeyCallback(long window) {
		GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
			private final GLFWKeyCallbackI delegate = GLFW.glfwSetKeyCallback(window, null);

			@Override
			public void invoke(long window, int key, int scancode, int action, int modifiers) {
				Callbacks.Key.EVENT.invoker().onKey(Keyboard.getKey(key), scancode, Action.getAction(action), Modifier.getModifiers(modifiers));

				if (delegate != null) {
					delegate.invoke(window, key, scancode, action, modifiers);
				}
			}
		};

		GLFW.glfwSetKeyCallback(window, keyCallback);
	}

	static void initCharCallback(long window) {
		GLFWCharModsCallback charCallback = new GLFWCharModsCallback() {
			private final GLFWCharModsCallbackI delegate = GLFW.glfwSetCharModsCallback(window, null);

			@Override
			public void invoke(long window, int codePoint, int modifiers) {
				Callbacks.Char.EVENT.invoker().onChar(Character.toChars(codePoint), Modifier.getModifiers(modifiers));

				if (delegate != null) {
					delegate.invoke(window, codePoint, modifiers);
				}
			}
		};

		GLFW.glfwSetCharModsCallback(window, charCallback);
	}
}
