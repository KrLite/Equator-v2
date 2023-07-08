package net.krlite.equator.math.geometry.flat;

import net.krlite.equator.base.Exceptions;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.render.base.Renderable;
import net.krlite.equator.render.frame.Convertible;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.base.Scissor;
import net.krlite.equator.render.renderer.Flat;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Function;

/**
 * <h1>Box</h1>
 * Represents a rectangle in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}
 * and is not rotatable.
 * @see Vector
 * @param origin	The top left corner.
 * @param size		The size.
 */
public record Box(Vector origin, Vector size) implements Convertible.Scaled<Box> {
	// Constants

	/**
	 * A box with a width and height of {@code 0}, whose center is at {@code (0, 0)}.
	 */
	public static final Box ZERO = new Box(Vector.ZERO);
	
	/**
	 * A box with a width and height of {@code 1}, whose origin is at {@code (0, 0)}.
	 */
	public static final Box UNIT = new Box(Vector.UNIT_SQUARE);

	/**
	 * A box with a width and height of {@code 1}, whose center is at {@code (0, 0)}.
	 */
	public static final Box UNIT_CENTERED = UNIT.center(Vector.ZERO);

	// Static Constructors

	/**
	 * Creates a box in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate} from the given 
	 * cartesian coordinate, width, and height.
	 * @param x			{@code x} of the top left corner.
	 * @param y			{@code y} of the top left corner.
	 * @param width		The width.
	 * @param height	The height.
	 * @return	A box with the given cartesian coordinate, width, and height.
	 */
	public static Box fromCartesian(double x, double y, double width, double height) {
		return new Box(Vector.fromCartesian(x, y), Vector.fromCartesian(width, height));
	}

	/**
	 * Creates a box in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate} from the given 
	 * width and height.
	 * @param width		The width.
	 * @param height	The height.
	 * @return	A box with the given width and height, whose origin is {@code (0, 0)}.
	 */
	public static Box fromCartesian(double width, double height) {
		return fromCartesian(0, 0, width, height);
	}

	/**
	 * Creates a box in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate} from the given
	 * cartesian coordinate, width, and height.
	 * @param xCenter		{@code x} of the center.
	 * @param yCenter		{@code y} of the center.
	 * @param width			The width.
	 * @param height		The height.
	 * @return	A box with the given cartesian coordinate, width, and height, whose center is the given cartesian coordinate.
	 */
	public static Box fromCartesianCentered(double xCenter, double yCenter, double width, double height) {
		return fromCartesian(xCenter - width / 2, yCenter - height / 2, width, height);
	}

	/**
	 * Creates a box in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate} from the given 
	 * width and height.
	 * @param width		The width.
	 * @param height	The height.
	 * @return	A box with the given width and height, whose center is {@code (0, 0)}.
	 */
	public static Box fromCartesianCentered(double width, double height) {
		return fromCartesianCentered(0, 0, width, height);
	}

	/**
	 * Creates a box from the given corner vectors.
	 * @param topLeft		The top left corner.
	 * @param bottomRight	The bottom right corner.
	 * @return	A box with the given corner vectors.
	 */
	public static Box fromVector(Vector topLeft, Vector bottomRight) {
		return new Box(topLeft, bottomRight.subtract(topLeft));
	}

	/**
	 * Creates a centered box from the given center and size.
	 * @param center	The center.
	 * @param size		The size. That is, the diagonal vector.
	 * @return	A box with the given size, whose center is the given center.
	 */
	public static Box fromVectorCentered(Vector center, Vector size) {
		return fromCartesianCentered(center.x(), center.y(), size.x(), size.y());
	}

	// Constructors

	/**
	 * Creates a box from the given origin and size.
	 * @param origin	The origin. That is, the top left corner.
	 * @param size		The size. That is, the diagonal vector.
	 */
	public Box(Vector origin, Vector size) {
		this.origin = origin.min(origin.add(size));
		this.size = origin.max(origin.add(size)).subtract(this.origin);
	}

	/**
	 * Creates a box from the given size, whose origin is {@code (0, 0)}.
	 * @param size	The size. That is, the diagonal vector.
	 */
	public Box(Vector size) {
		this(Vector.ZERO, size);
	}

	/**
	 * Creates a box in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate} from the given
	 * cartesian coordinates.
	 * @param xMin	{@code x} of the top left corner.
	 * @param yMin	{@code y} of the top left corner.
	 * @param xMax	{@code x} of the bottom right corner.
	 * @param yMax	{@code y} of the bottom right corner.
	 */
	public Box(double xMin, double yMin, double xMax, double yMax) {
		this(Vector.fromCartesian(xMin, yMin), Vector.fromCartesian(xMax - xMin, yMax - yMin));
	}

