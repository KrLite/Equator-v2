package net.krlite.equator.math.geometry.flat;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.render.frame.Convertible;
import net.krlite.equator.render.frame.FrameInfo;

/**
 * <h1>Vector</h1>
 * Represents a vector in the {@link FrameInfo.Convertor Scaled Coordinate} and is
 * stored in polar form.
 * @param theta		The angle <b>in radians.</b>
 * @param magnitude	The magnitude, which is always positive.
 */
@net.krlite.equator.base.Math("2.4.0")
public record Vector(double theta, double magnitude) implements Convertible.Scaled<Vector> {
	// Constants

	/**
	 * A vector with a {@code magnitude} of {@code 0} and an {@code angle} of {@code 0}.
	 */
	public static final Vector ZERO = new Vector(0, 0);

	/**
	 * A vector with a {@code magnitude} of {@code 1} and an {@code angle} of {@code π / 4},
	 * representing the unit vector {@code (√2 / 2, √2 / 2)}.
	 */
	public static final Vector UNIT = new Vector(Math.PI / 4, 1);

	/**
	 * A vector with a {@code magnitude} of {@code √2} and an {@code angle} of {@code π / 4},
	 * representing the unit vector {@code (1, 1)}.
	 */
	public static final Vector UNIT_SQUARE = new Vector(Math.PI / 4, Math.sqrt(2));

	/**
	 * A vector with a {@code magnitude} of {@code 1} and an {@code angle} of {@code 0},
	 * representing the unit vector {@code (1, 0)}, which is the unit vector on the positive x-axis(right).
	 */
	public static final Vector UNIT_X = new Vector(0, 1);

	/**
	 * A vector with a {@code magnitude} of {@code 1} and an {@code angle} of {@code π / 2},
	 * representing the unit vector {@code (0, 1)}, which is the unit vector on the positive y-axis(down).
	 */
	public static final Vector UNIT_Y = new Vector(Math.PI / 2, 1);

	// Static Constructors

	/**
	 * Creates a vector in the {@link FrameInfo.Convertor Scaled Coordinate} from the
	 * given cartesian coordinate.
	 * @param x	{@code x} component.
	 * @param y	{@code y} component.
	 */
	public static Vector fromCartesian(double x, double y) {
		return new Vector(Math.atan2(y, x), Math.sqrt(x * x + y * y));
	}

	/**
	 * Creates a vector from the given polar coordinate <b>in degrees.</b>
	 * @param thetaDegrees	The angle <b>in degrees,</b> and is normalized to the range {@code [0, 360)}.
	 * @param magnitude		The magnitude.
	 * @return	A new vector with the given polar coordinate.
	 */
	public static Vector fromDegrees(double thetaDegrees, double magnitude) {
		return new Vector(Math.toRadians(thetaDegrees), magnitude);
	}

	// Constructors

	/**
	 * Creates a vector from the given polar coordinate <b>in radians.</b>
	 * @param theta		The angle <b>in radians,</b> and is normalized to the range {@code [0, 2π)}.
	 * @param magnitude	The magnitude.
	 */
	public Vector(double theta, double magnitude) {
		this.theta = theta % (Math.PI * 2) + (magnitude < 0 ? Math.PI : 0);
		this.magnitude = Math.abs(magnitude);
	}

	// Accessors

	/**
	 * @return	The angle <b>in radians,</b> and is normalized to the range {@code [0, 2π)}.
	 */
	@Override
	public double theta() {
		return theta % (Math.PI * 2);
	}

	/**
	 * @return	The angle <b>in degrees,</b> and is normalized to the range {@code [0, 360)}.
	 */
	public double thetaDegrees() {
		return Math.toDegrees(theta());
	}

	/**
	 * @return	The magnitude, which is always positive.
	 */
	@Override
	public double magnitude() {
		return magnitude;
	}

	/**
	 * @return	{@code x} component.
	 */
	public double x() {
		return Math.cos(theta()) * magnitude();
	}

	/**
	 * @return	{@code y} component.
	 */
	public double y() {
		return Math.sin(theta()) * magnitude();
	}

	// Mutators

