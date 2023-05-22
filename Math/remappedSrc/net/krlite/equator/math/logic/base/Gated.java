package net.krlite.equator.math.logic.base;

@net.krlite.equator.base.Math("2.4.2")
public interface Gated<T> {
	T not();

	T and(T another);

	T or(T another);

	T nand(T another);

	T xor(T another);

	T nor(T another);

	T xnor(T another);
}
