package net.krlite.equator.math.logic.flat;

import net.krlite.equator.math.logic.base.Gate;
import net.krlite.equator.math.logic.base.Gated;

@net.krlite.equator.base.Math("2.4.2")
public record FlatGate(Gate x, Gate y) implements Gated<FlatGate> {
	// Constants

	public static final FlatGate TRUE = new FlatGate(Gate.TRUE, Gate.TRUE), FALSE = new FlatGate(Gate.FALSE, Gate.FALSE);

	// Constructors

	public FlatGate(Gate x, Gate y) {
		this.x = x;
		this.y = y;
	}

	// Accessors

	public Gate x() {
		return x;
	}

	public Gate y() {
		return y;
	}

	// Mutators

	public FlatGate x(Gate x) {
		return new FlatGate(x, y());
	}

	public FlatGate y(Gate y) {
		return new FlatGate(x(), y);
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

	// Interface implementations

	@Override
	public FlatGate not() {
		return new FlatGate(x().not(), y().not());
	}

	@Override
	public FlatGate and(FlatGate other) {
		return new FlatGate(x().and(other.x()), y().and(other.y()));
	}

	@Override
	public FlatGate or(FlatGate other) {
		return new FlatGate(x().or(other.x()), y().or(other.y()));
	}

	@Override
	public FlatGate nand(FlatGate other) {
		return new FlatGate(x().nand(other.x()), y().nand(other.y()));
	}

	@Override
	public FlatGate xor(FlatGate other) {
		return new FlatGate(x().xor(other.x()), y().xor(other.y()));
	}

	@Override
	public FlatGate nor(FlatGate other) {
		return new FlatGate(x().nor(other.x()), y().nor(other.y()));
	}

	@Override
	public FlatGate xnor(FlatGate other) {
		return new FlatGate(x().xnor(other.x()), y().xnor(other.y()));
	}
}
