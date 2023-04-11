package net.krlite.equator.math.geometry;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.render.frame.Convertible;
import net.krlite.equator.render.frame.FrameInfo;

/**
 * Represents a vector in the {@link FrameInfo.Convertor Scaled Coordinate}.
 * @see FrameInfo.Convertor
 * @param theta		The angle of the vector, in radians.
 * @param magnitude	The magnitude of the vector.
 */
@net.krlite.equator.base.Math("2.2.1")
public record Vector(double theta, double magnitude) implements Convertible.Scaled<Vector> {
	/**
	 * A vector with a magnitude of <code>0</code> and an angle of <code>0</code>.
	 */
	public static final Vector ZERO = new Vector(0, 0);

	/**
	 * A vector with a magnitude of <code>1</code> and an angle of <code>π / 4</code>, representing the unit vector <code>(√2 / 2, √2 / 2)</code>.
	 */
	public static final Vector UNIT = new Vector(Math.PI / 4, 1);

	/**
	 * A vector with a magnitude of <code>√2</code> and an angle of <code>π / 4</code>, representing the unit vector <code>(1, 1)</code>.
	 */
	public static final Vector UNIT_SQUARE = new Vector(Math.PI / 4, Math.sqrt(2));

	/**
	 * A vector with a magnitude of <code>1</code> and an angle of <code>0</code>, representing the unit vector <code>(1, 0)</code>
	 * which is the positive unit on the X axis.
	 */
	public static final Vector UNIT_X = new Vector(0, 1);

	/**
	 * A vector with a magnitude of <code>1</code> and an angle of <code>π / 2</code>, representing the unit vector <code>(0, 1)</code>
	 * which is the positive unit on the Y axis.
	 */
	public static final Vector UNIT_Y = new Vector(Math.PI / 2, 1);

	/**
	 * A vector with a magnitude of <code>1</code> and an angle of <code>π</code>, representing the unit vector <code>(-1, 0)</code>
	 * which is the negative unit on the X axis.
	 */
	public static final Vector NEGATIVE_UNIT_X = new Vector(Math.PI, 1);

	/**
	 * A vector with a magnitude of <code>1</code> and an angle of <code>3π / 2</code>, representing the unit vector <code>(0, -1)</code>
	 * which is tne negative unit on the Y axis.
	 */
	public static final Vector NEGATIVE_UNIT_Y = new Vector(-Math.PI / 2, 1);



	public static Vector fromCartesian(double x, double y) {
		return new Vector(Math.atan2(y, x), Math.sqrt(x * x + y * y));
	}

	public static Vector fromDegrees(double thetaDegrees, double magnitude) {
		return new Vector(Math.toRadians(thetaDegrees), magnitude);
	}



	public Vector(double theta, double magnitude) {
		this.theta = theta % (Math.PI * 2) + (magnitude < 0 ? Math.PI : 0);
		this.magnitude = Math.abs(magnitude);
	}



	@Override
	public double theta() {
		return theta % (Math.PI * 2);
	}

	public double thetaDegrees() {
		return Math.toDegrees(theta());
	}

	// magnitude() is a record method

	public double x() {
		return Math.cos(theta()) * magnitude();
	}

	public double y() {
		return Math.sin(theta()) * magnitude();
	}



	public Vector theta(double theta) {
		return new Vector(theta, magnitude());
	}

	public Vector thetaDegrees(double thetaDegrees) {
		return theta(Math.toRadians(thetaDegrees));
	}

	public Vector magnitude(double magnitude) {
		return new Vector(theta(), magnitude);
	}

	public Vector x(double x) {
		return fromCartesian(x, y());
	}

	public Vector y(double y) {
		return fromCartesian(x(), y);
	}

	public Vector scale(double xScalar, double yScalar) {
		return x(x() * xScalar).y(y() * yScalar);
	}



	public boolean isNormalized() {
		return Theory.looseEquals(magnitude(), 1);
	}

	public boolean isZero() {
		return Theory.looseEquals(magnitude(), 0);
	}



	public boolean parallelTo(Vector another) {
		return Theory.looseEquals(theta(), another.theta()) || isZero();
	}

	public boolean perpendicularTo(Vector another) {
		return Theory.looseEquals(theta(), another.theta() + Math.PI / 2) || isZero();
	}



	public double radiansBetween(Vector another) {
		return Math.acos(normalize().dot(another.normalize()));
	}

	public double degreesBetween(Vector another) {
		return Math.toDegrees(radiansBetween(another));
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

	public double magnitudeMin(Vector another) {
		return Math.min(magnitude(), another.magnitude());
	}

	public double magnitudeMax(Vector another) {
		return Math.max(magnitude(), another.magnitude());
	}



	public Vector normalize() {
		return magnitude(1);
	}

	public Vector projectOntoX() {
		return y(0);
	}

	public Vector projectOntoY() {
		return x(0);
	}

	public Vector normal() {
		return new Vector(theta() + Math.PI / 2, magnitude());
	}

	public Vector negateByX() {
		return x(-x());
	}

	public Vector negateByY() {
		return y(-y());
	}

	public Vector negate() {
		return theta(theta() + Math.PI);
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

	public Vector ceil(double magnitude) {
		return magnitude(Math.min(magnitude(), magnitude));
	}

	public Vector floor(double magnitude) {
		return magnitude(Math.max(magnitude(), magnitude));
	}

	public Vector round() {
		return magnitude(Math.round(magnitude()));
	}



	public Vector add(Vector another) {
		return add(another.x(), another.y());
	}

	public Vector add(double x, double y) {
		return fromCartesian(x() + x, y() + y);
	}

	public Vector subtract(Vector another) {
		return subtract(another.x(), another.y());
	}

	public Vector subtract(double x, double y) {
		return fromCartesian(x() - x, y() - y);
	}

	public Vector multiply(double scalar) {
		return magnitude(magnitude() * scalar);
	}

	public Vector divide(double scalar) {
		return magnitude(magnitude() / scalar);
	}

	public Vector min(Vector another) {
		return fromCartesian(Math.min(x(), another.x()), Math.min(y(), another.y()));
	}

	public Vector max(Vector another) {
		return fromCartesian(Math.max(x(), another.x()), Math.max(y(), another.y()));
	}



	public Vector projectOnto(Vector another) {
		return another.multiply(dot(another) / another.dot(another));
	}

	public Vector interpolate(Vector another, double lambda) {
		return add(another.subtract(this).multiply(lambda));
	}

	public Vector reflect(Vector normal) {
		return subtract(normal.multiply(2 * dot(normal)));
	}

	public Vector remainder(double magnitude) {
		return magnitude(magnitude() % magnitude);
	}



	public Vector fitToScreen() {
		return FrameInfo.Convertor.scaledToScreen(this);
	}

	public Vector fitFromScreen() {
		return FrameInfo.Convertor.screenToScaled(this);
	}

	public Vector fitToOpenGL() {
		return FrameInfo.Convertor.scaledToOpenGL(this);
	}

	public Vector fitFromOpenGL() {
		return FrameInfo.Convertor.openGLToScaled(this);
	}



	public String toStringAsCartesian() {
		return String.format("(%.5f, %.5f)", x(), y());
	}

	public String toStringAsCartesian(boolean precisely) {
		return !precisely ? toStringAsCartesian() : String.format("(%f, %f)", x(), y());
	}

	@Override
	public String toString() {
		return String.format("(θ=%.5f°, mag=%.5f)", thetaDegrees(), magnitude());
	}

	public String toString(boolean precisely) {
		return !precisely ? toString() : String.format("(θ=%f°, mag=%f)", thetaDegrees(), magnitude());
	}
}
