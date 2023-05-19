package net.krlite.equator.math.logic.base;

@net.krlite.equator.base.Math("2.4.0")
public interface Transformative<T> {
	T andThen(T after);
}
