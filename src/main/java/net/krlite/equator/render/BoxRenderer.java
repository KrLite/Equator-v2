package net.krlite.equator.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.math.ScreenAdapter;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.math.geometry.Vector;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.texture.Texture;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;

import java.util.Objects;
import java.util.function.UnaryOperator;

public class BoxRenderer {
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

	public BoxRenderer(Box box, Quaterniondc modifier, @Nullable AccurateColor color, @Nullable Texture texture) {
		this.box = box;
		this.modifier = modifier;
		this.color = color;
		this.texture = texture;
	}

	public BoxRenderer(Box box) {
		this(box, new Quaterniond(), null, null);
	}

	private final Box box;
	private final Quaterniondc modifier;
	@Nullable
	private final AccurateColor color;
	@Nullable
	private final Texture texture;

	public Box box() {
		return box;
	}

	public Quaterniondc modifier() {
		return modifier;
	}

	@Nullable
	public AccurateColor color() {
		return color;
	}

	@Nullable
	public Texture texture() {
		return texture;
	}

	private BoxRenderer preserve(Box box, Box uvBox) {
		return new BoxRenderer(box, modifier(), color(), hasTexture() ? Objects.requireNonNull(texture()).uvBox(uvBox) : texture());
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
		return new BoxRenderer(box(), modifier(), null, texture());
	}

	public BoxRenderer texture(Texture texture) {
		return new BoxRenderer(box(), modifier(), color(), texture);
	}

	public BoxRenderer removeTexture() {
		return new BoxRenderer(box(), modifier(), color(), null);
	}

	public boolean isRenderable() {
		return Theory.looseGreater(box().area(), 0) ||  hasTexture() || hasColor();
	}

	public boolean hasColor() {
		return color() != null;
	}

	public boolean hasTexture() {
		return texture() != null;
	}

	public State state() {
		if (hasTexture()) {
			if (hasColor())
				return State.COLOR_TEXTURE;
			else
				return State.TEXTURE;
		}
		else {
			if (hasColor())
				return State.COLOR;
			else
				return State.UNABLE;
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
		if (hasColor())
			RenderSystem.enableBlend();

		switch (state()) {
			case COLOR -> RenderSystem.setShader(GameRenderer::getPositionColorProgram);
			case TEXTURE -> RenderSystem.setShader(GameRenderer::getPositionTexProgram);
			case COLOR_TEXTURE -> RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
			default -> { return; }
		}

		if (hasTexture())
			RenderSystem.setShaderTexture(0, Objects.requireNonNull(texture()).identifier());

		BufferBuilder builder = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();

		builder.begin(VertexFormat.DrawMode.QUADS, state().vertexFormat());

		renderVertex(builder, matrix, box().topLeft(), hasTexture() ? Objects.requireNonNull(texture()).uvTopLeft() : Vector.ZERO, color(), z);
		renderVertex(builder, matrix, box().bottomLeft(), hasTexture() ? Objects.requireNonNull(texture()).uvBottomLeft() : Vector.UNIT_Y, color(), z);
		renderVertex(builder, matrix, box().bottomRight(), hasTexture() ? Objects.requireNonNull(texture()).uvBottomRight() : Vector.UNIT_SQUARE, color(), z);
		renderVertex(builder, matrix, box().topRight(), hasTexture() ? Objects.requireNonNull(texture()).uvTopRight() : Vector.UNIT_X, color(), z);

		BufferRenderer.drawWithGlobalProgram(builder.end());

		if (hasColor())
			RenderSystem.disableBlend();
	}

	public void render(MatrixStack matrixStack) {
		render(matrixStack, 0);
	}

	public void renderFixedCorners(MatrixStack matrixStack, float z) {
		Box corner = box().squareInner().scaleCentered(0.5);

		// Top left
		preserve(corner.alignTopLeft(box().topLeft()), new Box(0, 0, 0.5, 0.5)).render(matrixStack, z);

		// Bottom left
		preserve(corner.alignBottomLeft(box().bottomLeft()), new Box(0, 0.5, 0.5, 1)).render(matrixStack, z);

		// Bottom right
		preserve(corner.alignBottomRight(box().bottomRight()), new Box(0.5, 0.5, 1, 1)).render(matrixStack, z);

		// Top right
		preserve(corner.alignTopRight(box().topRight()), new Box(0.5, 0, 1, 0.5)).render(matrixStack, z);

		if (box().width().magnitude() > box().height().magnitude()) {
			Box gap = Box.fromVector(corner.alignTopLeft(box().topLeft()).topRight(), corner.alignTopRight(box().topRight()).bottomLeft());

			// Top
			preserve(gap, new Box(0.5, 0, 0.5, 0.5)).render(matrixStack, z);

			// Bottom
			preserve(gap.translate(0, 1), new Box(0.5, 0.5, 0.5, 1)).render(matrixStack, z);
		}
		else if (box().width().magnitude() < box().height().magnitude()) {
			Box gap = Box.fromVector(corner.alignTopLeft(box().topLeft()).bottomLeft(), corner.alignBottomLeft(box().bottomLeft()).topRight());

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
		preserve(ScreenAdapter.scaledScreen(), ScreenAdapter.scaledScreen().normalizeBy(box()).shift(0.5, 0.5)).render(matrixStack, z);
	}

	public void renderAndTile(MatrixStack matrixStack) {
		renderAndTile(matrixStack, 0);
	}
}
