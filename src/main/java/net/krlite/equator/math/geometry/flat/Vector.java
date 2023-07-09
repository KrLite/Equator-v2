package net.krlite.equator.math.geometry.flat;

import net.krlite.equator.render.frame.Convertible;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.math.algebra.Theory;

/**
 * <h1>Vector</h1>
 * Represents a vector in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate} and is
 * stored in polar form.
 * @param theta		The angle <b>in radians.</b>
 * @param magnitude	The magnitude, which is always positive.
 */
public record Vector(double angle, double magnitude) implements Convertible.Scaled<net.krlite.equator.math.geometry.flat.Vector> {
	// Constants

	public static final net.krlite.equator.math.geometry.flat.Vector ZERO = new net.krlite.equator.math.geometry.flat.Vector(0, 0);

	public static final net.krlite.equator.math.geometry.flat.Vector UNIT = new net.krlite.equator.math.geometry.flat.Vector(Math.PI / 4, 1);

	public static final net.krlite.equator.math.geometry.flat.Vector UNIT_SQUARE = new net.krlite.equator.math.geometry.flat.Vector(Math.PI / 4, Math.sqrt(2));

	public static final net.krlite.equator.math.geometry.flat.Vector UNIT_X = new net.krlite.equator.math.geometry.flat.Vector(0, 1);

	public static final net.krlite.equator.math.geometry.flat.Vector UNIT_Y = new net.krlite.equator.math.geometry.flat.Vector(Math.PI / 2, 1);

	// Static Constructors

	public static net.krlite.equator.math.geometry.flat.Vector fromCartesian(double x, double y) {
		return new net.krlite.equator.math.geometry.flat.Vector(Math.atan2(y, x), Math.sqrt(x * x + y * y));
	}

	public static net.krlite.equator.math.geometry.flat.Vector fromDegrees(double angle, double magnitude) {
		return new net.krlite.equator.math.geometry.flat.Vector(Math.toRadians(angle), magnitude);
	}

	// Constructors

	public Vector(double angle, double magnitude) {
		this.angle = angle % (Math.PI * 2) + (magnitude < 0 ? Math.PI : 0);
		this.magnitude = Math.abs(magnitude);
	}

	// Accessors

	@Override
	public double angle() { return angle % (Math.PI * 2); }

	public double angleDegrees() { return Math.toDegrees(angle()); }

	@Override
	public double magnitude() { return magnitude; }

	public double x() { return Math.cos(angle()) * magnitude(); }

	public double y() { return Math.sin(angle()) * magnitude(); }

	// Mutators

	public net.krlite.equator.math.geometry.flat.Vector angle(double angle) {
		return new net.krlite.equator.math.geometry.flat.Vector(angle, magnitude());
	}

	public net.krlite.equator.math.geometry.flat.Vector angleDegrees(double angleDegrees) {
		return angle(Math.toRadians(angleDegrees));
	}

	public net.krlite.equator.math.geometry.flat.Vector magnitude(double magnitude) {
		return new net.krlite.equator.math.geometry.flat.Vector(angle(), magnitude);
	}

	public net.krlite.equator.math.geometry.flat.Vector x(double x) {
		return fromCartesian(x, y());
	}

	public net.krlite.equator.math.geometry.flat.Vector y(double y) {
		return fromCartesian(x(), y);
	}

	// Properties

	/**
	 * @return	{@code true -} if the vector is normalized.
	 * That is, its magnitude is {@code 1}.
	 * <br />	{@code false -} otherwise.
	 */
	public boolean isNormalized() {
		return Theory.looseEquals(magnitude(), 1);
	}

	/**
	 * @return	{@code true -} if the vector is a zero vector.
	 * That is, its magnitude is {@code 0}.
	 * <br />	{@code false -} otherwise.
	 */
	public boolean isZero() {
		return Theory.isZero(magnitude());
	}

	/**
	 * @param another	The other vector.
	 * @return	{@code true -} if the two vectors are parallel.
	 * <br />	{@code false -} otherwise.
	 */
	public boolean isParallelTo(net.krlite.equator.math.geometry.flat.Vector another) {
		return isZero() || another.isZero() || Theory.looseEquals(angle(), another.angle());
	}

	/**
	 * @param another	The other vector.
	 * @return	{@code true -} if the two vectors are perpendicular.
	 * <br />	{@code false -} otherwise.
	 */
	public boolean isPerpendicularTo(net.krlite.equator.math.geometry.flat.Vector another) {
		return isZero() || another.isZero() || Theory.looseEquals(angle(), another.angle() + Math.PI / 2);
	}

