package net.krlite.equator.render.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.math.geometry.Box;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * <h1>Scissor</h1>
 * Represents a scissor in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Screen Coordinate}.
 * @param box	The scissor box.
 */
public record Scissor(Box box) {
	/**
	 * Gets the scissor box.
	 * @return	The scissor box.
	 */
	@Override
	public Box box() {
		return box;
	}

	/**
	 * Mutates the scissor box.
	 * @param box	The scissor box.
	 * @return	A new scissor with the given scissor box.
	 */
	public Scissor box(Box box) {
		return new Scissor(box);
	}

	/**
	 * Mutates the scissor box by applying the given operator.
	 * @param box	The operator to apply.
	 * @return	A new scissor with the mutated scissor box.
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

	/**
	 * Snips and renders the given renderable. Equivalent to:
	 * <pre>
	 *     snipOn();
	 *     // Renders the renderable
	 *     snipOff();
	 * </pre>
	 * @param renderable	The renderable to render. For example, a {@link net.krlite.equator.render.BoxRenderer BoxRenderer}.
	 * @param consumer		The consumer to render the renderable. For example, <code>(renderable) -> ((BoxRenderer)
	 *                      renderable).render(...)</code>, which is a consumer that calls
	 *                      {@link net.krlite.equator.render.BoxRenderer#render(MatrixStack) BoxRenderer.render(MatrixStack)}.
	 */
	public void snipWith(Renderable renderable, Consumer<Renderable> consumer) {
		snipOn();
		consumer.accept(renderable);
		snipOff();
	}
}
