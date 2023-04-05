package net.krlite.equator.math.geometry;

import net.krlite.equator.frame.FrameInfo;
import net.krlite.label.For;
import net.krlite.label.Module;

/**
 * A rectangle in 2D space(defined by the Screen Cartesian Coordinate) and is not rotated.
 * @see Vector
 * @param origin	The origin, which is the top left corner of the box.
 * @param size		The size of the box.
 */
@Module("Math")
@For("2.1.2")
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
		return origin(topLeft);
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

	public Box top(double y) {
		return new Box(topLeft().y(y), size());
	}

	public Box bottom(double y) {
		return new Box(bottomLeft().y(y), size());
	}

	public Box left(double x) {
		return new Box(topLeft().x(x), size());
	}

	public Box right(double x) {
		return new Box(topRight().x(x), size());
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

	public Box squareOuter() {
		double max = width().magnitudeMax(height());
		return width(max).height(max).center(center());
	}

	public Box squareInner() {
		double min = width().magnitudeMin(height());
		return width(min).height(min).center(center());
	}

	public Box shift(Vector offset) {
		return center(center().add(offset));
	}

	public Box shift(double xOffset, double yOffset) {
		return shift(Vector.fromCartesian(xOffset, yOffset));
	}

	public Box translate(double xTranslation, double yTranslation) {
		Vector translatedSize = Vector.fromCartesian(size().x() * xTranslation, size().y() * yTranslation);
		return topLeft(topLeft().add(translatedSize));
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
		return width(width().magnitude() * xScalar).height(height().magnitude() * yScalar);
	}

	public Box scale(double scalar) {
		return scale(scalar, scalar);
	}

	public Box scaleCentered(double xScalar, double yScalar) {
		return scale(xScalar, yScalar).center(center());
	}

	public Box scaleCentered(double scalar) {
		return scaleCentered(scalar, scalar);
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
		return scaleCentered(1 / another.width().magnitude(), 1 / another.height().magnitude())
					   .center(center().subtract(another.center()).scaleX(1 / another.width().magnitude()).scaleY(1 / another.height().magnitude()));
	}

	public Box interpolate(Vector vector, double lambda) {
		return new Box(topLeft().interpolate(vector, lambda), size().interpolate(vector, lambda));
	}

	public Box interpolate(Box box, double lambda) {
		return new Box(topLeft().interpolate(box.topLeft(), lambda), size().interpolate(box.topRight(), lambda));
	}

	public Box fitToScreen() {
		return FrameInfo.Convertor.scaledToScreen(this);
	}

	public Box fitToOpenGL() {
		return FrameInfo.Convertor.screenToOpenGL(this);
	}

	public String toStringAsCartesian() {
		return String.format("[%s, %s]", topLeft().toStringAsCartesian(), bottomRight().toStringAsCartesian());
	}

	@Override
	public String toString() {
		return String.format("[topLeft=%s, topRight=%s]", topLeft().toString(), bottomRight().toString());
	}
}
