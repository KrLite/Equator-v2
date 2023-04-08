package net.krlite.equator.math.geometry;

import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.BoxRenderer;
import net.krlite.equator.render.GradiantRenderer;
import net.krlite.equator.render.ModelRenderer;
import net.krlite.equator.render.SectionRenderer;
import net.krlite.equator.render.base.Scissor;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.text.Section;
import net.krlite.equator.visual.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.joml.Quaterniondc;

/**
 * A rectangle in 2D space(defined by the Screen Cartesian Coordinate) and is not rotated.
 * @see Vector
 * @param origin	The origin, which is the top left corner of the box.
 * @param size		The size of the box.
 */
public record Box(Vector origin, Vector size) {
	/**
	 * A box with a width and height of 0 and a position of (0, 0).
	 */
	public static final Box ZERO = new Box(Vector.ZERO), UNIT = new Box(Vector.UNIT_SQUARE);

	/**
	 * A box with a width and height of 1 and a position of (0, 0).
	 */
	public static final Box UNIT_CENTERED = UNIT.center(Vector.ZERO);

	public static Box fromCartesian(double x, double y, double width, double height) {
		return new Box(Vector.fromCartesian(x, y), Vector.fromCartesian(width, height));
	}

	public static Box fromCartesianCentered(double xCenter, double yCenter, double width, double height) {
		return fromCartesian(xCenter - width / 2, yCenter - height / 2, width, height);
	}

	public static Box fromVector(Vector topLeft, Vector bottomRight) {
		return new Box(topLeft, bottomRight.subtract(topLeft));
	}

	public static Box fromVectorCentered(Vector center, Vector size) {
		return fromCartesianCentered(center.x(), center.y(), size.x(), size.y());
	}

	public static Box fromScreen(Box box) {
		return FrameInfo.Convertor.screenToScaled(box);
	}

	public static Box fromOpenGL(Box box) {
		return FrameInfo.Convertor.openGLToScaled(box);
	}

	public Box(Vector origin, Vector size) {
		this.origin = origin.min(origin.add(size));
		this.size = origin.max(origin.add(size)).subtract(this.origin);
	}

	public Box(Vector size) {
		this(Vector.ZERO, size);
	}

	public Box(double xMin, double yMin, double xMax, double yMax) {
		this(Vector.fromCartesian(xMin, yMin), Vector.fromCartesian(xMax - xMin, yMax - yMin));
	}

	// origin() is a record method

	// size() is a record method

	public Vector topLeft() {
		return origin();
	}

	public Vector bottomLeft() {
		return Vector.fromCartesian(topLeft().x(), bottomRight().y());
	}

	public Vector bottomRight() {
		return origin().add(size());
	}

	public Vector topRight() {
		return Vector.fromCartesian(bottomRight().x(), topLeft().y());
	}

	public Vector topCenter() {
		return Vector.fromCartesian(center().x(), top());
	}

	public Vector bottomCenter() {
		return Vector.fromCartesian(center().x(), bottom());
	}

	public Vector leftCenter() {
		return Vector.fromCartesian(left(), center().y());
	}

	public Vector rightCenter() {
		return Vector.fromCartesian(right(), center().y());
	}

	public double top() {
		return topLeft().y();
	}

	public double bottom() {
		return bottomLeft().y();
	}

	public double left() {
		return topLeft().x();
	}

	public double right() {
		return topRight().x();
	}

	public Vector width() {
		return topRight().subtract(topLeft());
	}

	public Vector height() {
		return bottomLeft().subtract(topLeft());
	}

	public Vector center() {
		return topLeft().add(size().divide(2));
	}

	public Box origin(Vector origin) {
		return new Box(origin, size());
	}

	public Box size(Vector size) {
		return new Box(topLeft(), size);
	}

	public Box topLeft(Vector topLeft) {
		return Box.fromVector(topLeft, bottomRight());
	}

	public Box bottomLeft(Vector bottomLeft) {
		return Box.fromVector(bottomLeft, topRight());
	}

	public Box bottomRight(Vector bottomRight) {
		return Box.fromVector(topLeft(), bottomRight);
	}

