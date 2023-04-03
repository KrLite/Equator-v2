package net.krlite.equator.input;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.*;

import java.util.ArrayList;
import java.util.List;

public class Keyboard {
	public enum Modifier {
		SHIFT(GLFW.GLFW_MOD_SHIFT),
		CONTROL(GLFW.GLFW_MOD_CONTROL),
		ALT(GLFW.GLFW_MOD_ALT),
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
	}

	public static boolean isDown(int key) {
		return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
	}

	public static boolean isTyped(int key) {
		return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) == GLFW.GLFW_REPEAT;
	}

	public static boolean isUp(int key) {
		return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) == GLFW.GLFW_RELEASE;
	}

	static void initCallback(long window) {
		GLFWKeyCallback callback = new GLFWKeyCallback() {
			private final GLFWKeyCallbackI delegate = GLFW.glfwSetKeyCallback(window, null);

			@Override
			public void invoke(long window, int key, int scancode, int action, int modifiers) {
				if (action == GLFW.GLFW_PRESS) {
					InputEvent.Callbacks.Keyboard.EVENT.invoker().onKeyboard(InputEvent.KEY_PRESSED, key, scancode, Modifier.getModifiers(modifiers));
				} else if (action == GLFW.GLFW_RELEASE) {
					InputEvent.Callbacks.Keyboard.EVENT.invoker().onKeyboard(InputEvent.KEY_RELEASED, key, scancode, Modifier.getModifiers(modifiers));
				} else if (action == GLFW.GLFW_REPEAT) {
					InputEvent.Callbacks.Keyboard.EVENT.invoker().onKeyboard(InputEvent.KEY_TYPED, key, scancode, Modifier.getModifiers(modifiers));
				}

				if (delegate != null) {
					delegate.invoke(window, key, scancode, action, modifiers);
				}
			}
		};

		GLFW.glfwSetKeyCallback(window, callback);
	}

	static void initCharCallback(long window) {
		GLFWCharModsCallback charCallback = new GLFWCharModsCallback() {
			private final GLFWCharModsCallbackI delegate = GLFW.glfwSetCharModsCallback(window, null);

			@Override
			public void invoke(long window, int codepoint, int modifiers) {
				InputEvent.Callbacks.Char.EVENT.invoker().onChar(codepoint, Modifier.getModifiers(modifiers));

				if (delegate != null) {
					delegate.invoke(window, codepoint, modifiers);
				}
			}
		};

		GLFW.glfwSetCharModsCallback(window, charCallback);
	}
}