	// Accessors

	/**
	 * @return	The origin.
	 */
	@Override
	public Vector origin() {
		return origin;
	}

	/**
	 * @return	The size. That is, the diagonal vector.
	 */
	@Override
	public Vector size() {
		return size;
	}



	/**
	 * @return	The top left corner.
	 * @see #origin()
	 */
	public Vector topLeft() {
		return origin();
	}

	/**
	 * @return	The bottom left corner.
	 */
	public Vector bottomLeft() {
		return Vector.fromCartesian(topLeft().x(), bottomRight().y());
	}

	/**
	 * @return	The bottom right corner.
	 */
	public Vector bottomRight() {
		return origin().add(size());
	}

	/**
	 * @return	The top right corner.
	 */
	public Vector topRight() {
		return Vector.fromCartesian(bottomRight().x(), topLeft().y());
	}

	/**
	 * @return	The center.
	 */
	public Vector center() {
		return topLeft().add(size().scale(0.5));
	}



	/**
	 * @return	The top center. That is, the center of the top edge.
	 */
	public Vector topCenter() {
		return Vector.fromCartesian(center().x(), top());
	}

	/**
	 * @return	The bottom center. That is, the center of the bottom edge.
	 */
	public Vector bottomCenter() {
		return Vector.fromCartesian(center().x(), bottom());
	}

	/**
	 * @return	The left center. That is, the center of the left edge.
	 */
	public Vector leftCenter() {
		return Vector.fromCartesian(left(), center().y());
	}

	/**
	 * @return	The right center. That is, the center of the right edge.
	 */
	public Vector rightCenter() {
		return Vector.fromCartesian(right(), center().y());
	}

	public Vector at(double xOffset, double yOffset) {
		return origin().add(size().scale(xOffset, yOffset));
	}

	

	/**
	 * @return	{@code y} of the top edge.
	 */
	public double top() {
		return topLeft().y();
	}

	/**
	 * @return	{@code y} of the bottom edge.
	 */
	public double bottom() {
		return bottomLeft().y();
	}

	/**
	 * @return	{@code x} of the left edge.
	 */
	public double left() {
		return topLeft().x();
	}

	/**
	 * @return	{@code x} of the right edge.
	 */
	public double right() {
		return topRight().x();
	}



	/**
	 * @return	The width.
	 */
	public Vector width() {
		return topRight().subtract(topLeft());
	}

	/**
	 * @return	The height.
	 */
	public Vector height() {
		return bottomLeft().subtract(topLeft());
	}


	/**
	 * @return	{@code x} of the top left corner.
	 */
	public double x() {
		return topLeft().x();
	}

	/**
	 * @return	{@code y} of the top left corner.
	 */
	public double y() {
		return topLeft().y();
	}

	/**
	 * @return	The width.
	 * @see #width()
	 */
	public double w() {
		return width().magnitude();
	}

	/**
	 * @return	The height.
	 * @see #height()
	 */
	public double h() {
		return height().magnitude();
	}

	/**
	 * @return	The diagonal length.
	 */
	public double d() {
		return size().magnitude();
	}



	/**
	 * @return	{@code x} of the center.
	 */
	public double xCenter() {
		return center().x();
	}

	/**
	 * @return	{@code y} of the center.
	 */
	public double yCenter() {
		return center().y();
	}

	// Mutators

	/**
	 * Mutates the origin.
	 * @param origin	The origin. That is, the top left corner.
	 * @return	A new box with the given origin.
	 */
	public Box origin(Vector origin) {
		return new Box(origin, size());
	}

	/**
	 * Mutates the size.
	 * @param size	The size. That is, the diagonal vector.
	 * @return	A new box with the given size.
	 */
	public Box size(Vector size) {
		return new Box(topLeft(), size);
	}



	/**
	 * Mutates the top left corner.
	 * @param topLeft	The top left corner.
	 * @return	A new box with the given top left corner.
	 * @see #origin(Vector)
	 */
	public Box topLeft(Vector topLeft) {
		return Box.fromVector(topLeft, bottomRight());
	}

	/**
	 * Mutates the bottom left corner.
	 * @param bottomLeft	The bottom left corner.
	 * @return	A new box with the given bottom left corner.
	 */
	public Box bottomLeft(Vector bottomLeft) {
		return Box.fromVector(bottomLeft, topRight());
	}

	/**
	 * Mutates the bottom right corner.
	 * @param bottomRight	The bottom right corner.
	 * @return	A new box with the given bottom right corner.
	 */
	public Box bottomRight(Vector bottomRight) {
		return Box.fromVector(topLeft(), bottomRight);
	}

