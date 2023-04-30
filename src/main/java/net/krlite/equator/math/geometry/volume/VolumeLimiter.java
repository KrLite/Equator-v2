package net.krlite.equator.math.geometry.volume;

import net.krlite.equator.math.geometry.base.Limiter;

public record VolumeLimiter(Limiter x, Limiter y, Limiter z) {
	// Constants

	public static final VolumeLimiter TRUE = new VolumeLimiter(Limiter.TRUE, Limiter.TRUE, Limiter.TRUE), FALSE = new VolumeLimiter(Limiter.FALSE, Limiter.FALSE, Limiter.FALSE);

	// Constructors

	public VolumeLimiter(Limiter x, Limiter y, Limiter z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Accessors

	public Limiter x() {
		return x;
	}

	public Limiter y() {
		return y;
	}

	public Limiter z() {
		return z;
	}

	// Mutators

	public VolumeLimiter x(Limiter x) {
		return new VolumeLimiter(x, y(), z());
	}

	public VolumeLimiter y(Limiter y) {
		return new VolumeLimiter(x(), y, z());
	}

	public VolumeLimiter z(Limiter z) {
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
