package net.krlite.equator.math.geometry;

import com.google.common.collect.ImmutableMap;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.render.frame.Convertible;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.base.Scissor;
import net.krlite.equator.render.renderer.*;
import net.krlite.equator.render.vanilla.ButtonRenderImplementation;
import net.krlite.equator.render.vanilla.TooltipRenderImplementation;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.text.Section;
import net.krlite.equator.visual.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.UnaryOperator;

/**
 * <h1>Box</h1>
 * Represents a rectangle in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}
 * and is not rotatable.
 * @see Vector
 * @param origin	The top left corner of the box.
 * @param size		The size of the box.
 */
public record Box(Vector origin, Vector size) implements Convertible.Scaled<Box> {
	// Constants

	/**
	 * A box positioned at {@code (0, 0)} with an area of {@code 0}.
	 */
	public static final Box ZERO = new Box(Vector.ZERO), UNIT = new Box(Vector.UNIT_SQUARE);

	/**
	 * A box centered at {@code (0, 0)} with an area of {@code 1}.
	 */
	public static final Box UNIT_CENTERED = UNIT.center(Vector.ZERO);

	// Static Constructors

	/**
	 * Creates a box from the given cartesian coordinate in the
	 * {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
	 * @param x			The x-coordinate of the top left corner of the box.
	 * @param y			The y-coordinate of the top left corner of the box.
	 * @param width		The width of the box.
	 * @param height	The height of the box.
	 * @return	A box with the given cartesian coordinate.
	 */
	public static Box fromCartesian(double x, double y, double width, double height) {
		return new Box(Vector.fromCartesian(x, y), Vector.fromCartesian(width, height));
	}

	/**
	 * Creates a box positioned at {@code (0, 0)} from the given cartesian coordinate in the
	 * {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
	 * @param width		The width of the box.
	 * @param height	The height of the box.
	 * @return	A box positioned at {@code (0, 0)} with the given cartesian coordinate.
	 */
	public static Box fromCartesian(double width, double height) {
		return fromCartesian(0, 0, width, height);
	}

	/**
	 * Creates a box from the given cartesian coordinate in the
	 * {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
	 * @param xCenter		The x-coordinate of the center of the box.
	 * @param yCenter		The y-coordinate of the center of the box.
	 * @param width			The width of the box.
	 * @param height		The height of the box.
	 * @return	A box with the given cartesian coordinate.
	 */
	public static Box fromCartesianCentered(double xCenter, double yCenter, double width, double height) {
		return fromCartesian(xCenter - width / 2, yCenter - height / 2, width, height);
	}

	/**
	 * Creates a box centered at {@code (0, 0)} from the given cartesian coordinate in the
	 * {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
	 * @param width		The width of the box.
	 * @param height	The height of the box.
	 * @return	A box centered at {@code (0, 0)} with the given cartesian coordinate.
	 */
	public static Box fromCartesianCentered(double width, double height) {
		return fromCartesianCentered(0, 0, width, height);
	}

	/**
	 * Creates a box from the given corner vectors.
	 * @param topLeft		The origin of the box. This is, the top left corner of the box.
	 * @param bottomRight	The bottom right corner of the box.
	 * @return	A box with the given vectors.
	 */
	public static Box fromVector(Vector topLeft, Vector bottomRight) {
		return new Box(topLeft, bottomRight.subtract(topLeft));
	}

	/**
	 * Creates a centered box from the given center and size.
	 * @param center	The center of the box.
	 * @param size		The size of the box.
	 * @return	A centered box with the given vectors.
	 */
	public static Box fromVectorCentered(Vector center, Vector size) {
		return fromCartesianCentered(center.x(), center.y(), size.x(), size.y());
	}

	// Constructors

	/**
	 * Creates a box from the given origin and size.
	 * @param origin	The top left corner of the box.
	 * @param size		The size of the box.
	 */
	public Box(Vector origin, Vector size) {
		this.origin = origin.min(origin.add(size));
		this.size = origin.max(origin.add(size)).subtract(this.origin);
	}

	/**
	 * Creates a box positioned at {@code (0, 0)} from the given size.
	 * @param size		The size of the box.
	 */
	public Box(Vector size) {
		this(Vector.ZERO, size);
	}

	/**
	 * Creates a box from the given cartesian coordinated values in the
	 * {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
	 * @param xMin	The x-coordinate of the top left corner of the box.
	 * @param yMin	The y-coordinate of the top left corner of the box.
	 * @param xMax	The x-coordinate of the bottom right corner of the box.
	 * @param yMax	The y-coordinate of the bottom right corner of the box.
	 */
	public Box(double xMin, double yMin, double xMax, double yMax) {
		this(Vector.fromCartesian(xMin, yMin), Vector.fromCartesian(xMax - xMin, yMax - yMin));
	}

	// Instance Variable Accessors

	/**
	 * Gets the origin of the box. That is, the top left corner of the box.
	 * @return	The origin of the box.
	 */
	@Override
	public Vector origin() {
		return origin;
	}

	/**
	 * Gets the size of the box.
	 * @return	The size of the box.
	 */
	@Override
	public Vector size() {
		return size;
	}



	/**
	 * Gets the top left corner of the box.
	 * @return	The top left corner of the box.
	 * @see #origin()
	 */
	public Vector topLeft() {
		return origin();
	}

	/**
	 * Gets the bottom left corner of the box.
	 * @return	The bottom left corner of the box.
	 */
	public Vector bottomLeft() {
		return Vector.fromCartesian(topLeft().x(), bottomRight().y());
	}

	/**
	 * Gets the bottom right corner of the box.
	 * @return	The bottom right corner of the box.
	 */
	public Vector bottomRight() {
		return origin().add(size());
	}

	/**
	 * Gets the top right corner of the box.
	 * @return	The top right corner of the box.
	 */
	public Vector topRight() {
		return Vector.fromCartesian(bottomRight().x(), topLeft().y());
	}

	/**
	 * Gets the center of the box.
	 * @return	The center of the box.
	 */
	public Vector center() {
		return topLeft().add(size().divide(2));
	}



	/**
	 * Gets the top center of the box. That is, the center of the top edge of the box.
	 * @return	The top center of the box.
	 */
	public Vector topCenter() {
		return Vector.fromCartesian(center().x(), top());
	}

	/**
	 * Gets the bottom center of the box. That is, the center of the bottom edge of the box.
	 * @return	The bottom center of the box.
	 */
	public Vector bottomCenter() {
		return Vector.fromCartesian(center().x(), bottom());
	}

	/**
	 * Gets the left center of the box. That is, the center of the left edge of the box.
	 * @return	The left center of the box.
	 */
	public Vector leftCenter() {
		return Vector.fromCartesian(left(), center().y());
	}

	/**
	 * Gets the right center of the box. That is, the center of the right edge of the box.
	 * @return	The right center of the box.
	 */
	public Vector rightCenter() {
		return Vector.fromCartesian(right(), center().y());
	}