	/**
	 * Mutates the top right corner.
	 * @param topRight	The top right corner.
	 * @return	A new box with the given top right corner.
	 */
	public Box topRight(Vector topRight) {
		return Box.fromVector(bottomLeft(), topRight);
	}



	/**
	 * Mutates the top left corner.
	 * @param another	The box whose top left corner will be used.
	 * @return	A new box with the given top left corner.
	 * @see #origin(Vector)
	 */
	public Box topLeft(Box another) {
		return topLeft(another.topLeft());
	}

	/**
	 * Mutates the bottom left corner.
	 * @param another	The box whose bottom left corner will be used.
	 * @return	A new box with the given bottom left corner.
	 */
	public Box bottomLeft(Box another) {
		return bottomLeft(another.bottomLeft());
	}

	/**
	 * Mutates the bottom right corner.
	 * @param another	The box whose bottom right corner will be used.
	 * @return	A new box with the given bottom right corner.
	 */
	public Box bottomRight(Box another) {
		return bottomRight(another.bottomRight());
	}

	/**
	 * Mutates the top right corner.
	 * @param another	The box whose top right corner will be used.
	 * @return	A new box with the given top right corner.
	 */
	public Box topRight(Box another) {
		return topRight(another.topRight());
	}



	/**
	 * Mutates the center.
	 * @param center	The center.
	 * @return	A new box with the given center.
	 */
	public Box center(Vector center) {
		return new Box(center.subtract(size().scale(0.5)), size());
	}

	/**
	 * Mutates the top center.
	 * @param topCenter	The top center. That is, the center of the top edge.
	 * @return	A new box with the given top center.
	 */
	public Box topCenter(Vector topCenter) {
		return center(topCenter.add(height().scale(0.5)));
	}

	/**
	 * Mutates the bottom center.
	 * @param bottomCenter	The bottom center. That is, the center of the bottom edge.
	 * @return	A new box with the given bottom center.
	 */
	public Box bottomCenter(Vector bottomCenter) {
		return center(bottomCenter.subtract(height().scale(0.5)));
	}

	/**
	 * Mutates the left center.
	 * @param leftCenter	The left center. That is, the center of the left edge.
	 * @return	A new box with the given left center.
	 */
	public Box leftCenter(Vector leftCenter) {
		return center(leftCenter.add(width().scale(0.5)));
	}

	/**
	 * Mutates the right center.
	 * @param rightCenter	The right center. That is, the center of the right edge.
	 * @return	A new box with the given right center.
	 */
	public Box rightCenter(Vector rightCenter) {
		return center(rightCenter.subtract(width().scale(0.5)));
	}



	/**
	 * Mutates the center to the center of another box.
	 * @param another	The box whose center will be used.
	 * @return	A new box with the given center.
	 */
	public Box center(Box another) {
		return center(another.center());
	}

	/**
	 * Mutates the top center.
	 * @param another	The box whose top center will be used.
	 * @return	A new box with the given top center.
	 */
	public Box topCenter(Box another) {
		return topCenter(another.topCenter());
	}

	/**
	 * Mutates the bottom center.
	 * @param another	The box whose bottom center will be used.
	 * @return	A new box with the given bottom center.
	 */
	public Box bottomCenter(Box another) {
		return bottomCenter(another.bottomCenter());
	}

	/**
	 * Mutates the left center.
	 * @param another	The box whose left center will be used.
	 * @return	A new box with the given left center.
	 */
	public Box leftCenter(Box another) {
		return leftCenter(another.leftCenter());
	}

	/**
	 * Mutates the right center.
	 * @param another	The box whose right center will be used.
	 * @return	A new box with the given right center.
	 */
	public Box rightCenter(Box another) {
		return rightCenter(another.rightCenter());
	}



	/**
	 * Mutates the top edge.
	 * @param y	{@code y} of the top edge.
	 * @return	A new box with the given top edge.
	 */
	public Box top(double y) {
		return topLeft(topLeft().y(y));
	}

	/**
	 * Mutates the bottom edge.
	 * @param y	{@code y} of the bottom edge.
	 * @return	A new box with the given bottom edge.
	 */
	public Box bottom(double y) {
		return bottomLeft(bottomLeft().y(y));
	}

	/**
	 * Mutates the left edge.
	 * @param x	{@code x} of the left edge.
	 * @return	A new box with the given left edge.
	 */
	public Box left(double x) {
		return topLeft(topLeft().x(x));
	}

