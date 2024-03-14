package net.krlite.equator.math.geometry.flat;

import net.krlite.equator.render.frame.Convertible;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.math.algebra.Theory;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

/**
 * <h1>Vector</h1>
 * Represents a vector in the {@link net.krlite.equator.render.frame.FrameInfo.Convertor Scaled Coordinate} and is
 * stored in polar form.
 * @param angle		the angle <b>in radians.</b>
 * @param magnitude	the magnitude, which is always positive.
 */
public record Vector(double angle, double magnitude) implements Convertible.Scaled<Vector> {
	// Constants

	public static final Vector ZERO = new Vector(0, 0);

	public static final Vector UNIT = new Vector(Math.PI / 4, Math.sqrt(2));

	public static final Vector UNIT_X = new Vector(0, 1);

	public static final Vector UNIT_Y = new Vector(Math.PI / 2, 1);

	// Static Constructors

	public static Vector fromCartesian(double x, double y) {
		return new Vector(Math.atan2(y, x), Math.sqrt(x * x + y * y));
	}

	public static Vector fromDegrees(double angle, double magnitude) {
		return new Vector(Math.toRadians(angle), magnitude);
	}

	// Constructors

	public Vector(double angle, double magnitude) {
		this.angle = angle % (2 * Math.PI) + (magnitude < 0 ? Math.PI : 0);
		this.magnitude = Math.abs(magnitude);
	}

	// Accessors

	@Override
	public double angle() { return angle % (2 * Math.PI); }

	public double angleDegrees() { return Math.toDegrees(angle()); }

	@Override
	public double magnitude() { return magnitude; }

	public double x() { return Math.cos(angle()) * magnitude(); }

	public double y() { return Math.sin(angle()) * magnitude(); }

	// Mutators

	public Vector angle(double angle) {
		return new Vector(angle, magnitude());
	}

	public Vector angleDegrees(double angleDegrees) {
		return angle(Math.toRadians(angleDegrees));
	}

	public Vector magnitude(double magnitude) {
		return new Vector(angle(), magnitude);
	}

	public Vector x(double x) {
		return fromCartesian(x, y());
	}

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
	 * @param another	the other vector.
	 * @return	{@code true -} if the two vectors are parallel.
	 * <br />	{@code false -} otherwise.
	 */
	public boolean isParallelTo(Vector another) {
		return isZero() || another.isZero() || Theory.looseEquals(angle(), another.angle());
	}

	/**
	 * @param another	the other vector.
	 * @return	{@code true -} if the two vectors are perpendicular.
	 * <br />	{@code false -} otherwise.
	 */
	public boolean isPerpendicularTo(Vector another) {
		return isZero() || another.isZero() || Theory.looseEquals(angle(), another.angle() + Math.PI / 2);
	}

	/**
	 * @param another	the other vector.
	 * @return	the angle between the two vectors <b>in radians.</b>
	 */
	public double angleTo(Vector another) {
		return Math.acos(normalize().dot(another.normalize()));
	}

	/**
	 * @param another	the other vector.
	 * @return	the angle between the two vectors <b>in degrees.</b>
	 * @see #angleTo(Vector)
	 */
	public double angleDegreesTo(Vector another) {
		return Math.toDegrees(angleTo(another));
	}

	/**
	 * <h1>{@code a · b}</h1>
	 * @param another	the other vector.
	 * @return	the dot-product of the two vectors.
	 */
	public double dot(Vector another) {
		return x() * another.x() + y() * another.y();
	}

	/**
	 * <h1>{@code a × b}</h1>
	 * @param another	the other vector.
	 * @return	the cross-product of the two vectors.
	 */
	public double cross(Vector another) {
		return x() * another.y() - y() * another.x();
	}

	public double distanceTo(Vector another) {
		return subtract(another).magnitude();
	}

	/**
	 * @param another	the other vector.
	 * @return	the Manhattan distance between the two vectors. That is, the sum of the absolute differences of their
	 * 			{@code x} and {@code y} components.
	 */
	public double manhattanDistanceTo(Vector another) {
		return Math.abs(x() - another.x()) + Math.abs(y() - another.y());
	}

	/**
	 * @param another	The other vector.
	 * @return	the minimum of the two vectors' magnitudes.
	 */
	public double magnitudeMin(Vector another) {
		return Math.min(magnitude(), another.magnitude());
	}

	/**
	 * @param another	the other vector.
	 * @return	the maximum of the two vectors' magnitudes.
	 */
	public double magnitudeMax(Vector another) {
		return Math.max(magnitude(), another.magnitude());
	}

	// Operations