	/**
	 * Gets the top edge of the box.
	 * @return	The y-coordinate of the top edge of the box.
	 */
	public double top() {
		return topLeft().y();
	}

	/**
	 * Gets the bottom edge of the box.
	 * @return	The y-coordinate of the bottom edge of the box.
	 */
	public double bottom() {
		return bottomLeft().y();
	}

	/**
	 * Gets the left edge of the box.
	 * @return	The x-coordinate of the left edge of the box.
	 */
	public double left() {
		return topLeft().x();
	}

	/**
	 * Gets the right edge of the box.
	 * @return	The x-coordinate of the right edge of the box.
	 */
	public double right() {
		return topRight().x();
	}



	/**
	 * Gets the width of the box.
	 * @return	The width of the box.
	 */
	public Vector width() {
		return topRight().subtract(topLeft());
	}

	/**
	 * Gets the height of the box.
	 * @return	The height of the box.
	 */
	public Vector height() {
		return bottomLeft().subtract(topLeft());
	}

	// Instance Variable Mutators

	/**
	 * Mutates the origin of the box.
	 * @param origin	The origin of the box. That is, the top left corner of the box.
	 * @return	A new box with the given origin.
	 */
	public Box origin(Vector origin) {
		return new Box(origin, size());
	}

	/**
	 * Mutates the size of the box.
	 * @param size	The size of the box.
	 * @return	A new box with the given size.
	 */
	public Box size(Vector size) {
		return new Box(topLeft(), size);
	}



	/**
	 * Mutates the top left corner of the box.
	 * @param topLeft	The top left corner of the box.
	 * @return	A new box with the given top left corner.
	 * @see #origin(Vector)
	 */
	public Box topLeft(Vector topLeft) {
		return Box.fromVector(topLeft, bottomRight());
	}

	/**
	 * Mutates the bottom left corner of the box.
	 * @param bottomLeft	The bottom left corner of the box.
	 * @return	A new box with the given bottom left corner.
	 */
	public Box bottomLeft(Vector bottomLeft) {
		return Box.fromVector(bottomLeft, topRight());
	}

	/**
	 * Mutates the bottom right corner of the box.
	 * @param bottomRight	The bottom right corner of the box.
	 * @return	A new box with the given bottom right corner.
	 */
	public Box bottomRight(Vector bottomRight) {
		return Box.fromVector(topLeft(), bottomRight);
	}

	/**
	 * Mutates the top right corner of the box.
	 * @param topRight	The top right corner of the box.
	 * @return	A new box with the given top right corner.
	 */
	public Box topRight(Vector topRight) {
		return Box.fromVector(bottomLeft(), topRight);
	}

	/**
	 * Mutates the center of the box.
	 * @param center	The center of the box.
	 * @return	A new box with the given center.
	 */
	public Box center(Vector center) {
		return new Box(center.subtract(size().divide(2)), size());
	}

	/**
	 * Mutates the center of the box from the center of another box.
	 * @param another	The box whose center will be used.
	 * @return	A new box with the given center.
	 */
	public Box center(Box another) {
		return center(another.center());
	}



	/**
	 * Mutates the top center of the box.
	 * @param topCenter	The top center of the box. That is, the center of the top edge of the box.
	 * @return	A new box with the given top center.
	 */
	public Box topCenter(Vector topCenter) {
		return Box.fromVector(topCenter.subtract(width().divide(2)), topCenter.add(width().divide(2)));
	}

	/**
	 * Mutates the bottom center of the box.
	 * @param bottomCenter	The bottom center of the box. That is, the center of the bottom edge of the box.
	 * @return	A new box with the given bottom center.
	 */
	public Box bottomCenter(Vector bottomCenter) {
		return Box.fromVector(bottomCenter.subtract(width().divide(2)), bottomCenter.add(width().divide(2)));
	}

	/**
	 * Mutates the left center of the box.
	 * @param leftCenter	The left center of the box. That is, the center of the left edge of the box.
	 * @return	A new box with the given left center.
	 */
	public Box leftCenter(Vector leftCenter) {
		return Box.fromVector(leftCenter.subtract(height().divide(2)), leftCenter.add(height().divide(2)));
	}

	/**
	 * Mutates the right center of the box.
	 * @param rightCenter	The right center of the box. That is, the center of the right edge of the box.
	 * @return	A new box with the given right center.
	 */
	public Box rightCenter(Vector rightCenter) {
		return Box.fromVector(rightCenter.subtract(height().divide(2)), rightCenter.add(height().divide(2)));
	}



	/**
	 * Mutates the top edge of the box.
	 * @param y	The y-coordinate of the top edge of the box.
	 * @return	A new box with the given top edge.
	 */
	public Box top(double y) {
		return topLeft(topLeft().y(y));
	}

	/**
	 * Mutates the bottom edge of the box.
	 * @param y	The y-coordinate of the bottom edge of the box.
	 * @return	A new box with the given bottom edge.
	 */
	public Box bottom(double y) {
		return bottomLeft(bottomLeft().y(y));
	}

	/**
	 * Mutates the left edge of the box.
	 * @param x	The x-coordinate of the left edge of the box.
	 * @return	A new box with the given left edge.
	 */
	public Box left(double x) {
		return topLeft(topLeft().x(x));
	}

	/**
	 * Mutates the right edge of the box.
	 * @param x	The x-coordinate of the right edge of the box.
	 * @return	A new box with the given right edge.
	 */
	public Box right(double x) {
		return topRight(topRight().x(x));
	}



	/**
	 * Mutates the width of the box.
	 * @param width	The width of the box.
	 * @return	A new box with the given width.
	 */
	public Box width(double width) {
		return new Box(topLeft(), width().magnitude(width).add(height()));
	}

	/**
	 * Mutates the height of the box.
	 * @param height	The height of the box.
	 * @return	A new box with the given height.
	 */
	public Box height(double height) {
		return new Box(topLeft(), height().magnitude(height).add(width()));
	}

	// Instance Properties

	/**
	 * Gets the area of the box, always positive. Note well that this is calculated as the absolute value of the cross
	 * product of the width and height vectors.
	 * @return	The area of the box.
	 */
	public double area() {
		return Math.abs(width().cross(height()));
	}

	/**
	 * Gets the perimeter of the box, always positive.
	 * @return	The perimeter of the box.
	 */
	public double perimeter() {
		return w() * 2 + h() * 2;
	}

	/**
	 * Gets the x-coordinate of the origin of the box. That is, the x-coordinate of the top left corner of the box.
	 * @return	The x-coordinate of the origin of the box.
	 */
	public double x() {
		return topLeft().x();
	}

	/**
	 * Gets the y-coordinate of the origin of the box. That is, the y-coordinate of the top left corner of the box.
	 * @return	The y-coordinate of the origin of the box.
	 */
	public double y() {
		return topLeft().y();
	}

	/**
	 * Gets the x-coordinate of the center of the box.
	 * @return	The x-coordinate of the center of the box.
	 */
	public double xCenter() {
		return center().x();
	}