	/**
	 * Mutates the right edge.
	 * @param x	{@code x} of the right edge.
	 * @return	A new box with the given right edge.
	 */
	public Box right(double x) {
		return topRight(topRight().x(x));
	}



	/**
	 * Mutates the width.
	 * @param width	The width.
	 * @return	A new box with the given width.
	 */
	public Box width(double width) {
		return new Box(topLeft(), width().magnitude(width).add(height()));
	}

	/**
	 * Mutates the height.
	 * @param height	The height.
	 * @return	A new box with the given height.
	 */
	public Box height(double height) {
		return new Box(topLeft(), height().magnitude(height).add(width()));
	}

	// Properties

	/**
	 * @return	The area.
	 */
	public double area() {
		return w() * h();
	}

	/**
	 * @return	The perimeter.
	 */
	public double perimeter() {
		return w() * 2 + h() * 2;
	}

	/**
	 * @return	{@code true -} if the box is a square.
	 * <br />	{@code false -} otherwise.
	 */
	public boolean isSquare() {
		return Theory.looseEquals(w(), h());
	}

	/**
	 * @return	{@code true -} if the box contains the given vector.
	 * <br />	{@code false -} otherwise.
	 */
	public boolean contains(Vector vector) {
		return height().negate().cross(vector.subtract(bottomLeft())) * height().cross(vector.subtract(topRight())) >= 0
					   && width().cross(vector.subtract(topLeft())) * width().negate().cross(vector.subtract(bottomRight())) >= 0;
	}

	/**
	 * @return	{@code true -} if the box contains the given coordinate.
	 * <br />	{@code false -} otherwise.
	 * @see #contains(Vector)
	 */
	public boolean contains(double x, double y) {
		return contains(Vector.fromCartesian(x, y));
	}

	/**
	 * @return	{@code true -} if the box contains the given box.
	 * That is, for every point in the given box, there is a point in this box that is equivalent.
	 * <br />	{@code false -} otherwise.
	 */
	public boolean contains(Box another) {
		return contains(another.topLeft()) && contains(another.bottomRight());
	}

	/**
	 * @return	{@code true -} if the box intersects the given box.
	 * That is, if the two boxes share at least one point.
	 * <br />	{@code false -} otherwise.
	 */
	public boolean intersects(Box another) {
		return !(Theory.isZero(area()) || Theory.isZero(another.area()) ||
						 (Theory.looseGreater(left(), another.right()) && Theory.looseGreater(another.left(), right())) ||
						 (Theory.looseGreater(bottom(), another.top()) && Theory.looseGreater(another.bottom(), top())));
	}

	// Operations

	/**
	 * @return	The smallest square that contains this box, whose center is the same.
	 */
	public Box squareOuter() {
		double max = width().magnitudeMax(height());
		return width(max).height(max).center(center());
	}
	
	/**
	 * @return	The largest square that is contained by this box, whose center is the same.
	 */
	public Box squareInner() {
		double min = width().magnitudeMin(height());
		return width(min).height(min).center(center());
	}


	/**
	 * Translates the top left corner by the given factors. That is, the top left corner is moved by the
	 * given factors of the width and height, while the bottom right corner stays fixed.
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
		return topLeft(topLeft().add(width().scale(xFactor)).add(height().scale(yFactor)));
	}

	/**
	 * Translates the bottom left corner by the given factors. That is, the bottom left corner is moved by the
	 * given factors of the width and height, while the top right corner stays fixed.
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
		return bottomLeft(bottomLeft().add(width().scale(xFactor)).add(height().scale(yFactor)));
	}

	/**
	 * Translates the bottom right corner by the given factors. That is, the bottom right corner is moved by the
	 * given factors of the width and height, while the top left corner stays fixed.
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
		return bottomRight(bottomRight().add(width().scale(xFactor)).add(height().scale(yFactor)));
	}

	/**
	 * Translates the top right corner by the given factors. That is, the top right corner is moved by the
	 * given factors of the width and height, while the bottom left corner stays fixed.
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
		return topRight(topRight().add(width().scale(xFactor)).add(height().scale(yFactor)));
	}


	/**
	 * Translates the top edge by the given factor. That is, the top edge is moved by the given factor of the
	 * height, while the bottom edge stays fixed.
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
	 * Translates the bottom edge by the given factor. That is, the bottom edge is moved by the given factor of
	 * the height, while the top edge stays fixed.
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
	 * Translates the left edge by the given factor. That is, the left edge is moved by the given factor of the
	 * width, while the right edge stays fixed.
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
	 * Translates the right edge by the given factor. That is, the right edge is moved by the given factor of
	 * the width, while the left edge stays fixed.
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
	 * Translates the width by the given factor. That is, the width is multiplied by the given factor.
	 * @param factor	The factor by which to scale the width.
	 * @return	A new box whose width is multiplied by the given factor.
	 */
	public Box translateWidth(double factor) {
		return width(w() + width().scale(factor).magnitude());
	}