	/**
	 * Scales the vector by the given scalars.
	 * @param xScalar	the scalar by which to scale the {@code x} component.
	 * @param yScalar	the scalar by which to scale the {@code y} component.
	 * @return	a new vector scaled by the given scalars.
	 */
	public Vector scale(double xScalar, double yScalar) {
		return x(x() * xScalar).y(y() * yScalar);
	}

	/**
	 * Scales the vector by the given scalar.
	 * @param scalar	the scalar by which to scale the {@code x} and {@code y} components.
	 * @return	a new vector scaled by the given scalar.
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
		return angle(angle() + Math.PI);
	}

	public Vector negateByX() {
		return x(-x());
	}

	public Vector negateByY() {
		return y(-y());
	}

	public Vector rotate(double angle) {
		return angle(angle() + angle);
	}

	public Vector rotateDegrees(double angleDegrees) {
		return angleDegrees(angleDegrees() + angleDegrees);
	}

	public Vector rotateAround(Vector center, double angle) {
		return center.add(subtract(center).rotate(angle));
	}

	public Vector rotateAroundDegrees(Vector center, double angleDegrees) {
		return center.add(subtract(center).rotateDegrees(angleDegrees));
	}

	/**
	 * Gets a vector with the minimum x and y values of the two vectors. Note well that the vector may not be the same as
	 * either of the two vectors.
	 * @param another	the other vector.
	 * @return	a new vector with the minimum x and y values of the two vectors.
	 */
	public Vector min(Vector another) {
		return fromCartesian(Math.min(x(), another.x()), Math.min(y(), another.y()));
	}

	/**
	 * Gets a vector with the maximum x and y values of the two vectors. Note well that the vector may not be the same as
	 * either of the two vectors.
	 * @param another	the other vector.
	 * @return	a new vector with the maximum x and y values of the two vectors.
	 */
	public Vector max(Vector another) {
		return fromCartesian(Math.max(x(), another.x()), Math.max(y(), another.y()));
	}

	public Vector clamp(Vector min, Vector max) {
		return min.max(this).min(max);
	}

	/**
	 * Gets the vector with the minimum magnitude. If the magnitudes are equal, this vector is returned.
	 * @param another	the other vector.
	 * @return	the vector with the minimum magnitude.
	 */
	public Vector minByMagnitude(Vector another) {
		return magnitude() <= another.magnitude() ? this : another;
	}

	/**
	 * Gets the vector with the maximum magnitude. If the magnitudes are equal, this vector is returned.
	 * @param another	the other vector.
	 * @return	the vector with the maximum magnitude.
	 */
	public Vector maxByMagnitude(Vector another) {
		return magnitude() >= another.magnitude() ? this : another;
	}

	public Vector normal() {
		return new Vector(angle() + Math.PI / 2, magnitude());
	}

	public Vector reflect(Vector normal) {
		return subtract(normal.scale(2 * dot(normal)));
	}

	public Vector interpolate(Vector another, double factor) {
		return add(another.subtract(this).scale(factor));
	}

	public Vector sphericalInterpolate(Vector another, double factor) {
		return new Vector(
			Theory.lerp(angle(), another.angle(), factor),
			Theory.lerp(magnitude(), another.magnitude(), factor)
		);
	}

	// Interface Implementations

	/**
	 * @return	a new vector fitted to the {@link FrameInfo.Convertor Screen Coordinate}.
	 */
	public Vector fitToScreen() {
		return FrameInfo.Convertor.scaledToScreen(this);
	}

	/**
	 * @return	a new vector fitted to the {@link FrameInfo.Convertor OpenGL Coordinate}.
	 */
	public Vector fitToOpenGL() {
		return FrameInfo.Convertor.scaledToOpenGL(this);
	}

	/**
	 * @return	a new vector fitted from the {@link FrameInfo.Convertor Screen Coordinate}.
	 */
	public Vector fitFromScreen() {
		return FrameInfo.Convertor.screenToScaled(this);
	}

	/**
	 * @return	a new vector fitted from the {@link FrameInfo.Convertor OpenGL Coordinate}.
	 */
	public Vector fitFromOpenGL() {
		return FrameInfo.Convertor.openGLToScaled(this);
	}

	// Links

	public Vec2f toVec2f() {
		return new Vec2f((float) x(), (float) y());
	}

	// Object Methods

	public String toStringAsCartesian() {
		return toStringAsCartesian(false);
	}

	public String toStringAsCartesian(boolean precisely) {
		return getClass().getSimpleName() + (precisely ? String.format("(%f, %f)", x(), y()) : String.format("(%.5f, %.5f)", x(), y()));
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean precisely) {
		return getClass().getSimpleName()
					   + (isZero()
								  ? "(zero)"
								  : precisely
											? String.format("(θ=%f°, mag=%f)", angleDegrees(), magnitude())
											: String.format("(θ=%.5f°, mag=%.5f)", angleDegrees(), magnitude()));
	}
}