	public Box topRight(Vector topRight) {
		return Box.fromVector(bottomLeft(), topRight);
	}

	public Box topCenter(Vector topCenter) {
		return Box.fromVector(topCenter.subtract(width().divide(2)), topCenter.add(width().divide(2)));
	}

	public Box bottomCenter(Vector bottomCenter) {
		return Box.fromVector(bottomCenter.subtract(width().divide(2)), bottomCenter.add(width().divide(2)));
	}

	public Box leftCenter(Vector leftCenter) {
		return Box.fromVector(leftCenter.subtract(height().divide(2)), leftCenter.add(height().divide(2)));
	}

	public Box rightCenter(Vector rightCenter) {
		return Box.fromVector(rightCenter.subtract(height().divide(2)), rightCenter.add(height().divide(2)));
	}

	public Box top(double y) {
		return topLeft(topLeft().y(y));
	}

	public Box bottom(double y) {
		return bottomLeft(bottomLeft().y(y));
	}

	public Box left(double x) {
		return topLeft(topLeft().x(x));
	}

	public Box right(double x) {
		return topRight(topRight().x(x));
	}

	public Box width(double width) {
		return new Box(topLeft(), width().magnitude(width).add(height()));
	}

	public Box height(double height) {
		return new Box(topLeft(), height().magnitude(height).add(width()));
	}

	public Box center(Vector center) {
		return new Box(center.subtract(size().divide(2)), size());
	}

	public Box translateTopLeft(double xTranslation, double yTranslation) {
		return topLeft(topLeft().add(width().multiply(xTranslation)).add(height().multiply(yTranslation)));
	}

	public Box translateBottomLeft(double xTranslation, double yTranslation) {
		return bottomLeft(bottomLeft().add(width().multiply(xTranslation)).add(height().multiply(yTranslation)));
	}

	public Box translateBottomRight(double xTranslation, double yTranslation) {
		return bottomRight(bottomRight().add(width().multiply(xTranslation)).add(height().multiply(yTranslation)));
	}

	public Box translateTopRight(double xTranslation, double yTranslation) {
		return topRight(topRight().add(width().multiply(xTranslation)).add(height().multiply(yTranslation)));
	}

	public Box translateTop(double yTranslation) {
		return top(top() + h() * yTranslation);
	}

	public Box translateBottom(double yTranslation) {
		return bottom(bottom() + h() * yTranslation);
	}

	public Box translateLeft(double xTranslation) {
		return left(left() + w() * xTranslation);
	}

	public Box translateRight(double xTranslation) {
		return right(right() + w() * xTranslation);
	}

	public Box translateWidth(double widthTranslation) {
		return width(w() + width().multiply(widthTranslation).magnitude());
	}

	public Box translateHeight(double heightTranslation) {
		return height(h() + height().multiply(heightTranslation).magnitude());
	}

	public Box translate(double xTranslation, double yTranslation) {
		return center(center().add(width().multiply(xTranslation)).add(height().multiply(yTranslation)));
	}

	public Box shiftTopLeft(Vector offset) {
		return topLeft(topLeft().add(offset));
	}

	public Box shiftBottomLeft(Vector offset) {
		return bottomLeft(bottomLeft().add(offset));
	}

	public Box shiftBottomRight(Vector offset) {
		return bottomRight(bottomRight().add(offset));
	}

	public Box shiftTopRight(Vector offset) {
		return topRight(topRight().add(offset));
	}

	public Box shiftTop(double y) {
		return shiftTopLeft(Vector.fromCartesian(topLeft().x(), y));
	}

	public Box shiftBottom(double y) {
		return shiftBottomLeft(Vector.fromCartesian(bottomLeft().x(), y));
	}

	public Box shiftLeft(double x) {
		return shiftTopLeft(Vector.fromCartesian(x, topLeft().y()));
	}

	public Box shiftRight(double x) {
		return shiftTopRight(Vector.fromCartesian(x, topRight().y()));
	}

	public Box shift(Vector offset) {
		return center(center().add(offset));
	}