	/**
	 * Translates the height by the given factor. That is, the height is multiplied by the given factor.
	 * @param factor	The factor by which to scale the height.
	 * @return	A new box whose height is multiplied by the given factor.
	 */
	public Box translateHeight(double factor) {
		return height(h() + height().scale(factor).magnitude());
	}

	/**
	 * Translates the box by the given factors. That is, the box is moved by the given factors of the width and height of
	 * the box.
	 * <br />
	 * For example, if the box is {@code 10 × 10}, and the factors are {@code 1} and {@code 1}, then the
	 * box is moved {@code 10} units right and {@code 10} units down.
	 * <br />
	 * <b>Note well that the positive x-axis points right while the positive y-axis points down.</b>
	 * @param xFactor	The factor by which to translate the x-coordinate.
	 * @param yFactor	The factor by which to translate the y-coordinate.
	 * @return	A new box translated by the given factors.
	 */
	public Box translate(double xFactor, double yFactor) {
		return center(center().add(width().scale(xFactor)).add(height().scale(yFactor)));
	}



	/**
	 * Shifts the top left corner by the given offset. That is, the top left corner is moved by the given
	 * offset.
	 * <br />
	 * For example, if the offset is {@code 5, 5}, then the top left corner is added by the vector {@code (5, 5)}.
	 * @param offset	The offset by which to shift the top left corner.
	 * @return	A new box whose top left corner is shifted by the given offset.
	 */
	public Box shiftTopLeft(Vector offset) {
		return topLeft(topLeft().add(offset));
	}

	/**
	 * Shifts the bottom left corner by the given offset. That is, the top right corner is moved by the given
	 * offset.
	 * <br />
	 * For example, if the offset is {@code 5, 5}, then the bottom left corner is added by the vector {@code (5, 5)}.
	 * @param offset	The offset by which to shift the bottom left corner.
	 * @return	A new box whose bottom left corner is shifted by the given offset.
	 */
	public Box shiftBottomLeft(Vector offset) {
		return bottomLeft(bottomLeft().add(offset));
	}

	/**
	 * Shifts the bottom right corner by the given offset. That is, the bottom right corner is moved by the
	 * given offset.
	 * <br />
	 * For example, if the offset is {@code 5, 5}, then the bottom right corner is added by the vector {@code (5, 5)}.
	 * @param offset	The offset by which to shift the bottom right corner.
	 * @return	A new box whose bottom right corner is shifted by the given offset.
	 */
	public Box shiftBottomRight(Vector offset) {
		return bottomRight(bottomRight().add(offset));
	}

	/**
	 * Shifts the top right corner by the given offset. That is, the top right corner is moved by the given
	 * offset.
	 * <br />
	 * For example, if the offset is {@code 5, 5}, then the top right corner is added by the vector {@code (5, 5)}.
	 * @param offset	The offset by which to shift the top right corner.
	 * @return	A new box whose top right corner is shifted by the given offset.
	 */
	public Box shiftTopRight(Vector offset) {
		return topRight(topRight().add(offset));
	}


	/**
	 * Shifts the top edge by the given offset. That is, the top edge is moved by the given offset.
	 * <br />
	 * For example, if the offset is {@code 5}, then the y-coordinate of the top edge is added by {@code 5}.
	 * @param yOffset	The offset by which to shift the y-coordinate of the top edge.
	 * @return	A new box whose top edge is shifted by the given offset.
	 */
	public Box shiftTop(double yOffset) {
		return shiftTopLeft(Vector.fromCartesian(topLeft().x(), yOffset));
	}

	/**
	 * Shifts the bottom edge by the given offset. That is, the bottom edge is moved by the given offset.
	 * <br />
	 * For example, if the offset is {@code 5}, then the y-coordinate of the bottom edge is added by {@code 5}.
	 * @param yOffset	The offset by which to shift the y-coordinate of the bottom edge.
	 * @return	A new box whose bottom edge is shifted by the given offset.
	 */
	public Box shiftBottom(double yOffset) {
		return shiftBottomLeft(Vector.fromCartesian(bottomLeft().x(), yOffset));
	}

