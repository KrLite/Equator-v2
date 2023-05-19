package net.krlite.equator.math.logic.volume;

import net.krlite.equator.math.logic.base.Gate;
import net.krlite.equator.math.logic.base.Gated;

@net.krlite.equator.base.Math("2.4.0")
public record VolumeGate(Gate x, Gate y, Gate z) implements Gated<VolumeGate> {
	// Constants

	public static final VolumeGate TRUE = new VolumeGate(Gate.TRUE, Gate.TRUE, Gate.TRUE), FALSE = new VolumeGate(Gate.FALSE, Gate.FALSE, Gate.FALSE);

	// Constructors

	public VolumeGate(Gate x, Gate y, Gate z) {
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

	public VolumeGate x(Gate x) {
		return new VolumeGate(x, y(), z());
	}

	public VolumeGate y(Gate y) {
		return new VolumeGate(x(), y, z());
	}

	public VolumeGate z(Gate z) {
		return new VolumeGate(x(), y(), z);
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

	// Interface implementations

	@Override
	public VolumeGate not() {
		return new VolumeGate(x().not(), y().not(), z().not());
	}

	@Override
	public VolumeGate and(VolumeGate another) {
		return new VolumeGate(x().and(another.x()), y().and(another.y()), z().and(another.z()));
	}

	@Override
	public VolumeGate or(VolumeGate another) {
		return new VolumeGate(x().or(another.x()), y().or(another.y()), z().or(another.z()));
	}

	@Override
	public VolumeGate nand(VolumeGate another) {
		return new VolumeGate(x().nand(another.x()), y().nand(another.y()), z().nand(another.z()));
	}

	@Override
	public VolumeGate xor(VolumeGate another) {
		return new VolumeGate(x().xor(another.x()), y().xor(another.y()), z().xor(another.z()));
	}

	@Override
	public VolumeGate nor(VolumeGate another) {
		return new VolumeGate(x().nor(another.x()), y().nor(another.y()), z().nor(another.z()));
	}

	@Override
	public VolumeGate xnor(VolumeGate another) {
		return new VolumeGate(x().xnor(another.x()), y().xnor(another.y()), z().xnor(another.z()));
	}
}
