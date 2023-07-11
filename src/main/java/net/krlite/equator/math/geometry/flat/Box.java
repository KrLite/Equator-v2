package net.krlite.equator.math.geometry.flat;

import net.krlite.equator.base.Exceptions;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.render.base.Renderable;
import net.krlite.equator.render.base.Scissor;
import net.krlite.equator.render.frame.Convertible;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.renderer.Flat;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Function;

/**
 * <h1>Box</h1>
 * Represents a rectangle in the {@link FrameInfo.Convertor Scaled Coordinate}
 * and is not rotatable.
 * @see Vector
 * @param origin	The top left corner.
 * @param size		The size.
 */
public record Box(Vector origin, Vector size) implements Convertible.Scaled<Box> {
	// Constants

	public static final Box ZERO = new Box(Vector.ZERO);

	public static final Box UNIT = new Box(Vector.UNIT);

	public static final Box UNIT_CENTERED = UNIT.center(Vector.ZERO);

	// Static Constructors

	public static Box fromCartesian(double x, double y, double width, double height) {
		return new Box(Vector.fromCartesian(x, y), Vector.fromCartesian(width, height));
	}

	public static Box fromCartesian(double width, double height) {
		return fromCartesian(0, 0, width, height);
	}

	public static Box fromCartesianCentered(double xCenter, double yCenter, double width, double height) {
		return fromCartesian(xCenter - width / 2, yCenter - height / 2, width, height);
	}

	public static Box fromCartesianCentered(double width, double height) {
		return fromCartesianCentered(0, 0, width, height);
	}

	public static Box fromVector(Vector topLeft, Vector bottomRight) {
		return new Box(topLeft, bottomRight.subtract(topLeft));
	}

	public static Box fromVectorCentered(Vector center, Vector size) {
		return fromCartesianCentered(center.x(), center.y(), size.x(), size.y());
	}

	// Constructors

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

	// Accessors

	@Override
	public Vector origin() {
		return origin;
	}

	@Override
	public Vector size() {
		return size;
	}



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

	public Vector center() {
		return topLeft().add(size().scale(0.5));
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

	public Vector at(double xOffset, double yOffset) {
		return origin().add(size().scale(xOffset, yOffset));
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

	public double d() {
		return size().magnitude();
	}



	public double xCenter() {
		return center().x();
	}

	public double yCenter() {
		return center().y();
	}

	// Mutators

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



	public Box topLeft(Box another) {
		return topLeft(another.topLeft());
	}

	public Box bottomLeft(Box another) {
		return bottomLeft(another.bottomLeft());
	}

	public Box bottomRight(Box another) {
		return bottomRight(another.bottomRight());
	}

	public Box topRight(Box another) {
		return topRight(another.topRight());
	}



	public Box center(Vector center) {
		return new Box(center.subtract(size().scale(0.5)), size());
	}

	public Box topCenter(Vector topCenter) {
		return center(topCenter.add(height().scale(0.5)));
	}

	public Box bottomCenter(Vector bottomCenter) {
		return center(bottomCenter.subtract(height().scale(0.5)));
	}

	public Box leftCenter(Vector leftCenter) {
		return center(leftCenter.add(width().scale(0.5)));
	}

	public Box rightCenter(Vector rightCenter) {
		return center(rightCenter.subtract(width().scale(0.5)));
	}



	public Box center(Box another) {
		return center(another.center());
	}

	public Box topCenter(Box another) {
		return topCenter(another.topCenter());
	}

	public Box bottomCenter(Box another) {
		return bottomCenter(another.bottomCenter());
	}

	public Box leftCenter(Box another) {
		return leftCenter(another.leftCenter());
	}

	public Box rightCenter(Box another) {
		return rightCenter(another.rightCenter());
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
		return size(size().angle(Math.PI * 2 - size().angle()));
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
	 * @return	A new box fitted to the {@link FrameInfo.Convertor Screen Coordinate}.
	 */
	public Box fitToScreen() {
		return FrameInfo.Convertor.scaledToScreen(this);
	}

	/**
	 * @return	A new box fitted to the {@link FrameInfo.Convertor OpenGL Coordinate}.
	 */
	public Box fitToOpenGL() {
		return FrameInfo.Convertor.scaledToOpenGL(this);
	}

	/**
	 * @return	A new box fitted to the {@link FrameInfo.Convertor Screen Coordinate}.
	 */
	public Box fitFromScreen() {
		return FrameInfo.Convertor.screenToScaled(this);
	}

	/**
	 * @return	A new box fitted to the {@link FrameInfo.Convertor OpenGL Coordinate}.
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
	 * @param matrixStack		The {@link MatrixStack} to render in.
	 * @param z				{@code z} to render on.
	 * @param function		A {@link Function} that takes a pre-set {@link Flat} and returns a {@link Renderable}, whose
	 *                      {@link Renderable#render()} method will be called.
	 */
	public void render(MatrixStack matrixStack, float z, Function<Flat, Renderable> function) {
		function.apply(new Flat(matrixStack, z, this)).render();
	}

	public void render(MatrixStack matrixStack, Function<Flat, Renderable> function) {
		render(matrixStack, 0, function);
	}

	/**
	 * Starts rendering from a {@link Flat}, whose parameters are pre-set, in a snipped {@link Scissor}.
	 * @param scissor		The {@link Scissor} to snip with.
	 * @param matrixStack		The {@link MatrixStack} to render in.
	 * @param z				{@code z} to render on.
	 * @param function		A {@link Function} that takes a pre-set {@link Flat} and returns a {@link Renderable}, whose
	 *                      {@link Renderable#render()} method will be called.
	 */
	public void renderSnipped(Box scissor, MatrixStack matrixStack, float z, Function<Flat, Renderable> function) {
		scissor.scissor().snipWith(function.apply(new Flat(matrixStack, z, this)));
	}

	public void renderSnipped(Box scissor, MatrixStack matrixStack, Function<Flat, Renderable> function) {
		renderSnipped(scissor, matrixStack, 0, function);
	}

	// Object Methods

	public String toStringAsCartesian() {
		return toStringAsCartesian(false);
	}

	public String toStringAsCartesian(boolean precisely) {
		return getClass().getSimpleName() + String.format("[%s, %s]", topLeft().toStringAsCartesian(precisely), bottomRight().toStringAsCartesian(precisely));
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean precisely) {
		return getClass().getSimpleName() + String.format("[origin=%s, size=%s]", origin().toString(precisely), size().toString(precisely));
	}
}