	/**
	 * Gets the y-coordinate of the center of the box.
	 * @return	The y-coordinate of the center of the box.
	 */
	public double yCenter() {
		return center().y();
	}

	/**
	 * Gets the width of the box. Alias for {@link #width()} then {@link Vector#magnitude()}.
	 * @return	The width of the box.
	 */
	public double w() {
		return width().magnitude();
	}

	/**
	 * Gets the height of the box. Alias for {@link #height()} then {@link Vector#magnitude()}.
	 * @return	The height of the box.
	 */
	public double h() {
		return height().magnitude();
	}

	/**
	 * Gets the diagonal length of the box. Alias for {@link #size()} then {@link Vector#magnitude()}.
	 * @return	The diagonal length of the box.
	 */
	public double diag() {
		return size().magnitude();
	}

	// Cross-Instant Properties

	/**
	 * Checks if the coordinate of the given vector is contained within the box.
	 * @param point	The point to check.
	 * @return	{@code true} if the point is contained within the box, {@code false} otherwise.
	 */
	public boolean contains(Vector point) {
		return height().negate().cross(point.subtract(bottomLeft())) * height().cross(point.subtract(topRight())) >= 0
					   && width().cross(point.subtract(topLeft())) * width().negate().cross(point.subtract(bottomRight())) >= 0;
	}

	/**
	 * Checks if the given coordinate is contained within the box.
	 * @param x	The x-coordinate of the point to check.
	 * @param y	The y-coordinate of the point to check.
	 * @return	{@code true} if the point is contained within the box, {@code false} otherwise.
	 */
	public boolean contains(double x, double y) {
		return contains(Vector.fromCartesian(x, y));
	}

	/**
	 * Checks if the given box is contained within this box.
	 * @param another	The box to check.
	 * @return	{@code true} if the given box is contained within this box, {@code false} otherwise.
	 */
	public boolean contains(Box another) {
		return contains(another.topLeft()) && contains(another.bottomRight());
	}

	/**
	 * Checks if this box and the given box intersect. That is, if they have any points in common.
	 * @param another	The box to check.
	 * @return	{@code true} if the two boxes intersect, {@code false} otherwise.
	 */
	public boolean intersects(Box another) {
		return !(Theory.isZero(area()) || Theory.isZero(another.area()) ||
						 (Theory.looseGreater(left(), another.right()) && Theory.looseGreater(another.left(), right())) ||
						 (Theory.looseGreater(bottom(), another.top()) && Theory.looseGreater(another.bottom(), top())));
	}

	// Instant Variants

	/**
	 * Gets the smallest square box that contains this box. Note well that the center of the returned box is the same as
	 * the center of this box.
	 * @return	The minimum square box that contains this box.
	 */
	public Box squareOuter() {
		double max = width().magnitudeMax(height());
		return width(max).height(max).center(center());
	}

	/**
	 * Gets the largest square box that is contained by this box. Note well that the center of the returned box is the
	 * same as the center of this box.
	 * @return	The maximum square box that is contained by this box.
	 */
	public Box squareInner() {
		double min = width().magnitudeMin(height());
		return width(min).height(min).center(center());
	}

	// Instance Operations

	/**
	 * Translates the top left corner of the box by the given factors. That is, the top left corner is moved by the
	 * given factors of the width and height of the box, while the bottom right corner stays fixed.
	 * <br />
	 * For example, if the box is {@code 10 × 10}, and the factors are {@code 1} and {@code 1}, then the
	 * top left corner is moved {@code 10} units right and {@code 10} units down, while the bottom right corner
	 * stays fixed.
	 * <br />
	 * <b>Note well that the positive x-axis points right while the positive y-axis points down.</b>
	 * @param xFactor	The factor by which to translate the x-coordinate of the top left corner.
	 * @param yFactor	The factor by which to translate the y-coordinate of the top left corner.
	 * @return	A new box whose top left corner is translated by the given factors.
	 */
	public Box translateTopLeft(double xFactor, double yFactor) {
		return topLeft(topLeft().add(width().multiply(xFactor)).add(height().multiply(yFactor)));
	}

	/**
	 * Translates the bottom left corner of the box by the given factors. That is, the bottom left corner is moved by the
	 * given factors of the width and height of the box, while the top right corner stays fixed.
	 * <br />
	 * For example, if the box is {@code 10 × 10}, and the factors are {@code 1} and {@code 1}, then the
	 * bottom left corner is moved {@code 10} units right and {@code 10} units up, while the top right corner
	 * stays fixed.
	 * <br />
	 * <b>Note well that the positive x-axis points right while the positive y-axis points up.</b>
	 * @param xFactor	The factor by which to translate the x-coordinate of the bottom left corner.
	 * @param yFactor	The factor by which to translate the y-coordinate of the bottom left corner.
	 * @return	A new box whose bottom left corner is translated by the given factors.
	 */
	public Box translateBottomLeft(double xFactor, double yFactor) {
		return bottomLeft(bottomLeft().add(width().multiply(xFactor)).add(height().multiply(yFactor)));
	}

	/**
	 * Translates the bottom right corner of the box by the given factors. That is, the bottom right corner is moved by the
	 * given factors of the width and height of the box, while the top left corner stays fixed.
	 * <br />
	 * For example, if the box is {@code 10 × 10}, and the factors are {@code 1} and {@code 1}, then the
	 * bottom right corner is moved {@code 10} units left and {@code 10} units up, while the top left corner
	 * stays fixed.
	 * <br />
	 * <b>Note well that the positive x-axis points left while the positive y-axis points up.</b>
	 * @param xFactor	The factor by which to translate the x-coordinate of the bottom right corner.
	 * @param yFactor	The factor by which to translate the y-coordinate of the bottom right corner.
	 * @return	A new box whose bottom right corner is translated by the given factors.
	 */
	public Box translateBottomRight(double xFactor, double yFactor) {
		return bottomRight(bottomRight().add(width().multiply(xFactor)).add(height().multiply(yFactor)));
	}

	/**
	 * Translates the top right corner of the box by the given factors. That is, the top right corner is moved by the
	 * given factors of the width and height of the box, while the bottom left corner stays fixed.
	 * <br />
	 * For example, if the box is {@code 10 × 10}, and the factors are {@code 1} and {@code 1}, then the
	 * top right corner is moved {@code 10} units left and {@code 10} units down, while the bottom left corner
	 * stays fixed.
	 * <br />
	 * <b>Note well that the positive x-axis points left while the positive y-axis points down.</b>
	 * @param xFactor	The factor by which to translate the x-coordinate of the top right corner.
	 * @param yFactor	The factor by which to translate the y-coordinate of the top right corner.
	 * @return	A new box whose top right corner is translated by the given factors.
	 */
	public Box translateTopRight(double xFactor, double yFactor) {
		return topRight(topRight().add(width().multiply(xFactor)).add(height().multiply(yFactor)));
	}


