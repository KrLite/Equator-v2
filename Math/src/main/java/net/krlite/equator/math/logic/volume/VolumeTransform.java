package net.krlite.equator.math.logic.volume;

import net.krlite.equator.math.logic.base.Transform;
import net.krlite.equator.math.logic.base.Transformative;

@net.krlite.equator.base.Math("2.4.0")
public record VolumeTransform(Transform x, Transform y, Transform z) implements Transformative<VolumeTransform> {
	// Constants

	public static final VolumeTransform NONE = new VolumeTransform(Transform.NONE, Transform.NONE, Transform.NONE), NEGATE = new VolumeTransform(Transform.NEGATE, Transform.NEGATE, Transform.NEGATE);

	// Static Constructors

	public static VolumeTransform shift(double x, double y, double z) {
		return new VolumeTransform(xOrigin -> xOrigin + x, yOrigin -> yOrigin + y, zOrigin -> zOrigin + z);
	}

	// Constructors

	public VolumeTransform(Transform x, Transform y, Transform z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Accessors

	public Transform x() {
		return x;
	}

	public Transform y() {
		return y;
	}

	public Transform z() {
		return z;
	}

	// Mutators

	public VolumeTransform x(Transform x) {
		return new VolumeTransform(x, y(), z());
	}

	public VolumeTransform y(Transform y) {
		return new VolumeTransform(x(), y, z());
	}

	public VolumeTransform z(Transform z) {
		return new VolumeTransform(x(), y(), z);
	}

	// Operations

	public double x(double x) {
		return x().value(x);
	}

	public double y(double y) {
		return y().value(y);
	}

	public double z(double z) {
		return z().value(z);
	}

	// Interface Implementations

	@Override
	public VolumeTransform andThen(VolumeTransform after) {
		return new VolumeTransform(x().andThen(after.x()), y().andThen(after.y()), z().andThen(after.z()));
	}
}
