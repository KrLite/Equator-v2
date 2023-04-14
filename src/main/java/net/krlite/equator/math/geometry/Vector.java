package net.krlite.equator.math.geometry;

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

	/**
	 * A vector with a {@code magnitude} of {@code 1} and an {@code angle} of {@code π},
	 * representing the unit vector {@code (-1, 0)}, which is the unit vector on the negative x-axis(left).
	 */
	public static final Vector NEGATIVE_UNIT_X = new Vector(Math.PI, 1);

	/**
	 * A vector with a {@code magnitude} of {@code 1} and an {@code angle} of {@code 3π / 2},
	 * representing the unit vector {@code (0, -1)}, which is tne unit vector on the negative y-axis(up).
	 */
	public static final Vector NEGATIVE_UNIT_Y = new Vector(-Math.PI / 2, 1);

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

	// Instance Variable Accessors

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

	// Instance Variable Mutators

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

	// Instance Mutators

	/**
	 * Scales the vector by the given scalars
	 * @param xScalar	The scalar to scale the x-coordinate by.
	 * @param yScalar	The scalar to scale the y-coordinate by.
	 * @return	A new vector scaled with the given scalars.
	 */
	public Vector scale(double xScalar, double yScalar) {
		return x(x() * xScalar).y(y() * yScalar);
	}

	/**
	 * Scales the vector by the given scalar.
	 * @param scalar	The scalar to scale the vector by.
	 * @return	A new vector scaled with the given scalar.
	 * @see #scale(double, double)
	 */
	public Vector scale(double scalar) {
		return scale(scalar, scalar);
	}

	// Instance Properties

	/**
	 * Checks if the vector is normalized. That is, its {@code magnitude} is 1.
	 * @return	{@code true} if the vector is normalized, {@code false} otherwise.
	 */
	public boolean isNormalized() {
		return Theory.looseEquals(magnitude(), 1);
	}

	/**
	 * Checks if the vector is zero. That is, its {@code magnitude} is 0. In this case, the angle is considered
	 * undefined.
	 * @return	{@code true} if the vector is zero, {@code false} otherwise.
	 */
	public boolean isZero() {
		return Theory.looseEquals(magnitude(), 0);
	}

	// Cross-Instance Properties

	/**
	 * Checks if the vector is parallel to another vector. That is, if the angle between the two vectors is 0 or π, or if
	 * either vector is zero.
	 * @param another	The other vector to check against.
	 * @return	{@code true} if the vector is parallel to the other vector, {@code false} otherwise.
	 */
	public boolean parallelTo(Vector another) {
		return isZero() || another.isZero() || Theory.looseEquals(theta(), another.theta());
	}

	/**
	 * Checks if the vector is perpendicular to another vector. That is, if the angle between the two vectors is π/2, or if
	 * either vector is zero.
	 * @param another	The other vector to check against.
	 * @return	{@code true} if the vector is perpendicular to the other vector, {@code false} otherwise.
	 */
	public boolean perpendicularTo(Vector another) {
		return isZero() || another.isZero() || Theory.looseEquals(theta(), another.theta() + Math.PI / 2);
	}

	/**
	 * Calculates the angle between the vector and another vector <b>in radians.</b> Note well that the angle can be negative.
	 * @param another	The other vector to calculate the angle between.
	 * @return	The angle between the two vectors <b>in radians.</b>
	 */
	public double between(Vector another) {
		return Math.acos(normalize().dot(another.normalize()));
	}

	/**
	 * Calculates the angle between the vector and another vector <b>in degrees.</b> Note well that the angle can be negative.
	 * @param another	The other vector to calculate the angle between.
	 * @return	The angle between the two vectors <b>in degrees.</b>
	 */
	public double betweenDegrees(Vector another) {
		return Math.toDegrees(between(another));
	}

	/**
	 * <h1>{@code a · b}</h1>
	 * Calculates the dot product of the vector and another vector.
	 * @param another	The other vector to calculate the dot product with.
	 * @return	The dot product of the two vectors.
	 */
	public double dot(Vector another) {
		return x() * another.x() + y() * another.y();
	}

	/**
	 * <h1>{@code a ⨯ b}</h1>
	 * Calculates the cross product of the vector and another vector.
	 * @param another	The other vector to calculate the cross product with.
	 * @return	The cross product of the two vectors.
	 */
	public double cross(Vector another) {
		return x() * another.y() - y() * another.x();
	}

	/**
	 * Calculates the distance between the vector and another vector.
	 * @param another	The other vector to calculate the distance to.
	 * @return	The distance between the two vectors.
	 */
	public double distanceTo(Vector another) {
		return subtract(another).magnitude();
	}

	/**
	 * Calculates the minimum magnitude between the vector and another vector.
	 * @param another	The other vector to calculate the minimum magnitude with.
	 * @return	The minimum magnitude between the two vectors.
	 */
	public double magnitudeMin(Vector another) {
		return Math.min(magnitude(), another.magnitude());
	}

	/**
	 * Calculates the maximum magnitude between the vector and another vector.
	 * @param another	The other vector to calculate the maximum magnitude with.
	 * @return	The maximum magnitude between the two vectors.
	 */
	public double magnitudeMax(Vector another) {
		return Math.max(magnitude(), another.magnitude());
	}

	// Instance Variants

	/**
	 * Normalizes the vector. That is, sets its {@code magnitude} to 1.
	 * @return	A new vector normalized.
	 */
	public Vector normalize() {
		return magnitude(1);
	}

	/**
	 * Projects the vector onto the x-axis.
	 * @return	A new vector projected onto the x-axis.
	 */
	public Vector projectOntoX() {
		return y(0);
	}

	/**
	 * Projects the vector onto the y-axis.
	 * @return	A new vector projected onto the y-axis.
	 */
	public Vector projectOntoY() {
		return x(0);
	}

	/**
	 * Gets the normal of the vector. That is, a vector with the same {@code magnitude} and an angle of
	 * {@code θ + π/2}.
	 * @return	A new vector normal to the vector.
	 */
	public Vector normal() {
		return new Vector(theta() + Math.PI / 2, magnitude());
	}

	/**
	 * Negates the vector by its x-component.
	 * @return	A new vector negated by its x-component.
	 */
	public Vector negateByX() {
		return x(-x());
	}

	/**
	 * Negates the vector by its y-component.
	 * @return	A new vector negated by its y-component.
	 */
	public Vector negateByY() {
		return y(-y());
	}

	/**
	 * Negates the vector by its angle.
	 * @return	A new vector negated by its angle.
	 */
	public Vector negate() {
		return theta(theta() + Math.PI);
	}

	/**
	 * Rotates the vector by an angle <b>in radians.</b>
	 * @param theta	The angle to rotate the vector by <b>in radians.</b>
	 * @return	A new vector rotated by the angle.
	 */
	public Vector rotate(double theta) {
		return theta(theta() + theta);
	}

	/**
	 * Rotates the vector by an angle <b>in degrees.</b>
	 * @param thetaDegrees	The angle to rotate the vector by <b>in degrees.</b>
	 * @return	A new vector rotated by the angle.
	 */
	public Vector rotateDegrees(double thetaDegrees) {
		return thetaDegrees(thetaDegrees() + thetaDegrees);
	}

	/**
	 * Rotates the vector around another vector by an angle <b>in radians.</b>
	 * @param center	The vector to rotate the vector around.
	 * @param theta	The angle to rotate the vector by <b>in radians.</b>
	 * @return	A new vector rotated by the angle around the center.
	 */
	public Vector rotateAround(Vector center, double theta) {
		return center.add(subtract(center).rotate(theta));
	}

	/**
	 * Rotates the vector around another vector by an angle <b>in degrees.</b>
	 * @param center	The vector to rotate the vector around.
	 * @param thetaDegrees	The angle to rotate the vector by <b>in degrees.</b>
	 * @return	A new vector rotated by the angle around the center.
	 */
	public Vector rotateAroundDegrees(Vector center, double thetaDegrees) {
		return center.add(subtract(center).rotateDegrees(thetaDegrees));
	}

	/**
	 * Ceils the magnitude of the vector to a certain value. That is, sets the magnitude to the minimum of the current
	 * magnitude and the given magnitude.
	 * @param magnitude	The magnitude to ceil the vector to.
	 * @return	A new vector with a magnitude of at most {@code magnitude}.
	 */
	public Vector ceil(double magnitude) {
		return magnitude(Math.min(magnitude(), magnitude));
	}

	/**
	 * Floors the magnitude of the vector to a certain value. That is, sets the magnitude to the maximum of the current
	 * magnitude and the given magnitude.
	 * @param magnitude	The magnitude to floor the vector to.
	 * @return	A new vector with a magnitude of at least {@code magnitude}.
	 */
	public Vector floor(double magnitude) {
		return magnitude(Math.max(magnitude(), magnitude));
	}

	/**
	 * Rounds the magnitude of the vector.
	 * @return	A new vector with a magnitude rounded to the nearest integer.
	 */
	public Vector round() {
		return magnitude(Math.round(magnitude()));
	}

	// Instance Operations

	/**
	 * Adds another vector to the vector.
	 * @param another	The vector to add.
	 * @return	A new vector with the sum of the two vectors.
	 */
	public Vector add(Vector another) {
		return add(another.x(), another.y());
	}

	/**
	 * Adds another vector to the vector.
	 * @param x	The x-component of the vector to add.
	 * @param y	The y-component of the vector to add.
	 * @return	A new vector with the sum of the two vectors.
	 * @see #add(Vector)
	 */
	public Vector add(double x, double y) {
		return fromCartesian(x() + x, y() + y);
	}

	/**
	 * Subtracts another vector from the vector.
	 * @param another	The vector to subtract.
	 * @return	A new vector with the difference of the two vectors.
	 */
	public Vector subtract(Vector another) {
		return subtract(another.x(), another.y());
	}

	/**
	 * Subtracts another vector from the vector.
	 * @param x	The x-component of the vector to subtract.
	 * @param y	The y-component of the vector to subtract.
	 * @return	A new vector with the difference of the two vectors.
	 * @see #subtract(Vector)
	 */
	public Vector subtract(double x, double y) {
		return fromCartesian(x() - x, y() - y);
	}

	/**
	 * Multiplies the vector by a multiplier.
	 * @param scalar	The multiplier to multiply the vector by.
	 * @return	A new vector with the product of the vector and the multiplier.
	 */
	public Vector multiply(double scalar) {
		return magnitude(magnitude() * scalar);
	}

	/**
	 * Divides the vector by a divisor.
	 * @param scalar	The divisor to divide the vector by.
	 * @return	A new vector with the quotient of the vector and the divisor.
	 */
	public Vector divide(double scalar) {
		return magnitude(magnitude() / scalar);
	}

	/**
	 * Gets a vector with the minimum x and y components of the vector and another vector. Note well that this may result
	 * in a vector completely different from the vector and the other vector.
	 * @param another	The vector to compare the vector to.
	 * @return	A new vector with the minimum x and y components of the vector and the other vector.
	 */
	public Vector min(Vector another) {
		return fromCartesian(Math.min(x(), another.x()), Math.min(y(), another.y()));
	}

	/**
	 * Gets a vector with the maximum x and y components of the vector and another vector. Note well that this may result
	 * in a vector completely different from the vector and the other vector.
	 * @param another	The vector to compare the vector to.
	 * @return	A new vector with the maximum x and y components of the vector and the other vector.
	 */
	public Vector max(Vector another) {
		return fromCartesian(Math.max(x(), another.x()), Math.max(y(), another.y()));
	}

	/**
	 * Projects the vector onto another vector. That is, gets the vector that is parallel to the other vector and has the
	 * same magnitude as the projection of the vector onto the other vector.
	 * @param another	The vector to project the vector onto.
	 * @return	A new vector with the projection of the vector onto the other vector.
	 */
	public Vector projectOnto(Vector another) {
		return another.multiply(dot(another) / another.dot(another));
	}

	/**
	 * Interpolates between the vector and another vector by a factor. That is, gets a vector that is {@code factor}
	 * times the distance between the vector and the other vector away from the vector.
	 * @param another	The vector to interpolate towards.
	 * @param factor	The factor to interpolate by.
	 * @return	A new vector that is {@code factor} times the distance between the vector and the other vector away
	 * 			from the vector.
	 */
	public Vector interpolate(Vector another, double factor) {
		return add(another.subtract(this).multiply(factor));
	}

	/**
	 * Reflects the vector about a normal vector. That is, gets the vector that is the reflection of the vector about the
	 * normal vector.
	 * @param normal	The normal vector to reflect the vector about.
	 * @return	A new vector that is the reflection of the vector about the normal vector.
	 */
	public Vector reflect(Vector normal) {
		return subtract(normal.multiply(2 * dot(normal)));
	}

	/**
	 * Gets the remainder of the vector's magnitude divided by another magnitude. That is, gets the vector with the same
	 * direction as the vector and a magnitude of the remainder of the vector's magnitude divided by the given magnitude.
	 * @param magnitude	The magnitude to divide the vector's magnitude by.
	 * @return	A new vector with the same direction as the vector and a magnitude of the remainder of the vector's
	 * 			magnitude divided by the given magnitude.
	 */
	public Vector remainder(double magnitude) {
		return magnitude(magnitude() % magnitude);
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

	/**
	 * Gets the string representation of the vector as a cartesian coordinate. For example, {@code (1, 1)}.
	 * @return	The string representation of the vector as a cartesian coordinate.
	 * @see #toStringAsCartesian(boolean)
	 */
	public String toStringAsCartesian() {
		return toStringAsCartesian(false);
	}

	/**
	 * Gets the string representation of the vector as a cartesian coordinate. For example, {@code (1, 1)}.
	 * @param precisely	Whether to use precise formatting. That is, whether to not limit the decimal places to 5.
	 * @return	The string representation of the vector as a cartesian coordinate.
	 */
	public String toStringAsCartesian(boolean precisely) {
		return precisely ? String.format("(%f, %f)", x(), y()) : String.format("(%.5f, %.5f)", x(), y());
	}

	/**
	 * Gets the string representation of the vector as a polar coordinate. For example, {@code (θ=45°, mag=1)}.
	 * @return	The string representation of the vector as a polar coordinate.
	 * @see #toString(boolean)
	 */
	@Override
	public String toString() {
		return toString(false);
	}

	/**
	 * Gets the string representation of the vector as a polar coordinate. For example, {@code (θ=45°, mag=1)}.
	 * @param precisely	Whether to use precise formatting. That is, whether to not limit the decimal places to 5.
	 * @return	The string representation of the vector as a polar coordinate.
	 */
	public String toString(boolean precisely) {
		return isZero() ? "(zero)" : precisely
											 ? String.format("(θ=%f°, mag=%f)", thetaDegrees(), magnitude())
											 : String.format("(θ=%.5f°, mag=%.5f)", thetaDegrees(), magnitude());
	}
}
