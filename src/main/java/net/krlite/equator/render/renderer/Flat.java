package net.krlite.equator.render.renderer;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.Equator;
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
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class Flat extends Basic {
	// Constructors

	public Flat(DrawContext context, float z, Box box) {
		super(context);
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
		return new Flat(context(), z, box());
	}

	public Flat box(Box box) {
		return new Flat(context(), z(), box);
	}

	public class Rectangle implements Renderable {
		// Constructors

		public Rectangle(
				@Nullable Texture texture,
				@Nullable AccurateColor colorTopLeft, @Nullable AccurateColor colorBottomLeft, @Nullable AccurateColor colorBottomRight, @Nullable AccurateColor colorTopRight,
				Colorspace colorspace, RectangleMode rectangleMode
		) {
			this.texture = texture;
			this.colorTopLeft = 		(colorTopLeft == null ? 	AccurateColor.TRANSPARENT : colorTopLeft)		.colorspace(colorspace);
			this.colorBottomLeft = 		(colorBottomLeft == null ? 	AccurateColor.TRANSPARENT : colorBottomLeft)	.colorspace(colorspace);
			this.colorBottomRight = 	(colorBottomRight == null ? AccurateColor.TRANSPARENT : colorBottomRight)	.colorspace(colorspace);
			this.colorTopRight = 		(colorTopRight == null ? 	AccurateColor.TRANSPARENT : colorTopRight)		.colorspace(colorspace);
			this.colorspace = colorspace;
			this.rectangleMode = rectangleMode;
		}

		public Rectangle(
				@Nullable Texture texture,
				@Nullable AccurateColor colorTopLeft, @Nullable AccurateColor colorBottomLeft, @Nullable AccurateColor colorBottomRight, @Nullable AccurateColor colorTopRight,
				RectangleMode rectangleMode
		) {
			this(texture, colorTopLeft, colorBottomLeft, colorBottomRight, colorTopRight, Colorspace.RGB, rectangleMode);
		}

		public Rectangle(@Nullable Texture texture, @Nullable AccurateColor color, Colorspace colorspace, RectangleMode rectangleMode) {
			this(texture, color, color, color, color, colorspace, rectangleMode);
		}

		public Rectangle(@Nullable Texture texture, @Nullable AccurateColor color, RectangleMode rectangleMode) {
			this(texture, color, color, color, color, Colorspace.RGB, rectangleMode);
		}

		public Rectangle(Colorspace colorspace) {
			this(null, null, colorspace, RectangleMode.NORMAL);
		}

		public Rectangle(Texture texture) {
			this(texture, null, RectangleMode.NORMAL);
		}

		public Rectangle(AccurateColor color) {
			this(null, color, RectangleMode.NORMAL);
		}

		public Rectangle() {
			this(null, null, RectangleMode.NORMAL);
		}

		// Fields

		public enum RectangleMode {
			NORMAL, TILING, FIXED_CORNERS
		}

		@Nullable
		private final Texture texture;
		private final AccurateColor colorTopLeft, colorBottomLeft, colorBottomRight, colorTopRight;
		private final Colorspace colorspace;
		private final RectangleMode rectangleMode;

		// Accessors

		public Box box() {
			return box;
		}

		@Nullable
		public Texture texture() {
			return texture;
		}

		public AccurateColor colorTopLeft() {
			return colorTopLeft;
		}

		public AccurateColor colorBottomLeft() {
			return colorBottomLeft;
		}

		public AccurateColor colorBottomRight() {
			return colorBottomRight;
		}

		public AccurateColor colorTopRight() {
			return colorTopRight;
		}

		public AccurateColor[] colors() {
			return new AccurateColor[] { colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight() };
		}

		public AccurateColor colorAtCenter() {
			return colorAt(0.5, 0.5);
		}

		public RectangleMode rectangleMode() {
			return rectangleMode;
		}

		public Colorspace colorspace() {
			return colorspace;
		}

		// Mutators

		public Rectangle parent(UnaryOperator<Flat> flat) {
			return flat.apply(Flat.this).new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight(), colorspace(), rectangleMode());
		}

		protected Rectangle preserve(Box box, Box uvBox) {
			return parent(flat -> flat.box(box)).texture(hasTexture() ? Objects.requireNonNull(texture()).uvBox(uvBox) : texture());
		}

		protected Rectangle preserve(Box box) {
			return parent(flat -> flat.box(box));
		}

		public Rectangle texture(@Nullable Texture texture) {
			return new Rectangle(texture, colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight(), rectangleMode());
		}

		public Rectangle colorTopLeft(@Nullable AccurateColor colorTopLeft) {
			return new Rectangle(texture(), colorTopLeft, colorBottomLeft(), colorBottomRight(), colorTopRight(), colorspace(), rectangleMode());
		}

		public Rectangle colorBottomLeft(@Nullable AccurateColor colorBottomLeft) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft, colorBottomRight(), colorTopRight(), colorspace(), rectangleMode());
		}

		public Rectangle colorBottomRight(@Nullable AccurateColor colorBottomRight) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight, colorTopRight(), colorspace(), rectangleMode());
		}

		public Rectangle colorTopRight(@Nullable AccurateColor colorTopRight) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight, colorspace(), rectangleMode());
		}

		public Rectangle colorTop(@Nullable AccurateColor colorTop) {
			return new Rectangle(texture(), colorTop, colorBottomLeft(), colorBottomRight(), colorTop, colorspace(), rectangleMode());
		}

		public Rectangle colorBottom(@Nullable AccurateColor colorBottom) {
			return new Rectangle(texture(), colorTopLeft(), colorBottom, colorBottom, colorTopRight(), colorspace(), rectangleMode());
		}

		public Rectangle colorLeft(@Nullable AccurateColor colorLeft) {
			return new Rectangle(texture(), colorLeft, colorLeft, colorLeft, colorTopRight(), colorspace(), rectangleMode());
		}

		public Rectangle colorRight(@Nullable AccurateColor colorRight) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorRight, colorRight, colorspace(), rectangleMode());
		}

		public Rectangle colors(@Nullable AccurateColor colorTopLeft, @Nullable AccurateColor colorBottomLeft, @Nullable AccurateColor colorBottomRight, @Nullable AccurateColor colorTopRight) {
			return new Rectangle(texture(), colorTopLeft, colorBottomLeft, colorBottomRight, colorTopRight, colorspace(), rectangleMode());
		}

		public Rectangle colors(@Nullable AccurateColor color) {
			return new Rectangle(texture(), color, color, color, color, colorspace(), rectangleMode());
		}

		public Rectangle rectangleMode(RectangleMode rectangleMode) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight(), colorspace(), rectangleMode);
		}

		public Rectangle colorspace(Colorspace colorspace) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight(), colorspace, rectangleMode());
		}

		// Properties

		private enum State {
			UNABLE(null, null),
			COLOR(VertexFormats.POSITION_COLOR, GameRenderer::getPositionColorProgram),
			TEXTURE(VertexFormats.POSITION_TEXTURE, GameRenderer::getPositionTexProgram),
			COLOR_TEXTURE(VertexFormats.POSITION_COLOR_TEXTURE, GameRenderer::getPositionColorTexProgram);

			@Nullable
			private final VertexFormat vertexFormat;

			@Nullable
			private final Supplier<ShaderProgram> shaderProgram;

			State(@Nullable VertexFormat vertexFormat, @Nullable Supplier<ShaderProgram> shaderProgram) {
				this.vertexFormat = vertexFormat;
				this.shaderProgram = shaderProgram;
			}

			@Nullable
			public VertexFormat vertexFormat() {
				return vertexFormat;
			}

			@Nullable
			public Supplier<ShaderProgram> shaderProgram() {
				return shaderProgram;
			}
		}

		public boolean hasTexture() {
			return texture() != null;
		}

		public boolean hasColor() {
			return
					(colorTopLeft().hasColor() || colorBottomLeft().hasColor() || colorBottomRight().hasColor() || colorTopRight().hasColor())
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
			if (hasTexture()) {
				if (hasColor()) {
					return State.COLOR_TEXTURE;
				} else {
					return State.TEXTURE;
				}
			} else {
				if (hasColor()) {
					return State.COLOR;
				} else {
					return State.UNABLE;
				}
			}
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
			if (box().w() < 1) xDelta = box().w() / 2;
			else if (box().w() < 10) xDelta = box().w() / 4;
			else if (box().w() < 100) xDelta = box().w() / 8;
			else xDelta = Math.min(25, box().w() / 16);

			// Height optimization
			if (box().h() < 1) yDelta = box().h() / 2;
			else if (box().h() < 10) yDelta = box().h() / 4;
			else if (box().h() < 100) yDelta = box().h() / 8;
			else yDelta = Math.min(25, box().h() / 16);

			for (double yr = 0; yr < box().h(); yr += yDelta) {
				double y = Math.min(box().h(), yr);
				for (double xr = 0; xr < box().w() + xDelta; xr += xDelta) {
					double x = Math.min(box().w(), xr);
					renderVertex(
							builder, matrix, box().topLeft().add(x, y),
							hasTexture() ? Objects.requireNonNull(texture()).uvAt(x / box().w(), y / box().h()) : Vector.ZERO,
							assertColor(colorAt(x / box().w(), y / box().h())), z()
					);
					renderVertex(
							builder, matrix, box().topLeft().add(x, Math.min(box().h(), y + yDelta)),
							hasTexture() ? Objects.requireNonNull(texture()).uvAt(x / box().w(), Math.min(box().h(), y + yDelta) / box().h()) : Vector.ZERO,
							assertColor(colorAt(x / box().w(), Math.min(box().h(), y + yDelta) / box().h())), z()
					);
				}
			}

			BufferRenderer.drawWithGlobalProgram(builder.end());

			if (hasColor() && !blend) {
				RenderSystem.disableBlend();
			}
		}

		private void renderTiling() {
			preserve(FrameInfo.scaled(), FrameInfo.scaled().normalizeBy(box()).shift(0.5, 0.5)).render();
		}

		private void renderFixedCorners() {
			Box corner = box().squareInner().scaleCenter(0.5);

			// Top left
			preserve(corner.alignTopLeft(box()), new Box(0, 0, 0.5, 0.5)).render();

			// Bottom left
			preserve(corner.alignBottomLeft(box()), new Box(0, 0.5, 0.5, 1)).render();

			// Bottom right
			preserve(corner.alignBottomRight(box()), new Box(0.5, 0.5, 1, 1)).render();

			// Top right
			preserve(corner.alignTopRight(box()), new Box(0.5, 0, 1, 0.5)).render();

			if (box().w() > box().h()) {
				Box gap = Box.fromVector(corner.alignTopLeft(box()).topRight(), corner.alignTopRight(box()).bottomLeft());

				// Top
				preserve(gap, new Box(0.5, 0, 0.5, 0.5)).render();

				// Bottom
				preserve(gap.translate(0, 1), new Box(0.5, 0.5, 0.5, 1)).render();
			} else if (box().w() < box().h()) {
				Box gap = Box.fromVector(corner.alignTopLeft(box()).bottomLeft(), corner.alignBottomLeft(box()).topRight());

				// Left
				preserve(gap, new Box(0, 0.5, 0.5, 0.5)).render();

				// Right
				preserve(gap.translate(1, 0), new Box(0.5, 0.5, 1, 0.5)).render();
			}
		}

		@Override
		public boolean isRenderable() {
			return Renderable.isLegal(box()) && (hasTexture() || hasColor());
		}

		@Override
		public void render() {
			if (!isRenderable()) return;

			switch (rectangleMode()) {
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

			public Outlined(Vector expansion, OutliningMode outliningMode, OutliningStyle outliningStyle) {
				this.expansion = expansion;
				this.outliningMode = outliningMode;
				this.outliningStyle = outliningStyle;
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
			private final OutliningMode outliningMode;
			private final OutliningStyle outliningStyle;

			// Accessors

			public Vector expansion() {
				return expansion;
			}

			public OutliningMode outliningMode() {
				return outliningMode;
			}

			public OutliningStyle outliningStyle() {
				return outliningStyle;
			}

			// Mutators

			public Outlined parent(UnaryOperator<Rectangle> rectangle) {
				return rectangle.apply(Rectangle.this).new Outlined(expansion(), outliningMode(), outliningStyle());
			}

			public Outlined expansion(Vector expansion) {
				return new Outlined(expansion, outliningMode(), outliningStyle());
			}

			public Outlined outliningMode(OutliningMode outliningMode) {
				return new Outlined(expansion(), outliningMode, outliningStyle());
			}

			public Outlined outliningStyle(OutliningStyle outliningStyle) {
				return new Outlined(expansion(), outliningMode(), outliningStyle);
			}

			// Interface Implementations

			@Override
			public boolean isRenderable() {
				return outliningMode() == OutliningMode.NORMAL ? Rectangle.this.isRenderable() : (Renderable.isLegal(box()) && hasColor());
			}

			@Override
			public void render() {
				if (!isRenderable()) return;

				if (outliningMode() == OutliningMode.NORMAL) {
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

				switch (outliningStyle) {
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
						topLeft = topLeftTop = topLeftBottom = AccurateColor.TRANSPARENT;

						bottomLeftDiagonal = colorBottomLeft();
						bottomLeft = bottomLeftTop = bottomLeftBottom = AccurateColor.TRANSPARENT;

						bottomRightDiagonal = colorBottomRight();
						bottomRight = bottomRightTop = bottomRightBottom = AccurateColor.TRANSPARENT;

						topRightDiagonal = colorTopRight();
						topRight = topRightTop = topRightBottom = AccurateColor.TRANSPARENT;
					}
					default -> {
						topLeft = 		topLeftBottom = 		topLeftTop =	 	topLeftDiagonal = 		AccurateColor.TRANSPARENT;
						bottomLeft = 	bottomLeftBottom = 		bottomLeftTop = 	bottomLeftDiagonal = 	AccurateColor.TRANSPARENT;
						bottomRight = 	bottomRightBottom = 	bottomRightTop = 	bottomRightDiagonal = 	AccurateColor.TRANSPARENT;
						topRight = 		topRightBottom = 		topRightTop = 		topRightDiagonal = 		AccurateColor.TRANSPARENT;
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

		public Oval(double offset, double radians, double innerRadiusFactor, AccurateColor colorCenter, Map<Double, AccurateColor> colorMap, ColorStandard.MixMode mixMode, OvalMode ovalMode) {
			this.offset = offset;
			this.radians = radians;
			this.innerRadiusFactor = innerRadiusFactor;
			this.colorCenter = colorCenter;
			this.colorMap = ImmutableMap.copyOf(sortColorMap(colorMap));
			this.mixMode = mixMode;
			this.ovalMode = ovalMode;
		}

		public Oval(double offset, double radians, AccurateColor accurateColor) {
			this(offset, radians, 0, accurateColor, ImmutableMap.of(), ColorStandard.MixMode.BLEND, OvalMode.GRADIANT_OUT);
		}

		public Oval(AccurateColor color) {
			this(0, 2 * Math.PI, color);
		}

		public Oval() {
			this(AccurateColor.TRANSPARENT);
		}

		// Fields

		public enum OvalMode {
			FILL((oval, offset, radiusFactor) -> oval.colorAt(offset)),
			GRADIANT_OUT((oval, offset, radiusFactor) ->
								 oval.colorCenter().mix(oval.colorAt(offset), radiusFactor, oval.mixMode())),
			GRADIANT_IN((oval, offset, radiusFactor) ->
								oval.colorAt(offset).mix(oval.colorCenter(), radiusFactor, oval.mixMode())),
			FILL_GRADIANT_OUT((oval, offset, radiusFactor) -> Theory.looseEquals(radiusFactor, 1)
																			  ? FILL.getColor(oval, offset, radiusFactor)
																			  : oval.colorCenter()),
			FILL_GRADIANT_IN((oval, offset, radiusFactor) -> !Theory.looseEquals(radiusFactor, 1)
																			 ? FILL.getColor(oval, offset, radiusFactor)
																			 : oval.colorCenter());

			@FunctionalInterface
			interface ColorFunction {
				AccurateColor getColor(Oval oval, double offset, double radiusFactor);
			}

			private final ColorFunction colorFunction;

			OvalMode(ColorFunction colorFunction) {
				this.colorFunction = colorFunction;
			}

			public AccurateColor getColor(Oval oval, double offset, double radiusFactor) {
				return colorFunction.getColor(oval, offset, radiusFactor);
			}
		}

		private final double offset, radians, innerRadiusFactor;
		private final AccurateColor colorCenter;
		private final ImmutableMap<Double, AccurateColor> colorMap;
		private final ColorStandard.MixMode mixMode;
		private final OvalMode ovalMode;

		// Accessors

		public double offset() {
			return offset;
		}

		public double radians() {
			return radians;
		}

		public double innerRadiusFactor() {
			return innerRadiusFactor;
		}

		public AccurateColor colorCenter() {
			return colorCenter;
		}

		private static LinkedHashMap<Double, AccurateColor> sortColorMap(Map<Double, AccurateColor> colorMap) {
			return new LinkedHashMap<>(colorMap.entrySet()
											   .stream()
											   .sorted(Map.Entry.comparingByKey())
											   .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)));
		}

		public ImmutableMap<Double, AccurateColor> colorMap() {
			return ImmutableMap.copyOf(colorMap);
		}

		public Map<Double, AccurateColor> colorMapMutableCopy() {
			return sortColorMap(colorMap);
		}

		public AccurateColor firstColor() {
			return !colorMap().isEmpty() ? colorMap().values().stream().findFirst().orElseThrow() : existsColor() ? colorCenter() : AccurateColor.TRANSPARENT;
		}

		public AccurateColor lastColor() {
			return !colorMap().isEmpty() ? colorMap().values().stream().reduce((first, second) -> second).orElseThrow() : existsColor() ? colorCenter() : AccurateColor.TRANSPARENT;
		}

		public ImmutableMap<Double, AccurateColor> colorMapSafe() {
			Map<Double, AccurateColor> originalMap = colorMapMutableCopy();

			if (firstColor() != lastColor()) {
				if (!colorMap().containsKey(0.0) && !colorMap().containsKey(2 * Math.PI)) {
					AccurateColor color = firstColor();

					double firstOffset = originalMap.keySet().stream().findFirst().orElse(0.0);
					double offset = Math.abs(firstOffset - originalMap.keySet().stream().reduce((first, second) -> second).orElse(0.0));
					double factor = Theory.isZero(offset) ? 0 : (firstOffset / offset);
					color = color.mix(lastColor(), factor, mixMode());

					originalMap.put(0.0, color);
					originalMap.put(2 * Math.PI, color);
				} else if (colorMap().containsKey(0.0)) {
					originalMap.put(2 * Math.PI, colorMap().get(0.0));
				} else if (colorMap().containsKey(2 * Math.PI)) {
					originalMap.put(0.0, colorMap().get(2 * Math.PI));
				}
			}

			return ImmutableMap.copyOf(sortColorMap(originalMap));
		}

		public ColorStandard.MixMode mixMode() {
			return mixMode;
		}

		public OvalMode ovalMode() {
			return ovalMode;
		}

		// Mutators

		public Oval parent(UnaryOperator<Flat> flat) {
			return flat.apply(Flat.this).new Oval(offset(), radians(), innerRadiusFactor(), colorCenter(), colorMap(), mixMode(), ovalMode());
		}

		public Oval offset(double offset) {
			return new Oval(offset, radians(), innerRadiusFactor(), colorCenter(), colorMap(), mixMode(), ovalMode());
		}

		public Oval radians(double radians) {
			return new Oval(offset(), radians, innerRadiusFactor(), colorCenter(), colorMap(), mixMode(), ovalMode());
		}

		public Oval innerRadiusFactor(double innerRadiusFactor) {
			return new Oval(offset(), radians(), innerRadiusFactor, colorCenter(), colorMap(), mixMode(), ovalMode());
		}

		public Oval colorCenter(AccurateColor colorCenter) {
			return new Oval(offset(), radians(), innerRadiusFactor(), colorCenter, colorMap(), mixMode(), ovalMode());
		}

		public Oval colorMap(Map<Double, AccurateColor> colorMap) {
			return new Oval(offset(), radians(), innerRadiusFactor(), colorCenter(), colorMap, mixMode(), ovalMode());
		}

		public Oval addColor(double at, AccurateColor color) {
			return colorMap(ImmutableMap.<Double, AccurateColor>builder()
									.putAll(colorMap())
									.put(modOffset(at), color)
									.build());
		}

		public Oval mixMode(ColorStandard.MixMode mixMode) {
			return new Oval(offset(), radians(), innerRadiusFactor(), colorCenter(), colorMap(), mixMode, ovalMode());
		}

		public Oval ovalMode(OvalMode ovalMode) {
			return new Oval(offset(), radians(), innerRadiusFactor(), colorCenter(), colorMap(), mixMode(), ovalMode);
		}

		// Properties

		private static double modOffset(double offset) {
			return Theory.mod(offset, 2 * Math.PI);
		}

		public Map.Entry<Double, AccurateColor> nearestPrevious(double offset) {
			return !colorMapSafe().isEmpty() ? colorMapSafe().entrySet().stream().filter(entry -> entry.getKey() <= modOffset(offset)).reduce((first, second) -> second).orElseThrow() : new AbstractMap.SimpleEntry<>(0.0, existsColor() ? colorCenter() : AccurateColor.TRANSPARENT);
		}

		public Map.Entry<Double, AccurateColor> nearestNext(double offset) {
			return !colorMapSafe().isEmpty() ? colorMapSafe().entrySet().stream().filter(entry -> entry.getKey() >= modOffset(offset)).findFirst().orElseThrow() : new AbstractMap.SimpleEntry<>(2 * Math.PI, existsColor() ? colorCenter() : AccurateColor.TRANSPARENT);
		}

		public AccurateColor colorAt(double offset) {
			if (!existsColor()) {
				return AccurateColor.TRANSPARENT;
			}

			if (colorMap().isEmpty()) {
				return colorCenter();
			}

			if (colorMapSafe().containsKey(offset)) {
				return colorMapSafe().get(offset);
			}

			offset = modOffset(offset);

			final Map.Entry<Double, AccurateColor> prev = nearestPrevious(offset), next = nearestNext(offset);
			if (prev.getValue().approximates(next.getValue())) {
				return prev.getValue();
			}

			double weight = (offset - prev.getKey()) / Math.abs(next.getKey() - prev.getKey());

			return prev.getValue().mix(next.getValue(), weight, mixMode());
		}

		private Vector vertexAt(double offset, double radiusFactor) {
			offset = modOffset(offset);
			return box().center().add(Vector.fromCartesian(
					radiusFactor * box().w() / 2 * Math.cos(offset),
					radiusFactor * box().h() / 2 * Math.sin(offset)
			));
		}

		public boolean existsColor() {
			return colorCenter().hasColor() || colorMap().values().stream().anyMatch(AccurateColor::hasColor);
		}

		public double eccentricity() {
			return Math.sqrt(Math.abs(Math.pow(box().w(), 2) - Math.pow(box().h(), 2))) / Math.max(box().w(), box().h());
		}

		private void renderVertex(BufferBuilder builder, Matrix4f matrix, Vector vertex, AccurateColor color, float z) {
			builder.vertex(matrix, (float) vertex.x(), (float) vertex.y(), z)
					.color(color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(), color.opacityAsFloat())
					.next();
		}

		private void renderVertex(BufferBuilder builder, Matrix4f matrix, double offset, double radiusFactor, AccurateColor color, float z) {
			renderVertex(builder, matrix, vertexAt(offset, radiusFactor), color, z);
		}

		// Interface Implementations

		private static final double delta = 0.01;

		@Override
		public boolean isRenderable() {
			return Renderable.isLegal(box()) && Theory.looseGreater(radians(), 0) && existsColor();
		}

		@Override
		public void render() {
			if (!isRenderable()) return;

			if (Theory.looseEquals(innerRadiusFactor(), 1)) return;

			RenderSystem.enableBlend();
			RenderSystem.setShader(GameRenderer::getPositionColorProgram);
			RenderSystem.disableCull(); // Prevents triangles from being culled

			BufferBuilder builder = Tessellator.getInstance().getBuffer();
			Matrix4f matrix = matrixStack().peek().getPositionMatrix();

			builder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

			for (double offset = offset(); offset < offset() + radians() + delta; offset += delta) {
				double clampedOffset = Math.min(offset, offset() + radians()); // Prevents offset from exceeding the end of the arc
				Vector vertex = vertexAt(clampedOffset, 1);

				// Render the inner (center) vertex
				renderVertex(builder, matrix, vertexAt(clampedOffset, innerRadiusFactor()),
						ovalMode().getColor(this, clampedOffset - offset(), innerRadiusFactor()), z());

				// Render the outer vertex
				renderVertex(builder, matrix, vertex,
						ovalMode().getColor(this, clampedOffset - offset(), 1), z());
			}

			BufferRenderer.drawWithGlobalProgram(builder.end());

			RenderSystem.disableBlend();
			RenderSystem.enableCull();
		}

		// 'Oval'
	}

	public class Text implements Renderable {
		// Constructors

		public Text(Section section, @Nullable AccurateColor color, @Nullable TextRenderer textRenderer, Section.Alignment verticalAlignment, Paragraph.Alignment horizontalAlignment, boolean shadowed, boolean culled) {
			this.section = section;
			this.color = color;
			this.textRenderer = textRenderer == null ? MinecraftClient.getInstance().textRenderer : textRenderer;
			this.verticalAlignment = verticalAlignment;
			this.horizontalAlignment = horizontalAlignment;
			this.shadowed = shadowed;
			this.culled = culled;
		}

		public Text(Section section, @Nullable AccurateColor color, Section.Alignment verticalAlignment, Paragraph.Alignment horizontalAlignment, boolean shadowed) {
			this(section, color, null, verticalAlignment, horizontalAlignment, shadowed, false);
		}

		public Text(UnaryOperator<Section> builder) {
			this(builder.apply(Section.DEFAULT), AccurateColor.WHITE, null, Section.Alignment.TOP, Paragraph.Alignment.LEFT, false, false);
		}

		// Fields

		private final Section section;
		@Nullable
		private final AccurateColor color;
		private final TextRenderer textRenderer;
		private final Section.Alignment verticalAlignment;
		private final Paragraph.Alignment horizontalAlignment;
		private final boolean shadowed, culled;

		// Accessors

		public Section section() {
			return section;
		}

		@Nullable
		public AccurateColor color() {
			return color;
		}

		public TextRenderer textRenderer() {
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

		public Text section(Section section) {
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

			section().render(box().alignTopLeft(Vector.ZERO), context(), textRenderer(), color(), verticalAlignment(), horizontalAlignment(), shadowed());

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

				VanillaWidgets.Tooltip.render(context(), snapped.expand(bleeding()));
				preserve(snapped).render();
			}

			// 'Tooltip'
		}

		// 'Text'
	}

	public class Item implements Renderable {
		// Constructors

		public Item(ItemStack itemStack, @Nullable Quaternion modifier, boolean leftHanded) {
			this.itemStack = itemStack;
			this.modifier = modifier == null ? new Quaternion() : modifier;
			this.leftHanded = leftHanded;
		}

		public Item(ItemStack itemStack, boolean leftHanded) {
			this(itemStack, null, leftHanded);
		}

		public Item(ItemStack itemStack) {
			this(itemStack, false);
		}

		// Fields

		private final ItemStack itemStack;
		private final Quaternion modifier;
		private final boolean leftHanded;

		// Accessors

		public ItemStack itemStack() {
			return itemStack;
		}

		public Quaternion modifier() {
			return modifier;
		}

		public boolean leftHanded() {
			return leftHanded;
		}

		// Mutators

		public Item parent(UnaryOperator<Flat> flat) {
			return flat.apply(Flat.this).new Item(itemStack(), modifier(), leftHanded());
		}

		public Item model(ItemStack itemStack) {
			return new Item(itemStack, modifier(), leftHanded());
		}

		public Item modifier(Quaternion modifier) {
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

				MinecraftClient.getInstance().getItemRenderer().renderItem(
						itemStack(), ModelTransformationMode.GUI,
						leftHanded(), matrixStack(), context().getVertexConsumers(),
						0xF000F0, OverlayTexture.DEFAULT_UV, bakedModel
				);

				context().draw();
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

		public Block(BlockState blockState, @Nullable AccurateColor color, @Nullable Quaternion modifier) {
			this.blockState = blockState;
			this.color = color;
			this.modifier = modifier == null ? new Quaternion() : modifier;
		}

		public Block(BlockState blockState, @Nullable AccurateColor color) {
			this(blockState, color, null);
		}

		public Block(BlockState blockState, BlockPos blockPos, @Nullable Quaternion modifier) {
			this.blockState = blockState;
			this.color = AccurateColor.fromARGB(RenderManager.getBlockColorAt(blockState, MinecraftClient.getInstance().world, blockPos));
			this.modifier = modifier == null ? new Quaternion() : modifier;
		}

		public Block(BlockState blockState, BlockPos blockPos) {
			this(blockState, blockPos, null);
		}

		public Block(BlockState blockState) {
			this(blockState, (AccurateColor) null);
		}

		// Fields

		private final BlockState blockState;
		// FIXME: 2023/7/12 Not very useful for rendering
		@Nullable
		private final AccurateColor color;
		private final Quaternion modifier;

		// Accessors

		public BlockState blockState() {
			return blockState;
		}

		@Nullable
		public AccurateColor color() {
			return color;
		}

		public Quaternion modifier() {
			return modifier;
		}

		// Mutators

		public Block parent(UnaryOperator<Flat> flat) {
			return flat.apply(Flat.this).new Block(blockState(), color(), modifier());
		}

		public Block model(BlockState blockState) {
			return new Block(blockState, color(), modifier());
		}

		public Block modifier(Quaternion modifier) {
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

				if (blockState == null || blockState.getRenderType() == BlockRenderType.INVISIBLE) break renderBlockModel;

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

				switch (blockState.getRenderType()) {
					case MODEL -> {
						@NotNull AccurateColor color = hasColor() ? Objects.requireNonNull(color()) : Palette.WHITE;

						new BlockModelRenderer(MinecraftClient.getInstance().getBlockColors()).render(
								matrixStack().peek(), context().getVertexConsumers().getBuffer(RenderLayers.getBlockLayer(blockState)), blockState,
								MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState),
								color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(),
								0xF000F0, OverlayTexture.DEFAULT_UV
						);
					}
					case ENTITYBLOCK_ANIMATED -> {
						// FIXME: 2023/7/12 The lightning is incorrect
						new BuiltinModelItemRenderer(MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader()).render(
								blockState.getBlock().asItem().getDefaultStack(), ModelTransformationMode.NONE,
								matrixStack(), context().getVertexConsumers(), 0xF000F0, OverlayTexture.DEFAULT_UV
						);
					}
				}

				context().draw();
				RenderSystem.enableCull();
				RenderSystem.disableDepthTest();

				matrixStack().pop();
				RenderSystem.applyModelViewMatrix();
			}
		}

		// 'Block'
	}
}
