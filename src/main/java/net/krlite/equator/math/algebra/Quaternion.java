package net.krlite.equator.math.algebra;

import net.krlite.equator.math.geometry.volume.Pos;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public record Quaternion(double x, double y, double z, double w) {
	// Static Constructors

	public static Quaternion from(Quaternionfc quaternion) {
		return new Quaternion(quaternion.x(), quaternion.y(), quaternion.z(), quaternion.w());
	}

	public static Quaternion from(Quaterniondc quaternion) {
		return new Quaternion(quaternion.x(), quaternion.y(), quaternion.z(), quaternion.w());
	}

	public static Quaternion fromAxis(Pos axis, double angle) {
		double halfAngle = angle / 2;
		double sin = Math.sin(halfAngle);
		return new Quaternion(
				axis.x() * sin,
				axis.y() * sin,
				axis.z() * sin,
				Math.cos(halfAngle)
		);
	}

	public static Quaternion fromAxisDegrees(Pos axis, double angleDegrees) {
		return fromAxis(axis, Math.toRadians(angleDegrees));
	}

	public static Quaternion rotationXYZ(double x, double y, double z) {
		double halfX = x / 2, halfY = y / 2, halfZ = z / 2;
		double sinX = Math.sin(halfX), sinY = Math.sin(halfY), sinZ = Math.sin(halfZ);
		double cosX = Math.cos(halfX), cosY = Math.cos(halfY), cosZ = Math.cos(halfZ);
		return new Quaternion(
				sinX * cosY * cosZ - cosX * sinY * sinZ,
				cosX * sinY * cosZ + sinX * cosY * sinZ,
				cosX * cosY * sinZ - sinX * sinY * cosZ,
				cosX * cosY * cosZ + sinX * sinY * sinZ
		);
	}

	public static Quaternion rotationXYZDegrees(double x, double y, double z) {
		return rotationXYZ(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
	}

	public static Quaternion rotationZYX(double x, double y, double z) {
		double halfX = x / 2, halfY = y / 2, halfZ = z / 2;
		double sinX = Math.sin(halfX), sinY = Math.sin(halfY), sinZ = Math.sin(halfZ);
		double cosX = Math.cos(halfX), cosY = Math.cos(halfY), cosZ = Math.cos(halfZ);
		return new Quaternion(
				sinX * cosY * cosZ + cosX * sinY * sinZ,
				cosX * sinY * cosZ - sinX * cosY * sinZ,
				cosX * cosY * sinZ + sinX * sinY * cosZ,
				cosX * cosY * cosZ - sinX * sinY * sinZ
		);
	}

	public static Quaternion rotationZYXDegrees(double x, double y, double z) {
		return rotationZYX(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
	}

	public static Quaternion rotationYXZ(double x, double y, double z) {
		double halfX = x / 2, halfY = y / 2, halfZ = z / 2;
		double sinX = Math.sin(halfX), sinY = Math.sin(halfY), sinZ = Math.sin(halfZ);
		double cosX = Math.cos(halfX), cosY = Math.cos(halfY), cosZ = Math.cos(halfZ);
		return new Quaternion(
				sinX * cosY * cosZ + cosX * sinY * sinZ,
				cosX * sinY * cosZ - sinX * cosY * sinZ,
				cosX * cosY * sinZ - sinX * sinY * cosZ,
				cosX * cosY * cosZ + sinX * sinY * sinZ
		);
	}

	public static Quaternion rotationYXZDegrees(double x, double y, double z) {
		return rotationYXZ(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
	}

	public static Quaternion rotationX(double angle) {
		double halfAngle = angle / 2;
		return new Quaternion(
				Math.sin(halfAngle),
				0,
				0,
				Math.cos(halfAngle)
		);
	}

	public static Quaternion rotationXDegrees(double angleDegrees) {
		return rotationX(Math.toRadians(angleDegrees));
	}

	public static Quaternion rotationY(double angle) {
		double halfAngle = angle / 2;
		return new Quaternion(
				0,
				Math.sin(halfAngle),
				0,
				Math.cos(halfAngle)
		);
	}

	public static Quaternion rotationYDegrees(double angleDegrees) {
		return rotationY(Math.toRadians(angleDegrees));
	}

	public static Quaternion rotationZ(double angle) {
		double halfAngle = angle / 2;
		return new Quaternion(
				0,
				0,
				Math.sin(halfAngle),
				Math.cos(halfAngle)
		);
	}

	public static Quaternion rotationZDegrees(double angleDegrees) {
		return rotationZ(Math.toRadians(angleDegrees));
	}

	// Constructors

	public Quaternion(double x, double y, double z, double w) {
		this.x = x; this.y = y; this.z = z; this.w = w;
	}

	public Quaternion() {
		this(0, 0, 0, 1);
	}

	// Accessors

	@Override
	public double x() { return x; }

	@Override
	public double y() { return y; }

	@Override
	public double z() { return z; }

	@Override
	public double w() { return w; }

	public double angle() {
		return Math.acos(w()) * 2;
	}

	public double magnitude() {
		return Math.sqrt(x() * x() + y() * y() + z() * z() + w() * w());
	}

	// Mutators

	public Quaternion x(double x) {
		return new Quaternion(x, y(), z(), w());
	}

	public Quaternion y(double y) {
		return new Quaternion(x(), y, z(), w());
	}

	public Quaternion z(double z) {
		return new Quaternion(x(), y(), z, w());
	}

	public Quaternion w(double w) {
		return new Quaternion(x(), y(), z(), w);
	}

	public Quaternion angle(double angle) {
		double halfAngle = angle / 2;
		double sin = Math.sin(halfAngle);
		return new Quaternion(
				x() * sin,
				y() * sin,
				z() * sin,
				Math.cos(halfAngle)
		);
	}

	public Quaternion magnitude(double magnitude) {
		double scalar = magnitude / magnitude();
		return new Quaternion(x() * scalar, y() * scalar, z() * scalar, w() * scalar);
	}

	// Properties

	public boolean isZero() {
		return Theory.isZero(x()) && Theory.isZero(y()) && Theory.isZero(z()) && Theory.isZero(w());
	}

	public double dot(Quaternion another) {
		return x() * another.x() + y() * another.y() + z() * another.z() + w() * another.w();
	}

	public Quaternion normalize() {
		double magnitude = Math.sqrt(x() * x() + y() * y() + z() * z() + w() * w());
		return new Quaternion(x() / magnitude, y() / magnitude, z() / magnitude, w() / magnitude);
	}

	public Quaternion add(Quaternion another) {
		return new Quaternion(x() + another.x(), y() + another.y(), z() + another.z(), w() + another.w());
	}

	public Quaternion add(double x, double y, double z, double w) {
		return add(new Quaternion(x, y, z, w));
	}

	public Quaternion scale(double xScalar, double yScalar, double zScalar, double wScalar) {
		return new Quaternion(x() * xScalar, y() * yScalar, z() * zScalar, w() * wScalar);
	}

	public Quaternion scale(double scalar) {
		return scale(scalar, scalar, scalar, scalar);
	}

	public Quaternion scale(Pos iScalar) {
		return scale(iScalar.x(), iScalar.y(), iScalar.z(), 1);
	}

	public Quaternion multiply(Quaternion another) {
		return new Quaternion(
				w() * another.x() + x() * another.w() + y() * another.z() - z() * another.y(),
				w() * another.y() - x() * another.z() + y() * another.w() + z() * another.x(),
				w() * another.z() + x() * another.y() - y() * another.x() + z() * another.w(),
				w() * another.w() - x() * another.x() - y() * another.y() - z() * another.z()
		);
	}

	public Quaternion multiply(double x, double y, double z, double w) {
		return multiply(new Quaternion(x, y, z, w));
	}

	public Quaternion preMultiply(Quaternion another) {
		return another.multiply(this);
	}

	public Quaternion preMultiply(double x, double y, double z, double w) {
		return preMultiply(new Quaternion(x, y, z, w));
	}

	public Quaternion conjugate() {
		return new Quaternion(-x(), -y(), -z(), w());
	}

	public Quaternion rotateXYZ(double x, double y, double z) {
		double xSin = Math.sin(x / 2), xCos = Math.cos(x / 2);
		double ySin = Math.sin(y / 2), yCos = Math.cos(y / 2);
		double zSin = Math.sin(z / 2), zCos = Math.cos(z / 2);

		double yCosZCos = yCos * zCos, yCosZSin = yCos * zSin, ySinZCos = ySin * zCos, ySinZSin = ySin * zSin;

		double
				rw = xCos * yCosZCos + xSin * ySinZSin,
				rx = xSin * yCosZCos - xCos * ySinZSin,
				ry = xCos * ySinZCos + xSin * yCosZSin,
				rz = xCos * yCosZSin - xSin * ySinZCos;

		return new Quaternion(
				Math.fma(w(), rx, Math.fma(x(), 	rw, Math.fma(y(), 	rz, -z() 	* ry))),
				Math.fma(w(), ry, Math.fma(-x(), 	rz, Math.fma(y(), 	rw, z() 	* rx))),
				Math.fma(w(), rz, Math.fma(x(), 	ry, Math.fma(-y(), 	rx, z() 	* rw))),
				Math.fma(w(), rw, Math.fma(-x(), 	rx, Math.fma(-y(), 	ry, -z() 	* rz)))
		);
	}

	public Quaternion rotateXYZDegrees(double x, double y, double z) {
		return rotateXYZ(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
	}

	public Quaternion rotateZYX(double x, double y, double z) {
		double xSin = Math.sin(x / 2), xCos = Math.cos(x / 2);
		double ySin = Math.sin(y / 2), yCos = Math.cos(y / 2);
		double zSin = Math.sin(z / 2), zCos = Math.cos(z / 2);

		double yCosZCos = yCos * zCos, yCosZSin = yCos * zSin, ySinZCos = ySin * zCos, ySinZSin = ySin * zSin;

		double
				rw = xCos * yCosZCos + xSin * ySinZSin,
				rx = xSin * yCosZCos - xCos * ySinZSin,
				ry = xCos * ySinZCos + xSin * yCosZSin,
				rz = xCos * yCosZSin - xSin * ySinZCos;

		return new Quaternion(
				Math.fma(w(), rx, Math.fma(x(), 	rw, Math.fma(y(), 	rz, -z() 	* ry))),
				Math.fma(w(), ry, Math.fma(-x(), 	rz, Math.fma(y(), 	rw, z() 	* rx))),
				Math.fma(w(), rz, Math.fma(x(), 	ry, Math.fma(-y(), 	rx, z() 	* rw))),
				Math.fma(w(), rw, Math.fma(-x(), 	rx, Math.fma(-y(), 	ry, -z() 	* rz)))
		);
	}

	public Quaternion rotateZYXDegrees(double x, double y, double z) {
		return rotateZYX(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
	}

	public Quaternion rotateYXZ(double x, double y, double z) {
		double xSin = Math.sin(x / 2), xCos = Math.cos(x / 2);
		double ySin = Math.sin(y / 2), yCos = Math.cos(y / 2);
		double zSin = Math.sin(z / 2), zCos = Math.cos(z / 2);

		double yCosXCos = yCos * xCos, yCosXSin = yCos * xSin, ySinXCos = ySin * xCos, ySinXSin = ySin * xSin;

		double
				rw = zCos * yCosXSin + zSin * ySinXCos,
				rx = zCos * ySinXCos - zSin * yCosXSin,
				ry = zCos * yCosXCos + zSin * ySinXSin,
				rz = zCos * yCosXSin - zSin * ySinXCos;

		return new Quaternion(
				Math.fma(w(), rx, Math.fma(x(), 	rw, Math.fma(y(), 	rz, -z() 	* ry))),
				Math.fma(w(), ry, Math.fma(-x(), 	rz, Math.fma(y(), 	rw, z() 	* rx))),
				Math.fma(w(), rz, Math.fma(x(), 	ry, Math.fma(-y(), 	rx, z() 	* rw))),
				Math.fma(w(), rw, Math.fma(-x(), 	rx, Math.fma(-y(), 	ry, -z() 	* rz)))
		);
	}

	public Quaternion rotateYXZDegrees(double y, double x, double z) {
		return rotateYXZ(Math.toRadians(y), Math.toRadians(x), Math.toRadians(z));
	}

	public Quaternion interpolation(Quaternion another, double ratio) {
		return new Quaternion(
				(1 - ratio) * x() + ratio * another.x(),
				(1 - ratio) * y() + ratio * another.y(),
				(1 - ratio) * z() + ratio * another.z(),
				(1 - ratio) * w() + ratio * another.w()
		);
	}

	public Quaternion sphericalInterpolation(Quaternion another, double ratio) {
		double dot = dot(another);

		if (dot < 0) {
			dot = -dot;
			another = another.scale(-1);
		}

		if (dot > 0.9995) {
			return interpolation(another, ratio);
		}

		double theta = Math.acos(dot);
		double sinTheta = Math.sin(theta);
		double sinRatioTheta = Math.sin(ratio * theta);
		double sinOneMinusRatioTheta = Math.sin((1 - ratio) * theta);

		return new Quaternion(
				(1 - ratio) * x() * sinTheta / sinOneMinusRatioTheta + ratio * another.x() * sinTheta / sinRatioTheta,
				(1 - ratio) * y() * sinTheta / sinOneMinusRatioTheta + ratio * another.y() * sinTheta / sinRatioTheta,
				(1 - ratio) * z() * sinTheta / sinOneMinusRatioTheta + ratio * another.z() * sinTheta / sinRatioTheta,
				(1 - ratio) * w() * sinTheta / sinOneMinusRatioTheta + ratio * another.w() * sinTheta / sinRatioTheta
		);
	}

	public Quaternion rotateX(double angle) {
		double sin = Math.sin(angle / 2), cos = Math.cos(angle / 2);

		return new Quaternion(
				w() * sin + x() * cos,
				y() * cos + z() * sin,
				z() * cos - y() * sin,
				w() * cos - x() * sin
		);
	}

	public Quaternion rotateXDegrees(double angleDegrees) {
		return rotateX(Math.toRadians(angleDegrees));
	}

	public Quaternion rotateY(double angle) {
		double sin = Math.sin(angle / 2), cos = Math.cos(angle / 2);

		return new Quaternion(
				x() * cos - z() * sin,
				w() * sin + y() * cos,
				x() * sin + z() * cos,
				w() * cos - y() * sin
		);
	}

	public Quaternion rotateYDegrees(double angleDegrees) {
		return rotateY(Math.toRadians(angleDegrees));
	}

	public Quaternion rotateZ(double angle) {
		double sin = Math.sin(angle / 2), cos = Math.cos(angle / 2);

		return new Quaternion(
				x() * cos + y() * sin,
				y() * cos - x() * sin,
				w() * sin + z() * cos,
				w() * cos - z() * sin
		);
	}

	public Quaternion rotateZDegrees(double angleDegrees) {
		return rotateZ(Math.toRadians(angleDegrees));
	}

	public Quaternion rotateLocalX(double angle) {
		double sin = Math.sin(angle / 2), cos = Math.cos(angle / 2);

		return new Quaternion(
				w() * sin + x() * cos,
				y() * cos - z() * sin,
				z() * cos + y() * sin,
				w() * cos - x() * sin
		);
	}

	public Quaternion rotateLocalXDegrees(double angleDegrees) {
		return rotateLocalX(Math.toRadians(angleDegrees));
	}

	public Quaternion rotateLocalY(double angle) {
		double sin = Math.sin(angle / 2), cos = Math.cos(angle / 2);

		return new Quaternion(
				x() * cos + z() * sin,
				y() * cos + w() * sin,
				z() * cos - x() * sin,
				w() * cos - y() * sin
		);
	}

	public Quaternion rotateLocalYDegrees(double angleDegrees) {
		return rotateLocalY(Math.toRadians(angleDegrees));
	}

	public Quaternion rotateLocalZ(double angle) {
		double sin = Math.sin(angle / 2), cos = Math.cos(angle / 2);

		return new Quaternion(
				x() * cos - y() * sin,
				y() * cos + x() * sin,
				z() * cos + w() * sin,
				w() * cos - z() * sin
		);
	}

	public Quaternion rotateLocalZDegrees(double angleDegrees) {
		return rotateLocalZ(Math.toRadians(angleDegrees));
	}

	public Pos transform(Pos pos) {
		return transform(pos.x(), pos.y(), pos.z());
	}

	public Pos transform(double x, double y, double z) {
		double
				xx = x() * x(), yy = y() * y(), zz = z() * z(),
				xy = x() * y(), xz = x() * z(), yz = y() * z(),
				xw = x() * w(), yw = y() * w(), zw = z() * w();

		return new Pos(
				Math.fma(Math.fma(-2, yy + zz, 1), 	x, Math.fma(2 * (xy - zw), 				y, (2 * (xz + yw)) 			* z)),
				Math.fma(2 * (xy + zw), 			x, Math.fma(Math.fma(-2, xx + zz, 1), 	y, (2 * (yz - xw)) 			* z)),
				Math.fma(2 * (xz - yw), 			x, Math.fma(2 * (yz + xw), 				y, Math.fma(-2, xx + yy, 1) * z))
		);
	}

	public Quaternionf toFloat() {
		return new Quaternionf((float) x(), (float) y(), (float) z(), (float) w());
	}

	public Quaterniond toDouble() {
		return new Quaterniond(x(), y(), z(), w());
	}

	public Pos toNormalizedUnitPos() {
		return transform(Pos.UNIT.normalize());
	}

	// Object Methods

	public String toStringAsNormalizedUnitPos() {
		return toStringAsNormalizedUnitPos(false);
	}

	public String toStringAsNormalizedUnitPos(boolean precisely) {
		return toNormalizedUnitPos().toString(precisely);
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean precisely) {
		return getClass().getSimpleName()
					   + (isZero()
								  ? "{zero}"
								  : precisely
											? String.format("{x=%f, y=%f, z=%f, w=%f}", x(), y(), z(), w())
											: String.format("{x=%.5f, y=%.5f, z=%.5f, w=%.5f}", x(), y(), z(), w()));
	}
}
