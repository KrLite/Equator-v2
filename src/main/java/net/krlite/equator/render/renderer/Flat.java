package net.krlite.equator.render.renderer;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.math.algebra.Quaternion;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.RenderManager;
import net.krlite.equator.render.base.Renderable;
import net.krlite.equator.render.base.Scissor;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.renderer.base.Basic;
import net.krlite.equator.render.vanilla.VanillaWidgets;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Colorspace;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.krlite.equator.visual.texture.Texture;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class Flat extends Basic {
	// Constructors

	public Flat(MatrixStack matrixStack, float z, Box box) {
		super(matrixStack);
		this.z = z;
		this.box = box;
	}

	// Fields

	private final float z;
	private final Box box;

	// Accessors

	public float z() {
		return z;
	}

	public Box box() {
		return box;
	}

	// Mutators

	public Flat z(float z) {
		return new Flat(matrixStack(), z, box());
	}

	public Flat box(Box box) {
		return new Flat(matrixStack(), z(), box);
	}

	public class Rectangle implements Renderable {
		// Constructors

		public Rectangle(
				@Nullable Texture texture,
				@Nullable AccurateColor colorTopLeft, 		@Nullable AccurateColor colorBottomLeft,
				@Nullable AccurateColor colorBottomRight, 	@Nullable AccurateColor colorTopRight,
				double opacityMultiplier, @Nullable Colorspace colorspace, RectangleMode mode
		) {
			this.texture = texture;

			this.colorTopLeft = 		AccurateColor.notnull(colorTopLeft)		.colorspace(colorspace);
			this.colorBottomLeft = 		AccurateColor.notnull(colorBottomLeft)	.colorspace(colorspace);
			this.colorBottomRight = 	AccurateColor.notnull(colorBottomRight)	.colorspace(colorspace);
			this.colorTopRight = 		AccurateColor.notnull(colorTopRight)	.colorspace(colorspace);

			this.opacityMultiplier = Theory.clamp(opacityMultiplier, 0, 1);
			this.colorspace = colorspace == null ? Colorspace.RGB : colorspace;
			this.mode = mode;
		}

		public Rectangle(
				@Nullable Texture texture,
				@Nullable AccurateColor colorTopLeft, 		@Nullable AccurateColor colorBottomLeft,
				@Nullable AccurateColor colorBottomRight, 	@Nullable AccurateColor colorTopRight
		) {
			this(texture, colorTopLeft, colorBottomLeft, colorBottomRight, colorTopRight, 1, Colorspace.RGB, RectangleMode.NORMAL);
		}

		public Rectangle(@Nullable Texture texture, @Nullable AccurateColor color, @Nullable Colorspace colorspace) {
			this(texture, color, color, color, color, 1, colorspace, RectangleMode.NORMAL);
		}

		public Rectangle(@Nullable AccurateColor color) {
			this(null, color, null);
		}

		public Rectangle(@Nullable Texture texture) {
			this(texture, null, null);
		}

		public Rectangle(@Nullable Colorspace colorspace) {
			this(null, null, colorspace);
		}

		public Rectangle() {
			this(null, null, null);
		}

		// Fields

		public enum RectangleMode {
			NORMAL, TILING, FIXED_CORNERS
		}

		private final @Nullable Texture texture;
		private final @NotNull AccurateColor colorTopLeft, colorBottomLeft, colorBottomRight, colorTopRight;
		private final double opacityMultiplier;
		private final @NotNull Colorspace colorspace;
		private final RectangleMode mode;

		// Accessors

		public Box box() {
			return box;
		}

		public @Nullable Texture texture() {
			return texture;
		}

		public @NotNull AccurateColor colorTopLeft() {
			return colorTopLeft;
		}

		public @NotNull AccurateColor colorBottomLeft() {
			return colorBottomLeft;
		}

		public @NotNull AccurateColor colorBottomRight() {
			return colorBottomRight;
		}

		public @NotNull AccurateColor colorTopRight() {
			return colorTopRight;
		}

		public @NotNull AccurateColor[] colors() {
			return new AccurateColor[] { colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight() };
		}

		public @NotNull AccurateColor colorAtCenter() {
			return colorAt(0.5, 0.5);
		}

		public double opacityMultiplier() {
			return opacityMultiplier;
		}

		public @NotNull Colorspace colorspace() {
			return colorspace;
		}

		public RectangleMode mode() {
			return mode;
		}

		// Mutators

		public Rectangle parent(UnaryOperator<Flat> flat) {
			return flat.apply(Flat.this).new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight(), opacityMultiplier(), colorspace(), mode());
		}

		protected Rectangle preserve(Box box, Box uvBox) {
			return parent(flat -> flat.box(box)).texture(hasTexture() ? Objects.requireNonNull(texture()).uvBox(uvBox) : texture());
		}

		protected Rectangle preserve(Box box) {
			return parent(flat -> flat.box(box));
		}

		public Rectangle texture(@Nullable Texture texture) {
			return new Rectangle(texture, colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight(), opacityMultiplier(), colorspace(), mode());
		}

		public Rectangle colorTopLeft(@Nullable AccurateColor colorTopLeft) {
			return new Rectangle(texture(), colorTopLeft, colorBottomLeft(), colorBottomRight(), colorTopRight(), opacityMultiplier(), colorspace(), mode());
		}

		public Rectangle colorBottomLeft(@Nullable AccurateColor colorBottomLeft) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft, colorBottomRight(), colorTopRight(), opacityMultiplier(), colorspace(), mode());
		}

		public Rectangle colorBottomRight(@Nullable AccurateColor colorBottomRight) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight, colorTopRight(), opacityMultiplier(), colorspace(), mode());
		}

		public Rectangle colorTopRight(@Nullable AccurateColor colorTopRight) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight, opacityMultiplier(), colorspace(), mode());
		}

		public Rectangle colorTop(@Nullable AccurateColor colorTop) {
			return new Rectangle(texture(), colorTop, colorBottomLeft(), colorBottomRight(), colorTop, opacityMultiplier(), colorspace(), mode());
		}

		public Rectangle colorBottom(@Nullable AccurateColor colorBottom) {
			return new Rectangle(texture(), colorTopLeft(), colorBottom, colorBottom, colorTopRight(), opacityMultiplier(), colorspace(), mode());
		}

		public Rectangle colorLeft(@Nullable AccurateColor colorLeft) {
			return new Rectangle(texture(), colorLeft, colorLeft, colorLeft, colorTopRight(), opacityMultiplier(), colorspace(), mode());
		}

		public Rectangle colorRight(@Nullable AccurateColor colorRight) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorRight, colorRight, opacityMultiplier(), colorspace(), mode());
		}

		public Rectangle colors(
				@Nullable AccurateColor colorTopLeft, 		@Nullable AccurateColor colorBottomLeft,
				@Nullable AccurateColor colorBottomRight, 	@Nullable AccurateColor colorTopRight
		) {
			return new Rectangle(texture(), colorTopLeft, colorBottomLeft, colorBottomRight, colorTopRight, opacityMultiplier(), colorspace(), mode());
		}

		public Rectangle colors(@Nullable AccurateColor color) {
			return new Rectangle(texture(), color, color, color, color, opacityMultiplier(), colorspace(), mode());
		}

		public Rectangle opacityMultiplier(double opacityMultiplier) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight(), opacityMultiplier, colorspace(), mode());
		}

		public Rectangle mode(RectangleMode mode) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight(), opacityMultiplier(), colorspace(), mode);
		}

		public Rectangle colorspace(@Nullable Colorspace colorspace) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight(), opacityMultiplier(), colorspace, mode());
		}

		// Properties

		private enum State {
			UNABLE(null, null),
			COLOR(VertexFormats.POSITION_COLOR, GameRenderer::getPositionColorProgram),
			TEXTURE(VertexFormats.POSITION_TEXTURE, GameRenderer::getPositionTexProgram),
			COLOR_TEXTURE(VertexFormats.POSITION_COLOR_TEXTURE, GameRenderer::getPositionColorTexProgram);

			private final @Nullable VertexFormat vertexFormat;
			private final @Nullable Supplier<ShaderProgram> shaderProgram;

			State(@Nullable VertexFormat vertexFormat, @Nullable Supplier<ShaderProgram> shaderProgram) {
				this.vertexFormat = vertexFormat;
				this.shaderProgram = shaderProgram;
			}

			public @Nullable VertexFormat vertexFormat() {
				return vertexFormat;
			}

			public @Nullable Supplier<ShaderProgram> shaderProgram() {
				return shaderProgram;
			}
		}

		public boolean hasTexture() {
			return texture() != null;
		}

		public boolean hasColor() {
			return Theory.looseGreater(opacityMultiplier(), 0)
						   && (colorTopLeft().hasColor() || colorBottomLeft().hasColor() || colorBottomRight().hasColor() || colorTopRight().hasColor())
						   && (colorTopLeft().hasOpacity() || colorBottomLeft().hasOpacity() || colorBottomRight().hasOpacity() || colorTopRight().hasOpacity());
		}

		public AccurateColor colorAt(double xOffset, double yOffset) {
			AccurateColor leftSide = colorTopLeft().mix(colorBottomLeft(), yOffset);
			AccurateColor rightSide = colorTopRight().mix(colorBottomRight(), yOffset);

			return leftSide.mix(rightSide, xOffset);
		}

		private AccurateColor assertColor(AccurateColor color) {
			return color.hasColor() ? color : colorAtCenter().transparent();
		}

		private State state() {
			if (hasColor() && hasTexture())
				return State.COLOR_TEXTURE;
			else if (hasColor())
				return State.COLOR;
			else if (hasTexture())
				return State.TEXTURE;
			else
				return State.UNABLE;
		}

		// Interface Implementations

		private void renderVertex(BufferBuilder builder, Matrix4f matrix, Vector vertex, Vector uv, AccurateColor color, float z) {
			switch (state()) {
				case COLOR -> builder.vertex(matrix, (float) vertex.x(), (float) vertex.y(), z)
									  .color(color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(), color.opacityAsFloat())
									  .next();
				case TEXTURE -> builder.vertex(matrix, (float) vertex.x(), (float) vertex.y(), z)
										.texture((float) uv.x(), (float) uv.y())
										.next();
				case COLOR_TEXTURE -> builder.vertex(matrix, (float) vertex.x(), (float) vertex.y(), z)
											  .color(color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(), color.opacityAsFloat())
											  .texture((float) uv.x(), (float) uv.y())
											  .next();
			}
		}

		private void renderNormal() {
			boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);

			if (hasColor() && !blend) {
				RenderSystem.enableBlend();
			}

			RenderSystem.setShader(Objects.requireNonNull(state().shaderProgram()));

			if (hasTexture()) {
				RenderSystem.setShaderTexture(0, Objects.requireNonNull(texture()).identifier());
			}

			BufferBuilder builder = Tessellator.getInstance().getBuffer();
			Matrix4f matrix = matrixStack().peek().getPositionMatrix();


			builder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, state().vertexFormat());

			double xDelta, yDelta;

			// Width optimization
			if 		(box().w() < 1) 	xDelta = box().w() / 2;
			else if (box().w() < 10) 	xDelta = box().w() / 4;
			else if (box().w() < 100) 	xDelta = box().w() / 8;
			else 						xDelta = Math.min(25, box().w() / 16);

			// Height optimization
			if 		(box().h() < 1) 	yDelta = box().h() / 2;
			else if (box().h() < 10) 	yDelta = box().h() / 4;
			else if (box().h() < 100) 	yDelta = box().h() / 8;
			else 						yDelta = Math.min(25, box().h() / 16);

			for (double yr = 0; yr < box().h(); yr += yDelta) {
				double y = Math.min(box().h(), yr);

				for (double xr = 0; xr < box().w() + xDelta; xr += xDelta) {
					double x = Math.min(box().w(), xr);

					renderVertex(
							builder, matrix, box().topLeft().add(x, y),
							hasTexture() ? Objects.requireNonNull(texture()).uvAt(x / box().w(), y / box().h()) : Vector.ZERO,
							assertColor(colorAt(x / box().w(), y / box().h())).multiplyOpacity(opacityMultiplier()), z()
					);

					renderVertex(
							builder, matrix, box().topLeft().add(x, Math.min(box().h(), y + yDelta)),
							hasTexture() ? Objects.requireNonNull(texture()).uvAt(x / box().w(), Math.min(box().h(), y + yDelta) / box().h()) : Vector.ZERO,
							assertColor(colorAt(x / box().w(), Math.min(box().h(), y + yDelta) / box().h())).multiplyOpacity(opacityMultiplier()), z()
					);
				}
			}

			BufferRenderer.drawWithGlobalProgram(builder.end());

			if (hasColor() && !blend) {
				RenderSystem.disableBlend();
			}
		}

		private void renderTiling() {
			preserve(
					FrameInfo.scaled(),
					FrameInfo.scaled().normalizeBy(box()).shift(0.5, 0.5)
			).render();
		}

		private void renderFixedCorners() {
			Box corner = box().squareInner().scaleCenter(0.5);

			// Top left
			preserve(
					corner.alignTopLeft(box()),
					new Box(0, 0, 0.5, 0.5)
			).render();

			// Bottom left
			preserve(
					corner.alignBottomLeft(box()),
					new Box(0, 0.5, 0.5, 1)
			).render();

			// Bottom right
			preserve(
					corner.alignBottomRight(box()),
					new Box(0.5, 0.5, 1, 1)
			).render();

			// Top right
			preserve(
					corner.alignTopRight(box()),
					new Box(0.5, 0, 1, 0.5)
			).render();

			if (box().w() > box().h()) {
				Box gap = Box.fromVector(corner.alignTopLeft(box()).topRight(), corner.alignTopRight(box()).bottomLeft());

				// Top
				preserve(
						gap,
						new Box(0.5, 0, 0.5, 0.5)
				).render();

				// Bottom
				preserve(
						gap.translate(0, 1),
						new Box(0.5, 0.5, 0.5, 1)
				).render();
			} else if (box().w() < box().h()) {
				Box gap = Box.fromVector(corner.alignTopLeft(box()).bottomLeft(), corner.alignBottomLeft(box()).topRight());

				// Left
				preserve(
						gap,
						new Box(0, 0.5, 0.5, 0.5)
				).render();

				// Right
				preserve(
						gap.translate(1, 0),
						new Box(0.5, 0.5, 1, 0.5)
				).render();
			}
		}

		@Override
		public boolean isRenderable() {
			return Renderable.isLegal(box()) && (hasTexture() || hasColor());
		}

		@Override
		public void render() {
			if (!isRenderable()) return;

			switch (mode()) {
				case NORMAL -> renderNormal();
				case TILING -> renderTiling();
				case FIXED_CORNERS -> renderFixedCorners();
			}
		}

		public class Repeated implements Renderable {
			// Constructors

			public Repeated(double width, double height) {
				this.width = width;
				this.height = height;
			}

			// Fields

			private final double width, height;

			// Accessors

			public double width() {
				return width;
			}

			public double height() {
				return height;
			}

			// Mutators

			public Repeated parent(UnaryOperator<Rectangle> rectangle) {
				return rectangle.apply(Rectangle.this).new Repeated(width, height);
			}

			public Repeated width(double width) {
				return new Repeated(width, height());
			}

			public Repeated height(double height) {
				return new Repeated(width(), height);
			}

			// Interface Implementations

			@Override
			public boolean isRenderable() {
				return Rectangle.this.isRenderable() && !Theory.looseEquals(width(), 0) && !Theory.looseEquals(height(), 0);
			}

			@Override
			public void render() {
				if (!isRenderable()) return;

				if (!hasTexture()) {
					Rectangle.this.render();
					return;
				}

				for (double x = 0; x < box().w(); x += width()) {
					for (double y = 0; y < box().h(); y += height()) {
						preserve(
								box().width(Math.min(width(), box().w() - x)).height(Math.min(height(), box().h() - y)).shift(x, y),
								Objects.requireNonNull(texture()).uvBox().scale(
										Math.min(width(), box().w() - x) / width(),
										Math.min(height(), box().h() - y) / height()
								).shift(x / width(), y / height())
						).render();
					}
				}
			}

			// 'Repeating'
		}

		public final class NineSliced implements Renderable {
			// Constructors

			public NineSliced(double leftWidth, double rightWidth, double topHeight, double bottomHeight, double width, double height) {
				this.leftWidth = Math.min(leftWidth, box().w() / 2);
				this.rightWidth = Math.min(rightWidth, box().w() / 2);
				this.topHeight = Math.min(topHeight, box().h() / 2);
				this.bottomHeight = Math.min(bottomHeight, box().h() / 2);
				this.width = width;
				this.height = height;
			}

			// Fields

			private final double leftWidth, rightWidth, topHeight, bottomHeight, width, height;

			// Accessors

			public double leftWidth() {
				return leftWidth;
			}

			public double rightWidth() {
				return rightWidth;
			}

			public double topHeight() {
				return topHeight;
			}

			public double bottomHeight() {
				return bottomHeight;
			}

			public double width() {
				return width;
			}

			public double height() {
				return height;
			}

			// Mutators

			public NineSliced parent(UnaryOperator<Rectangle> rectangle) {
				return rectangle.apply(Rectangle.this).new NineSliced(leftWidth(), rightWidth(), topHeight(), bottomHeight(), width(), height());
			}

			public NineSliced leftWidth(double leftWidth) {
				return new NineSliced(leftWidth, rightWidth(), topHeight(), bottomHeight(), width(), height());
			}

			public NineSliced rightWidth(double rightWidth) {
				return new NineSliced(leftWidth(), rightWidth, topHeight(), bottomHeight(), width(), height());
			}

			public NineSliced topHeight(double topHeight) {
				return new NineSliced(leftWidth(), rightWidth(), topHeight, bottomHeight(), width(), height());
			}

			public NineSliced bottomHeight(double bottomHeight) {
				return new NineSliced(leftWidth(), rightWidth(), topHeight(), bottomHeight, width(), height());
			}

			public NineSliced width(double width) {
				return new NineSliced(leftWidth(), rightWidth(), topHeight(), bottomHeight(), width, height());
			}

			public NineSliced height(double height) {
				return new NineSliced(leftWidth(), rightWidth(), topHeight(), bottomHeight(), width(), height);
			}

			// Interface Implementations

			@Override
			public boolean isRenderable() {
				return Rectangle.this.isRenderable();
			}

			@Override
			public void render() {
				if (!isRenderable()) return;

				if (!hasTexture() || (Theory.looseEquals(box().w(), width()) && Theory.looseEquals(box().h(), height()))) {
					Rectangle.this.render();
					return;
				}

				Box uvBox = Objects.requireNonNull(texture()).uvBox();

				if (Theory.looseEquals(box().w(), width())) {
					// Left
					preserve(
							box().width(leftWidth()),
							uvBox.scale(leftWidth() / width(), 1)
					).render();

					// Center
					preserve(
							box().width(box().w() - leftWidth() - rightWidth()).center(box()),
							uvBox.scale((width() - leftWidth() - rightWidth()) / width(), 1).center(uvBox)
					).new Repeated(width() - leftWidth() - rightWidth(), height()).render();

					// Right
					preserve(
							box().width(rightWidth()).alignRight(box()),
							uvBox.scale(rightWidth() / width(), 1).alignRight(uvBox)
					).render();
				} else if (Theory.looseEquals(box().h(), height())) {
					// Top
					preserve(
							box().height(topHeight()),
							uvBox.scale(1, topHeight() / height())
					).render();

					// Center
					preserve(
							box().height(box().h() - topHeight() - bottomHeight()).center(box()),
							uvBox.scale(1, (height() - topHeight() - bottomHeight()) / height()).center(uvBox)
					).new Repeated(width(), height() - topHeight() - bottomHeight()).render();

					// Bottom
					preserve(
							box().height(bottomHeight()).alignBottom(box()),
							uvBox.scale(1, bottomHeight() / height()).alignBottom(uvBox)
					).render();
				} else {
					// Top left
					preserve(
							box().width(leftWidth()).height(topHeight()),
							uvBox.scale(leftWidth() / width(), topHeight() / height())
					).render();

					// Top center
					preserve(
							box().width(box().w() - leftWidth() - rightWidth()).height(topHeight()).center(box()).alignTop(box()),
							uvBox.scale((width() - leftWidth() - rightWidth()) / width(), topHeight() / height()).center(uvBox).alignTop(uvBox)
					).new Repeated(width() - leftWidth() - rightWidth(), topHeight()).render();

					// Top right
					preserve(
							box().width(rightWidth()).height(topHeight()).alignTopRight(box()),
							uvBox.scale(rightWidth() / width(), topHeight() / height()).alignTopRight(uvBox)
					).render();

					// Center left
					preserve(
							box().width(leftWidth()).height(box().h() - topHeight() - bottomHeight()).center(box()).alignLeft(box()),
							uvBox.scale(leftWidth() / width(), (height() - topHeight() - bottomHeight()) / height()).center(uvBox).alignLeft(uvBox)
					).new Repeated(leftWidth(), height() - topHeight() - bottomHeight()).render();

					// Center
					preserve(
							box().width(box().w() - leftWidth() - rightWidth()).height(box().h() - topHeight() - bottomHeight()).center(box()),
							uvBox.scale((width() - leftWidth() - rightWidth()) / width(), (height() - topHeight() - bottomHeight()) / height()).center(uvBox)
					).new Repeated(width() - leftWidth() - rightWidth(), height() - topHeight() - bottomHeight()).render();

					// Center right
					preserve(
							box().width(rightWidth()).height(box().h() - topHeight() - bottomHeight()).center(box()).alignRight(box()),
							uvBox.scale(rightWidth() / width(), (height() - topHeight() - bottomHeight()) / height()).center(uvBox).alignRight(uvBox)
					).new Repeated(rightWidth(), height() - topHeight() - bottomHeight()).render();

					// Bottom left
					preserve(
							box().width(leftWidth()).height(bottomHeight()).alignBottomLeft(box()),
							uvBox.scale(leftWidth() / width(), bottomHeight() / height()).alignBottomLeft(uvBox)
					).render();

					// Bottom center
					preserve(
							box().width(box().w() - leftWidth() - rightWidth()).height(bottomHeight()).center(box()).alignBottom(box()),
							uvBox.scale((width() - leftWidth() - rightWidth()) / width(), bottomHeight() / height()).center(uvBox).alignBottom(uvBox)
					).new Repeated(width() - leftWidth() - rightWidth(), bottomHeight()).render();

					// Bottom right
					preserve(
							box().width(rightWidth()).height(bottomHeight()).alignBottomRight(box()),
							uvBox.scale(rightWidth() / width(), bottomHeight() / height()).alignBottomRight(uvBox)
					).render();
				}
			}

			// 'NineSlicing'
		}

		public class Outlined implements Renderable {
			// Constructors

			public Outlined(Vector expansion, OutliningMode mode, OutliningStyle style) {
				this.expansion = expansion;
				this.mode = mode;
				this.style = style;
			}

			public Outlined(Vector expansion) {
				this(expansion, OutliningMode.NORMAL, OutliningStyle.CLAMPED);
			}

			// Fields

			public enum OutliningMode {
				NORMAL, SCISSORED
			}

			public enum OutliningStyle {
				CLAMPED, EDGE, EDGE_FADED
			}

			private final Vector expansion;
			private final OutliningMode mode;
			private final OutliningStyle style;

			// Accessors

			public Vector expansion() {
				return expansion;
			}

			public OutliningMode mode() {
				return mode;
			}

			public OutliningStyle style() {
				return style;
			}

			// Mutators

			public Outlined parent(UnaryOperator<Rectangle> rectangle) {
				return rectangle.apply(Rectangle.this).new Outlined(expansion(), mode(), style());
			}

			public Outlined expansion(Vector expansion) {
				return new Outlined(expansion, mode(), style());
			}

			public Outlined mode(OutliningMode mode) {
				return new Outlined(expansion(), mode, style());
			}

			public Outlined style(OutliningStyle style) {
				return new Outlined(expansion(), mode(), style);
			}

			// Interface Implementations

			@Override
			public boolean isRenderable() {
				return mode() == OutliningMode.NORMAL ? Rectangle.this.isRenderable() : (Renderable.isLegal(box()) && hasColor());
			}

			@Override
			public void render() {
				if (!isRenderable()) return;

				if (mode() == OutliningMode.NORMAL) {
					Rectangle.this.render();
				}

				Box corner 			= Box.fromVectorCentered(box().center(), expansion);
				Box gapHorizontal 	= Box.fromVectorCentered(box().center(), Vector.fromCartesian(box().w(), expansion.y()));
				Box gapVertical 	= Box.fromVectorCentered(box().center(), Vector.fromCartesian(expansion.x(), box().h()));

				double width = box().w() + expansion.x() * 2, height = box().h() + expansion.y() * 2;
				double xCornerScalar = expansion.x() / width, yCornerScalar = expansion.y() / height;

				AccurateColor topLeft, 		topLeftBottom, 		topLeftTop, 	topLeftDiagonal;
				AccurateColor bottomLeft, 	bottomLeftBottom, 	bottomLeftTop, 	bottomLeftDiagonal;
				AccurateColor bottomRight, 	bottomRightBottom, 	bottomRightTop, bottomRightDiagonal;
				AccurateColor topRight, 	topRightBottom, 	topRightTop, 	topRightDiagonal;

				switch (style) {
					case CLAMPED -> {
						topLeft 				= colorTopLeft();
						topLeftTop 				= colorAt(xCornerScalar, 0);
						topLeftBottom 			= colorAt(0, yCornerScalar);
						topLeftDiagonal 		= colorAt(xCornerScalar, yCornerScalar);

						bottomLeft 				= colorBottomLeft();
						bottomLeftTop 			= colorAt(0, 1 - yCornerScalar);
						bottomLeftBottom 		= colorAt(xCornerScalar, 1);
						bottomLeftDiagonal 		= colorAt(xCornerScalar, 1 - yCornerScalar);

						bottomRight 			= colorBottomRight();
						bottomRightTop 			= colorAt(1, 1 - yCornerScalar);
						bottomRightBottom 		= colorAt(1 - xCornerScalar, 1);
						bottomRightDiagonal 	= colorAt(1 - xCornerScalar, 1 - yCornerScalar);

						topRight 				= colorTopRight();
						topRightTop 			= colorAt(1 - xCornerScalar, 0);
						topRightBottom 			= colorAt(1, yCornerScalar);
						topRightDiagonal 		= colorAt(1 - xCornerScalar, yCornerScalar);
					}
					case EDGE -> {
						topLeft 				= colorTopLeft();
						topLeftTop 				= colorAt(xCornerScalar, 0);
						topLeftBottom 			= colorAt(0, yCornerScalar);
						topLeftDiagonal 		= colorAtCenter();

						bottomLeft 				= colorBottomLeft();
						bottomLeftTop 			= colorAt(0, 1 - yCornerScalar);
						bottomLeftBottom 		= colorAt(xCornerScalar, 1);
						bottomLeftDiagonal 		= colorAtCenter();

						bottomRight 			= colorBottomRight();
						bottomRightTop 			= colorAt(1, 1 - yCornerScalar);
						bottomRightBottom 		= colorAt(1 - xCornerScalar, 1);
						bottomRightDiagonal 	= colorAtCenter();

						topRight 				= colorTopRight();
						topRightTop 			= colorAt(1 - xCornerScalar, 0);
						topRightBottom 			= colorAt(1, yCornerScalar);
						topRightDiagonal 		= colorAtCenter();
					}
					case EDGE_FADED -> {
						topLeftDiagonal = colorTopLeft();
						topLeft = topLeftTop = topLeftBottom = Palette.TRANSPARENT;

						bottomLeftDiagonal = colorBottomLeft();
						bottomLeft = bottomLeftTop = bottomLeftBottom = Palette.TRANSPARENT;

						bottomRightDiagonal = colorBottomRight();
						bottomRight = bottomRightTop = bottomRightBottom = Palette.TRANSPARENT;

						topRightDiagonal = colorTopRight();
						topRight = topRightTop = topRightBottom = Palette.TRANSPARENT;
					}
					default -> {
						topLeft = 		topLeftBottom = 		topLeftTop =	 	topLeftDiagonal = 		Palette.TRANSPARENT;
						bottomLeft = 	bottomLeftBottom = 		bottomLeftTop = 	bottomLeftDiagonal = 	Palette.TRANSPARENT;
						bottomRight = 	bottomRightBottom = 	bottomRightTop = 	bottomRightDiagonal = 	Palette.TRANSPARENT;
						topRight = 		topRightBottom = 		topRightTop = 		topRightDiagonal = 		Palette.TRANSPARENT;
					}
				}

				// Top left
				preserve(corner.alignBottomRight(box().topLeft()))
						.colorTopLeft(topLeft)
						.colorBottomLeft(topLeftBottom)
						.colorBottomRight(topLeftDiagonal)
						.colorTopRight(topLeftTop)
						.render();

				// Bottom left
				preserve(corner.alignTopRight(box().bottomLeft()))
						.colorTopLeft(bottomLeftTop)
						.colorBottomLeft(bottomLeft)
						.colorBottomRight(bottomLeftBottom)
						.colorTopRight(bottomLeftDiagonal)
						.render();

				// Bottom right
				preserve(corner.alignTopLeft(box().bottomRight()))
						.colorTopLeft(bottomRightDiagonal)
						.colorBottomLeft(bottomRightBottom)
						.colorBottomRight(bottomRight)
						.colorTopRight(bottomRightTop)
						.render();

				// Top right
				preserve(corner.alignBottomLeft(box().topRight()))
						.colorTopLeft(topRightTop)
						.colorBottomLeft(topRightDiagonal)
						.colorBottomRight(topRightBottom)
						.colorTopRight(topRight)
						.render();

				// Top
				preserve(gapHorizontal.alignBottomLeft(box().topLeft()))
						.colorTopLeft(topLeftTop)
						.colorBottomLeft(topLeftDiagonal)
						.colorBottomRight(topRightDiagonal)
						.colorTopRight(topRightTop)
						.render();

				// Bottom
				preserve(gapHorizontal.alignTopLeft(box().bottomLeft()))
						.colorTopLeft(bottomLeftDiagonal)
						.colorBottomLeft(bottomLeftBottom)
						.colorBottomRight(bottomRightBottom)
						.colorTopRight(bottomRightDiagonal)
						.render();

				// Left
				preserve(gapVertical.alignTopRight(box().topLeft()))
						.colorTopLeft(topLeftBottom)
						.colorBottomLeft(bottomLeftTop)
						.colorBottomRight(bottomLeftDiagonal)
						.colorTopRight(topLeftDiagonal)
						.render();

				// Right
				preserve(gapVertical.alignTopLeft(box().topRight()))
						.colorTopLeft(topRightDiagonal)
						.colorBottomLeft(bottomRightDiagonal)
						.colorBottomRight(bottomRightTop)
						.colorTopRight(topRightBottom)
						.render();
			}

			// 'Outlined'
		}

		// 'Rectangle'
	}

	public class Oval implements Renderable {
		// Constructors

		public Oval(
				double offset, double radians, double breadth,
				@Nullable AccurateColor colorCenter, @Nullable ColorTable colors,
				double opacityMultiplier,
				ColorStandard.MixMode mixMode, VertexProvider outline, OvalMode mode
		) {
			this.offset = Theory.mod(offset, 2 * Math.PI);
			this.radians = Theory.clamp(radians, -2 * Math.PI, 2 * Math.PI);
			this.breadth = breadth;

			this.colorCenter = AccurateColor.notnull(colorCenter);
			this.colors = colors == null ? new ColorTable(null) : colors;

			this.opacityMultiplier = Theory.clamp(opacityMultiplier, 0, 1);
			this.mixMode = mixMode;
			this.outline = outline;
			this.mode = mode;
		}

		public Oval(double offset, double radians, AccurateColor color) {
			this(offset, radians, 0, color, null, 1, ColorStandard.MixMode.BLEND, VertexProvider.NONE, OvalMode.FILL);
		}

		public Oval(AccurateColor color) {
			this(0, 2 * Math.PI, color);
		}

		public Oval() {
			this(Palette.TRANSPARENT);
		}

		// Fields

		public enum VertexProvider {
			NONE(
					(box, offset, breadth) -> box.center(),
					(box, offset, breadth) -> box.center().add(Vector.fromCartesian(
							Math.cos(offset) * (box.w() / 2),
							Math.sin(offset) * (box.h() / 2)
					))
			),
			INNER(
					(box, offset, breadth) -> box.center().add(Vector.fromCartesian(
							Math.cos(offset) * (box.w() / 2 - breadth),
							Math.sin(offset) * (box.h() / 2 - breadth)
					)),
					NONE.outerVertexFunction
			),
			OUTER(
					NONE.outerVertexFunction,
					(box, offset, breadth) -> box.center().add(Vector.fromCartesian(
							Math.cos(offset) * (box.w() / 2 + breadth),
							Math.sin(offset) * (box.h() / 2 + breadth)
					))
			),
			BOTH(
					(box, offset, breadth) -> box.center().add(Vector.fromCartesian(
							Math.cos(offset) * (box.w() / 2 - breadth / 2),
							Math.sin(offset) * (box.h() / 2 - breadth / 2)
					)),
					(box, offset, breadth) -> box.center().add(Vector.fromCartesian(
							Math.cos(offset) * (box.w() / 2 + breadth / 2),
							Math.sin(offset) * (box.h() / 2 + breadth / 2)
					))
			);

			@FunctionalInterface
			interface VertexFunction {
				@NotNull Vector vertexAt(Box box, double offset, double breadth);
			}

			private final VertexFunction innerVertexFunction, outerVertexFunction;

			VertexProvider(VertexFunction innerVertexFunction, VertexFunction outerVertexFunction) {
				this.innerVertexFunction = innerVertexFunction;
				this.outerVertexFunction = outerVertexFunction;
			}

			public @NotNull Vector innerVertexAt(Box box, double offset, double breadth) {
				return innerVertexFunction.vertexAt(box, offset, breadth);
			}

			public @NotNull Vector outerVertexAt(Box box, double offset, double breadth) {
				return outerVertexFunction.vertexAt(box, offset, breadth);
			}
		}
		
		public static class ColorTable {
			public static @NotNull LinkedHashMap<Double, @NotNull AccurateColor> sort(@Nullable LinkedHashMap<Double, @NotNull AccurateColor> colors) {
				if (colors == null) {
					return new LinkedHashMap<>();
				}

				LinkedHashMap<Double, @NotNull AccurateColor> sorted = new LinkedHashMap<>();

				colors.entrySet().stream()
						.sorted(Map.Entry.comparingByKey())
						.map(ColorTable::check)
						.forEachOrdered(entry -> sorted.put(entry.getKey(), entry.getValue()));

				return sorted;
			}

			public static @NotNull Map.Entry<Double, @NotNull AccurateColor> check(@NotNull Map.Entry<Double, @NotNull AccurateColor> entry) {
				return new AbstractMap.SimpleEntry<>(modOffset(entry.getKey()), entry.getValue());
			}

			private static double modOffset(double offset) {
				return Theory.mod(Theory.mod(offset, 2 * Math.PI) + 2 * Math.PI, 2 * Math.PI);
			}

			public ColorTable(@Nullable Map<Double, @NotNull AccurateColor> colors) {
				this.colors = colors == null ? ImmutableMap.of() : ImmutableMap.copyOf(sort(new LinkedHashMap<>(colors)));
			}
			
			public ColorTable() {
				this(null);
			}

			private final @NotNull ImmutableMap<Double, @NotNull AccurateColor> colors;

			public @NotNull ImmutableMap<Double, @NotNull AccurateColor> colors() {
				return colors;
			}

			public boolean hasColor() {
				return !colors().isEmpty();
			}

			public ColorTable putColor(double offset, @Nullable AccurateColor color) {
				color = AccurateColor.notnull(color);

				Map<Double, @NotNull AccurateColor> colors = new HashMap<>(colors());
				colors.put(offset, color);

				return new ColorTable(colors);
			}

			public @Nullable Map.Entry<Double, @NotNull AccurateColor> previous(double offset) {
				if (!hasColor()) return null;
				assert !colors().isEmpty();

				final double targetOffset = modOffset(offset);

				Optional<Map.Entry<Double, @NotNull AccurateColor>> previous = colors().entrySet().stream()
																					   .filter(entry -> entry.getKey() <= targetOffset)
																					   .reduce((first, second) -> second)
																					   .stream()
																					   .findFirst();

				Map.Entry<Double, @NotNull AccurateColor> last = colors().entrySet().stream()
																		 .reduce((first, second) -> second)
																		 .stream().
																		 findFirst()
																		 .orElse(null);

				return previous.orElse(last);
			}

			public @Nullable Map.Entry<Double, @NotNull AccurateColor> next(double offset) {
				if (!hasColor()) return null;
				assert !colors().isEmpty();

				final double targetOffset = modOffset(offset);

				Optional<Map.Entry<Double, @NotNull AccurateColor>> next = colors().entrySet().stream()
																				   .filter(entry -> entry.getKey() >= targetOffset)
																				   .findFirst();

				Map.Entry<Double, @NotNull AccurateColor> first = colors().entrySet().stream()
																		  .findFirst()
																		  .orElse(null);

				return next.orElse(first);
			}

			public @NotNull AccurateColor colorAt(double offset, ColorStandard.MixMode mixMode) {
				if (!hasColor()) return Palette.TRANSPARENT;
				offset = modOffset(offset);

				@Nullable Map.Entry<Double, @NotNull AccurateColor>
						previous = previous(offset),
						next = next(offset);

				if (previous == null || next == null) {
					if (previous == null && next == null) {
						return Palette.TRANSPARENT;
					} else return Objects.requireNonNullElse(previous, next).getValue();
				}

				double
						previousOffset = previous.getKey(),
						nextOffset = next.getKey(),
						previousDistance = Math.abs(previousOffset + (previousOffset >= offset ? -2 * Math.PI : 0) - offset),
						nextDistance = Math.abs(nextOffset + (nextOffset <= offset ? 2 * Math.PI : 0) - offset),
						totalDistance = previousDistance + nextDistance;

				double ratio = totalDistance != 0 ? previousDistance / totalDistance : 0;

				return previous.getValue().mix(next.getValue(), ratio, mixMode);
			}
		}

		public enum OvalMode {
			FILL((oval, offset1, radiusFactor) -> oval.colorCenter()),
			GRADIANT((oval, offset, radiusFactor) -> oval.colorAt(offset)),
			GRADIANT_OUT((oval, offset, radiusFactor) ->
								 oval.colorCenter().mix(oval.colorAt(offset), radiusFactor, oval.mixMode())),
			GRADIANT_IN((oval, offset, radiusFactor) ->
								oval.colorAt(offset).mix(oval.colorCenter(), radiusFactor, oval.mixMode())),
			FILL_GRADIANT_OUT((oval, offset, radiusFactor) -> Theory.looseEquals(radiusFactor, 1)
																			  ? GRADIANT.colorAt(oval, offset, radiusFactor)
																			  : oval.colorCenter()),
			FILL_GRADIANT_IN((oval, offset, radiusFactor) -> !Theory.looseEquals(radiusFactor, 1)
																			 ? GRADIANT.colorAt(oval, offset, radiusFactor)
																			 : oval.colorCenter());

			@FunctionalInterface
			interface ColorFunction {
				@NotNull AccurateColor getColor(Oval oval, double offset, double radiusFactor);
			}

			private final ColorFunction colorFunction;

			OvalMode(ColorFunction colorFunction) {
				this.colorFunction = colorFunction;
			}

			public @NotNull AccurateColor colorAt(Oval oval, double offset, double radiusFactor) {
				return colorFunction.getColor(oval, offset, radiusFactor);
			}
		}

		private final double offset, radians, breadth;
		private final @NotNull AccurateColor colorCenter;
		private final @NotNull ColorTable colors;
		private final double opacityMultiplier;
		private final ColorStandard.MixMode mixMode;
		private final VertexProvider outline;
		private final OvalMode mode;

		// Accessors

		public double offset() {
			return offset;
		}

		public double radians() {
			return radians;
		}

		public double breadth() {
			return breadth;
		}

		public @NotNull AccurateColor colorCenter() {
			return colorCenter;
		}

		public @NotNull ColorTable colors() {
			return colors;
		}

		public double opacityMultiplier() {
			return opacityMultiplier;
		}

		public ColorStandard.MixMode mixMode() {
			return mixMode;
		}

		public VertexProvider outline() {
			return outline;
		}

		public OvalMode mode() {
			return mode;
		}

		// Mutators

		public Oval parent(UnaryOperator<Flat> flat) {
			return flat.apply(Flat.this).new Oval(offset(), radians(), breadth(), colorCenter(), colors(), opacityMultiplier(), mixMode(), outline(), mode());
		}

		public Oval offset(double offset) {
			return new Oval(offset, radians(), breadth(), colorCenter(), colors(), opacityMultiplier(), mixMode(), outline(), mode());
		}

		public Oval radians(double radians) {
			return new Oval(offset(), radians, breadth(), colorCenter(), colors(), opacityMultiplier(), mixMode(), outline(), mode());
		}

		public Oval breadth(double breadth) {
			return new Oval(offset(), radians(), breadth, colorCenter(), colors(), opacityMultiplier(), mixMode(), outline(), mode());
		}

		public Oval colorCenter(@Nullable AccurateColor colorCenter) {
			return new Oval(offset(), radians(), breadth(), colorCenter, colors(), opacityMultiplier(), mixMode(), outline(), mode());
		}

		public Oval colors(@Nullable ColorTable colors) {
			return new Oval(offset(), radians(), breadth(), colorCenter(), colors, opacityMultiplier(), mixMode(), outline(), mode());
		}

		public Oval addColor(double offset, @Nullable AccurateColor color) {
			return new Oval(offset(), radians(), breadth(), colorCenter(), colors().putColor(offset, color), opacityMultiplier(), mixMode(), outline(), mode());
		}

		public Oval opacityMultiplier(double opacityMultiplier) {
			return new Oval(offset(), radians(), breadth(), colorCenter(), colors(), opacityMultiplier, mixMode(), outline(), mode());
		}

		public Oval mixMode(ColorStandard.MixMode mixMode) {
			return new Oval(offset(), radians(), breadth(), colorCenter(), colors(), opacityMultiplier(), mixMode, outline(), mode());
		}

		public Oval outline(VertexProvider outline) {
			return new Oval(offset(), radians(), breadth(), colorCenter(), colors(), opacityMultiplier(), mixMode(), outline, mode());
		}

		public Oval outline(VertexProvider outline, double breadth) {
			return outline(outline).breadth(breadth);
		}

		public Oval mode(OvalMode mode) {
			return new Oval(offset(), radians(), breadth(), colorCenter(), colors(), opacityMultiplier(), mixMode(), outline(), mode);
		}

		// Properties

		public @NotNull AccurateColor colorAt(double offset) {
			if (!hasColor()) return Palette.TRANSPARENT;

			return colors().colorAt(offset, mixMode());
		}

		private @NotNull Vector innerVertexAt(double offset) {
			return outline().innerVertexAt(box(), offset, radians());
		}

		private @NotNull Vector outerVertexAt(double offset) {
			return outline().outerVertexAt(box(), offset, radians());
		}

		public boolean hasCenter() {
			return colorCenter().hasColor();
		}

		public boolean hasColor() {
			if (!Theory.looseGreater(opacityMultiplier(), 0)) return false;

			return colors().hasColor();
		}

		public double eccentricity() {
			return Math.sqrt(Math.abs(Math.pow(box().w(), 2) - Math.pow(box().h(), 2))) / Math.max(box().w(), box().h());
		}

		private void renderVertex(BufferBuilder builder, Matrix4f matrix, Vector vertex, AccurateColor color, float z) {
			builder.vertex(matrix, (float) vertex.x(), (float) vertex.y(), z)
					.color(color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(), color.opacityAsFloat())
					.next();
		}

		private void renderInnerVertex(BufferBuilder builder, Matrix4f matrix, double offset, AccurateColor color, float z) {
			renderVertex(builder, matrix, innerVertexAt(offset), color, z);
		}

		private void renderOuterVertex(BufferBuilder builder, Matrix4f matrix, double offset, AccurateColor color, float z) {
			renderVertex(builder, matrix, outerVertexAt(offset), color, z);
		}

		private double delta() {
			double maxLength = Math.max(box().w(), box().h());
			return Math.max(0.01, 1 / (maxLength / 2 * 2 * Math.PI)); // Approximately 1 pixel per segment
		}

		private double clampOffset(double offset) {
			return Theory.clamp(offset, -radians(), radians());
		}

		private double nextOffset(double offset) {
			boolean positive = radians() >= 0;

			if (positive) {
				return offset + delta();
			} else {
				return offset - delta();
			}
		}

		private boolean isOffsetLegal(double offset) {
			return radians() >= 0 ? offset <= radians() + delta() : offset >= radians() - delta();
		}

		// Interface Implementations

		@Override
		public boolean isRenderable() {
			return (hasCenter() || hasColor()) && !Theory.looseEquals(radians(), 0) && Renderable.isLegal(box());
		}

		@Override
		public void render() {
			if (!isRenderable()) return;

			if (outline() != VertexProvider.NONE && Theory.looseEquals(breadth(), 1)) return;

			RenderSystem.enableBlend();
			RenderSystem.setShader(GameRenderer::getPositionColorProgram);
			RenderSystem.disableCull(); // Prevents triangles from being culled

			BufferBuilder builder = Tessellator.getInstance().getBuffer();
			Matrix4f matrix = matrixStack().peek().getPositionMatrix();

			builder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

			for (
					double offset = offset();
					isOffsetLegal(offset);
					offset = nextOffset(offset)
			) {
				double clampedOffset = clampOffset(offset); // Prevents offset from exceeding the end of the arc

				Vector
						edge = VertexProvider.NONE.outerVertexAt(box(), clampedOffset, breadth()),
						innerEdge = innerVertexAt(clampedOffset),
						outerEdge = outerVertexAt(clampedOffset);
				double
						radius = edge.distanceTo(box().center()),
						radiusFactor = 1 - (edge.distanceTo(innerEdge) + edge.distanceTo(outerEdge)) / radius;

				// Render the inner (center) vertex
				renderVertex(builder, matrix, innerEdge,
						mode().colorAt(this, clampedOffset - offset(), radiusFactor)
								.multiplyOpacity(opacityMultiplier()), z());

				// Render the outer vertex
				renderVertex(builder, matrix, outerEdge,
						mode().colorAt(this, clampedOffset - offset(), 1)
								.multiplyOpacity(opacityMultiplier()), z());
			}

			BufferRenderer.drawWithGlobalProgram(builder.end());

			RenderSystem.disableBlend();
			RenderSystem.enableCull();
		}

		// 'Oval'
	}

	public class Text implements Renderable {
		// Constructors

		public Text(@NotNull Section section, @Nullable AccurateColor color, @Nullable TextRenderer textRenderer, Section.Alignment verticalAlignment, Paragraph.Alignment horizontalAlignment, boolean shadowed, boolean culled) {
			this.section = section;
			this.color = color;
			this.textRenderer = textRenderer == null ? MinecraftClient.getInstance().textRenderer : textRenderer;
			this.verticalAlignment = verticalAlignment;
			this.horizontalAlignment = horizontalAlignment;
			this.shadowed = shadowed;
			this.culled = culled;
		}

		public Text(@NotNull Section section, @Nullable AccurateColor color, Section.Alignment verticalAlignment, Paragraph.Alignment horizontalAlignment, boolean shadowed) {
			this(section, color, null, verticalAlignment, horizontalAlignment, shadowed, false);
		}

		public Text(UnaryOperator<Section> builder) {
			this(builder.apply(Section.DEFAULT), Palette.WHITE, null, Section.Alignment.TOP, Paragraph.Alignment.LEFT, false, false);
		}

		// Fields

		private final @NotNull Section section;
		private final @Nullable AccurateColor color;
		private final @NotNull TextRenderer textRenderer;
		private final Section.Alignment verticalAlignment;
		private final Paragraph.Alignment horizontalAlignment;
		private final boolean shadowed, culled;

		// Accessors

		public @NotNull Section section() {
			return section;
		}

		public @Nullable AccurateColor color() {
			return color;
		}

		public @NotNull TextRenderer textRenderer() {
			return textRenderer;
		}

		public Section.Alignment verticalAlignment() {
			return verticalAlignment;
		}

		public Paragraph.Alignment horizontalAlignment() {
			return horizontalAlignment;
		}

		public boolean shadowed() {
			return shadowed;
		}

		public boolean culled() {
			return culled;
		}

		// Mutators

		public Text parent(UnaryOperator<Flat> flat) {
			return flat.apply(Flat.this).new Text(section(), color(), textRenderer(), verticalAlignment(), horizontalAlignment(), shadowed(), culled());
		}

		protected Text preserve(Box box) {
			return parent(flat -> flat.box(box));
		}

		public Text section(@NotNull Section section) {
			return new Text(section, color(), textRenderer(), verticalAlignment(), horizontalAlignment(), shadowed(), culled());
		}

		public Text section(UnaryOperator<Section> section) {
			return section(section.apply(section()));
		}

		public Text textRenderer(@Nullable TextRenderer textRenderer) {
			return new Text(section(), color(), textRenderer, verticalAlignment(), horizontalAlignment(), shadowed(), culled());
		}

		public Text color(@Nullable AccurateColor color) {
			return new Text(section(), color, textRenderer(), verticalAlignment(), horizontalAlignment(), shadowed(), culled());
		}

		public Text verticalAlignment(Section.Alignment verticalAlignment) {
			return new Text(section(), color(), textRenderer(), verticalAlignment, horizontalAlignment(), shadowed(), culled());
		}

		public Text horizontalAlignment(Paragraph.Alignment horizontalAlignment) {
			return new Text(section(), color(), textRenderer(), verticalAlignment(), horizontalAlignment, shadowed(), culled());
		}

		public Text shadowed(boolean shadowed) {
			return new Text(section(), color(), textRenderer(), verticalAlignment(), horizontalAlignment(), shadowed, culled());
		}

		public Text withShadow() {
			return shadowed(true);
		}

		public Text withoutShadow() {
			return shadowed(false);
		}

		public Text culled(boolean culled) {
			return new Text(section(), color(), textRenderer(), verticalAlignment(), horizontalAlignment(), shadowed(), culled);
		}

		public Text enableCulling() {
			return culled(true);
		}

		public Text disableCulling() {
			return culled(false);
		}

		// Properties

		public boolean hasColor() {
			return color() != null;
		}

		public double width() {
			return section().width() + Theory.EPSILON;
		}

		public double height() {
			return section().wrappedHeight(box().w()) + Theory.EPSILON;
		}

		// Interface Implementations

		@Override
		public boolean isRenderable() {
			return Renderable.isLegal(box()) && !section().isEmpty() && hasColor();
		}

		@Override
		public void render() {
			if (!isRenderable()) return;

			Scissor scissor = box().scissor();

			if (culled()) {
				scissor.snipOn();
			}

			matrixStack().push();
			matrixStack().translate(box().x(), box().y(), 0);

			section().render(box().alignTopLeft(Vector.ZERO), matrixStack(), textRenderer(), color(), verticalAlignment(), horizontalAlignment(), shadowed());

			matrixStack().pop();

			if (culled()) {
				scissor.snipOff();
			}
		}

		public class Tooltip implements Renderable {
			// Constructors

			public Tooltip(double bleeding, TooltipSnap tooltipSnap) {
				this.bleeding = bleeding;
				this.tooltipSnap = tooltipSnap;
			}

			public Tooltip(TooltipSnap tooltipSnap) {
				this(3, tooltipSnap);
			}

			public Tooltip() {
				this(TooltipSnap.NONE);
			}

			// Fields
			
			public enum TooltipSnap {
				NONE((box, width, height) -> box, (height, wrappedHeight) -> height),
				HORIZONTAL((box, width, height) -> box.width(width), (height, wrappedHeight) -> wrappedHeight),
				VERTICAL((box, width, height) -> box.height(height), (height, wrappedHeight) -> height),
				BOTH((box, width, height) -> box.width(width).height(height), (height, wrappedHeight) -> wrappedHeight);
				
				@FunctionalInterface
				interface SnapFunction {
					Box snap(Box box, double width, double height);
				}
				
				private final SnapFunction snapFunction;
				private final BinaryOperator<Double> heightOperator;
				
				TooltipSnap(SnapFunction snapFunction, BinaryOperator<Double> heightOperator) {
					this.snapFunction = snapFunction;
					this.heightOperator = heightOperator;
				}
				
				public Box snap(Box box, double width, double height) {
					return snapFunction.snap(box, width, height);
				}

				public double snapHeight(double height, double wrappedHeight) {
					return heightOperator.apply(height, wrappedHeight);
				}
			}
			
			private final double bleeding;
			private final TooltipSnap tooltipSnap;

			// Accessors

			public double bleeding() {
				return bleeding;
			}

			public TooltipSnap tooltipSnap() {
				return tooltipSnap;
			}

			// Mutators

			public Tooltip parent(UnaryOperator<Text> text) {
				return text.apply(Text.this).new Tooltip(bleeding(), tooltipSnap());
			}

			public Tooltip bleeding(double bleeding) {
				return new Tooltip(bleeding, tooltipSnap());
			}

			public Tooltip tooltipSnap(TooltipSnap tooltipSnap) {
				return new Tooltip(bleeding(), tooltipSnap);
			}

			// Interface Implementations

			@Override
			public boolean isRenderable() {
				return Text.this.isRenderable();
			}

			@Override
			public void render() {
				if (!isRenderable()) return;

				Box
						context = box().expand(-bleeding()),
						raw = Box.fromCartesian(
								width(),
								tooltipSnap().snapHeight(section().height(), preserve(context).height())
						).alignTopLeft(context),
						snapped = tooltipSnap().snap(raw, context.w(), context.h());

				VanillaWidgets.Tooltip.render(matrixStack(), snapped.expand(bleeding()));
				preserve(snapped).render();
			}

			// 'Tooltip'
		}

		// 'Text'
	}

	public class Item implements Renderable {
		// Constructors

		public Item(@NotNull ItemStack itemStack, @Nullable Quaternion modifier, boolean leftHanded) {
			this.itemStack = itemStack;
			this.modifier = modifier == null ? new Quaternion() : modifier;
			this.leftHanded = leftHanded;
		}

		public Item(@NotNull ItemStack itemStack, boolean leftHanded) {
			this(itemStack, null, leftHanded);
		}

		public Item(@NotNull ItemStack itemStack) {
			this(itemStack, false);
		}

		// Fields

		private final @NotNull ItemStack itemStack;
		private final @NotNull Quaternion modifier;
		private final boolean leftHanded;

		// Accessors

		public @NotNull ItemStack itemStack() {
			return itemStack;
		}

		public @NotNull Quaternion modifier() {
			return modifier;
		}

		public boolean leftHanded() {
			return leftHanded;
		}

		// Mutators

		public Item parent(UnaryOperator<Flat> flat) {
			return flat.apply(Flat.this).new Item(itemStack(), modifier(), leftHanded());
		}

		public Item model(@NotNull ItemStack itemStack) {
			return new Item(itemStack, modifier(), leftHanded());
		}

		public Item modifier(@Nullable Quaternion modifier) {
			return new Item(itemStack(), modifier, leftHanded());
		}

		public Item modifier(UnaryOperator<Quaternion> modifier) {
			return modifier(modifier.apply(modifier()));
		}

		public Item leftHanded(boolean leftHanded) {
			return new Item(itemStack(), modifier(), leftHanded);
		}

		public Item leftHand() {
			return leftHanded(true);
		}

		public Item rightHand() {
			return leftHanded(false);
		}

		// Interface Implementations

		@Override
		public boolean isRenderable() {
			return Renderable.isLegal(box());
		}

		@SuppressWarnings("deprecation")
		private void prepareModel() {
			MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
			RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}

		private void applyModelView(MatrixStack matrixStack) {
			matrixStack.scale(1, -1, 1);
			matrixStack.scale((float) box().w(), (float) box().h(), 1);
			matrixStack.multiply(modifier().toFloat());

			RenderSystem.applyModelViewMatrix();
		}

		@Override
		public void render() {
			if (!isRenderable()) return;

			renderItemModel: {
				BakedModel bakedModel = MinecraftClient.getInstance().getItemRenderer().getModel(itemStack(), null, null, 0);

				prepareModel();

				matrixStack().push();
				matrixStack().translate(box().center().x(), box().center().y(), z());
				applyModelView(matrixStack());

				if (!bakedModel.isSideLit())
					DiffuseLighting.disableGuiDepthLighting();

				VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

				MinecraftClient.getInstance().getItemRenderer().renderItem(
						itemStack(), ModelTransformation.Mode.GUI,
						leftHanded(), matrixStack(), immediate,
						0xF000F0, OverlayTexture.DEFAULT_UV, bakedModel
				);

				immediate.draw();
				RenderSystem.enableDepthTest();

				if (!bakedModel.isSideLit())
					DiffuseLighting.enableGuiDepthLighting();

				matrixStack().pop();
				RenderSystem.applyModelViewMatrix();
			}
		}

		// 'Item'
	}

	public class Block implements Renderable {
		// Constructors

		public Block(@NotNull BlockState blockState, @Nullable AccurateColor color, @Nullable Quaternion modifier) {
			this.blockState = blockState;
			this.color = color;
			this.modifier = modifier == null ? new Quaternion() : modifier;
		}

		public Block(@NotNull BlockState blockState, @Nullable AccurateColor color) {
			this(blockState, color, null);
		}

		public Block(@NotNull BlockState blockState, BlockPos blockPos, @Nullable Quaternion modifier) {
			this.blockState = blockState;
			this.color = AccurateColor.fromARGB(RenderManager.getBlockColorAt(blockState, MinecraftClient.getInstance().world, blockPos));
			this.modifier = modifier == null ? new Quaternion() : modifier;
		}

		public Block(@NotNull BlockState blockState, BlockPos blockPos) {
			this(blockState, blockPos, null);
		}

		public Block(@NotNull BlockState blockState) {
			this(blockState, (AccurateColor) null);
		}

		// Fields

		private final @NotNull BlockState blockState;
		// FIXME: 2023/7/12 Not very useful when rendering
		private final @Nullable AccurateColor color;
		private final @NotNull Quaternion modifier;

		// Accessors

		public @NotNull BlockState blockState() {
			return blockState;
		}

		public @Nullable AccurateColor color() {
			return color;
		}

		public @NotNull Quaternion modifier() {
			return modifier;
		}

		// Mutators

		public Block parent(UnaryOperator<Flat> flat) {
			return flat.apply(Flat.this).new Block(blockState(), color(), modifier());
		}

		public Block model(@NotNull BlockState blockState) {
			return new Block(blockState, color(), modifier());
		}

		public Block modifier(@Nullable Quaternion modifier) {
			return new Block(blockState(), color(), modifier);
		}

		public Block modifier(UnaryOperator<Quaternion> modifier) {
			return modifier(modifier.apply(modifier()));
		}

		// Properties

		public boolean hasColor() {
			return color() != null;
		}

		// Interface Implementations

		@Override
		public boolean isRenderable() {
			return Renderable.isLegal(box());
		}

		@Override
		public void render() {
			if (!isRenderable()) return;

			renderBlockModel: {
				BlockState blockState = blockState();

				if (blockState.getRenderType() == BlockRenderType.INVISIBLE) break renderBlockModel;

				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.setShaderColor(1, 1, 1, 1);

				matrixStack().push();
				matrixStack().translate(box.center().x(), box.center().y(), 0);

				matrixStack().scale(1, -1, 1);
				matrixStack().scale((float) box.w(), (float) box.h(), 1);
				matrixStack().multiply(modifier().toFloat());

				RenderSystem.applyModelViewMatrix();
				RenderSystem.disableCull();
				RenderSystem.enableDepthTest();

				matrixStack().translate(-0.5, -0.5, -0.5);

				VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

				switch (blockState.getRenderType()) {
					case MODEL -> {
						@NotNull AccurateColor color = hasColor() ? Objects.requireNonNull(color()) : Palette.WHITE;

						new BlockModelRenderer(MinecraftClient.getInstance().getBlockColors()).render(
								matrixStack().peek(), immediate.getBuffer(RenderLayers.getBlockLayer(blockState)), blockState,
								MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState),
								color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(),
								0xF000F0, OverlayTexture.DEFAULT_UV
						);
					}
					case ENTITYBLOCK_ANIMATED -> {
						// FIXME: 2023/7/12 The lightning is incorrect
						new BuiltinModelItemRenderer(MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader()).render(
								blockState.getBlock().asItem().getDefaultStack(), ModelTransformation.Mode.NONE,
								matrixStack(), immediate, 0xF000F0, OverlayTexture.DEFAULT_UV
						);
					}
				}

				immediate.draw();
				RenderSystem.enableCull();
				RenderSystem.disableDepthTest();

				matrixStack().pop();
				RenderSystem.applyModelViewMatrix();
			}
		}

		// 'Block'
	}
}
