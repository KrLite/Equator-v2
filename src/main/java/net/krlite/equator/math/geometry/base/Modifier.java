package net.krlite.equator.math.geometry.base;

@FunctionalInterface
public interface Modifier {
	Modifier NONE = origin -> origin, NEGATE = origin -> -origin;

	double value(double origin);
}
