package net.krlite.equator.input;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.krlite.equator.Equator;
import net.krlite.equator.math.geometry.Vector;
import net.krlite.equator.render.frame.FrameInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <h1>Mouse</h1>
 * Provides access to the mouse properties and callbacks.
 * @see Callbacks
 * @see Cursor
 * @see GLFW
 */
public enum Mouse {
	LEFT(GLFW.GLFW_MOUSE_BUTTON_LEFT),
	RIGHT(GLFW.GLFW_MOUSE_BUTTON_RIGHT),
	MIDDLE(GLFW.GLFW_MOUSE_BUTTON_MIDDLE),
	EXTRA_1(GLFW.GLFW_MOUSE_BUTTON_4),
	EXTRA_2(GLFW.GLFW_MOUSE_BUTTON_5),
	EXTRA_3(GLFW.GLFW_MOUSE_BUTTON_6),
	EXTRA_4(GLFW.GLFW_MOUSE_BUTTON_7),
	EXTRA_5(GLFW.GLFW_MOUSE_BUTTON_8);

	private final int value;

	Mouse(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public static Mouse getButton(int button) {
		return Arrays.stream(values()).filter(btn -> btn.value() == button).findFirst().orElse(null);
	}

	public enum Action {
		PRESS(GLFW.GLFW_PRESS),
		RELEASE(GLFW.GLFW_RELEASE);

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

		public static Action getAction(int action) {
			return Arrays.stream(values()).filter(act -> act.value() == action).findFirst().orElse(null);
		}
	}

	public enum Cursor {
		ARROW(GLFW.GLFW_ARROW_CURSOR),
		I_BEAM(GLFW.GLFW_IBEAM_CURSOR),
		CROSSHAIR(GLFW.GLFW_CROSSHAIR_CURSOR),
		HAND(GLFW.GLFW_POINTING_HAND_CURSOR),
		RESIZE_HORIZONTAL(GLFW.GLFW_RESIZE_EW_CURSOR),
		RESIZE_VERTICAL(GLFW.GLFW_RESIZE_NS_CURSOR),
		RESIZE_NW_SE(GLFW.GLFW_RESIZE_NWSE_CURSOR),
		RESIZE_NE_SW(GLFW.GLFW_RESIZE_NESW_CURSOR),
		RESIZE_ALL(GLFW.GLFW_RESIZE_ALL_CURSOR),
		NOT_ALLOWED(GLFW.GLFW_NOT_ALLOWED_CURSOR);

		private final int value;

		Cursor(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static Cursor getCursor(int cursor) {
			return Arrays.stream(values()).filter(crs -> crs.value() == cursor).findFirst().orElse(null);
		}
	}

	/**
	 * Callbacks for mouse events.
	 * @see Mouse
	 */
	public static class Callbacks {
		/**
		 * Callback for the {@link Mouse} click event.
		 */
		public interface Click {
			Event<Click> EVENT = EventFactory.createArrayBacked(Click.class, (listeners) -> (button, action, modifiers) -> {
				for (Click listener : listeners) {
					listener.onClick(button, action, modifiers);
				}
			});

			/**
			 * Called when a mouse button is pressed or released.
			 * @param button	The mouse button that was pressed or released.
			 * @param action	The action that was performed.
			 * @param modifiers	The modifiers that were pressed.
			 */
			void onClick(Mouse button, Action action, Keyboard.Modifier[] modifiers);
		}

		/**
		 * Callback for the {@link Mouse} scroll event, while the <code>scroll</code> is in the
		 * {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
		 * @see net.krlite.equator.render.frame.FrameInfo.Convertor
		 */
		public interface Scroll {
			Event<Scroll> EVENT = EventFactory.createArrayBacked(Scroll.class, (listeners) -> (scroll) -> {
				for (Scroll listener : listeners) {
					listener.onScroll(scroll);
				}
			});

			/**
			 * Called when the mouse wheel is scrolled.
			 * @param scroll	The scroll amount, in the
			 *                  {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
			 * @see net.krlite.equator.render.frame.FrameInfo.Convertor
			 */
			void onScroll(Vector scroll);
		}

		/**
		 * Callback for the {@link Mouse} drag event, while the <code>position</code> is in the
		 * {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
		 * @see net.krlite.equator.render.frame.FrameInfo.Convertor
		 */
		public interface Drag {
			Event<Drag> EVENT = EventFactory.createArrayBacked(Drag.class, (listeners) -> (button, position) -> {
				for (Drag listener : listeners) {
					listener.onDrag(button, position);
				}
			});

			/**
			 * Called when the mouse is dragged.
			 * @param button	The mouse button that is being dragged.
			 * @param position	The position of the mouse, in the
			 * 					{@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
			 * @see net.krlite.equator.render.frame.FrameInfo.Convertor
			 */
			void onDrag(Mouse button, Vector position);
		}

		/**
		 * Callback for the {@link Mouse} drop event.
		 */
		public interface Drop {
			Event<Drop> EVENT = EventFactory.createArrayBacked(Drop.class, (listeners) -> (count, paths) -> {
				for (Drop listener : listeners) {
					listener.onDrop(count, paths);
				}
			});

			/**
			 * Called when files are dropped onto the window.
			 * @param count	The number of files dropped.
			 * @param paths	The paths of the files dropped.
			 */
			void onDrop(int count, Path[] paths);
		}

		/**
		 * Callback for the {@link Mouse} move event, while the <code>position</code> is in the
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
			 * Called when the mouse is moved.
			 * @param position	The position of the mouse, in the
			 * 					{@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
			 * @see net.krlite.equator.render.frame.FrameInfo.Convertor
			 */
			void onMove(Vector position);
		}

		/**
		 * Callback for the {@link Mouse} enter event.
		 */
		public interface Enter {
			Event<Enter> EVENT = EventFactory.createArrayBacked(Enter.class, (listeners) -> (entered) -> {
				for (Enter listener : listeners) {
					listener.onEnter(entered);
				}
			});

			/**
			 * Called when the mouse enters or leaves the window.
			 * @param entered	Whether the mouse entered the window.
			 */
			void onEnter(boolean entered);
		}
	}

	public static boolean isDown(Mouse button) {
		return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), button.value()) == GLFW.GLFW_PRESS;
	}

	public static boolean isUp(Mouse button) {
		return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), button.value()) == GLFW.GLFW_RELEASE;
	}

	/**
	 * @return	The position of the mouse, in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
	 * @see net.krlite.equator.render.frame.FrameInfo.Convertor
	 */
	public static Vector position() {
		double[] x = new double[1], y = new double[1];
		GLFW.glfwGetCursorPos(MinecraftClient.getInstance().getWindow().getHandle(), x, y);
		return Vector.fromCartesian(x[0], y[0]).fitFromScreen();
	}

	public static long createCursor(Identifier identifier, int xHot, int yHot) {
		Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(identifier);
		if (resource.isEmpty()) {
			Equator.LOGGER.error("Failed to find cursor texture");
			return 0;
		}
		
		try {
			InputStream stream = resource.get().getInputStream();
			BufferedImage textureImage = ImageIO.read(stream);

			int width = textureImage.getWidth();
			int height = textureImage.getHeight();
			int[] pixels = textureImage.getRGB(0, 0, width, height, null, 0, width);
			ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);

			for (int y = height - 1; y >= 0; y--) {
				for (int x = 0; x < width; x++) {
					int pixel = pixels[y * width + x];
					buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
					buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green component
					buffer.put((byte) (pixel & 0xFF));         // Blue component
					buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component
				}
			}

			buffer.flip();
			GLFWImage cursorImage = GLFWImage.create();
			cursorImage.set(width, height, buffer);

			return GLFW.glfwCreateCursor(cursorImage, xHot, yHot);
		} catch (IOException ioException) {
			Equator.LOGGER.error("Failed to read cursor texture", ioException);
			return 0;
		}
	}

	public static long createCursor(Identifier identifier) {
		return createCursor(identifier, 0, 0);
	}

	public static long createCursor(Cursor cursor) {
		return GLFW.glfwCreateStandardCursor(cursor.value());
	}

	public static void setCursor(long cursor) {
		GLFW.glfwSetCursor(MinecraftClient.getInstance().getWindow().getHandle(), cursor);
	}

	public static void setCursor(Identifier identifier, int xHot, int yHot) {
		setCursor(createCursor(identifier, xHot, yHot));
	}

	public static void setCursor(Cursor cursor) {
		setCursor(createCursor(cursor));
	}

	public static void resetCursor() {
		setCursor(Cursor.ARROW);
	}

	public static void destroyCursor(long cursor) {
		GLFW.glfwDestroyCursor(cursor);
	}

	private static final AtomicBoolean initialized = new AtomicBoolean(false);

	public static void initCallbacks(long window) {
		if (!initialized.compareAndSet(false, true)) {
			Equator.LOGGER.warn("Mouse callbacks have already been initialized!");
			return;
		}
		initMouseCallback(window);
		initScrollCallback(window);
		initDropCallback(window);
		initMoveCallback(window);
		initEnterCallback(window);
	}

	static void initMouseCallback(long window) {
		GLFWMouseButtonCallback mouseCallback = new GLFWMouseButtonCallback() {
			private final GLFWMouseButtonCallbackI delegate = GLFW.glfwSetMouseButtonCallback(window, null);

			@Override
			public void invoke(long window, int button, int action, int modifiers) {
				Callbacks.Click.EVENT.invoker().onClick(Mouse.getButton(button), Action.getAction(action), Keyboard.Modifier.getModifiers(modifiers));

				if (delegate != null) {
					delegate.invoke(window, button, action, modifiers);
				}
			}
		};

		GLFW.glfwSetMouseButtonCallback(window, mouseCallback);
	}

	static void initScrollCallback(long window) {
		GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
			private final GLFWScrollCallbackI delegate = GLFW.glfwSetScrollCallback(window, null);

			@Override
			public void invoke(long window, double horizontal, double vertical) {
				Callbacks.Scroll.EVENT.invoker().onScroll(Vector.fromCartesian(horizontal, vertical).fitFromScreen());

				if (delegate != null) {
					delegate.invoke(window, horizontal, vertical);
				}
			}
		};

		GLFW.glfwSetScrollCallback(window, scrollCallback);
	}

	static void initDropCallback(long window) {
		GLFWDropCallback dropCallback = new GLFWDropCallback() {
			private final GLFWDropCallbackI delegate = GLFW.glfwSetDropCallback(window, null);

			@Override
			public void invoke(long window, int count, long names) {
				Path[] paths = new Path[count];
				for (int i = 0; i < count; i++) {
					paths[i] = Paths.get(GLFWDropCallback.getName(names, i));
				}

				Callbacks.Drop.EVENT.invoker().onDrop(count, paths);

				if (delegate != null) {
					delegate.invoke(window, count, names);
				}
			}
		};

		GLFW.glfwSetDropCallback(window, dropCallback);
	}

	static void initMoveCallback(long window) {
		GLFWCursorPosCallback moveCallback = new GLFWCursorPosCallback() {
			private final GLFWCursorPosCallbackI delegate = GLFW.glfwSetCursorPosCallback(window, null);

			@Override
			public void invoke(long window, double x, double y) {
				Callbacks.Move.EVENT.invoker().onMove(Vector.fromCartesian(x, y).fitFromScreen());
				Arrays.stream(values()).filter(Mouse::isDown).forEach(button -> Callbacks.Drag.EVENT.invoker().onDrag(button, Vector.fromCartesian(x, y).fitFromScreen()));

				if (delegate != null) {
					delegate.invoke(window, x, y);
				}
			}
		};

		GLFW.glfwSetCursorPosCallback(window, moveCallback);
	}

	static void initEnterCallback(long window) {
		GLFWCursorEnterCallback enterCallback = new GLFWCursorEnterCallback() {
			private final GLFWCursorEnterCallbackI delegate = GLFW.glfwSetCursorEnterCallback(window, null);

			@Override
			public void invoke(long window, boolean entered) {
				Callbacks.Enter.EVENT.invoker().onEnter(entered);

				if (delegate != null) {
					delegate.invoke(window, entered);
				}
			}
		};

		GLFW.glfwSetCursorEnterCallback(window, enterCallback);
	}
}
