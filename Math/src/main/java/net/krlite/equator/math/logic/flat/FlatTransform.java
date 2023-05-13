package net.krlite.equator.math.logic.flat;

import net.krlite.equator.math.logic.base.Transform;
import net.krlite.equator.math.logic.base.Transformative;

@net.krlite.equator.base.Math("2.3.0")
public record FlatTransform(Transform x, Transform y) implements Transformative<FlatTransform> {
	// Constants

	public static final FlatTransform NONE = new FlatTransform(Transform.NONE, Transform.NONE), NEGATE = new FlatTransform(Transform.NEGATE, Transform.NEGATE);

	// Static Constructors

	public static FlatTransform shift(double x, double y) {
		return new FlatTransform(xOrigin -> xOrigin + x, yOrigin -> yOrigin + y);
	}

	// Constructors

	public FlatTransform(Transform x, Transform y) {
		this.x = x;
		this.y = y;
	}

	// Accessors

	public Transform x() {
		return x;
	}

	public Transform y() {
		return y;
	}

	// Mutators

	public FlatTransform x(Transform x) {
		return new FlatTransform(x, y());
	}

	public FlatTransform y(Transform y) {
		return new FlatTransform(x(), y);
	}

	// Operations

	public double x(double x) {
		return x().value(x);
	}

	public double y(double y) {
		return y().value(y);
	}

	// Interface Implementations

	@Override
	public FlatTransform andThen(FlatTransform after) {
		return new FlatTransform(x().andThen(after.x()), y().andThen(after.y()));
	}
}