	/**
	 * @param another	The other vector.
	 * @return	The angle between the two vectors <b>in radians.</b>
	 */
	public double between(net.krlite.equator.math.geometry.flat.Vector another) {
		return Math.acos(normalize().dot(another.normalize()));
	}

	/**
	 * @param another	The other vector.
	 * @return	The angle between the two vectors <b>in degrees.</b>
	 * @see #between(net.krlite.equator.math.geometry.flat.Vector)
	 */
	public double betweenDegrees(net.krlite.equator.math.geometry.flat.Vector another) {
		return Math.toDegrees(between(another));
	}

	/**
	 * <h1>{@code a · b}</h1>
	 * @param another	The other vector.
	 * @return	The dot-product of the two vectors.
	 */
	public double dot(net.krlite.equator.math.geometry.flat.Vector another) {
		return x() * another.x() + y() * another.y();
	}

	/**
	 * <h1>{@code a × b}</h1>
	 * @param another	The other vector.
	 * @return	The cross-product of the two vectors.
	 */
	public double cross(net.krlite.equator.math.geometry.flat.Vector another) {
		return x() * another.y() - y() * another.x();
	}

	public double distanceTo(net.krlite.equator.math.geometry.flat.Vector another) {
		return subtract(another).magnitude();
	}

	/**
	 * @param another	The other vector.
	 * @return	The Manhattan distance between the two vectors. That is, the sum of the absolute differences of their
	 * 			{@code x} and {@code y} components.
	 */
	public double manhattanDistanceTo(net.krlite.equator.math.geometry.flat.Vector another) {
		return Math.abs(x() - another.x()) + Math.abs(y() - another.y());
	}

	/**
	 * @param another	The other vector.
	 * @return	The minimum of the two vectors' magnitudes.
	 */
	public double magnitudeMin(net.krlite.equator.math.geometry.flat.Vector another) {
		return Math.min(magnitude(), another.magnitude());
	}

	/**
	 * @param another	The other vector.
	 * @return	The maximum of the two vectors' magnitudes.
	 */
	public double magnitudeMax(net.krlite.equator.math.geometry.flat.Vector another) {
		return Math.max(magnitude(), another.magnitude());
	}

	// Operations

	/**
	 * Scales the vector by the given scalars.
	 * @param xScalar	The scalar by which to scale the {@code x} component.
	 * @param yScalar	The scalar by which to scale the {@code y} component.
	 * @return	A new vector scaled by the given scalars.
	 */
	public net.krlite.equator.math.geometry.flat.Vector scale(double xScalar, double yScalar) {
		return x(x() * xScalar).y(y() * yScalar);
	}

	/**
	 * Scales the vector by the given scalar.
	 * @param scalar	The scalar by which to scale the {@code x} and {@code y} components.
	 * @return	A new vector scaled by the given scalar.
	 * @see #scale(double, double)
	 */
	public net.krlite.equator.math.geometry.flat.Vector scale(double scalar) {
		return scale(scalar, scalar);
	}

	public net.krlite.equator.math.geometry.flat.Vector add(double x, double y) {
		return fromCartesian(x() + x, y() + y);
	}

	public net.krlite.equator.math.geometry.flat.Vector add(net.krlite.equator.math.geometry.flat.Vector another) {
		return add(another.x(), another.y());
	}

	public net.krlite.equator.math.geometry.flat.Vector subtract(double x, double y) {
		return fromCartesian(x() - x, y() - y);
	}

	public net.krlite.equator.math.geometry.flat.Vector subtract(net.krlite.equator.math.geometry.flat.Vector another) {
		return subtract(another.x(), another.y());
	}

	public net.krlite.equator.math.geometry.flat.Vector normalize() {
		return magnitude(1);
	}

	public net.krlite.equator.math.geometry.flat.Vector projectOnto(net.krlite.equator.math.geometry.flat.Vector another) {
		return another.scale(dot(another) / another.dot(another));
	}

	public net.krlite.equator.math.geometry.flat.Vector projectOntoX() {
		return y(0);
	}

	public net.krlite.equator.math.geometry.flat.Vector projectOntoY() {
		return x(0);
	}

	public net.krlite.equator.math.geometry.flat.Vector negate() {
		return angle(angle() + Math.PI);
	}

	public net.krlite.equator.math.geometry.flat.Vector negateByX() {
		return x(-x());
	}

	public net.krlite.equator.math.geometry.flat.Vector negateByY() {
		return y(-y());
	}

	public net.krlite.equator.math.geometry.flat.Vector rotate(double angle) {
		return angle(angle() + angle);
	}

	public net.krlite.equator.math.geometry.flat.Vector rotateDegrees(double angleDegrees) {
		return angleDegrees(angleDegrees() + angleDegrees);
	}