	public Box shift(double xOffset, double yOffset) {
		return shift(Vector.fromCartesian(xOffset, yOffset));
	}

	public Box alignTopLeft(Vector topLeft) {
		return new Box(topLeft, size());
	}

	public Box alignBottomLeft(Vector bottomLeft) {
		return Box.fromVector(bottomLeft.subtract(height()), bottomLeft.add(width()));
	}

	public Box alignBottomRight(Vector bottomRight) {
		return Box.fromVector(bottomRight.subtract(size()), bottomRight);
	}

	public Box alignTopRight(Vector topRight) {
		return Box.fromVector(topRight.subtract(width()), topRight.add(height()));
	}

	public Box alignTop(double y) {
		return alignTopLeft(Vector.fromCartesian(topLeft().x(), y));
	}

	public Box alignBottom(double y) {
		return alignBottomLeft(Vector.fromCartesian(bottomLeft().x(), y));
	}

	public Box alignLeft(double x) {
		return alignTopLeft(Vector.fromCartesian(x, topLeft().y()));
	}

	public Box alignRight(double x) {
		return alignTopRight(Vector.fromCartesian(x, topRight().y()));
	}

	public boolean contains(Vector point) {
		return height().negate().cross(point.subtract(bottomLeft())) * height().cross(point.subtract(topRight())) >= 0
				&& width().cross(point.subtract(topLeft())) * width().negate().cross(point.subtract(bottomRight())) >= 0;
	}

	public boolean contains(double x, double y) {
		return contains(Vector.fromCartesian(x, y));
	}

	public boolean contains(Box box) {
		return contains(box.topLeft()) && contains(box.bottomRight());
	}

	public double area() {
		return Math.abs(width().cross(height()));
	}

	public double x() {
		return topLeft().x();
	}

	public double y() {
		return topLeft().y();
	}

	public double w() {
		return width().magnitude();
	}

	public double h() {
		return height().magnitude();
	}

	public double xCenter() {
		return center().x();
	}

	public double yCenter() {
		return center().y();
	}

	public Box squareOuter() {
		double max = width().magnitudeMax(height());
		return width(max).height(max).center(center());
	}

	public Box squareInner() {
		double min = width().magnitudeMin(height());
		return width(min).height(min).center(center());
	}

	public Box rotateByRightAngle(int rotationCount) {
		if (rotationCount % 2 == 0) {
			return this;
		}
		return size(size().theta(Math.PI * 2 - size().theta()));
	}

	public Box rotateByRightAngleCentered(int rotationCount) {
		return rotateByRightAngle(rotationCount).center(center());
	}

	public Box scale(double xScalar, double yScalar) {
		return width(w() * xScalar).height(h() * yScalar);
	}

	public Box scale(double scalar) {
		return scale(scalar, scalar);
	}

	public Box scaleCenter(double xScalar, double yScalar) {
		return scale(xScalar, yScalar).center(center());
	}

	public Box scaleCenter(double scalar) {
		return scaleCenter(scalar, scalar);
	}

	public Box expand(Vector expansion) {
		return Box.fromVector(topLeft().subtract(expansion), bottomRight().add(expansion));
	}

	public Box expand(double xExpansion, double yExpansion) {
		return expand(Vector.fromCartesian(xExpansion, yExpansion));
	}

	public Box expand(double expansion) {
		return expand(expansion, expansion);
	}

	public Box expandCentered(Vector expansion) {
		return expand(expansion).center(center());
	}

	public Box expandCentered(double xExpansion, double yExpansion) {
		return expandCentered(Vector.fromCartesian(xExpansion, yExpansion));
	}

	public Box expandCentered(double expansion) {
		return expandCentered(expansion, expansion);
	}

	public Box normalizeBy(Box another) {
		return scaleCenter(1 / another.w(), 1 / another.height().magnitude())
					   .center(center().subtract(another.center()).scaleX(1 / another.width().magnitude()).scaleY(1 / another.height().magnitude()));
	}

	public Box interpolate(Vector vector, double lambda) {
		return new Box(topLeft().interpolate(vector, lambda), size().interpolate(vector, lambda));
	}