	/**
	 * Translates the top edge of the box by the given factor. That is, the top edge is moved by the given factor of the
	 * height of the box, while the bottom edge stays fixed.
	 * <br />
	 * For example, if the box is {@code 10 × 10}, and the factor is {@code 1}, then the top edge is moved
	 * {@code 10} units down, while the bottom edge stays fixed.
	 * <br />
	 * <b>Note well that the positive y-axis points down.</b>
	 * @param yFactor	The factor by which to translate the y-coordinate of the top edge.
	 * @return	A new box whose top edge is translated by the given factor.
	 */
	public Box translateTop(double yFactor) {
		return top(top() + h() * yFactor);
	}

	/**
	 * Translates the bottom edge of the box by the given factor. That is, the bottom edge is moved by the given factor of
	 * the height of the box, while the top edge stays fixed.
	 * <br />
	 * For example, if the box is {@code 10 × 10}, and the factor is {@code 1}, then the bottom edge is moved
	 * {@code 10} units up, while the top edge stays fixed.
	 * <br />
	 * <b>Note well that the positive y-axis points up.</b>
	 * @param yFactor	The factor by which to translate the y-coordinate of the bottom edge.
	 * @return	A new box whose bottom edge is translated by the given factor.
	 */
	public Box translateBottom(double yFactor) {
		return bottom(bottom() + h() * yFactor);
	}

	/**
	 * Translates the left edge of the box by the given factor. That is, the left edge is moved by the given factor of the
	 * width of the box, while the right edge stays fixed.
	 * <br />
	 * For example, if the box is {@code 10 × 10}, and the factor is {@code 1}, then the left edge is moved
	 * {@code 10} units right, while the right edge stays fixed.
	 * <br />
	 * <b>Note well that the positive x-axis points right.</b>
	 * @param xFactor	The factor by which to translate the x-coordinate of the left edge.
	 * @return	A new box whose left edge is translated by the given factor.
	 */
	public Box translateLeft(double xFactor) {
		return left(left() + w() * xFactor);
	}

	/**
	 * Translates the right edge of the box by the given factor. That is, the right edge is moved by the given factor of
	 * the width of the box, while the left edge stays fixed.
	 * <br />
	 * For example, if the box is {@code 10 × 10}, and the factor is {@code 1}, then the right edge is moved
	 * {@code 10} units left, while the left edge stays fixed.
	 * <br />
	 * <b>Note well that the positive x-axis points left.</b>
	 * @param xFactor	The factor by which to translate the x-coordinate of the right edge.
	 * @return	A new box whose right edge is translated by the given factor.
	 */
	public Box translateRight(double xFactor) {
		return right(right() + w() * xFactor);
	}


	/**
	 * Translates the width of the box by the given factor. That is, the width is multiplied by the given factor.
	 * @param factor	The factor by which to multiply the width of the box.
	 * @return	A new box whose width is multiplied by the given factor.
	 */
	public Box translateWidth(double factor) {
		return width(w() + width().multiply(factor).magnitude());
	}

	/**
	 * Translates the height of the box by the given factor. That is, the height is multiplied by the given factor.
	 * @param factor	The factor by which to multiply the height of the box.
	 * @return	A new box whose height is multiplied by the given factor.
	 */
	public Box translateHeight(double factor) {
		return height(h() + height().multiply(factor).magnitude());
	}

	/**
	 * Translates the box by the given factors. That is, the box is moved by the given factors of the width and height of
	 * the box.
	 * <br />
	 * For example, if the box is {@code 10 × 10}, and the factors are {@code 1} and {@code 1}, then the
	 * box is moved {@code 10} units right and {@code 10} units down.
	 * <br />
	 * <b>Note well that the positive x-axis points right while the positive y-axis points down.</b>
	 * @param xFactor	The factor by which to translate the x-coordinate of the box.
	 * @param yFactor	The factor by which to translate the y-coordinate of the box.
	 * @return	A new box translated by the given factors.
	 */
	public Box translate(double xFactor, double yFactor) {
		return center(center().add(width().multiply(xFactor)).add(height().multiply(yFactor)));
	}



	/**
	 * Shifts the top left corner of the box by the given offset. That is, the top left corner is moved by the given
	 * offset.
	 * <br />
	 * For example, if the offset is {@code 5, 5}, then the top left corner is added by the vector {@code (5, 5)}.
	 * @param offset	The offset by which to shift the top left corner of the box.
	 * @return	A new box whose top left corner is shifted by the given offset.
	 */
	public Box shiftTopLeft(Vector offset) {
		return topLeft(topLeft().add(offset));
	}

	/**
	 * Shifts the bottom left corner of the box by the given offset. That is, the top right corner is moved by the given
	 * offset.
	 * <br />
	 * For example, if the offset is {@code 5, 5}, then the bottom left corner is added by the vector {@code (5, 5)}.
	 * @param offset	The offset by which to shift the bottom left corner of the box.
	 * @return	A new box whose bottom left corner is shifted by the given offset.
	 */
	public Box shiftBottomLeft(Vector offset) {
		return bottomLeft(bottomLeft().add(offset));
	}

	/**
	 * Shifts the bottom right corner of the box by the given offset. That is, the bottom right corner is moved by the
	 * given offset.
	 * <br />
	 * For example, if the offset is {@code 5, 5}, then the bottom right corner is added by the vector {@code (5, 5)}.
	 * @param offset	The offset by which to shift the bottom right corner of the box.
	 * @return	A new box whose bottom right corner is shifted by the given offset.
	 */
	public Box shiftBottomRight(Vector offset) {
		return bottomRight(bottomRight().add(offset));
	}

	/**
	 * Shifts the top right corner of the box by the given offset. That is, the top right corner is moved by the given
	 * offset.
	 * <br />
	 * For example, if the offset is {@code 5, 5}, then the top right corner is added by the vector {@code (5, 5)}.
	 * @param offset	The offset by which to shift the top right corner of the box.
	 * @return	A new box whose top right corner is shifted by the given offset.
	 */
	public Box shiftTopRight(Vector offset) {
		return topRight(topRight().add(offset));
	}


	/**
	 * Shifts the top edge of the box by the given offset. That is, the top edge is moved by the given offset.
	 * <br />
	 * For example, if the offset is {@code 5}, then the y-coordinate of the top edge is added by {@code 5}.
	 * @param yOffset	The offset by which to shift the y-coordinate of the top edge of the box.
	 * @return	A new box whose top edge is shifted by the given offset.
	 */
	public Box shiftTop(double yOffset) {
		return shiftTopLeft(Vector.fromCartesian(topLeft().x(), yOffset));
	}

	/**
	 * Shifts the bottom edge of the box by the given offset. That is, the bottom edge is moved by the given offset.
	 * <br />
	 * For example, if the offset is {@code 5}, then the y-coordinate of the bottom edge is added by {@code 5}.
	 * @param yOffset	The offset by which to shift the y-coordinate of the bottom edge of the box.
	 * @return	A new box whose bottom edge is shifted by the given offset.
	 */
	public Box shiftBottom(double yOffset) {
		return shiftBottomLeft(Vector.fromCartesian(bottomLeft().x(), yOffset));
	}