	public net.krlite.equator.math.geometry.flat.Vector rotateAround(net.krlite.equator.math.geometry.flat.Vector center, double angle) {
		return center.add(subtract(center).rotate(angle));
	}

	public net.krlite.equator.math.geometry.flat.Vector rotateAroundDegrees(net.krlite.equator.math.geometry.flat.Vector center, double angleDegrees) {
		return center.add(subtract(center).rotateDegrees(angleDegrees));
	}

	/**
	 * Gets a vector with the minimum x and y values of the two vectors. Note well that the vector may not be the same as
	 * either of the two vectors.
	 * @param another	The other vector.
	 * @return	A new vector with the minimum x and y values of the two vectors.
	 */
	public net.krlite.equator.math.geometry.flat.Vector min(net.krlite.equator.math.geometry.flat.Vector another) {
		return fromCartesian(Math.min(x(), another.x()), Math.min(y(), another.y()));
	}

	/**
	 * Gets a vector with the maximum x and y values of the two vectors. Note well that the vector may not be the same as
	 * either of the two vectors.
	 * @param another	The other vector.
	 * @return	A new vector with the maximum x and y values of the two vectors.
	 */
	public net.krlite.equator.math.geometry.flat.Vector max(net.krlite.equator.math.geometry.flat.Vector another) {
		return fromCartesian(Math.max(x(), another.x()), Math.max(y(), another.y()));
	}

	public net.krlite.equator.math.geometry.flat.Vector clamp(net.krlite.equator.math.geometry.flat.Vector min, net.krlite.equator.math.geometry.flat.Vector max) {
		return min.max(this).min(max);
	}

	/**
	 * Gets the vector with the minimum magnitude. If the magnitudes are equal, this vector is returned.
	 * @param another	The other vector.
	 * @return	The vector with the minimum magnitude.
	 */
	public net.krlite.equator.math.geometry.flat.Vector minByMagnitude(net.krlite.equator.math.geometry.flat.Vector another) {
		return magnitude() <= another.magnitude() ? this : another;
	}

	/**
	 * Gets the vector with the maximum magnitude. If the magnitudes are equal, this vector is returned.
	 * @param another	The other vector.
	 * @return	The vector with the maximum magnitude.
	 */
	public net.krlite.equator.math.geometry.flat.Vector maxByMagnitude(net.krlite.equator.math.geometry.flat.Vector another) {
		return magnitude() >= another.magnitude() ? this : another;
	}

	public net.krlite.equator.math.geometry.flat.Vector normal() {
		return new net.krlite.equator.math.geometry.flat.Vector(angle() + Math.PI / 2, magnitude());
	}

	public net.krlite.equator.math.geometry.flat.Vector reflect(net.krlite.equator.math.geometry.flat.Vector normal) {
		return subtract(normal.scale(2 * dot(normal)));
	}

	public net.krlite.equator.math.geometry.flat.Vector interpolate(net.krlite.equator.math.geometry.flat.Vector another, double factor) {
		return add(another.subtract(this).scale(factor));
	}

	// Interface Implementations

	/**
	 * @return	A new vector fitted to the {@link FrameInfo.Convertor Screen Coordinate}.
	 */
	public net.krlite.equator.math.geometry.flat.Vector fitToScreen() {
		return FrameInfo.Convertor.scaledToScreen(this);
	}

	/**
	 * @return	A new vector fitted to the {@link FrameInfo.Convertor OpenGL Coordinate}.
	 */
	public net.krlite.equator.math.geometry.flat.Vector fitToOpenGL() {
		return FrameInfo.Convertor.scaledToOpenGL(this);
	}

	/**
	 * @return	A new vector fitted from the {@link FrameInfo.Convertor Screen Coordinate}.
	 */
	public net.krlite.equator.math.geometry.flat.Vector fitFromScreen() {
		return FrameInfo.Convertor.screenToScaled(this);
	}

	/**
	 * @return	A new vector fitted from the {@link FrameInfo.Convertor OpenGL Coordinate}.
	 */
	public net.krlite.equator.math.geometry.flat.Vector fitFromOpenGL() {
		return FrameInfo.Convertor.openGLToScaled(this);
	}

	// Object Methods

	public String toStringAsCartesian() {
		return toStringAsCartesian(false);
	}

	public String toStringAsCartesian(boolean precisely) {
		return precisely ? String.format("(%f, %f)", x(), y()) : String.format("(%.5f, %.5f)", x(), y());
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean precisely) {
		return isZero() ? "(zero)" : precisely
											 ? String.format("(θ=%f°, mag=%f)", angleDegrees(), magnitude())
											 : String.format("(θ=%.5f°, mag=%.5f)", angleDegrees(), magnitude());
	}
}
