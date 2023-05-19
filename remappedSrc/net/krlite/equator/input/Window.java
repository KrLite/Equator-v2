package net.krlite.equator.input;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.krlite.equator.Equator;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <h1>Window</h1>
 * Provides access to the window's properties and callbacks.
 * @see Callbacks
 * @see GLFW
 */
public class Window {
	/**
	 * Callbacks for window events.
	 * @see Window
	 */
	public static class Callbacks {
		/**
		 * Callback for the {@link Window} close event.
		 */
		public interface Close {
			Event<Close> EVENT = EventFactory.createArrayBacked(Close.class, (listeners) -> () -> {
				for (Close listener : listeners) {
					listener.onClose();
				}
			});

			/**
			 * Called when the window is closed.
			 */
			void onClose();
		}

		/**
		 * Callback for the {@link Window} iconify event.
		 */
		public interface Iconify {
			Event<Iconify> EVENT = EventFactory.createArrayBacked(Iconify.class, (listeners) -> (iconified) -> {
				for (Iconify listener : listeners) {
					listener.onIconify(iconified);
				}
			});

			/**
			 * Called when the window is iconified.
			 * @param iconified	{@code true} if the window is iconified, {@code false} otherwise.
			 */
			void onIconify(boolean iconified);
		}

		/**
		 * Callback for the {@link Window} maximize event.
		 */
		public interface Maximize {
			Event<Maximize> EVENT = EventFactory.createArrayBacked(Maximize.class, (listeners) -> (maximized) -> {
				for (Maximize listener : listeners) {
					listener.onMaximize(maximized);
				}
			});

			/**
			 * Called when the window is maximized.
			 * @param maximized	{@code true} if the window is maximized, {@code false} otherwise.
			 */
			void onMaximize(boolean maximized);
		}

		/**
		 * Callback for the {@link Window} focus event.
		 */
		public interface Focus {
			Event<Focus> EVENT = EventFactory.createArrayBacked(Focus.class, (listeners) -> (focused) -> {
				for (Focus listener : listeners) {
					listener.onFocus(focused);
				}
			});

			/**
			 * Called when the window is focused.
			 * @param focused	{@code true} if the window is focused, {@code false} otherwise.
			 */
			void onFocus(boolean focused);
		}

		/**
		 * Callback for the {@link Window} move event, while the {@code position} is in the
		 * {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
		 * @see net.krlite.equator.render.frame.FrameInfo.Convertor
		 */
		public interface Move {
			Event<Move> EVENT = EventFactory.createArrayBacked(Move.class, (listeners) -> (position) -> {
				for (Move listener : listeners) {
					listener.onMove(position);
				}
			});

			/**
			 * Called when the window is moved.
			 * @param position	The new position of the window, in the
			 * 					{@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
			 * @see net.krlite.equator.render.frame.FrameInfo.Convertor
			 */
			void onMove(Vector position);
		}

		/**
		 * Callback for the {@link Window} resize event, while the {@code window} is in the
		 * {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
		 * @see net.krlite.equator.render.frame.FrameInfo.Convertor
		 */
		public interface Resize {
			Event<Resize> EVENT = EventFactory.createArrayBacked(Resize.class, (listeners) -> (window) -> {
				for (Resize listener : listeners) {
					listener.onResize(window);
				}
			});

			/**
			 * Called when the window is resized.
			 * @param width		The new width of the window.
			 * @param height	The new height of the window.
			 */
			void onResize(Box window);
		}

		/**
		 * Callback for the {@link Window} content scale event.
		 */
		public interface ContentScale {
			Event<ContentScale> EVENT = EventFactory.createArrayBacked(ContentScale.class, (listeners) -> (xScaling, yScaling) -> {
				for (ContentScale listener : listeners) {
					listener.onContentScale(xScaling, yScaling);
				}
			});

			/**
			 * Called when the window's content scale is changed.
			 * @param xScaling	The new {@code x-scaling} of the window.
			 * @param yScaling	The new {@code y-scaling} of the window.
			 */
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
				Callbacks.Move.EVENT.invoker().onMove(Vector.fromCartesian(x, y).fitFromScreen());

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
				Callbacks.Resize.EVENT.invoker().onResize(Box.fromCartesian(width, height).fitFromScreen());

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