	public Box interpolate(Box another, double lambda) {
		return new Box(topLeft().interpolate(another.topLeft(), lambda), size().interpolate(another.size(), lambda));
	}

	public Box fitToScreen() {
		return FrameInfo.Convertor.scaledToScreen(this);
	}

	public Box fitToOpenGL() {
		return FrameInfo.Convertor.scaledToOpenGL(this);
	}

	/**
	 * Ready a {@link BoxRenderer} for this box. Will be able to render with a
	 * {@link Texture}, an {@link AccurateColor} <b>AND</b> a {@link Quaterniondc}.
	 * @return	A {@link BoxRenderer} for this box.
	 */
	public BoxRenderer ready() {
		return new BoxRenderer(this);
	}

	/**
	 * Ready a {@link GradiantRenderer} for this box. Will be able to render with
	 * up to 4 {@link AccurateColor}s.
	 * @return	A {@link GradiantRenderer} for this box.
	 */
	public GradiantRenderer readyGradiant() {
		return new GradiantRenderer(this);
	}

	/**
	 * Ready a {@link ModelRenderer} for this box. Will be able to render with a
	 * model from an {@link ItemStack}, an {@link Item}, a {@link BlockState}
	 * <b>OR</b> a {@link Block}.
	 * @return	A {@link ModelRenderer} for this box.
	 */
	public ModelRenderer readyModel() {
		return new ModelRenderer(this);
	}

	/**
	 * Alias for {@link #ready()}, with a {@link Texture} set.
	 * @param texture	The {@link Texture} to use.
	 * @return	A {@link BoxRenderer} for this box.
	 * @see #ready()
	 */
	public BoxRenderer ready(Texture texture) {
		return new BoxRenderer(this).texture(texture);
	}

	/**
	 * Alias for {@link #readyGradiant()}, with an {@link AccurateColor} filled.
	 * @param color	The {@link AccurateColor} to use.
	 * @return	A {@link GradiantRenderer} for this box.
	 * @see #readyGradiant()
	 */
	public GradiantRenderer ready(AccurateColor color) {
		return new GradiantRenderer(this).fill(color);
	}

	/**
	 * Alias for {@link #readyModel()}, with an {@link ItemStack} set.
	 * @param itemStack	The {@link ItemStack} to use.
	 * @return	A {@link ModelRenderer} for this box.
	 * @see #readyModel()
	 */
	public ModelRenderer ready(ItemStack itemStack) {
		return new ModelRenderer(this).model(itemStack);
	}

	/**
	 * Alias for {@link #readyModel()}, with an {@link Item} set.
	 * @param item	The {@link Item} to use.
	 * @return	A {@link ModelRenderer} for this box.
	 * @see #readyModel()
	 */
	public ModelRenderer ready(Item item) {
		return new ModelRenderer(this).model(item);
	}

	/**
	 * Alias for {@link #readyModel()}, with a {@link BlockState} set.
	 * @param blockState	The {@link BlockState} to use.
	 * @return	A {@link ModelRenderer} for this box.
	 * @see #readyModel()
	 */
	public ModelRenderer ready(BlockState blockState) {
		return new ModelRenderer(this).model(blockState);
	}

	/**
	 * Alias for {@link #readyModel()}, with a {@link Block} set.
	 * @param block	The {@link Block} to use.
	 * @return	A {@link ModelRenderer} for this box.
	 * @see #readyModel()
	 */
	public ModelRenderer ready(Block block) {
		return new ModelRenderer(this).model(block);
	}

	public SectionRenderer ready(Section section) {
		return new SectionRenderer(this, section);
	}

	/**
	 * Transform this {@link Box} into a {@link Scissor}.
	 * @return	A {@link Scissor} with the same dimensions as this {@link Box}.
	 */
	public Scissor scissor() {
		return new Scissor(this);
	}

	public String toStringAsCartesian() {
		return String.format("[%s, %s]", topLeft().toStringAsCartesian(), bottomRight().toStringAsCartesian());
	}

	@Override
	public String toString() {
		return String.format("[topLeft=%s, topRight=%s]", topLeft().toString(), bottomRight().toString());
	}
}
