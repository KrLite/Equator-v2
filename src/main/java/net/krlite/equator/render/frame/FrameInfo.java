package net.krlite.equator.render.frame;

import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.math.geometry.Vector;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/**
 * <h1>FrameInfo</h1>
 * Provides access to the screen and window properties.
 */
public class FrameInfo {
	public static float tickDelta() {
		return MinecraftClient.getInstance().getTickDelta();
	}

	public static Box scaled() {
		return new Box(Vector.fromCartesian(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight()));
	}

	public static Box screen() {
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(MinecraftClient.getInstance().getWindow().getHandle(), width, height);
		return new Box(Vector.fromCartesian(width[0], height[0]));
	}

	public static Box openGL() {
		return Convertor.screenToOpenGL(screen());
	}

	/**
	 * Converts {@link Vector}s and {@link Box}es between different coordinate systems.
	 * <p>
	 * 		<h1>The 2 Coordinate Systems</h1>
	 * 		<p>
	 * 			<h2>Cartesian Coordinate System</h2>
	 * 			<p>
	 * 			    The <b>Cartesian Coordinate System</b> is used to represent the location of mathematical
	 * 			    objects on a plane. On the screen, it is somehow similar to the <b>x-flipped Raster Coordinate
	 * 			    System</b>.
	 * 			    <br />
	 * 			    <br />
	 * 			    <code>origin -</code> the bottom left of the screen.
	 * 			    <br />
	 * 			    <code>positive angle -</code> counter-clockwise.
	 * 			    <br />
	 * 			    <br />
	 * 				<code>
	 * 					&emsp;x<0 ↑ x>0<br />
	 * 					&emsp;y>0 | y>0<br />
	 * 					-----+----→ θ+↑<br />
	 * 					&emsp;x<0 | x>0<br />
	 * 					&emsp;y<0 | y<0
	 * 				</code>
	 * 			</p>
	 * 		</p>
	 * 		<p>
	 * 			<h2>Raster Coordinate System</h2>
	 * 			<p>
	 * 			    The <b>Raster Coordinate System</b> is used to represent the location of pixels
	 * 			    on the screen.
	 *				<br />
	 * 		        <br />
	 * 		        <code>origin -</code> the top left corner of the screen.
	 * 		        <br />
	 * 		        <code>positive angle -</code> clockwise.
	 * 			    <br />
	 * 			    <br />
	 * 				<code>
	 * 					&emsp;x<0 | x>0<br />
	 * 					&emsp;y<0 | y<0<br />
	 * 					-----+----→ θ+↓<br />
	 * 					&emsp;x<0 | x>0<br />
	 * 					&emsp;y>0 ↓ y>0
	 * 				</code>
	 * 			</p>
	 * 		</p>
	 * </p>
	 * <p>
	 * 		<h1>The 3 Coordinates</h1>
	 * 		<p>
	 * 		    <h2>Scaled Coordinate</h2>
	 * 		    <p>
	 * 		        Based on the <b>Raster Coordinate System</b> and is used by Minecraft.
	 * 		        <br />
	 * 		        <br />
	 * 		        <code>size -</code> the <b>scaled</b> size of the window.
	 * 		</p>
	 * 		<p>
	 * 		    <h2>Screen Coordinate</h2>
	 * 		    <p>
	 * 		        Based on the <b>Raster Coordinate System</b> and is used by GLFW.
	 * 		        <br />
	 * 		        <br />
	 * 		        <code>size -</code> the <b>unscaled</b> size of the window.
	 * 		    </p>
	 * 		</p>
	 * 		<p>
	 * 		    <h2>OpenGL Coordinate</h2>
	 * 		    <p>
	 * 		        Based on the <b>Cartesian Coordinate System</b> and is used by OpenGL and GLSL.
	 * 		        <br />
	 * 		        <br />
	 * 		        <code>size -</code> the size of the frame.
	 * 		    </p>
	 * 		<p>
	 * </p>
	 */
	public static class Convertor {
		public static Vector scaledToScreen(Vector vector) {
			return vector.multiply(screen().s() / scaled().s());
		}

		public static Vector screenToScaled(Vector vector) {
			return vector.multiply(scaled().s() / screen().s());
		}

		public static Vector scaledToOpenGL(Vector vector) {
			return screenToOpenGL(scaledToScreen(vector));
		}

		public static Vector openGLToScaled(Vector vector) {
			return screenToScaled(openGLToScreen(vector));
		}

		public static Vector screenToOpenGL(Vector vector) {
			return vector.y(screen().h() - vector.y()).multiply(2);
		}

		public static Vector openGLToScreen(Vector vector) {
			return vector.y(screen().h() - vector.y()).divide(2);
		}

		public static Box scaledToScreen(Box box) {
			return new Box(scaledToScreen(box.origin()), scaledToScreen(box.size()));
		}

		public static Box screenToScaled(Box box) {
			return new Box(screenToScaled(box.origin()), screenToScaled(box.size()));
		}

		public static Box scaledToOpenGL(Box box) {
			return screenToOpenGL(scaledToScreen(box));
		}

		public static Box openGLToScaled(Box box) {
			return screenToScaled(openGLToScreen(box));
		}

		public static Box screenToOpenGL(Box box) {
			return new Box(box.origin().y(screen().h() - box.origin().y() - box.h()).multiply(2), box.size().multiply(2));
		}

		public static Box openGLToScreen(Box box) {
			return new Box(box.origin().y(screen().h() - box.origin().y() - box.h()).divide(2), box.size().divide(2));
		}
	}
}
