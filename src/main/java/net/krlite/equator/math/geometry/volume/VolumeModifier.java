package net.krlite.equator.math.geometry.volume;

import net.krlite.equator.math.geometry.base.Modifier;

public record VolumeModifier(Modifier x, Modifier y, Modifier z) {
	// Constants

	public static final VolumeModifier NONE = new VolumeModifier(Modifier.NONE, Modifier.NONE, Modifier.NONE), NEGATE = new VolumeModifier(Modifier.NEGATE, Modifier.NEGATE, Modifier.NEGATE);

	// Static Constructors

	public static VolumeModifier shift(double x, double y, double z) {
		return new VolumeModifier(xOrigin -> xOrigin + x, yOrigin -> yOrigin + y, zOrigin -> zOrigin + z);
	}

	// Constructors

	public VolumeModifier(Modifier x, Modifier y, Modifier z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Accessors

	public Modifier x() {
		return x;
	}

	public Modifier y() {
		return y;
	}

	public Modifier z() {
		return z;
	}

	// Mutators

	public VolumeModifier x(Modifier x) {
		return new VolumeModifier(x, y(), z());
	}

	public VolumeModifier y(Modifier y) {
		return new VolumeModifier(x(), y, z());
	}

	public VolumeModifier z(Modifier z) {
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
