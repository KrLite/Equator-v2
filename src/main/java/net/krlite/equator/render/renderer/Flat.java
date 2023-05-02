package net.krlite.equator.render.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.base.Exceptions;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.math.logic.flat.FlatGate;
import net.krlite.equator.math.logic.flat.FlatTransform;
import net.krlite.equator.render.base.Renderable;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.renderer.base.Basic;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Colorspace;
import net.krlite.equator.visual.texture.Texture;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
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
				@Nullable AccurateColor colorTopLeft, @Nullable AccurateColor colorBottomLeft, @Nullable AccurateColor colorBottomRight, @Nullable AccurateColor colorTopRight,
				Colorspace colorspace, RectangleMode rectangleMode
		) {
			this.texture = texture;
			this.colorTopLeft = colorTopLeft == null ? AccurateColor.TRANSPARENT : colorTopLeft.colorspace(colorspace);
			this.colorBottomLeft = colorBottomLeft == null ? AccurateColor.TRANSPARENT : colorBottomLeft.colorspace(colorspace);
			this.colorBottomRight = colorBottomRight == null ? AccurateColor.TRANSPARENT : colorBottomRight.colorspace(colorspace);
			this.colorTopRight = colorTopRight == null ? AccurateColor.TRANSPARENT : colorTopRight.colorspace(colorspace);
			this.rectangleMode = rectangleMode;
		}

		public Rectangle(
				@Nullable Texture texture,
				@Nullable AccurateColor colorTopLeft, @Nullable AccurateColor colorBottomLeft, @Nullable AccurateColor colorBottomRight, @Nullable AccurateColor colorTopRight,
				RectangleMode rectangleMode
		) {
			this(texture, colorTopLeft, colorBottomLeft, colorBottomRight, colorTopRight, Colorspace.RGB, rectangleMode);
		}

		public Rectangle(@Nullable Texture texture, @Nullable AccurateColor color, RectangleMode rectangleMode) {
			this(texture, color, color, color, color, rectangleMode);
		}

		// Fields

		public enum RectangleMode {
			NORMAL, TILING, FIXED_CORNERS
		}

		@Nullable
		private final Texture texture;
		private final AccurateColor colorTopLeft, colorBottomLeft, colorBottomRight, colorTopRight;
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
			return colorTopLeft().colorspace();
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
			return new Rectangle(texture(), colorTopLeft, colorBottomLeft(), colorBottomRight(), colorTopRight(), rectangleMode());
		}

		public Rectangle colorBottomLeft(@Nullable AccurateColor colorBottomLeft) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft, colorBottomRight(), colorTopRight(), rectangleMode());
		}

		public Rectangle colorBottomRight(@Nullable AccurateColor colorBottomRight) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight, colorTopRight(), rectangleMode());
		}

		public Rectangle colorTopRight(@Nullable AccurateColor colorTopRight) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight, rectangleMode());
		}

		public Rectangle colors(@Nullable AccurateColor colorTopLeft, @Nullable AccurateColor colorBottomLeft, @Nullable AccurateColor colorBottomRight, @Nullable AccurateColor colorTopRight) {
			return new Rectangle(texture(), colorTopLeft, colorBottomLeft, colorBottomRight, colorTopRight, rectangleMode());
		}

		public Rectangle colors(@Nullable AccurateColor color) {
			return new Rectangle(texture(), color, color, color, color, rectangleMode());
		}

		public Rectangle rectangleMode(RectangleMode rectangleMode) {
			return new Rectangle(texture(), colorTopLeft(), colorBottomLeft(), colorBottomRight(), colorTopRight(), rectangleMode);
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
			return colorTopLeft().hasColor() || colorBottomLeft().hasColor() || colorBottomRight().hasColor() || colorTopRight().hasColor();
		}

		public AccurateColor colorAt(double xOffset, double yOffset) {
			return new AccurateColor(
					colorTopLeft().red() 				* (1 - xOffset) * (1 - yOffset)
							+ colorBottomLeft().red() 		* (1 - xOffset) * yOffset
							+ colorBottomRight().red() 		* xOffset 		* yOffset
							+ colorTopRight().red() 		* xOffset 		* (1 - yOffset),
					colorTopLeft().green() 			* (1 - xOffset) * (1 - yOffset)
							+ colorBottomLeft().green() 	* (1 - xOffset) * yOffset
							+ colorBottomRight().green() 	* xOffset 		* yOffset
							+ colorTopRight().green() 		* xOffset 		* (1 - yOffset),
					colorTopLeft().blue() 				* (1 - xOffset) * (1 - yOffset)
							+ colorBottomLeft().blue() 		* (1 - xOffset) * yOffset
							+ colorBottomRight().blue() 	* xOffset 		* yOffset
							+ colorTopRight().blue() 		* xOffset 		* (1 - yOffset),
					colorTopLeft().opacity() 		* (1 - xOffset) * (1 - yOffset)
							+ colorBottomLeft().opacity() 	* (1 - xOffset) * yOffset
							+ colorBottomRight().opacity() 	* xOffset 		* yOffset
							+ colorTopRight().opacity() 	* xOffset 		* (1 - yOffset)
			);
		}

		private AccurateColor assertColor(int index) {
			AccurateColor[] colors = colors();
			if (index < 0 || index >= colors.length /* normally it would be 4 */ ) {
				throw new RuntimeException(Exceptions.Visual.colorIndexOutOfBounds(index, colors.length));
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

		private Rectangle assertColors() {
			return new Rectangle(texture(), assertColor(0), assertColor(1), assertColor(2), assertColor(3), colorspace(), rectangleMode());
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

			builder.begin(VertexFormat.DrawMode.QUADS, state().vertexFormat());

			renderVertex(builder, matrix, box().topLeft(), hasTexture() ? Objects.requireNonNull(texture()).uvTopLeft() : Vector.ZERO, colorAtCenter(), z());
			renderVertex(builder, matrix, box().bottomLeft(), hasTexture() ? Objects.requireNonNull(texture()).uvBottomLeft() : Vector.UNIT_Y, colorAtCenter(), z());
			renderVertex(builder, matrix, box().bottomRight(), hasTexture() ? Objects.requireNonNull(texture()).uvBottomRight() : Vector.UNIT_SQUARE, colorAtCenter(), z());
			renderVertex(builder, matrix, box().topRight(), hasTexture() ? Objects.requireNonNull(texture()).uvTopRight() : Vector.UNIT_X, colorAtCenter(), z());

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

		// Interface Implementations

		@Override
		public void render() {
			if (!isRenderable()) return;

			switch (rectangleMode()) {
				case NORMAL -> assertColors().renderNormal();
				case TILING -> assertColors().renderTiling();
				case FIXED_CORNERS -> assertColors().renderFixedCorners();
			}
		}

		@Override
		public boolean isRenderable() {
			return Renderable.isBoxLegal(box()) && (hasTexture() || hasColor());
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

		public class Transformed implements Renderable {
			// Constructors

			public Transformed(FlatTransform transform, FlatGate gate) {
				this.transform = transform;
				this.gate = gate;
			}

			public Transformed(FlatTransform transform) {
				this(transform, FlatGate.TRUE);
			}

			public Transformed(FlatGate gate) {
				this(FlatTransform.NONE, gate);
			}

			// Fields

			private final FlatTransform transform;
			private final FlatGate gate;

			// Accessors

			public FlatTransform transform() {
				return transform;
			}

			public FlatGate gate() {
				return gate;
			}

			// Mutators

			public Transformed parent(UnaryOperator<Rectangle> rectangle) {
				return rectangle.apply(Rectangle.this).new Transformed(transform(), gate());
			}

			public Transformed transform(FlatTransform transform) {
				return new Transformed(transform, gate());
			}

			public Transformed andThen(FlatTransform transform) {
				return new Transformed(transform().andThen(transform), gate());
			}

			public Transformed gate(FlatGate gate) {
				return new Transformed(transform(), gate);
			}

			public Transformed not() {
				return new Transformed(transform(), gate().not());
			}

			public Transformed and(FlatGate gate) {
				return new Transformed(transform(), gate().and(gate));
			}

			public Transformed or(FlatGate gate) {
				return new Transformed(transform(), gate().or(gate));
			}

			public Transformed nand(FlatGate gate) {
				return new Transformed(transform(), gate().nand(gate));
			}

			public Transformed xor(FlatGate gate) {
				return new Transformed(transform(), gate().xor(gate));
			}

			public Transformed nor(FlatGate gate) {
				return new Transformed(transform(), gate().nor(gate));
			}

			public Transformed xnor(FlatGate gate) {
				return new Transformed(transform(), gate().xnor(gate));
			}

			// Interface Implementations

			@Override
			public boolean isRenderable() {
				return Rectangle.this.isRenderable();
			}

			@Override
			public void render() {
				if (!isRenderable()) return;

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

				for (double xr = 0; xr - 1 <= box().w(); xr++) {
					for (double yr = 0; yr - 1 <= box().h(); yr++) {
						double x = xr + box().x(), y = yr + box().y();
						if (!gate().pass(x, y)) continue;

						double u = xr / box().w(), v = yr / box().h();
						AccurateColor color = colorAt(xr / box().w(), yr / box().h());

						renderVertex(builder, matrix, Vector.fromCartesian(x, y), Vector.fromCartesian(u, v), color, z());
					}
				}

				BufferRenderer.drawWithGlobalProgram(builder.end());

				if (hasColor() && !blend) {
					RenderSystem.disableBlend();
				}
			}

			public class Outlined implements Renderable {
				// Constructors

				public Outlined(Vector expansion, OutliningMode outliningMode, OutliningStyle outliningStyle) {
					this.expansion = expansion;
					this.outliningMode = outliningMode;
					this.outliningStyle = outliningStyle;
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

				public Outlined parent(UnaryOperator<Transformed> transformed) {
					return transformed.apply(Transformed.this).new Outlined(expansion(), outliningMode(), outliningStyle());
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
					return outliningMode() == OutliningMode.NORMAL ? Rectangle.this.isRenderable() : (Renderable.isBoxLegal(box()) && hasColor());
				}

				@Override
				public void render() {
					if (!isRenderable()) return;

					Box corner 			= Box.fromVectorCentered(box().center(), expansion);
					Box gapHorizontal 	= Box.fromVectorCentered(box().center(), Vector.fromCartesian(box().w(), expansion.y()));
					Box gapVertical 	= Box.fromVectorCentered(box().center(), Vector.fromCartesian(expansion.x(), box().h()));

					double width = box().w() + expansion.x() * 2, height = box().h() + expansion.y() * 2;
					double xCornerScalar = expansion.x() / width, yCornerScalar = expansion.y() / height;

					AccurateColor topLeft 		= colorTopLeft(), 		topLeftBottom 		= AccurateColor.TRANSPARENT, 	topLeftTop 		= AccurateColor.TRANSPARENT, 	topLeftDiagonal 		= AccurateColor.TRANSPARENT;
					AccurateColor bottomLeft 	= colorBottomLeft(), 	bottomLeftBottom 	= AccurateColor.TRANSPARENT, 	bottomLeftTop 	= AccurateColor.TRANSPARENT, 	bottomLeftDiagonal 		= AccurateColor.TRANSPARENT;
					AccurateColor bottomRight 	= colorBottomRight(), 	bottomRightBottom 	= AccurateColor.TRANSPARENT, 	bottomRightTop 	= AccurateColor.TRANSPARENT, 	bottomRightDiagonal 	= AccurateColor.TRANSPARENT;
					AccurateColor topRight 		= colorTopRight(), 		topRightBottom 		= AccurateColor.TRANSPARENT, 	topRightTop 	= AccurateColor.TRANSPARENT, 	topRightDiagonal 		= AccurateColor.TRANSPARENT;

					switch (outliningStyle) {
						case CLAMPED -> {
							topLeftTop 				= colorAt(xCornerScalar, 0);
							topLeftBottom 			= colorAt(0, yCornerScalar);
							topLeftDiagonal 		= colorAt(xCornerScalar, yCornerScalar);

							bottomLeftTop 			= colorAt(0, 1 - yCornerScalar);
							bottomLeftBottom 		= colorAt(xCornerScalar, 1);
							bottomLeftDiagonal 		= colorAt(xCornerScalar, 1 - yCornerScalar);

							bottomRightTop 			= colorAt(1, 1 - yCornerScalar);
							bottomRightBottom 		= colorAt(1 - xCornerScalar, 1);
							bottomRightDiagonal 	= colorAt(1 - xCornerScalar, 1 - yCornerScalar);

							topRightTop 			= colorAt(1 - xCornerScalar, 0);
							topRightBottom 			= colorAt(1, yCornerScalar);
							topRightDiagonal 		= colorAt(1 - xCornerScalar, yCornerScalar);
						}
						case EDGE -> {
							topLeftTop 				= colorAt(xCornerScalar, 0);
							topLeftBottom 			= colorAt(0, yCornerScalar);
							topLeftDiagonal 		= colorAtCenter();

							bottomLeftTop 			= colorAt(0, 1 - yCornerScalar);
							bottomLeftBottom 		= colorAt(xCornerScalar, 1);
							bottomLeftDiagonal 		= colorAtCenter();

							bottomRightTop 			= colorAt(1, 1 - yCornerScalar);
							bottomRightBottom 		= colorAt(1 - xCornerScalar, 1);
							bottomRightDiagonal 	= colorAtCenter();

							topRightTop 			= colorAt(1 - xCornerScalar, 0);
							topRightBottom 			= colorAt(1, yCornerScalar);
							topRightDiagonal 		= colorAtCenter();
						}
						case EDGE_FADED -> {
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

			// 'Transformative'
		}

		// 'Rectangle'
	}
}
