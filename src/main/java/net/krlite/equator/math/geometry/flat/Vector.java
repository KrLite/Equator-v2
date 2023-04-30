package net.krlite.equator.math.geometry.flat;

import net.krlite.equator.render.frame.Convertible;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.math.algebra.Theory;

/**
 * <h1>Vector</h1>
 * Represents a vector in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate} and is
 * stored in polar form.
 * @param theta		The angle of the vector <b>in radians.</b>
 * @param magnitude	The magnitude of the vector, always positive.
 */
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
	 * Creates a vector from the given cartesian coordinate in the
	 * {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
	 * @param x	The x-coordinate of the vector.
	 * @param y	The y-coordinate of the vector.
	 */
	public static Vector fromCartesian(double x, double y) {
		return new Vector(Math.atan2(y, x), Math.sqrt(x * x + y * y));
	}

	/**
	 * Creates a vector from the given polar coordinate <b>in degrees.</b>
	 * @param thetaDegrees	The angle of the vector <b>in degrees,</b> and is normalized to the range {@code [0, 360)}.
	 * @param magnitude		The magnitude of the vector.
	 * @return	A new vector with the given polar coordinate.
	 */
	public static Vector fromDegrees(double thetaDegrees, double magnitude) {
		return new Vector(Math.toRadians(thetaDegrees), magnitude);
	}

	// Constructors

	/**
	 * Creates a vector from the given polar coordinate <b>in radians.</b>
	 * @param theta		The angle of the vector <b>in radians,</b> and is normalized to the range {@code [0, 2π)}.
	 * @param magnitude	The magnitude of the vector.
	 */
	public Vector(double theta, double magnitude) {
		this.theta = theta % (Math.PI * 2) + (magnitude < 0 ? Math.PI : 0);
		this.magnitude = Math.abs(magnitude);
	}

	// Accessors

	/**
	 * Gets the angle of the vector <b>in radians.</b>
	 * @return	The angle of the vector <b>in radians,</b> and is normalized to the range {@code [0, 2π)}.
	 */
	@Override
	public double theta() {
		return theta % (Math.PI * 2);
	}

	/**
	 * Gets the angle of the vector <b>in degrees.</b>
	 * @return	The angle of the vector <b>in degrees,</b> and is normalized to the range {@code [0, 360)}.
	 */
	public double thetaDegrees() {
		return Math.toDegrees(theta());
	}

	/**
	 * Gets the magnitude of the vector.
	 * @return	The magnitude of the vector, always positive.
	 */
	@Override
	public double magnitude() {
		return magnitude;
	}

	/**
	 * Gets the x-coordinate of the vector in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
	 * @return	The x-coordinate of the vector.
	 */
	public double x() {
		return Math.cos(theta()) * magnitude();
	}

	/**
	 * Gets the y-coordinate of the vector in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
	 * @return	The y-coordinate of the vector.
	 */
	public double y() {
		return Math.sin(theta()) * magnitude();
	}

	// Mutators

	/**
	 * Mutates the angle of the vector.
	 * @param theta	The angle of the vector <b>in radians,</b> and is normalized to the range {@code [0, 2π)}.
	 * @return	A new vector with the given angle.
	 */
	public Vector theta(double theta) {
		return new Vector(theta, magnitude());
	}

	/**
	 * Mutates the angle of the vector.
	 * @param thetaDegrees	The angle of the vector <b>in degrees,</b> and is normalized to the range {@code [0, 360)}.
	 * @return	A new vector with the given angle.
	 */
	public Vector thetaDegrees(double thetaDegrees) {
		return theta(Math.toRadians(thetaDegrees));
	}

	/**
	 * Mutates the magnitude of the vector.
	 * @param magnitude	The magnitude of the vector.
	 * @return	A new vector with the given magnitude.
	 */
	public Vector magnitude(double magnitude) {
		return new Vector(theta(), magnitude);
	}

	/**
	 * Mutates the x-coordinate of the vector.
	 * @param x	The x-coordinate of the vector in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
	 * @return	A new vector with the given x-coordinate.
	 */
	public Vector x(double x) {
		return fromCartesian(x, y());
	}

	/**
	 * Mutates the y-coordinate of the vector.
	 * @param y	The y-coordinate of the vector in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate}.
	 * @return	A new vector with the given y-coordinate.
	 */
	public Vector y(double y) {
		return fromCartesian(x(), y);
	}

	// Properties

	public boolean isNormalized() {
		return Theory.looseEquals(magnitude(), 1);
	}

	public boolean isZero() {
		return Theory.isZero(magnitude());
	}

	public boolean isParallelTo(Vector another) {
		return isZero() || another.isZero() || Theory.looseEquals(theta(), another.theta());
	}

	public boolean isPerpendicularTo(Vector another) {
		return isZero() || another.isZero() || Theory.looseEquals(theta(), another.theta() + Math.PI / 2);
	}

	public double between(Vector another) {
		return Math.acos(normalize().dot(another.normalize()));
	}

	public double betweenDegrees(Vector another) {
		return Math.toDegrees(between(another));
	}

	public double dot(Vector another) {
		return x() * another.x() + y() * another.y();
	}

	public double cross(Vector another) {
		return x() * another.y() - y() * another.x();
	}

	public double distanceTo(Vector another) {
		return subtract(another).magnitude();
	}

	public double manhattanDistanceTo(Vector another) {
		return Math.abs(x() - another.x()) + Math.abs(y() - another.y());
	}

	public double magnitudeMin(Vector another) {
		return Math.min(magnitude(), another.magnitude());
	}

	public double magnitudeMax(Vector another) {
		return Math.max(magnitude(), another.magnitude());
	}

	// Operations

	public Vector scale(double xScalar, double yScalar) {
		return x(x() * xScalar).y(y() * yScalar);
	}

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
	 * Fits the vector to the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Screen Coordinate}.
	 * @return	A new vector fitted to the screen coordinate.
	 * @see Convertible#fitToScreen()
	 */
	public Vector fitToScreen() {
		return FrameInfo.Convertor.scaledToScreen(this);
	}

	/**
	 * Fits the vector to the {@link net.krlite.equator.render.frame.FrameInfo.Convertor OpenGL Coordinate}.
	 * @return	A new vector fitted to the OpenGL coordinate.
	 * @see Convertible#fitToOpenGL()
	 */
	public Vector fitToOpenGL() {
		return FrameInfo.Convertor.scaledToOpenGL(this);
	}

	/**
	 * Fits the vector from the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Screen Coordinate}.
	 * @return	A new vector fitted from the screen coordinate.
	 * @see Convertible#fitFromScreen()
	 */
	public Vector fitFromScreen() {
		return FrameInfo.Convertor.screenToScaled(this);
	}

	/**
	 * Fits the vector from the {@link net.krlite.equator.render.frame.FrameInfo.Convertor OpenGL Coordinate}.
	 * @return	A new vector fitted from the OpenGL coordinate.
	 * @see Convertible#fitFromOpenGL()
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
