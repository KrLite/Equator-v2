package net.krlite.equator.math.logic.base;

@FunctionalInterface
public interface Transform extends Transformative<Transform> {
	Transform NONE = origin -> origin, NEGATE = origin -> -origin;

	double value(double origin);

	default Transform andThen(Transform after) {
		return origin -> after.value(value(origin));
	}
}