	/**
	 * Shifts the left edge of the box by the given offset. That is, the left edge is moved by the given offset.
	 * <br />
	 * For example, if the offset is {@code 5}, then the x-coordinate of the left edge is added by {@code 5}.
	 * @param xOffset	The offset by which to shift the x-coordinate of the left edge of the box.
	 * @return	A new box whose left edge is shifted by the given offset.
	 */
	public Box shiftLeft(double xOffset) {
		return shiftTopLeft(Vector.fromCartesian(xOffset, topLeft().y()));
	}

	/**
	 * Shifts the right edge of the box by the given offset. That is, the right edge is moved by the given offset.
	 * <br />
	 * For example, if the offset is {@code 5}, then the x-coordinate of the right edge is added by {@code 5}.
	 * @param xOffset	The offset by which to shift the x-coordinate of the right edge of the box.
	 * @return	A new box whose right edge is shifted by the given offset.
	 */
	public Box shiftRight(double xOffset) {
		return shiftTopRight(Vector.fromCartesian(xOffset, topRight().y()));
	}


	/**
	 * Shifts the box by the given offset. That is, the box is moved by the given offset.
	 * <br />
	 * For example, if the offset is {@code 5, 5}, then the coordinates of the box are added by the vector
	 * {@code (5, 5)}.
	 * @param offset	The offset by which to shift the box.
	 * @return	A new box shifted by the given offset.
	 */
	public Box shift(Vector offset) {
		return center(center().add(offset));
	}

	/**
	 * Shifts the box by the given offset. That is, the box is moved by the given offset.
	 * <br />
	 * For example, if the offset is {@code 5, 5}, then the coordinates of the box are added by the vector
	 * {@code (5, 5)}.
	 * @param xOffset	The offset by which to shift the x-coordinate of the box.
	 * @param yOffset	The offset by which to shift the y-coordinate of the box.
	 * @return	A new box shifted by the given offset.
	 * @see #shift(Vector)
	 */
	public Box shift(double xOffset, double yOffset) {
		return shift(Vector.fromCartesian(xOffset, yOffset));
	}


	/**
	 * Aligns the top left corner of the box to the given vector without modifying the size.
	 * @param topLeft	The vector to which to align the top left corner of the box to.
	 * @return	A new box whose top left corner is aligned with the given vector.
	 */
	public Box alignTopLeft(Vector topLeft) {
		return new Box(topLeft, size());
	}

	/**
	 * Aligns the bottom left corner of the box to the given vector without modifying the size.
	 * @param bottomLeft	The vector to which to align the bottom left corner of the box to.
	 * @return	A new box whose bottom left corner is aligned with the given vector.
	 */
	public Box alignBottomLeft(Vector bottomLeft) {
		return Box.fromVector(bottomLeft.subtract(height()), bottomLeft.add(width()));
	}

	/**
	 * Aligns the bottom right corner of the box to the given vector without modifying the size.
	 * @param bottomRight	The vector to which to align the bottom right corner of the box to.
	 * @return	A new box whose bottom right corner is aligned with the given vector.
	 */
	public Box alignBottomRight(Vector bottomRight) {
		return Box.fromVector(bottomRight.subtract(size()), bottomRight);
	}

	/**
	 * Aligns the top right corner of the box to the given vector without modifying the size.
	 * @param topRight	The vector to which to align the top right corner of the box to.
	 * @return	A new box whose top right corner is aligned with the given vector.
	 */
	public Box alignTopRight(Vector topRight) {
		return Box.fromVector(topRight.subtract(width()), topRight.add(height()));
	}


	/**
	 * Aligns the top edge of the box to the given y-coordinate without modifying the size.
	 * @param y	The y-coordinate to which to align the top edge of the box to.
	 * @return	A new box whose top edge is aligned with the given y-coordinate.
	 */
	public Box alignTop(double y) {
		return alignTopLeft(Vector.fromCartesian(topLeft().x(), y));
	}

	/**
	 * Aligns the bottom edge of the box to the given y-coordinate without modifying the size.
	 * @param y	The y-coordinate to which to align the bottom edge of the box to.
	 * @return	A new box whose bottom edge is aligned with the given y-coordinate.
	 */
	public Box alignBottom(double y) {
		return alignBottomLeft(Vector.fromCartesian(bottomLeft().x(), y));
	}

	/**
	 * Aligns the left edge of the box to the given x-coordinate without modifying the size.
	 * @param x	The x-coordinate to which to align the left edge of the box to.
	 * @return	A new box whose left edge is aligned with the given x-coordinate.
	 */
	public Box alignLeft(double x) {
		return alignTopLeft(Vector.fromCartesian(x, topLeft().y()));
	}

	/**
	 * Aligns the right edge of the box to the given x-coordinate without modifying the size.
	 * @param x	The x-coordinate to which to align the right edge of the box to.
	 * @return	A new box whose right edge is aligned with the given x-coordinate.
	 */
	public Box alignRight(double x) {
		return alignTopRight(Vector.fromCartesian(x, topRight().y()));
	}


	/**
	 * Aligns the top left corner of the box to the top left corner of the given box without modifying the size.
	 * @param another	The box to which to align the top left corner of the box to.
	 * @return	A new box whose top left corner is aligned with the top left corner of the given box.
	 * @see #alignTopLeft(Vector)
	 */
	public Box alignTopLeft(Box another) {
		return alignTopLeft(another.topLeft());
	}

	/**
	 * Aligns the bottom left corner of the box to the bottom left corner of the given box without modifying the size.
	 * @param another	The box to which to align the bottom left corner of the box to.
	 * @return	A new box whose bottom left corner is aligned with the bottom left corner of the given box.
	 * @see #alignBottomLeft(Vector)
	 */
	public Box alignBottomLeft(Box another) {
		return alignBottomLeft(another.bottomLeft());
	}

	/**
	 * Aligns the bottom right corner of the box to the bottom right corner of the given box without modifying the size.
	 * @param another	The box to which to align the bottom right corner of the box to.
	 * @return	A new box whose bottom right corner is aligned with the bottom right corner of the given box.
	 * @see #alignBottomRight(Vector)
	 */
	public Box alignBottomRight(Box another) {
		return alignBottomRight(another.bottomRight());
	}

	/**
	 * Aligns the top right corner of the box to the top right corner of the given box without modifying the size.
	 * @param another	The box to which to align the top right corner of the box to.
	 * @return	A new box whose top right corner is aligned with the top right corner of the given box.
	 * @see #alignTopRight(Vector)
	 */
	public Box alignTopRight(Box another) {
		return alignTopRight(another.topRight());
	}