	/**
	 * Mutates the angle.
	 * @param theta	The angle <b>in radians,</b> and is normalized to the range {@code [0, 2π)}.
	 * @return	A new vector with the given angle.
	 */
	public Vector theta(double theta) {
		return new Vector(theta, magnitude());
	}

	/**
	 * Mutates the angle.
	 * @param thetaDegrees	The angle <b>in degrees,</b> and is normalized to the range {@code [0, 360)}.
	 * @return	A new vector with the given angle.
	 */
	public Vector thetaDegrees(double thetaDegrees) {
		return theta(Math.toRadians(thetaDegrees));
	}

	/**
	 * Mutates the magnitude.
	 * @param magnitude	The magnitude.
	 * @return	A new vector with the given magnitude.
	 */
	public Vector magnitude(double magnitude) {
		return new Vector(theta(), magnitude);
	}

	/**
	 * Mutates the {@code x} component.
	 * @param x	{@code x} component.
	 * @return	A new vector with the given {@code x} component.
	 */
	public Vector x(double x) {
		return fromCartesian(x, y());
	}

	/**
	 * Mutates the {@code y} component.
	 * @param y	{@code y} component.
	 * @return	A new vector with the given {@code y} component.
	 */
	public Vector y(double y) {
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
	public boolean isParallelTo(Vector another) {
		return isZero() || another.isZero() || Theory.looseEquals(theta(), another.theta());
	}

	/**
	 * @param another	The other vector.
	 * @return	{@code true -} if the two vectors are perpendicular.
	 * <br />	{@code false -} otherwise.
	 */
	public boolean isPerpendicularTo(Vector another) {
		return isZero() || another.isZero() || Theory.looseEquals(theta(), another.theta() + Math.PI / 2);
	}

	/**
	 * @param another	The other vector.
	 * @return	The angle between the two vectors <b>in radians.</b>
	 */
	public double between(Vector another) {
		return Math.acos(normalize().dot(another.normalize()));
	}

	/**
	 * @param another	The other vector.
	 * @return	The angle between the two vectors <b>in degrees.</b>
	 * @see #between(Vector)
	 */
	public double betweenDegrees(Vector another) {
		return Math.toDegrees(between(another));
	}

	/**
	 * <h1>{@code a · b}</h1>
	 * @param another	The other vector.
	 * @return	The dot-product of the two vectors.
	 */
	public double dot(Vector another) {
		return x() * another.x() + y() * another.y();
	}

	/**
	 * <h1>{@code a × b}</h1>
	 * @param another	The other vector.
	 * @return	The cross-product of the two vectors.
	 */
	public double cross(Vector another) {
		return x() * another.y() - y() * another.x();
	}

	public double distanceTo(Vector another) {
		return subtract(another).magnitude();
	}

	/**
	 * @param another	The other vector.
	 * @return	The Manhattan distance between the two vectors. That is, the sum of the absolute differences of their
	 * 			{@code x} and {@code y} components.
	 */
	public double manhattanDistanceTo(Vector another) {
		return Math.abs(x() - another.x()) + Math.abs(y() - another.y());
	}

	/**
	 * @param another	The other vector.
	 * @return	The minimum of the two vectors' magnitudes.
	 */
	public double magnitudeMin(Vector another) {
		return Math.min(magnitude(), another.magnitude());
	}

	/**
	 * @param another	The other vector.
	 * @return	The maximum of the two vectors' magnitudes.
	 */
	public double magnitudeMax(Vector another) {
		return Math.max(magnitude(), another.magnitude());
	}

	// Operations

	/**
	 * Scales the vector by the given scalars.
	 * @param xScalar	The scalar by which to scale the {@code x} component.
	 * @param yScalar	The scalar by which to scale the {@code y} component.
	 * @return	A new vector scaled by the given scalars.
	 */
	public Vector scale(double xScalar, double yScalar) {
		return x(x() * xScalar).y(y() * yScalar);
	}

	/**
	 * Scales the vector by the given scalar.
	 * @param scalar	The scalar by which to scale the {@code x} and {@code y} components.
	 * @return	A new vector scaled by the given scalar.
	 * @see #scale(double, double)
	 */
	public Vector scale(double scalar) {
		return scale(scalar, scalar);
	}

	public Vector add(double x, double y) {
		return fromCartesian(x() + x, y() + y);
	}

	public Vector add(Vector another) {
		return add(another.x(), another.y());
	}

	public Vector subtract(double x, double y) {
		return fromCartesian(x() - x, y() - y);
	}

	public Vector subtract(Vector another) {
		return subtract(another.x(), another.y());
	}

	public Vector normalize() {
		return magnitude(1);
	}

	public Vector projectOnto(Vector another) {
		return another.scale(dot(another) / another.dot(another));
	}

	public Vector projectOntoX() {
		return y(0);
	}

	public Vector projectOntoY() {
		return x(0);
	}

	public Vector negate() {
		return theta(theta() + Math.PI);
	}

	public Vector negateByX() {
		return x(-x());
	}

	public Vector negateByY() {
		return y(-y());
	}

	public Vector rotate(double theta) {
		return theta(theta() + theta);
	}

	public Vector rotateDegrees(double thetaDegrees) {
		return thetaDegrees(thetaDegrees() + thetaDegrees);
	}

	public Vector rotateAround(Vector center, double theta) {
		return center.add(subtract(center).rotate(theta));
	}

	public Vector rotateAroundDegrees(Vector center, double thetaDegrees) {
		return center.add(subtract(center).rotateDegrees(thetaDegrees));
	}

	/**
	 * Gets a vector with the minimum x and y values of the two vectors. Note well that the vector may not be the same as
	 * either of the two vectors.
	 * @param another	The other vector.
	 * @return	A new vector with the minimum x and y values of the two vectors.
	 */
	public Vector min(Vector another) {
		return fromCartesian(Math.min(x(), another.x()), Math.min(y(), another.y()));
	}

	/**
	 * Gets a vector with the maximum x and y values of the two vectors. Note well that the vector may not be the same as
	 * either of the two vectors.
	 * @param another	The other vector.
	 * @return	A new vector with the maximum x and y values of the two vectors.
	 */
	public Vector max(Vector another) {
		return fromCartesian(Math.max(x(), another.x()), Math.max(y(), another.y()));
	}

	public Vector clamp(Vector min, Vector max) {
		return min.max(this).min(max);
	}

	/**
	 * Gets the vector with the minimum magnitude. If the magnitudes are equal, this vector is returned.
	 * @param another	The other vector.
	 * @return	The vector with the minimum magnitude.
	 */
	public Vector minByMagnitude(Vector another) {
		return magnitude() <= another.magnitude() ? this : another;
	}

	/**
	 * Gets the vector with the maximum magnitude. If the magnitudes are equal, this vector is returned.
	 * @param another	The other vector.
	 * @return	The vector with the maximum magnitude.
	 */
	public Vector maxByMagnitude(Vector another) {
		return magnitude() >= another.magnitude() ? this : another;
	}

	public Vector normal() {
		return new Vector(theta() + Math.PI / 2, magnitude());
	}

	public Vector reflect(Vector normal) {
		return subtract(normal.scale(2 * dot(normal)));
	}

	public Vector interpolate(Vector another, double factor) {
		return add(another.subtract(this).scale(factor));
	}

	// Interface Implementations

	/**
	 * @return	A new vector fitted to the {@link FrameInfo.Convertor Screen Coordinate}.
	 */
	public Vector fitToScreen() {
		return FrameInfo.Convertor.scaledToScreen(this);
	}

	/**
	 * @return	A new vector fitted to the {@link FrameInfo.Convertor OpenGL Coordinate}.
	 */
	public Vector fitToOpenGL() {
		return FrameInfo.Convertor.scaledToOpenGL(this);
	}

	/**
	 * @return	A new vector fitted from the {@link FrameInfo.Convertor Screen Coordinate}.
	 */
	public Vector fitFromScreen() {
		return FrameInfo.Convertor.screenToScaled(this);
	}

	/**
	 * @return	A new vector fitted from the {@link FrameInfo.Convertor OpenGL Coordinate}.
	 */
	public Vector fitFromOpenGL() {
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
											 ? String.format("(θ=%f°, mag=%f)", thetaDegrees(), magnitude())
											 : String.format("(θ=%.5f°, mag=%.5f)", thetaDegrees(), magnitude());
	}
}