	/**
	 * Shifts the left edge by the given offset. That is, the left edge is moved by the given offset.
	 * <br />
	 * For example, if the offset is {@code 5}, then the x-coordinate of the left edge is added by {@code 5}.
	 * @param xOffset	The offset by which to shift the x-coordinate of the left edge.
	 * @return	A new box whose left edge is shifted by the given offset.
	 */
	public Box shiftLeft(double xOffset) {
		return shiftTopLeft(Vector.fromCartesian(xOffset, topLeft().y()));
	}

	/**
	 * Shifts the right edge by the given offset. That is, the right edge is moved by the given offset.
	 * <br />
	 * For example, if the offset is {@code 5}, then the x-coordinate of the right edge is added by {@code 5}.
	 * @param xOffset	The offset by which to shift the x-coordinate of the right edge.
	 * @return	A new box whose right edge is shifted by the given offset.
	 */
	public Box shiftRight(double xOffset) {
		return shiftTopRight(Vector.fromCartesian(xOffset, topRight().y()));
	}


	/**
	 * Shifts the box by the given offset. That is, the box is moved by the given offset.
	 * <br />
	 * For example, if the offset is {@code 5, 5}, then the coordinates are added by the vector
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
	 * For example, if the offset is {@code 5, 5}, then the coordinates are added by the vector
	 * {@code (5, 5)}.
	 * @param xOffset	The offset by which to shift the x-coordinate.
	 * @param yOffset	The offset by which to shift the y-coordinate.
	 * @return	A new box shifted by the given offset.
	 * @see #shift(Vector)
	 */
	public Box shift(double xOffset, double yOffset) {
		return shift(Vector.fromCartesian(xOffset, yOffset));
	}


	/**
	 * Aligns the top left corner to the given vector without modifying the size.
	 * @param topLeft	The vector to which to align the top left corner to.
	 * @return	A new box whose top left corner is aligned with the given vector.
	 */
	public Box alignTopLeft(Vector topLeft) {
		return new Box(topLeft, size());
	}

	/**
	 * Aligns the bottom left corner to the given vector without modifying the size.
	 * @param bottomLeft	The vector to which to align the bottom left corner to.
	 * @return	A new box whose bottom left corner is aligned with the given vector.
	 */
	public Box alignBottomLeft(Vector bottomLeft) {
		return Box.fromVector(bottomLeft.subtract(height()), bottomLeft.add(width()));
	}

	/**
	 * Aligns the bottom right corner to the given vector without modifying the size.
	 * @param bottomRight	The vector to which to align the bottom right corner to.
	 * @return	A new box whose bottom right corner is aligned with the given vector.
	 */
	public Box alignBottomRight(Vector bottomRight) {
		return Box.fromVector(bottomRight.subtract(size()), bottomRight);
	}

	/**
	 * Aligns the top right corner to the given vector without modifying the size.
	 * @param topRight	The vector to which to align the top right corner to.
	 * @return	A new box whose top right corner is aligned with the given vector.
	 */
	public Box alignTopRight(Vector topRight) {
		return Box.fromVector(topRight.subtract(width()), topRight.add(height()));
	}


	/**
	 * Aligns the top edge to the given y-coordinate without modifying the size.
	 * @param y	{@code y} to which to align the top edge to.
	 * @return	A new box whose top edge is aligned with the given {@code y}.
	 */
	public Box alignTop(double y) {
		return alignTopLeft(Vector.fromCartesian(topLeft().x(), y));
	}

	/**
	 * Aligns the bottom edge to the given y-coordinate without modifying the size.
	 * @param y	{@code y} to which to align the bottom edge to.
	 * @return	A new box whose bottom edge is aligned with the given {@code y}.
	 */
	public Box alignBottom(double y) {
		return alignBottomLeft(Vector.fromCartesian(bottomLeft().x(), y));
	}

	/**
	 * Aligns the left edge to the given x-coordinate without modifying the size.
	 * @param x	{@code x} to which to align the left edge to.
	 * @return	A new box whose left edge is aligned with the given {@code x}.
	 */
	public Box alignLeft(double x) {
		return alignTopLeft(Vector.fromCartesian(x, topLeft().y()));
	}

	/**
	 * Aligns the right edge to the given x-coordinate without modifying the size.
	 * @param x	{@code x} to which to align the right edge to.
	 * @return	A new box whose right edge is aligned with the given {@code x}.
	 */
	public Box alignRight(double x) {
		return alignTopRight(Vector.fromCartesian(x, topRight().y()));
	}


	/**
	 * Aligns the top left corner to the top left corner of the given box without modifying the size.
	 * @param another	The box to which to align the top left corner to.
	 * @return	A new box whose top left corner is aligned with the top left corner of the given box.
	 * @see #alignTopLeft(Vector)
	 */
	public Box alignTopLeft(Box another) {
		return alignTopLeft(another.topLeft());
	}