	/**
	 * Aligns the top edge of the box to the top edge of the given box without modifying the size.
	 * @param another	The box to which to align the top edge of the box to.
	 * @return	A new box whose top edge is aligned with the top edge of the given box.
	 * @see #alignTop(double)
	 */
	public Box alignTop(Box another) {
		return alignTop(another.top());
	}

	/**
	 * Aligns the bottom edge of the box to the bottom edge of the given box without modifying the size.
	 * @param another	The box to which to align the bottom edge of the box to.
	 * @return	A new box whose bottom edge is aligned with the bottom edge of the given box.
	 * @see #alignBottom(double)
	 */
	public Box alignBottom(Box another) {
		return alignBottom(another.bottom());
	}

	/**
	 * Aligns the left edge of the box to the left edge of the given box without modifying the size.
	 * @param another	The box to which to align the left edge of the box to.
	 * @return	A new box whose left edge is aligned with the left edge of the given box.
	 * @see #alignLeft(double)
	 */
	public Box alignLeft(Box another) {
		return alignLeft(another.left());
	}

	/**
	 * Aligns the right edge of the box to the right edge of the given box without modifying the size.
	 * @param another	The box to which to align the right edge of the box to.
	 * @return	A new box whose right edge is aligned with the right edge of the given box.
	 * @see #alignRight(double)
	 */
	public Box alignRight(Box another) {
		return alignRight(another.right());
	}


	/**
	 * Rotates the box by the given amount of right angles (90 degrees).
	 * @param rotationCount	The number of right angles by which to rotate the box.
	 * @return	A new box rotated by the given amount of right angles.
	 */
	public Box rotateByRightAngle(int rotationCount) {
		if (rotationCount % 2 == 0) {
			return this;
		}
		return size(size().theta(Math.PI * 2 - size().theta()));
	}

	/**
	 * Rotates the box by the given amount of right angles (90 degrees) without modifying the center.
	 * @param rotationCount	The number of right angles by which to rotate the box.
	 * @return	A new box rotated by the given amount of right angles.
	 * @see #rotateByRightAngle(int)
	 */
	public Box rotateByRightAngleCentered(int rotationCount) {
		return rotateByRightAngle(rotationCount).center(center());
	}

	/**
	 * Scales the box by the given scalars.
	 * @param xScalar	The scalar by which to scale the width of the box.
	 * @param yScalar	The scalar by which to scale the height of the box.
	 * @return	A new box scaled by the given scalars.
	 */
	public Box scale(double xScalar, double yScalar) {
		return width(w() * xScalar).height(h() * yScalar);
	}

	/**
	 * Scales the box by the given scalar.
	 * @param scalar	The scalar by which to scale the width and height of the box.
	 * @return	A new box scaled by the given scalar.
	 */
	public Box scale(double scalar) {
		return scale(scalar, scalar);
	}

	/**
	 * Scales the box by the given scalars without modifying the center.
	 * @param xScalar	The scalar by which to scale the width of the box.
	 * @param yScalar	The scalar by which to scale the height of the box.
	 * @return	A new box scaled by the given scalars.
	 * @see #scale(double, double)
	 */
	public Box scaleCenter(double xScalar, double yScalar) {
		return scale(xScalar, yScalar).center(center());
	}

	/**
	 * Scales the box by the given scalar without modifying the center.
	 * @param scalar	The scalar by which to scale the width and height of the box.
	 * @return	A new box scaled by the given scalar.
	 * @see #scale(double)
	 */
	public Box scaleCenter(double scalar) {
		return scaleCenter(scalar, scalar);
	}

	/**
	 * Expands the box by the given expansion. For example, if the box is {@code [(0, 0), (1, 1)]} and the expansion is
	 * {@code (1, 2)}, the result is {@code [(-1, -2), (2, 3)]}. That is, the top left corner is subtracted by the
	 * given expansion and the bottom right corner is added by the given expansion.
	 * @param expansion	The expansion to apply to the box.
	 * @return	A new box expanded by the given expansion.
	 */
	public Box expand(Vector expansion) {
		return Box.fromVector(topLeft().subtract(expansion), bottomRight().add(expansion));
	}

	/**
	 * Expands the box by the given expansion. For example, if the box is {@code [(0, 0), (1, 1)]} and the expansion is
	 * {@code (1, 2)}, the result is {@code [(-1, -2), (2, 3)]}. That is, the top left corner is subtracted by the
	 * given expansion and the bottom right corner is added by the given expansion.
	 * @param xExpansion	The expansion to apply to the box in the x-axis.
	 * @param yExpansion	The expansion to apply to the box in the y-axis.
	 * @return	A new box expanded by the given expansion.
	 * @see #expand(Vector)
	 */
	public Box expand(double xExpansion, double yExpansion) {
		return expand(Vector.fromCartesian(xExpansion, yExpansion));
	}

	/**
	 * Expands the box by the given expansion. For example, if the box is {@code [(0, 0, 1, 1)} and the expansion is
	 * {@code (1, 2)}, the result is {@code [(-1, -2, 2, 3)}. That is, the top left corner is subtracted by the
	 * given expansion and the bottom right corner is added by the given expansion.
	 * @param expansion	The expansion to apply to the box.
	 * @return	A new box expanded by the given expansion.
	 * @see #expand(Vector)
	 */
	public Box expand(double expansion) {
		return expand(expansion, expansion);
	}

	/**
	 * Normalizes the box by the given box. For example, if the box is {@code [(0, 0, 6, 6)} and the given box is
	 * {@code [(2, 2, 4, 4)}, the result is {@code [(-1, -1, 2, 2)}. That is, the box is scaled as how the given
	 * box is scaled to the unit box, and moved so that the relative position of this box to the given box is preserved
	 * between the scaled box and the unit box.
	 * @param another	The box to normalize by.
	 * @return	A new box normalized by the given box.
	 */
	public Box normalizeBy(Box another) {
		return scaleCenter(1 / another.w(), 1 / another.h())
					   .center(center().subtract(another.center()).scale(1 / another.w(), 1 / another.h()));
	}

	/**
	 * <h1>{@code a ∩ b}</h1>
	 * Gets a box which is the intersection of this box and the given box. If the boxes do not intersect, the result is
	 * {@link Box#ZERO}.
	 * @param another	The box to intersect with.
	 * @return	A new box which is the intersection of this box and the given box.
	 */
	public Box min(Box another) {
		if (!intersects(another)) return Box.ZERO;
		else {
			return Box.fromVector(topLeft().max(another.topLeft()), bottomRight().min(another.bottomRight()));
		}
	}

	/**
	 * <h1>{@code a ∪ b}</h1>
	 * Gets a box which is the union of this box and the given box. If the boxes do not intersect, the result is the
	 * smallest box which contains both boxes.
	 * @param another	The box to union with.
	 * @return	A new box which is the union of this box and the given box.
	 */
	public Box max(Box another) {
		return Box.fromVector(topLeft().min(another.topLeft()), bottomRight().max(another.bottomRight()));
	}

