package net.krlite.equator.math.geometry.volume.base;

import net.krlite.equator.math.logic.base.Transform;

public record VolumeModifier(Transform x, Transform y, Transform z) {
	// Constants

	public static final VolumeModifier NONE = new VolumeModifier(Transform.NONE, Transform.NONE, Transform.NONE), NEGATE = new VolumeModifier(Transform.NEGATE, Transform.NEGATE, Transform.NEGATE);

	// Static Constructors

	public static VolumeModifier shift(double x, double y, double z) {
		return new VolumeModifier(xOrigin -> xOrigin + x, yOrigin -> yOrigin + y, zOrigin -> zOrigin + z);
	}

	// Constructors

	public VolumeModifier(Transform x, Transform y, Transform z) {
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

	public VolumeModifier x(Transform x) {
		return new VolumeModifier(x, y(), z());
	}

	public VolumeModifier y(Transform y) {
		return new VolumeModifier(x(), y, z());
	}

	public VolumeModifier z(Transform z) {
		return new VolumeModifier(x(), y(), z);
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
}
