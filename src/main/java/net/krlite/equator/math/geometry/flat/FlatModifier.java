package net.krlite.equator.math.geometry.flat;

import net.krlite.equator.math.geometry.base.Modifier;

public record FlatModifier(Modifier x, Modifier y) {
	// Constants

	public static final FlatModifier NONE = new FlatModifier(Modifier.NONE, Modifier.NONE), NEGATE = new FlatModifier(Modifier.NEGATE, Modifier.NEGATE);

	// Static Constructors

	public static FlatModifier shift(double x, double y) {
		return new FlatModifier(xOrigin -> xOrigin + x, yOrigin -> yOrigin + y);
	}

	// Constructors

	public FlatModifier(Modifier x, Modifier y) {
		this.x = x;
		this.y = y;
	}

	// Accessors

	public Modifier x() {
		return x;
	}

	public Modifier y() {
		return y;
	}

	// Mutators

	public FlatModifier x(Modifier x) {
		return new FlatModifier(x, y());
	}

	public FlatModifier y(Modifier y) {
		return new FlatModifier(x(), y);
	}

	// Operations

	public double x(double x) {
		return x().value(x);
	}

	public double y(double y) {
		return y().value(y);
	}
}