	/**
	 * Interpolates between this box and the given vector by the given factor. That is, the corners of the box is
	 * interpolated towards the given vector by the given factor.
	 * @param vector	The vector to interpolate towards.
	 * @param factor	The factor to interpolate by.
	 * @return	A new box interpolated towards the given vector by the given factor.
	 * @see Vector#interpolate(Vector, double)
	 */
	public Box interpolate(Vector vector, double factor) {
		return new Box(topLeft().interpolate(vector, factor), size().interpolate(vector, factor));
	}

	/**
	 * Interpolates between this box and the given box by the given factor. That is, the corners of the box is
	 * interpolated towards the corresponding corners of the given box by the given factor.
	 * @param another	The box to interpolate towards.
	 * @param factor	The factor to interpolate by.
	 * @return	A new box interpolated towards the given box by the given factor.
	 * @see Vector#interpolate(Vector, double)
	 */
	public Box interpolate(Box another, double factor) {
		return new Box(topLeft().interpolate(another.topLeft(), factor), size().interpolate(another.size(), factor));
	}


	/**
	 * Gets a grid (2D array) of boxes with the given steps. For example, if the box is {@code [(0, 0), (10, 10)]} and
	 * the steps are {@code (2, 5)}, the result is
	 * <table border="10">
	 *     <tr>
	 *         <th></th>
	 *         <th>{@code 0}</th>
	 *         <th>{@code 1}</th>
	 *     </tr>
	 *     <tr>
	 *         <th>{@code 0}</th>
	 *         <td>{@code [(0, 0), (5, 2)]}</td>
	 *         <td>{@code [(5, 0), (5, 2)]}</td>
	 *     </tr>
	 *     <tr>
	 *         <th>{@code 1}</th>
	 *         <td>{@code [(0, 2), (5, 2)]}</td>
	 *         <td>{@code [(5, 2), (5, 2)]}</td>
	 *     </tr>
	 *     <tr>
	 *         <th>{@code 2}</th>
	 *         <td>{@code [(0, 4), (5, 2)]}</td>
	 *         <td>{@code [(5, 4), (5, 2)]}</td>
	 *     </tr>
	 *     <tr>
	 *         <th>{@code 3}</th>
	 *         <td>{@code [(0, 6), (5, 2)]}</td>
	 *         <td>{@code [(5, 6), (5, 2)]}</td>
	 *     </tr>
	 *     <tr>
	 *         <th>{@code 4}</th>
	 *         <td>{@code [(0, 8), (5, 2)]}</td>
	 *         <td>{@code [(5, 8), (5, 2)]}</td>
	 *     </tr>
	 * </table>
	 * @param xStep	The step in the x-direction.
	 * @param yStep	The step in the y-direction.
	 * @return	A grid (2D array) of boxes with the given steps.
	 */
	public Box[][] grid(int xStep, int yStep) {
		if (xStep <= 0 || yStep <= 0) {
			throw new IllegalArgumentException("Step must be positive");
		}

		Box[][] grid = new Box[xStep][yStep];

		for (int x = 0; x < xStep; x++) {
			for (int y = 0; y < yStep; y++) {
				grid[x][y] = new Box(topLeft().add(width().multiply(x / (double) xStep)).add(height().multiply(y / (double) yStep)),
						width().multiply(1 / (double) xStep).add(height().multiply(1 / (double) yStep)));
			}
		}

		return grid;
	}

	/**
	 * Gets a grid (2D array) of boxes with the given step. For example, if the box is {@code (0, 0, 10, 10)} and
	 * the step is {@code 2}, the result is
	 * <table border="10">
	 *     <tr>
	 *         <th></th>
	 *         <th>{@code 0}</th>
	 *         <th>{@code 1}</th>
	 *     </tr>
	 *     <tr>
	 *         <th>{@code 0}</th>
	 *         <td>{@code [(0, 0), (5, 5)]}</td>
	 *         <td>{@code [(5, 0), (5, 5)]}</td>
	 *     </tr>
	 *     <tr>
	 *         <th>{@code 1}</th>
	 *         <td>{@code [(0, 5), (5, 5)]}</td>
	 *         <td>{@code [(5, 5), (5, 5)]}</td>
	 *     </tr>
	 * </table>
	 * @param step	The step in both the x-direction and the y-direction.
	 * @return	A grid (2D array) of boxes with the given step.
	 * @see #grid(int, int)
	 */
	public Box[][] grid(int step) {
		return grid(step, step);
	}

	// Interface Implementations

	/**
	 * Fits the box to the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Screen Coordinate}.
	 * @return	A new box fitted to the screen coordinate.
	 * @see Convertible#fitToScreen()
	 */
	public Box fitToScreen() {
		return FrameInfo.Convertor.scaledToScreen(this);
	}

	/**
	 * Fits the box to the {@link net.krlite.equator.render.frame.FrameInfo.Convertor OpenGL Coordinate}.
	 * @return	A new box fitted to the OpenGL coordinate.
	 * @see Convertible#fitToOpenGL()
	 */
	public Box fitToOpenGL() {
		return FrameInfo.Convertor.scaledToOpenGL(this);
	}

	/**
	 * Fits the box to the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Screen Coordinate}.
	 * @return	A new box fitted to the screen coordinate.
	 * @see Convertible#fitFromScreen()
	 */
	public Box fitFromScreen() {
		return FrameInfo.Convertor.screenToScaled(this);
	}

	/**
	 * Fits the box to the {@link net.krlite.equator.render.frame.FrameInfo.Convertor OpenGL Coordinate}.
	 * @return	A new box fitted to the OpenGL coordinate.
	 * @see Convertible#fitFromOpenGL()
	 */
	public Box fitFromOpenGL() {
		return FrameInfo.Convertor.openGLToScaled(this);
	}

	// Links

	/**
	 * Readies a {@link BoxRenderer} for rendering, which can hold a {@link Texture} <b>and</b>
	 * an {@link AccurateColor}.
	 * @param renderer	An {@link UnaryOperator} which processes the {@link BoxRenderer}.
	 * @return	The processed {@link BoxRenderer}.
	 */
	public BoxRenderer ready(UnaryOperator<BoxRenderer> renderer) {
		return renderer.apply(new BoxRenderer(this));
	}

	/**
	 * Readies a {@link GradiantRenderer} for rendering, which can hold up to 4 {@link AccurateColor}s.
	 * @param renderer	An {@link UnaryOperator} which processes the {@link GradiantRenderer}.
	 * @return	The processed {@link GradiantRenderer}.
	 */
	public GradiantRenderer readyGradiant(UnaryOperator<GradiantRenderer> renderer) {
		return renderer.apply(new GradiantRenderer(this));
	}

	/**
	 * Readies a {@link ModelRenderer} for rendering, which can hold an {@link ItemStack} <b>or</b>
	 * a {@link BlockState} and renders it as a 3D model.
	 * @param renderer	An {@link UnaryOperator} which processes the {@link ModelRenderer}.
	 * @return	The processed {@link ModelRenderer}.
	 */
	public ModelRenderer readyModel(UnaryOperator<ModelRenderer> renderer) {
		return renderer.apply(new ModelRenderer(this));
	}