	/**
	 * Aligns the bottom left corner to the bottom left corner of the given box without modifying the size.
	 * @param another	The box to which to align the bottom left corner to.
	 * @return	A new box whose bottom left corner is aligned with the bottom left corner of the given box.
	 * @see #alignBottomLeft(Vector)
	 */
	public Box alignBottomLeft(Box another) {
		return alignBottomLeft(another.bottomLeft());
	}

	/**
	 * Aligns the bottom right corner to the bottom right corner of the given box without modifying the size.
	 * @param another	The box to which to align the bottom right corner to.
	 * @return	A new box whose bottom right corner is aligned with the bottom right corner of the given box.
	 * @see #alignBottomRight(Vector)
	 */
	public Box alignBottomRight(Box another) {
		return alignBottomRight(another.bottomRight());
	}

	/**
	 * Aligns the top right corner to the top right corner of the given box without modifying the size.
	 * @param another	The box to which to align the top right corner to.
	 * @return	A new box whose top right corner is aligned with the top right corner of the given box.
	 * @see #alignTopRight(Vector)
	 */
	public Box alignTopRight(Box another) {
		return alignTopRight(another.topRight());
	}


	/**
	 * Aligns the top edge to the top edge of the given box without modifying the size.
	 * @param another	The box to which to align the top edge to.
	 * @return	A new box whose top edge is aligned with the top edge of the given box.
	 * @see #alignTop(double)
	 */
	public Box alignTop(Box another) {
		return alignTop(another.top());
	}

	/**
	 * Aligns the bottom edge to the bottom edge of the given box without modifying the size.
	 * @param another	The box to which to align the bottom edge to.
	 * @return	A new box whose bottom edge is aligned with the bottom edge of the given box.
	 * @see #alignBottom(double)
	 */
	public Box alignBottom(Box another) {
		return alignBottom(another.bottom());
	}

	/**
	 * Aligns the left edge to the left edge of the given box without modifying the size.
	 * @param another	The box to which to align the left edge to.
	 * @return	A new box whose left edge is aligned with the left edge of the given box.
	 * @see #alignLeft(double)
	 */
	public Box alignLeft(Box another) {
		return alignLeft(another.left());
	}

	/**
	 * Aligns the right edge to the right edge of the given box without modifying the size.
	 * @param another	The box to which to align the right edge to.
	 * @return	A new box whose right edge is aligned with the right edge of the given box.
	 * @see #alignRight(double)
	 */
	public Box alignRight(Box another) {
		return alignRight(another.right());
	}


	/**
	 * Rotates the box by the given number of right angles (90 degrees).
	 * @param rotationCount	The number of right angles by which to rotate the box.
	 * @return	A new box rotated by the given number of right angles.
	 */
	public Box rotateByRightAngle(int rotationCount) {
		if (rotationCount % 2 == 0) {
			return this;
		}
		return size(size().theta(Math.PI * 2 - size().theta()));
	}

	/**
	 * Rotates the box by the given number of right angles (90 degrees) without modifying the center.
	 * @param rotationCount	The number of right angles by which to rotate the box.
	 * @return	A new box rotated by the given number of right angles.
	 * @see #rotateByRightAngle(int)
	 */
	public Box rotateByRightAngleCentered(int rotationCount) {
		return rotateByRightAngle(rotationCount).center(center());
	}

	/**
	 * Scales the box by the given scalars.
	 * @param xScalar	The scalar by which to scale the width.
	 * @param yScalar	The scalar by which to scale the height.
	 * @return	A new box scaled by the given scalars.
	 */
	public Box scale(double xScalar, double yScalar) {
		return width(w() * xScalar).height(h() * yScalar);
	}

	/**
	 * Scales the box by the given scalar.
	 * @param scalar	The scalar by which to scale the width and height.
	 * @return	A new box scaled by the given scalar.
	 */
	public Box scale(double scalar) {
		return scale(scalar, scalar);
	}

	/**
	 * Scales the box by the given scalars without modifying the center.
	 * @param xScalar	The scalar by which to scale the width.
	 * @param yScalar	The scalar by which to scale the height.
	 * @return	A new box scaled by the given scalars.
	 * @see #scale(double, double)
	 */
	public Box scaleCenter(double xScalar, double yScalar) {
		return scale(xScalar, yScalar).center(center());
	}

	/**
	 * Scales the box by the given scalar without modifying the center.
	 * @param scalar	The scalar by which to scale the width and height.
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
	 * @param another	The box to normal by.
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
	 * Gets a box which is the minimum box which contains both this box and the given box.
	 * @param another	The box to union with.
	 * @return	A new box which is the union of this box and the given box.
	 */
	public Box max(Box another) {
		return Box.fromVector(topLeft().min(another.topLeft()), bottomRight().max(another.bottomRight()));
	}

