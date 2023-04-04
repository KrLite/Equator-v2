package net.krlite.equator.input;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.krlite.equator.Equator;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class Window {
	public static class Callbacks {
		public interface Close {
			Event<Close> EVENT = EventFactory.createArrayBacked(Close.class, (listeners) -> () -> {
				for (Close listener : listeners) {
					listener.onClose();
				}
			});

			void onClose();
		}

		public interface Iconify {
			Event<Iconify> EVENT = EventFactory.createArrayBacked(Iconify.class, (listeners) -> (iconified) -> {
				for (Iconify listener : listeners) {
					listener.onIconify(iconified);
				}
			});

			void onIconify(boolean iconified);
		}

		public interface Maximize {
			Event<Maximize> EVENT = EventFactory.createArrayBacked(Maximize.class, (listeners) -> (maximized) -> {
				for (Maximize listener : listeners) {
					listener.onMaximize(maximized);
				}
			});

			void onMaximize(boolean maximized);
		}

		public interface Focus {
			Event<Focus> EVENT = EventFactory.createArrayBacked(Focus.class, (listeners) -> (focused) -> {
				for (Focus listener : listeners) {
					listener.onFocus(focused);
				}
			});

			void onFocus(boolean focused);
		}

		public interface Move {
			Event<Move> EVENT = EventFactory.createArrayBacked(Move.class, (listeners) -> (x, y) -> {
				for (Move listener : listeners) {
					listener.onMove(x, y);
				}
			});

			void onMove(int x, int y);
		}

		public interface Resize {
			Event<Resize> EVENT = EventFactory.createArrayBacked(Resize.class, (listeners) -> (width, height) -> {
				for (Resize listener : listeners) {
					listener.onResize(width, height);
				}
			});

			void onResize(int width, int height);
		}

		public interface ContentScale {
			Event<ContentScale> EVENT = EventFactory.createArrayBacked(ContentScale.class, (listeners) -> (xScaling, yScaling) -> {
				for (ContentScale listener : listeners) {
					listener.onContentScale(xScaling, yScaling);
				}
			});

			void onContentScale(float xScaling, float yScaling);
		}
	}

	public static boolean isVisible() {
		return GLFW.glfwGetWindowAttrib(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_VISIBLE) == GLFW.GLFW_TRUE;
	}

	public static boolean isIconified() {
		return GLFW.glfwGetWindowAttrib(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_ICONIFIED) == GLFW.GLFW_TRUE;
	}

	public static boolean isMaximized() {
		return GLFW.glfwGetWindowAttrib(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MAXIMIZED) == GLFW.GLFW_TRUE;
	}

	public static boolean isFocused() {
		return GLFW.glfwGetWindowAttrib(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
	}

	public static boolean isHovered() {
		return GLFW.glfwGetWindowAttrib(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_HOVERED) == GLFW.GLFW_TRUE;
	}

	private static final AtomicBoolean initialized = new AtomicBoolean(false);

	public static void initCallbacks(long window) {
		if (!initialized.compareAndSet(false, true)) {
			Equator.LOGGER.warn("Window callbacks have already been initialized!");
			return;
		}
		initCloseCallback(window);
		initIconifyCallback(window);
		initMaximizeCallback(window);
		initFocusCallback(window);
		initMoveCallback(window);
		initResizeCallback(window);
		initContentScaleCallback(window);
	}

	static void initCloseCallback(long window) {
		GLFWWindowCloseCallback closeCallback = new GLFWWindowCloseCallback() {
			private final GLFWWindowCloseCallbackI delegate = GLFW.glfwSetWindowCloseCallback(window, null);

			@Override
			public void invoke(long window) {
				Callbacks.Close.EVENT.invoker().onClose();

				if (delegate != null) {
					delegate.invoke(window);
				}
			}
		};

		GLFW.glfwSetWindowCloseCallback(window, closeCallback);
	}

	static void initIconifyCallback(long window) {
		GLFWWindowIconifyCallback iconifyCallback = new GLFWWindowIconifyCallback() {
			private final GLFWWindowIconifyCallbackI delegate = GLFW.glfwSetWindowIconifyCallback(window, null);

			@Override
			public void invoke(long window, boolean iconified) {
				Callbacks.Iconify.EVENT.invoker().onIconify(iconified);

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
				Callbacks.Maximize.EVENT.invoker().onMaximize(maximized);

				if (delegate != null) {
					delegate.invoke(window, maximized);
				}
			}
		};

		GLFW.glfwSetWindowMaximizeCallback(window, maximizeCallback);
	}

	static void initFocusCallback(long window) {
		GLFWWindowFocusCallback focusCallback = new GLFWWindowFocusCallback() {
			private final GLFWWindowFocusCallbackI delegate = GLFW.glfwSetWindowFocusCallback(window, null);

			@Override
			public void invoke(long window, boolean focused) {
				Callbacks.Focus.EVENT.invoker().onFocus(focused);

				if (delegate != null) {
					delegate.invoke(window, focused);
				}
			}
		};

		GLFW.glfwSetWindowFocusCallback(window, focusCallback);
	}

	static void initMoveCallback(long window) {
		GLFWWindowPosCallback moveCallback = new GLFWWindowPosCallback() {
			private final GLFWWindowPosCallbackI delegate = GLFW.glfwSetWindowPosCallback(window, null);

			@Override
			public void invoke(long window, int x, int y) {
				Callbacks.Move.EVENT.invoker().onMove(x, y);

				if (delegate != null) {
					delegate.invoke(window, x, y);
				}
			}
		};

		GLFW.glfwSetWindowPosCallback(window, moveCallback);
	}

	static void initResizeCallback(long window) {
		GLFWWindowSizeCallback resizeCallback = new GLFWWindowSizeCallback() {
			private final GLFWWindowSizeCallbackI delegate = GLFW.glfwSetWindowSizeCallback(window, null);

			@Override
			public void invoke(long window, int width, int height) {
				Callbacks.Resize.EVENT.invoker().onResize(width, height);

				if (delegate != null) {
					delegate.invoke(window, width, height);
				}
			}
		};

		GLFW.glfwSetWindowSizeCallback(window, resizeCallback);
	}

	static void initContentScaleCallback(long window) {
		GLFWWindowContentScaleCallback contentScaleCallback = new GLFWWindowContentScaleCallback() {
			private final GLFWWindowContentScaleCallbackI delegate = GLFW.glfwSetWindowContentScaleCallback(window, null);

			@Override
			public void invoke(long window, float xScaling, float yScaling) {
				Callbacks.ContentScale.EVENT.invoker().onContentScale(xScaling, yScaling);

				if (delegate != null) {
					delegate.invoke(window, xScaling, yScaling);
				}
			}
		};

		GLFW.glfwSetWindowContentScaleCallback(window, contentScaleCallback);
	}
}