	/**
	 * Readies a {@link OvalRenderer} for rendering, which can hold multiple {@link AccurateColor}s and renders them
	 * as an oval.
	 * @param renderer	An {@link UnaryOperator} which processes the {@link OvalRenderer}.
	 * @return	The processed {@link OvalRenderer}.
	 */
	public OvalRenderer readyOval(UnaryOperator<OvalRenderer> renderer) {
		return renderer.apply(new OvalRenderer(this));
	}

	/**
	 * Readies a {@link SectionRenderer} for rendering, which can hold a {@link Section} to render text.
	 * @param section	An {@link UnaryOperator} which processes the {@link Section} that handles
	 *                  the text.
	 * @param renderer	An {@link UnaryOperator} which processes the {@link SectionRenderer}.
	 * @return	The processed {@link SectionRenderer}.
	 */
	public SectionRenderer readySection(UnaryOperator<Section> section, UnaryOperator<SectionRenderer> renderer) {
		return renderer.apply(new SectionRenderer(this, section.apply(new Section())));
	}

	/**
	 * An alias for {@link #ready(UnaryOperator)} with a {@link Texture} ready.
	 * @param texture	The {@link Texture} to render.
	 * @return	A {@link BoxRenderer} with the {@link Texture} ready.
	 * @see #ready(UnaryOperator)
	 */
	public BoxRenderer ready(Texture texture) {
		return new BoxRenderer(this).texture(texture);
	}

	/**
	 * An alias for {@link #readyGradiant(UnaryOperator)} with an {@link AccurateColor} filled.
	 * @param color	The {@link AccurateColor} to fill.
	 * @return	A {@link BoxRenderer} with the {@link AccurateColor} ready.
	 * @see #ready(UnaryOperator)
	 */
	public GradiantRenderer ready(AccurateColor color) {
		return new GradiantRenderer(this).fill(color);
	}

	/**
	 * An alias for {@link #readyModel(UnaryOperator)} with an {@link ItemStack} ready.
	 * @param itemStack	The {@link ItemStack} to render.
	 * @return	A {@link ModelRenderer} with the {@link ItemStack} ready.
	 * @see #readyModel(UnaryOperator)
	 */
	public ModelRenderer ready(ItemStack itemStack) {
		return new ModelRenderer(this).model(itemStack);
	}

	/**
	 * An alias for {@link #readyModel(UnaryOperator)} with an {@link Item} ready.
	 * @param item	The {@link Item} to render.
	 * @return	A {@link ModelRenderer} with the {@link Item} ready.
	 * @see #readyModel(UnaryOperator)
	 */
	public ModelRenderer ready(Item item) {
		return new ModelRenderer(this).model(item);
	}

	/**
	 * An alias for {@link #readyModel(UnaryOperator)} with a {@link BlockState} ready.
	 * @param blockState	The {@link BlockState} to render.
	 * @return	A {@link ModelRenderer} with the {@link BlockState} ready.
	 * @see #readyModel(UnaryOperator)
	 */
	public ModelRenderer ready(BlockState blockState) {
		return new ModelRenderer(this).model(blockState);
	}

	/**
	 * An alias for {@link #readyModel(UnaryOperator)} with a {@link Block} ready.
	 * @param block	The {@link Block} to render.
	 * @return	A {@link ModelRenderer} with the {@link Block} ready.
	 * @see #readyModel(UnaryOperator)
	 */
	public ModelRenderer ready(Block block) {
		return new ModelRenderer(this).model(block);
	}

	/**
	 * An alias for {@link #readyOval(UnaryOperator)} with an {@link AccurateColor} center color ready, and readies the
	 * color map for rendering.
	 * @param centerColor	The {@link AccurateColor} of the center of the oval.
	 * @param colorMap		An {@link UnaryOperator} which processes the {@link ImmutableMap.Builder} of the color map.
	 * @return	An {@link OvalRenderer} with the {@link AccurateColor} center color and the color map ready.
	 */
	public OvalRenderer ready(AccurateColor centerColor, UnaryOperator<ImmutableMap.Builder<Double, AccurateColor>> colorMap) {
		OvalRenderer ovalRenderer = new OvalRenderer(this).centerColor(centerColor);
		return ovalRenderer.colorMap(colorMap.apply(ImmutableMap.builder()).build());
	}

	/**
	 * An alias for {@link #readySection(UnaryOperator, UnaryOperator)} with a {@link Section} ready.
	 * @param section	The {@link Section} to render.
	 * @return	A {@link SectionRenderer} with the {@link Section} ready.
	 * @see #readySection(UnaryOperator, UnaryOperator)
	 */
	public SectionRenderer ready(Section section) {
		return new SectionRenderer(this, section);
	}

	/**
	 * Create a {@link Scissor} from this {@link Box}.
	 * @return	A {@link Scissor} with the same dimensions as this {@link Box}.
	 * @see Scissor
	 */
	public Scissor scissor() {
		return new Scissor(this);
	}

	public void renderButton(MatrixStack matrixStack, ButtonRenderImplementation.State state) {
		ButtonRenderImplementation.render(matrixStack, this, state);
	}

	public void renderToolTip(MatrixStack matrixStack) {
		TooltipRenderImplementation.render(matrixStack, this);
	}

	// Object Methods

	/**
	 * Gets the string representation of the box as cartesian coordinates. For example, {@code [(0, 0), (1 , 1)]}.
	 * @return	The string representation of the box as cartesian coordinates.
	 * @see #toStringAsCartesian(boolean)
	 */
	public String toStringAsCartesian() {
		return toStringAsCartesian(false);
	}

	/**
	 * Gets the string representation of the box as cartesian coordinates. For example, {@code [(0, 0), (1 , 1)]}.
	 * @param precisely	Whether to use precise formatting. That is, whether to not limit the decimal places to 5.
	 * @return	The string representation of the box as cartesian coordinates.
	 */
	public String toStringAsCartesian(boolean precisely) {
		return String.format("[%s, %s]", topLeft().toStringAsCartesian(precisely), bottomRight().toStringAsCartesian(precisely));
	}

	/**
	 * Gets the string representation of the box as polar coordinates. For example, {@code [origin=(zero), size=(θ=45°, mag=1)]}.
	 * @return	The string representation of the box as polar coordinates.
	 */
	@Override
	public String toString() {
		return toString(false);
	}

	/**
	 * Gets the string representation of the box as polar coordinates. For example, {@code [origin=(zero), size=(θ=45°, mag=1)]}.
	 * @param precisely	Whether to use precise formatting. That is, whether to not limit the decimal places to 5.
	 * @return	The string representation of the box as polar coordinates.
	 */
	public String toString(boolean precisely) {
		return String.format("[origin=%s, size=%s]", origin().toString(precisely), size().toString(precisely));
	}
}
