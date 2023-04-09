package net.krlite.equator.render;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.render.base.Scissor;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.math.geometry.Vector;
import net.krlite.equator.render.base.Renderable;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.texture.Texture;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;

import java.util.Objects;
import java.util.function.UnaryOperator;

public record BoxRenderer(
		Box box, Quaterniondc modifier,
		@Nullable AccurateColor color, @Nullable Texture texture
) implements Renderable {
	protected enum State {
		UNABLE(null),
		COLOR(VertexFormats.POSITION_COLOR),
		TEXTURE(VertexFormats.POSITION_TEXTURE),
		COLOR_TEXTURE(VertexFormats.POSITION_COLOR_TEXTURE);

		@Nullable
		private final VertexFormat vertexFormat;

		State(@Nullable VertexFormat vertexFormat) {
			this.vertexFormat = vertexFormat;
		}

		@Nullable
		public VertexFormat vertexFormat() {
			return vertexFormat;
		}
	}

	public BoxRenderer(Box box) {
		this(box, new Quaterniond(), null, null);
	}

	// box() is a record method

	// modifier() is a record method

	// color() is a record method

	// texture() is a record method

	private BoxRenderer preserve(Box box, Box uvBox) {
		return new BoxRenderer(box, modifier(), color(), hasTexture() ? Objects.requireNonNull(texture()).uvBox(uvBox) : texture());
	}
	
	private BoxRenderer preserve(Box box) {
		return new BoxRenderer(box, modifier(), color(), texture());
	}

	public BoxRenderer modifier(Quaterniondc modifier) {
		return new BoxRenderer(box(), modifier, color(), texture());
	}

	public BoxRenderer modifier(UnaryOperator<Quaterniondc> modifier) {
		return modifier(modifier.apply(modifier()));
	}

	public BoxRenderer removeModifier() {
		return modifier(new Quaterniond());
	}

	public BoxRenderer color(AccurateColor color) {
		return new BoxRenderer(box(), modifier(), color, texture());
	}

	public BoxRenderer color(UnaryOperator<AccurateColor> color) {
		return color(color.apply(color()));
	}

	public BoxRenderer removeColor() {
		return color(color -> null);
	}

	public BoxRenderer texture(Texture texture) {
		return new BoxRenderer(box(), modifier(), color(), texture);
	}

	public BoxRenderer removeTexture() {
		return texture(null);
	}

	@Override
	public boolean isRenderable() {
		return Renderable.isBoxLegal(box()) && (hasTexture() || hasColor());
	}

	public boolean hasColor() {
		return color() != null;
	}

	public boolean hasTexture() {
		return texture() != null;
	}

	public State state() {
		if (hasTexture()) {
			if (hasColor()) {
				return State.COLOR_TEXTURE;
			}
			else {
				return State.TEXTURE;
			}
		}
		else {
			if (hasColor()) {
				return State.COLOR;
			}
			else {
				return State.UNABLE;
			}
		}
	}

	private void renderVertex(BufferBuilder builder, Matrix4f matrix, Vector vertex, Vector uv, AccurateColor color, float z) {
		switch (state()) {
			case COLOR ->
					builder.vertex(matrix, (float) vertex.x(), (float) vertex.y(), z)
							.color(color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(), color.opacityAsFloat())
							.next();
			case TEXTURE ->
					builder.vertex(matrix, (float) vertex.x(), (float) vertex.y(), z)
							.texture((float) uv.x(), (float) uv.y())
							.next();
			case COLOR_TEXTURE ->
					builder.vertex(matrix, (float) vertex.x(), (float) vertex.y(), z)
							.color(color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(), color.opacityAsFloat())
							.texture((float) uv.x(), (float) uv.y())
							.next();
		}
	}

	public void render(MatrixStack matrixStack, float z) {
		if (!isRenderable()) {
			return;
		}

		if (hasColor()) {
			RenderSystem.enableBlend();
		}

		switch (state()) {
			case COLOR -> RenderSystem.setShader(GameRenderer::getPositionColorProgram);
			case TEXTURE -> RenderSystem.setShader(GameRenderer::getPositionTexProgram);
			case COLOR_TEXTURE -> RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
			default -> { return; }
		}

		if (hasTexture()) {
			RenderSystem.setShaderTexture(0, Objects.requireNonNull(texture()).identifier());
		}

		matrixStack.push();
		matrixStack.multiply(new Quaternionf(modifier()));

		BufferBuilder builder = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();

		builder.begin(VertexFormat.DrawMode.QUADS, state().vertexFormat());

		renderVertex(builder, matrix, box().topLeft(), hasTexture() ? Objects.requireNonNull(texture()).uvTopLeft() : Vector.ZERO, color(), z);
		renderVertex(builder, matrix, box().bottomLeft(), hasTexture() ? Objects.requireNonNull(texture()).uvBottomLeft() : Vector.UNIT_Y, color(), z);
		renderVertex(builder, matrix, box().bottomRight(), hasTexture() ? Objects.requireNonNull(texture()).uvBottomRight() : Vector.UNIT_SQUARE, color(), z);
		renderVertex(builder, matrix, box().topRight(), hasTexture() ? Objects.requireNonNull(texture()).uvTopRight() : Vector.UNIT_X, color(), z);

		BufferRenderer.drawWithGlobalProgram(builder.end());
		matrixStack.pop();

		if (hasColor()) {
			RenderSystem.disableBlend();
		}
	}

	public void render(MatrixStack matrixStack) {
		render(matrixStack, 0);
	}

	public void renderFixedCorners(MatrixStack matrixStack, float z) {
		Box corner = box().squareInner().scaleCenter(0.5);

		// Top left
		preserve(corner.alignTopLeft(box()), new Box(0, 0, 0.5, 0.5)).render(matrixStack, z);

		// Bottom left
		preserve(corner.alignBottomLeft(box()), new Box(0, 0.5, 0.5, 1)).render(matrixStack, z);

		// Bottom right
		preserve(corner.alignBottomRight(box()), new Box(0.5, 0.5, 1, 1)).render(matrixStack, z);

		// Top right
		preserve(corner.alignTopRight(box()), new Box(0.5, 0, 1, 0.5)).render(matrixStack, z);

		if (box().w() > box().height().magnitude()) {
			Box gap = Box.fromVector(corner.alignTopLeft(box()).topRight(), corner.alignTopRight(box()).bottomLeft());

			// Top
			preserve(gap, new Box(0.5, 0, 0.5, 0.5)).render(matrixStack, z);

			// Bottom
			preserve(gap.translate(0, 1), new Box(0.5, 0.5, 0.5, 1)).render(matrixStack, z);
		}
		else if (box().w() < box().height().magnitude()) {
			Box gap = Box.fromVector(corner.alignTopLeft(box()).bottomLeft(), corner.alignBottomLeft(box()).topRight());

			// Left
			preserve(gap, new Box(0, 0.5, 0.5, 0.5)).render(matrixStack, z);

			// Right
			preserve(gap.translate(1, 0), new Box(0.5, 0.5, 1, 0.5)).render(matrixStack, z);
		}
	}

	public void renderFixedCorners(MatrixStack matrixStack) {
		renderFixedCorners(matrixStack, 0);
	}

	public void renderAndTile(MatrixStack matrixStack, float z) {
		preserve(FrameInfo.scaled(), FrameInfo.scaled().normalizeBy(box()).shift(0.5, 0.5)).render(matrixStack, z);
	}

	public void renderAndTile(MatrixStack matrixStack) {
		renderAndTile(matrixStack, 0);
	}
	
	public void renderNineSliced(MatrixStack matrixStack, double leftWidth, double rightWidth, double topHeight, double bottomHeight, double width, double height) {
		leftWidth = Math.min(leftWidth, box().w() / 2);
		rightWidth = Math.min(rightWidth, box().w() / 2);
		topHeight = Math.min(topHeight, box().h() / 2);
		bottomHeight = Math.min(bottomHeight, box().h() / 2);

		if (!hasTexture() || (Theory.looseEquals(box().w(), width) && Theory.looseEquals(box().h(), height))) {
			render(matrixStack);
			return;
		}

		assert texture() != null;

		Box uvBox = texture().uvBox();

		if (Theory.looseEquals(box().w(), width)) {
			// Left
			preserve(
					box().width(leftWidth),
					uvBox.scale(leftWidth / width, 1)
			).render(matrixStack);

			// Center
			preserve(
					box().width(box().w() - leftWidth - rightWidth).center(box()),
					uvBox.scale((width - leftWidth - rightWidth) / width, 1).center(uvBox)
			).renderRepeating(matrixStack, width - leftWidth - rightWidth, height);

			// Right
			preserve(
					box().width(rightWidth).alignRight(box()),
					uvBox.scale(rightWidth / width, 1).alignRight(uvBox)
			).render(matrixStack);
		} else if (Theory.looseEquals(box().h(), height)) {
			// Top
			preserve(
					box().height(topHeight),
					uvBox.scale(1, topHeight / height)
			).render(matrixStack);

			// Center
			preserve(
					box().height(box().h() - topHeight - bottomHeight).center(box()),
					uvBox.scale(1, (height - topHeight - bottomHeight) / height).center(uvBox)
			).renderRepeating(matrixStack, width, height - topHeight - bottomHeight);

			// Bottom
			preserve(
					box().height(bottomHeight).alignBottom(box()),
					uvBox.scale(1, bottomHeight / height).alignBottom(uvBox)
			).render(matrixStack);
		} else {
			// Top left
			preserve(
					box().width(leftWidth).height(topHeight),
					uvBox.scale(leftWidth / width, topHeight / height)
			).render(matrixStack);

			// Top center
			preserve(
					box().width(box().w() - leftWidth - rightWidth).height(topHeight).center(box()).alignTop(box()),
					uvBox.scale((width - leftWidth - rightWidth) / width, topHeight / height).center(uvBox).alignTop(uvBox)
			).renderRepeating(matrixStack, width - leftWidth - rightWidth, topHeight);

			// Top right
			preserve(
					box().width(rightWidth).height(topHeight).alignTopRight(box()),
					uvBox.scale(rightWidth / width, topHeight / height).alignTopRight(uvBox)
			).render(matrixStack);

			// Center left
			preserve(
					box().width(leftWidth).height(box().h() - topHeight - bottomHeight).center(box()).alignLeft(box()),
					uvBox.scale(leftWidth / width, (height - topHeight - bottomHeight) / height).center(uvBox).alignLeft(uvBox)
			).renderRepeating(matrixStack, leftWidth, height - topHeight - bottomHeight);

			// Center
			preserve(
					box().width(box().w() - leftWidth - rightWidth).height(box().h() - topHeight - bottomHeight).center(box()),
					uvBox.scale((width - leftWidth - rightWidth) / width, (height - topHeight - bottomHeight) / height).center(uvBox)
			).renderRepeating(matrixStack, width - leftWidth - rightWidth, height - topHeight - bottomHeight);

			// Center right
			preserve(
					box().width(rightWidth).height(box().h() - topHeight - bottomHeight).center(box()).alignRight(box()),
					uvBox.scale(rightWidth / width, (height - topHeight - bottomHeight) / height).center(uvBox).alignRight(uvBox)
			).renderRepeating(matrixStack, rightWidth, height - topHeight - bottomHeight);

			// Bottom left
			preserve(
					box().width(leftWidth).height(bottomHeight).alignBottomLeft(box()),
					uvBox.scale(leftWidth / width, bottomHeight / height).alignBottomLeft(uvBox)
			).render(matrixStack);

			// Bottom center
			preserve(
					box().width(box().w() - leftWidth - rightWidth).height(bottomHeight).center(box()).alignBottom(box()),
					uvBox.scale((width - leftWidth - rightWidth) / width, bottomHeight / height).center(uvBox).alignBottom(uvBox)
			).renderRepeating(matrixStack, width - leftWidth - rightWidth, bottomHeight);

			// Bottom right
			preserve(
					box().width(rightWidth).height(bottomHeight).alignBottomRight(box()),
					uvBox.scale(rightWidth / width, bottomHeight / height).alignBottomRight(uvBox)
			).render(matrixStack);
		}
	}

	private void renderRepeating(MatrixStack matrixStack, double width, double height) {
		if (!hasTexture()) render(matrixStack);

		assert texture() != null;

		for (double x = 0; x < box().w(); x += width) {
			for (double y = 0; y < box().h(); y += height) {
				preserve(
						box().width(Math.min(width, box().w() - x)).height(Math.min(height, box().h() - y)).shift(x, y),
						texture().uvBox()
								.scale(Math.min(width, box().w() - x) / width, Math.min(height, box().h() - y) / height)
								.shift(x / width, y / height)
				).render(matrixStack);
			}
		}
	}
}
