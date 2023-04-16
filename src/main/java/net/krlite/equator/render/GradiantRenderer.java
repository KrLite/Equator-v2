package net.krlite.equator.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.math.geometry.Vector;
import net.krlite.equator.render.base.Renderable;
import net.krlite.equator.visual.color.AccurateColor;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.util.function.UnaryOperator;

/**
 * <h1>GradiantRenderer</h1>
 * Based on a {@link Box} and contains up to 4 {@link AccurateColor}s, usually used to render a gradiant.
 * A {@link org.joml.Quaterniondc Quaternion} modifier can be applied during rendering.
 * @see Box
 * @see AccurateColor
 * @param box			The {@link Box} to render the gradiant in. If a color is not specified, it will be transparent
 *                      and equivalent to {@link AccurateColor#TRANSPARENT}. Transparent colors <b>only involve opacity</b>
 *                      in the rendering process.
 * @param topLeft		The <b>Optional</b> {@link AccurateColor} of the top left corner.
 * @param bottomLeft	The <b>Optional</b> {@link AccurateColor} of the bottom left corner.
 * @param bottomRight	The <b>Optional</b> {@link AccurateColor} of the bottom right corner.
 * @param topRight		The <b>Optional</b> {@link AccurateColor} of the top right corner.
 */
public record GradiantRenderer(
		Box box, AccurateColor topLeft, AccurateColor bottomLeft, AccurateColor bottomRight, AccurateColor topRight
) implements Renderable {
	public static GradiantRenderer readyHorizontal(Box box, AccurateColor left, AccurateColor right) {
		return new GradiantRenderer(box, left, left, right, right);
	}

	public static GradiantRenderer readyVertical(Box box, AccurateColor top, AccurateColor bottom) {
		return new GradiantRenderer(box, top, bottom, bottom, top);
	}

	public GradiantRenderer(Box box, AccurateColor color) {
		this(box, color, color, color, color);
	}

	public GradiantRenderer(Box box) {
		this(box, AccurateColor.TRANSPARENT);
	}

	@Override
	public Box box() {
		return box;
	}

	@Override
	public AccurateColor topLeft() {
		return topLeft;
	}

	@Override
	public AccurateColor bottomLeft() {
		return bottomLeft;
	}

	@Override
	public AccurateColor bottomRight() {
		return bottomRight;
	}

	@Override
	public AccurateColor topRight() {
		return topRight;
	}

	private GradiantRenderer preserve(Box box) {
		return new GradiantRenderer(box, topLeft(), bottomLeft(), bottomRight(), topRight());
	}

	public GradiantRenderer topLeft(AccurateColor topLeft) {
		return new GradiantRenderer(box(), topLeft, bottomLeft(), bottomRight(), topRight());
	}
	
	public GradiantRenderer layerTopLeft(UnaryOperator<AccurateColor> topLeft) {
		return topLeft(topLeft.apply(topLeft()));
	}

	public GradiantRenderer eraseTopLeft() {
		return topLeft(AccurateColor.TRANSPARENT);
	}

	public GradiantRenderer bottomLeft(AccurateColor bottomLeft) {
		return new GradiantRenderer(box(), topLeft(), bottomLeft, bottomRight(), topRight());
	}
	
	public GradiantRenderer layerBottomLeft(UnaryOperator<AccurateColor> bottomLeft) {
		return bottomLeft(bottomLeft.apply(bottomLeft()));
	}

	public GradiantRenderer eraseBottomLeft() {
		return bottomLeft(AccurateColor.TRANSPARENT);
	}

	public GradiantRenderer bottomRight(AccurateColor bottomRight) {
		return new GradiantRenderer(box(), topLeft(), bottomLeft(), bottomRight, topRight());
	}
	
	public GradiantRenderer layerBottomRight(UnaryOperator<AccurateColor> bottomRight) {
		return bottomRight(bottomRight.apply(bottomRight()));
	}

	public GradiantRenderer eraseBottomRight() {
		return bottomRight(AccurateColor.TRANSPARENT);
	}

	public GradiantRenderer topRight(AccurateColor topRight) {
		return new GradiantRenderer(box(), topLeft(), bottomLeft(), bottomRight(), topRight);
	}
	
	public GradiantRenderer layerTopRight(UnaryOperator<AccurateColor> topRight) {
		return topRight(topRight.apply(topRight()));
	}

	public GradiantRenderer eraseTopRight() {
		return topRight(AccurateColor.TRANSPARENT);
	}

	public GradiantRenderer top(AccurateColor top) {
		return new GradiantRenderer(box(), top, bottomLeft(), bottomRight(), top);
	}

	public GradiantRenderer layerTop(UnaryOperator<AccurateColor> top) {
		return top(top.apply(topLeft()));
	}

	public GradiantRenderer eraseTop() {
		return top(AccurateColor.TRANSPARENT);
	}

	public GradiantRenderer bottom(AccurateColor bottom) {
		return new GradiantRenderer(box(), topLeft(), bottom, bottom, topRight());
	}

	public GradiantRenderer layerBottom(UnaryOperator<AccurateColor> bottom) {
		return bottom(bottom.apply(bottomLeft()));
	}

	public GradiantRenderer eraseBottom() {
		return bottom(AccurateColor.TRANSPARENT);
	}

	public GradiantRenderer left(AccurateColor left) {
		return new GradiantRenderer(box(), left, left, bottomRight(), topRight());
	}
	
	public GradiantRenderer layerLeft(UnaryOperator<AccurateColor> left) {
		return left(left.apply(topLeft()));
	}

	public GradiantRenderer eraseLeft() {
		return left(AccurateColor.TRANSPARENT);
	}

	public GradiantRenderer right(AccurateColor right) {
		return new GradiantRenderer(box(), topLeft(), bottomLeft(), right, right);
	}
	
	public GradiantRenderer layerRight(UnaryOperator<AccurateColor> right) {
		return right(right.apply(topRight()));
	}

	public GradiantRenderer eraseRight() {
		return right(AccurateColor.TRANSPARENT);
	}

	public GradiantRenderer fill(AccurateColor color) {
		return new GradiantRenderer(box(), color);
	}
	
	public GradiantRenderer layer(UnaryOperator<AccurateColor> color) {
		return fill(color.apply(get(0.5, 0.5)));
	}

	public GradiantRenderer erase() {
		return new GradiantRenderer(box());
	}

	public AccurateColor get(double xOffset, double yOffset) {
		return new AccurateColor(
				topLeft().red() * (1 - xOffset) * (1 - yOffset) + bottomLeft().red() * (1 - xOffset) * yOffset + bottomRight().red() * xOffset * yOffset + topRight().red() * xOffset * (1 - yOffset),
				topLeft().green() * (1 - xOffset) * (1 - yOffset) + bottomLeft().green() * (1 - xOffset) * yOffset + bottomRight().green() * xOffset * yOffset + topRight().green() * xOffset * (1 - yOffset),
				topLeft().blue() * (1 - xOffset) * (1 - yOffset) + bottomLeft().blue() * (1 - xOffset) * yOffset + bottomRight().blue() * xOffset * yOffset + topRight().blue() * xOffset * (1 - yOffset),
				topLeft().opacity() * (1 - xOffset) * (1 - yOffset) + bottomLeft().opacity() * (1 - xOffset) * yOffset + bottomRight().opacity() * xOffset * yOffset + topRight().opacity() * xOffset * (1 - yOffset)
		);
	}

	public AccurateColor getCenter() {
		return get(0.5, 0.5);
	}

	public boolean existsColor() {
		return topLeft().hasColor() || topRight().hasColor() || bottomRight().hasColor() || bottomLeft().hasColor();
	}

	@Override
	public boolean isRenderable() {
		return Renderable.isBoxLegal(box()) && existsColor();
	}

	private AccurateColor[] getColors() {
		return new AccurateColor[] {
				topLeft(), bottomLeft(),
				bottomRight(), topRight()
		};
	}

	private AccurateColor assertColor(int index) {
		AccurateColor[] colors = getColors();
		if (index < 0 || index >= colors.length /* normally it would be 4 */ ) {
			throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for a gradiant with " + colors.length + " colors");
		}
		AccurateColor color = colors[index];
		if (color.hasColor()) {
			return color;
		}

		AccurateColor fallbackFirst = colors[(index + 1) % colors.length];
		AccurateColor fallbackSecond = colors[(index + colors.length - 1) % colors.length];

		if (fallbackFirst.hasColor() || fallbackSecond.hasColor()) {
			if (fallbackFirst.hasColor() && fallbackSecond.hasColor()) {
				return fallbackFirst.mix(fallbackSecond).transparent();
			}
			else if (fallbackFirst.hasColor()) {
				return fallbackFirst.transparent();
			}
			else {
				return fallbackSecond.transparent();
			}
		}
		else {
			return color;
		}
	}

	public GradiantRenderer assertColors() {
		return new GradiantRenderer(box(), assertColor(0), assertColor(1), assertColor(2), assertColor(3));
	}

	private void renderVertex(BufferBuilder builder, Matrix4f matrix, Vector vertex, AccurateColor color, float z) {
		builder.vertex(matrix, (float) vertex.x(), (float) vertex.y(), z)
				.color(color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(), color.opacityAsFloat())
				.next();
	}

	public void render(MatrixStack matrixStack, float z) {
		if (!isRenderable()) return;

		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);

		BufferBuilder builder = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();

		builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

		renderVertex(builder, matrix, box().topLeft(), assertColor(0), z);
		renderVertex(builder, matrix, box().bottomLeft(), assertColor(1), z);
		renderVertex(builder, matrix, box().bottomRight(), assertColor(2), z);
		renderVertex(builder, matrix, box().topRight(), assertColor(3), z);

		BufferRenderer.drawWithGlobalProgram(builder.end());
		RenderSystem.disableBlend();
	}

	public void render(MatrixStack matrixStack) {
		render(matrixStack, 0);
	}

	public enum OutliningMode {
		SCISSOR, EDGE, EDGE_FADING
	}

	public void renderOutline(MatrixStack matrixStack, Vector expansion, OutliningMode outliningMode, float z) {
		Box corner 			= Box.fromVectorCentered(box().center(), expansion);
		Box gapHorizontal 	= Box.fromVectorCentered(box().center(), Vector.fromCartesian(box().w(), expansion.y()));
		Box gapVertical 	= Box.fromVectorCentered(box().center(), Vector.fromCartesian(expansion.x(), box().h()));

		double width = box().w() + expansion.x() * 2, height = box().h() + expansion.y() * 2;
		double xCornerScalar = expansion.x() / width, yCornerScalar = expansion.y() / height;

		AccurateColor topLeft 		= topLeft(), 		topLeftBottom 		= AccurateColor.TRANSPARENT, 	topLeftTop 		= AccurateColor.TRANSPARENT, 	topLeftDiagonal 		= AccurateColor.TRANSPARENT;
		AccurateColor bottomLeft 	= bottomLeft(), 	bottomLeftBottom 	= AccurateColor.TRANSPARENT, 	bottomLeftTop 	= AccurateColor.TRANSPARENT, 	bottomLeftDiagonal 		= AccurateColor.TRANSPARENT;
		AccurateColor bottomRight 	= bottomRight(), 	bottomRightBottom 	= AccurateColor.TRANSPARENT, 	bottomRightTop 	= AccurateColor.TRANSPARENT, 	bottomRightDiagonal 	= AccurateColor.TRANSPARENT;
		AccurateColor topRight 		= topRight(), 		topRightBottom 		= AccurateColor.TRANSPARENT, 	topRightTop 	= AccurateColor.TRANSPARENT, 	topRightDiagonal 		= AccurateColor.TRANSPARENT;

		switch (outliningMode) {
			case SCISSOR -> {
				topLeftTop 				= get(xCornerScalar, 0);
				topLeftBottom 			= get(0, yCornerScalar);
				topLeftDiagonal 		= get(xCornerScalar, yCornerScalar);

				bottomLeftTop 			= get(0, 1 - yCornerScalar);
				bottomLeftBottom 		= get(xCornerScalar, 1);
				bottomLeftDiagonal 		= get(xCornerScalar, 1 - yCornerScalar);

				bottomRightTop 			= get(1, 1 - yCornerScalar);
				bottomRightBottom 		= get(1 - xCornerScalar, 1);
				bottomRightDiagonal 	= get(1 - xCornerScalar, 1 - yCornerScalar);

				topRightTop 			= get(1 - xCornerScalar, 0);
				topRightBottom 			= get(1, yCornerScalar);
				topRightDiagonal 		= get(1 - xCornerScalar, yCornerScalar);
			}
			case EDGE -> {
				topLeftTop 				= get(xCornerScalar, 0);
				topLeftBottom 			= get(0, yCornerScalar);
				topLeftDiagonal 		= getCenter();

				bottomLeftTop 			= get(0, 1 - yCornerScalar);
				bottomLeftBottom 		= get(xCornerScalar, 1);
				bottomLeftDiagonal 		= getCenter();

				bottomRightTop 			= get(1, 1 - yCornerScalar);
				bottomRightBottom 		= get(1 - xCornerScalar, 1);
				bottomRightDiagonal 	= getCenter();

				topRightTop 			= get(1 - xCornerScalar, 0);
				topRightBottom 			= get(1, yCornerScalar);
				topRightDiagonal 		= getCenter();
			}
			case EDGE_FADING -> {
				topLeftDiagonal = topLeft;
				topLeft = topLeftTop = topLeftBottom = AccurateColor.TRANSPARENT;

				bottomLeftDiagonal = bottomLeft;
				bottomLeft = bottomLeftTop = bottomLeftBottom = AccurateColor.TRANSPARENT;

				bottomRightDiagonal = bottomRight;
				bottomRight = bottomRightTop = bottomRightBottom = AccurateColor.TRANSPARENT;

				topRightDiagonal = topRight;
				topRight = topRightTop = topRightBottom = AccurateColor.TRANSPARENT;
			}
		}

		// Top left
		preserve(corner.alignBottomRight(box().topLeft()))
				.topLeft(topLeft)
				.bottomLeft(topLeftBottom)
				.bottomRight(topLeftDiagonal)
				.topRight(topLeftTop)
				.render(matrixStack, z);

		// Bottom left
		preserve(corner.alignTopRight(box().bottomLeft()))
				.topLeft(bottomLeftTop)
				.bottomLeft(bottomLeft)
				.bottomRight(bottomLeftBottom)
				.topRight(bottomLeftDiagonal)
				.render(matrixStack, z);

		// Bottom right
		preserve(corner.alignTopLeft(box().bottomRight()))
				.topLeft(bottomRightDiagonal)
				.bottomLeft(bottomRightBottom)
				.bottomRight(bottomRight)
				.topRight(bottomRightTop)
				.render(matrixStack, z);

		// Top right
		preserve(corner.alignBottomLeft(box().topRight()))
				.topLeft(topRightTop)
				.bottomLeft(topRightDiagonal)
				.bottomRight(topRightBottom)
				.topRight(topRight)
				.render(matrixStack, z);

		// Top
		preserve(gapHorizontal.alignBottomLeft(box().topLeft()))
				.topLeft(topLeftTop)
				.bottomLeft(topLeftDiagonal)
				.bottomRight(topRightDiagonal)
				.topRight(topRightTop)
				.render(matrixStack, z);

		// Bottom
		preserve(gapHorizontal.alignTopLeft(box().bottomLeft()))
				.topLeft(bottomLeftDiagonal)
				.bottomLeft(bottomLeftBottom)
				.bottomRight(bottomRightBottom)
				.topRight(bottomRightDiagonal)
				.render(matrixStack, z);

		// Left
		preserve(gapVertical.alignTopRight(box().topLeft()))
				.topLeft(topLeftBottom)
				.bottomLeft(bottomLeftTop)
				.bottomRight(bottomLeftDiagonal)
				.topRight(topLeftDiagonal)
				.render(matrixStack, z);

		// Right
		preserve(gapVertical.alignTopLeft(box().topRight()))
				.topLeft(topRightDiagonal)
				.bottomLeft(bottomRightDiagonal)
				.bottomRight(bottomRightTop)
				.topRight(topRightBottom)
				.render(matrixStack, z);
	}

	public void renderOutline(MatrixStack matrixStack, Vector expansion, OutliningMode outliningMode) {
		renderOutline(matrixStack, expansion, outliningMode, 0);
	}

	public void renderOutline(MatrixStack matrixStack, Vector expansion) {
		renderOutline(matrixStack, expansion, OutliningMode.SCISSOR, 0);
	}

	public void renderOutline(MatrixStack matrixStack, double xExpansion, double yExpansion, OutliningMode outliningMode, float z) {
		renderOutline(matrixStack, Vector.fromCartesian(xExpansion, yExpansion), outliningMode, z);
	}

	public void renderOutline(MatrixStack matrixStack, double xExpansion, double yExpansion, OutliningMode outliningMode) {
		renderOutline(matrixStack, xExpansion, yExpansion, outliningMode, 0);
	}

	public void renderOutline(MatrixStack matrixStack, double xExpansion, double yExpansion) {
		renderOutline(matrixStack, xExpansion, yExpansion, OutliningMode.SCISSOR, 0);
	}

	public void renderOutline(MatrixStack matrixStack, double expansion, OutliningMode outliningMode, float z) {
		renderOutline(matrixStack, expansion, expansion, outliningMode, z);
	}

	public void renderOutline(MatrixStack matrixStack, double expansion, OutliningMode outliningMode) {
		renderOutline(matrixStack, expansion, outliningMode, 0);
	}

	public void renderOutline(MatrixStack matrixStack, double expansion) {
		renderOutline(matrixStack, expansion, OutliningMode.SCISSOR, 0);
	}

	public void renderOutlineShadow(MatrixStack matrixStack, float z) {
		renderOutline(matrixStack, box.diag() * 0.55, OutliningMode.EDGE_FADING, z);
	}

	public void renderOutlineShadow(MatrixStack matrixStack) {
		renderOutlineShadow(matrixStack, 0);
	}
}
