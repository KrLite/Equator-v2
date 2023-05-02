package net.krlite.equator.math.geometry.volume.base;

import net.krlite.equator.math.logic.base.Gate;

public record VolumeLimiter(Gate x, Gate y, Gate z) {
	// Constants

	public static final VolumeLimiter TRUE = new VolumeLimiter(Gate.TRUE, Gate.TRUE, Gate.TRUE), FALSE = new VolumeLimiter(Gate.FALSE, Gate.FALSE, Gate.FALSE);

	// Constructors

	public VolumeLimiter(Gate x, Gate y, Gate z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Accessors

	public Gate x() {
		return x;
	}

	public Gate y() {
		return y;
	}

	public Gate z() {
		return z;
	}

	// Mutators

	public VolumeLimiter x(Gate x) {
		return new VolumeLimiter(x, y(), z());
	}

	public VolumeLimiter y(Gate y) {
		return new VolumeLimiter(x(), y, z());
	}

	public VolumeLimiter z(Gate z) {
		return new VolumeLimiter(x(), y(), z);
	}

	// Operations

	public boolean x(double x) {
		return x().pass(x);
	}

	public boolean y(double y) {
		return y().pass(y);
	}

	public boolean z(double z) {
		return z().pass(z);
	}

	public boolean xy(double x, double y) {
		return x(x) && y(y);
	}

	public boolean xz(double x, double z) {
		return x(x) && z(z);
	}

	public boolean yz(double y, double z) {
		return y(y) && z(z);
	}

	public boolean pass(double x, double y, double z) {
		return x(x) && y(y) && z(z);
	}
}
