package net.krlite.equator.render.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.math.geometry.flat.Box;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * <h1>Scissor</h1>
 * Represents a scissor in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Screen Coordinate}.
 * @param box	the scissor box.
 */
public record Scissor(Box box) {
	/**
	 * Gets the scissor box.
	 * @return	the scissor box.
	 */
	@Override
	public Box box() {
		return box;
	}

	/**
	 * Mutates the scissor box.
	 * @param box	the scissor box.
	 * @return	a new scissor with the given scissor box.
	 */
	public Scissor box(Box box) {
		return new Scissor(box);
	}

	/**
	 * Mutates the scissor box by applying the given operator.
	 * @param box	the operator to apply.
	 * @return	a new scissor with the mutated scissor box.
	 */
	public Scissor box(UnaryOperator<Box> box) {
		return new Scissor(box.apply(box()));
	}

	/**
	 * Enables snipping. Note well that the scissor box is automatically fitted to the
	 * {@link net.krlite.equator.render.frame.FrameInfo.Convertor OpenGL Coordinate}.
	 */
	public void snipOn() {
		// Fits the box to the OpenGL Coordinate
		Box fitted = box().fitToOpenGL();
		RenderSystem.enableScissor((int) fitted.x(), (int) fitted.y(), (int) fitted.w(), (int) fitted.h());
	}

	/**
	 * Disables snipping.
	 */
	public void snipOff() {
		RenderSystem.disableScissor();
	}

	public void snipWith(Renderable renderable) {
		snipOn();
		renderable.render();
		snipOff();
	}
}
