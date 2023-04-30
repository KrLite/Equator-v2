package net.krlite.equator.math.geometry.flat;

import net.krlite.equator.math.geometry.base.Limiter;

public record FlatLimiter(Limiter x, Limiter y) {
	// Constants

	public static final FlatLimiter TRUE = new FlatLimiter(Limiter.TRUE, Limiter.TRUE), FALSE = new FlatLimiter(Limiter.FALSE, Limiter.FALSE);

	// Constructors

	public FlatLimiter(Limiter x, Limiter y) {
		this.x = x;
		this.y = y;
	}

	// Accessors

	public Limiter x() {
		return x;
	}

	public Limiter y() {
		return y;
	}

	// Mutators

	public FlatLimiter x(Limiter x) {
		return new FlatLimiter(x, y());
	}

	public FlatLimiter y(Limiter y) {
		return new FlatLimiter(x(), y);
	}

	// Operations

	public boolean x(double x) {
		return x().pass(x);
	}

	public boolean y(double y) {
		return y().pass(y);
	}

	public boolean pass(double x, double y) {
		return x(x) && y(y);
	}
}