	public Box interpolate(Vector vector, double factor) {
		return new Box(topLeft().interpolate(vector, factor), size().interpolate(vector, factor));
	}

	public Box interpolate(Box another, double factor) {
		return new Box(topLeft().interpolate(another.topLeft(), factor), size().interpolate(another.size(), factor));
	}


	/**
	 * Gets a grid (2D array) of boxes with the given steps. For example, if the box is {@code [(0, 0), (10, 10)]} and
	 * the steps are {@code (2, 5)}, the result is
	 * <table>
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
			throw new Exceptions.StepMustBePositiveException(xStep, yStep);
		}

		Box[][] grid = new Box[xStep][yStep];

		for (int x = 0; x < xStep; x++) {
			for (int y = 0; y < yStep; y++) {
				grid[x][y] = new Box(topLeft().add(width().scale(x / (double) xStep)).add(height().scale(y / (double) yStep)),
						width().scale(1 / (double) xStep).add(height().scale(1 / (double) yStep)));
			}
		}

		return grid;
	}

	/**
	 * Gets a grid (2D array) of boxes with the given step. For example, if the box is {@code (0, 0, 10, 10)} and
	 * the step is {@code 2}, the result is
	 * <table>
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
		if (step <= 0) {
			throw new Exceptions.StepMustBePositiveException(step);
		}

		return grid(step, step);
	}

	// Interface Implementations

	/**
	 * @return	A new box fitted to the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Screen Coordinate}.
	 */
	public Box fitToScreen() {
		return FrameInfo.Convertor.scaledToScreen(this);
	}

	/**
	 * @return	A new box fitted to the {@link net.krlite.equator.render.frame.FrameInfo.Convertor OpenGL Coordinate}.
	 */
	public Box fitToOpenGL() {
		return FrameInfo.Convertor.scaledToOpenGL(this);
	}

	/**
	 * @return	A new box fitted to the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Screen Coordinate}.
	 */
	public Box fitFromScreen() {
		return FrameInfo.Convertor.screenToScaled(this);
	}

	/**
	 * @return	A new box fitted to the {@link net.krlite.equator.render.frame.FrameInfo.Convertor OpenGL Coordinate}.
	 */
	public Box fitFromOpenGL() {
		return FrameInfo.Convertor.openGLToScaled(this);
	}

	// Links

	/**
	 * Creates a {@link Scissor} from this {@link Box}.
	 * @return	A {@link Scissor} with the same dimension.
	 * @see Scissor
	 */
	public Scissor scissor() {
		return new Scissor(this);
	}

	/**
	 * Starts rendering from a {@link Flat}, whose parameters are pre-set.
	 * @param context		The {@link DrawContext} to render in.
	 * @param z				{@code z} to render on.
	 * @param function		A {@link Function} that takes a pre-set {@link Flat} and returns a {@link Renderable}, whose
	 *                      {@link Renderable#render()} method will be called.
	 */
	public void render(DrawContext context, float z, Function<Flat, Renderable> function) {
		function.apply(new Flat(context, z, this)).render();
	}

	public void render(DrawContext context, Function<Flat, Renderable> function) {
		render(context, 0, function);
	}

	/**
	 * Starts rendering from a {@link Flat}, whose parameters are pre-set, in a snipped {@link Scissor}.
	 * @param scissor		The {@link Scissor} to snip with.
	 * @param context		The {@link DrawContext} to render in.
	 * @param z				{@code z} to render on.
	 * @param function		A {@link Function} that takes a pre-set {@link Flat} and returns a {@link Renderable}, whose
	 *                      {@link Renderable#render()} method will be called.
	 */
	public void renderSnipped(Box scissor, DrawContext context, float z, Function<Flat, Renderable> function) {
		scissor.scissor().snipWith(function.apply(new Flat(context, z, this)));
	}

	public void renderSnipped(Box scissor, DrawContext context, Function<Flat, Renderable> function) {
		renderSnipped(scissor, context, 0, function);
	}

	// Object Methods

	public String toStringAsCartesian() {
		return toStringAsCartesian(false);
	}

	public String toStringAsCartesian(boolean precisely) {
		return String.format("[%s, %s]", topLeft().toStringAsCartesian(precisely), bottomRight().toStringAsCartesian(precisely));
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean precisely) {
		return String.format("[origin=%s, size=%s]", origin().toString(precisely), size().toString(precisely));
	}
}
