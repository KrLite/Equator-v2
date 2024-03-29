package net.krlite.equator.math.geometry.volume;

import net.krlite.equator.math.algebra.Quaternion;
import net.krlite.equator.math.algebra.Theory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public record Pos(@Nullable RegistryKey<World> dimension, double x, double y, double z) {
	// Constants

	public static final Pos ZERO = new Pos(0, 0, 0), ZERO_OVERWORLD = new Pos(World.OVERWORLD, 0, 0, 0),
			ZERO_NETHER = new Pos(World.NETHER, 0, 0, 0), ZERO_END = new Pos(World.END, 0, 0, 0);

	public static final Pos UNIT = new Pos(1, 1, 1), UNIT_OVERWORLD = new Pos(World.OVERWORLD, 1, 1, 1),
			UNIT_NETHER = new Pos(World.NETHER, 1, 1, 1), UNIT_END = new Pos(World.END, 1, 1, 1);

	public static final Pos UNIT_X = new Pos(1, 0, 0), UNIT_X_OVERWORLD = new Pos(World.OVERWORLD, 1, 0, 0),
			UNIT_X_NETHER = new Pos(World.NETHER, 1, 0, 0), UNIT_X_END = new Pos(World.END, 1, 0, 0);

	public static final Pos UNIT_Y = new Pos(0, 1, 0), UNIT_Y_OVERWORLD = new Pos(World.OVERWORLD, 0, 1, 0),
			UNIT_Y_NETHER = new Pos(World.NETHER, 0, 1, 0), UNIT_Y_END = new Pos(World.END, 0, 1, 0);

	public static final Pos UNIT_Z = new Pos(0, 0, 1), UNIT_Z_OVERWORLD = new Pos(World.OVERWORLD, 0, 0, 1),
			UNIT_Z_NETHER = new Pos(World.NETHER, 0, 0, 1), UNIT_Z_END = new Pos(World.END, 0, 0, 1);

	// Constructors

	public Pos(@Nullable RegistryKey<World> dimension, double x, double y, double z) {
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Pos(double x, double y, double z) {
		this(null, x, y, z);
	}

	public Pos(BlockPos blockPos) {
		this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	public Pos(Entity entity) {
		this(entity.getWorld().getRegistryKey(), entity.getX(), entity.getY(), entity.getZ());
	}

	// Accessors

	public @Nullable RegistryKey<World> dimension() {
		return dimension;
	}

	public World world() {
		IntegratedServer server = MinecraftClient.getInstance().getServer();
		if (server != null) return server.getWorld(dimension());
		else return MinecraftClient.getInstance().world;
	}

	@Override
	public double x() { return x; }

	@Override
	public double y() { return y; }

	@Override
	public double z() { return z; }

	// Mutators

	public Pos dimension(@Nullable RegistryKey<World> dimension) {
		return new Pos(dimension, x(), y(), z());
	}

	public Pos x(double x) {
		return new Pos(dimension(), x, y(), z());
	}

	public Pos y(double y) {
		return new Pos(dimension(), x(), y, z());
	}

	public Pos z(double z) {
		return new Pos(dimension(), x(), y(), z);
	}

	// Properties

	public boolean hasDimension() {
		return dimension != null;
	}

	public boolean isNormalized() {
		return Theory.looseEquals(magnitude(), 1);
	}

	public boolean isZero() {
		return Theory.isZero(x()) && Theory.isZero(y()) && Theory.isZero(z());
	}

	private boolean explicitlySameDimension(Pos another, boolean ignore, boolean allowDimensionNullCase) {
		return allowDimensionNullCase
					   ? (ignore || !hasDimension() || !another.hasDimension() || dimension() == another.dimension())
					   : (ignore || hasDimension() && another.hasDimension() && dimension() == another.dimension());
	}

	public boolean sameDimension(Pos another, boolean allowNullCase) {
		return explicitlySameDimension(another, false, allowNullCase);
	}

	public boolean sameDimension(Pos another) {
		return sameDimension(another, true);
	}

	public boolean isParallelTo(Pos another) {
		return isZero() || another.isZero() || Theory.isZero(cross(another));
	}

	public boolean isPerpendicularTo(Pos another) {
		return isZero() || another.isZero() || Theory.isZero(dot(another));
	}

	public double angleTo(Pos another) {
		return Math.acos(normalize().dot(another.normalize()));
	}

	public double angleDegreesTo(Pos another) {
		return Math.toDegrees(angleTo(another));
	}

	public double magnitude() {
		return Math.sqrt(x() * x() + y() * y() + z() * z());
	}

	public double dot(Pos another) {
		return x() * another.x() + y() * another.y() + z() * another.z();
	}

	public double cross(Pos another) {
		return x() * another.y() - y() * another.x() + y() * another.z() - z() * another.y() + z() * another.x() - x() * another.z();
	}

	public double distanceTo(Pos another) {
		return Math.sqrt(Math.pow(x() - another.x(), 2) + Math.pow(y() - another.y(), 2) + Math.pow(z() - another.z(), 2));
	}

	public double manhattanDistanceTo(Pos another) {
		return Math.abs(x() - another.x()) + Math.abs(y() - another.y()) + Math.abs(z() - another.z());
	}

	public double magnitudeMin(Pos another) {
		return Math.min(magnitude(), another.magnitude());
	}

	public double magnitudeMax(Pos another) {
		return Math.max(magnitude(), another.magnitude());
	}

	// Operations

	public Pos scale(double xScalar, double yScalar, double zScalar) {
		return new Pos(dimension(), x() * xScalar, y() * yScalar, z() * zScalar);
	}

	public Pos scale(double scalar) {
		return scale(scalar, scalar, scalar);
	}

	public Pos add(double x, double y, double z) {
		return new Pos(dimension(), x() + x, y() + y, z() + z);
	}

	public Pos add(Pos another) {
		return add(another.x(), another.y(), another.z());
	}

	public Pos subtract(double x, double y, double z) {
		return new Pos(dimension(), x() - x, y() - y, z() - z);
	}

	public Pos subtract(Pos another) {
		return subtract(another.x(), another.y(), another.z());
	}

	public Pos magnitude(double magnitude) {
		return scale(magnitude / magnitude());
	}

	public Pos withoutDimension() {
		return dimension(null);
	}

	public Pos normalize() {
		return magnitude(1);
	}

	public Pos projectOnto(Pos another) {
		return another.scale(dot(another) / another.dot(another));
	}

	public Pos projectOntoXY() {
		return z(0);
	}

	public Pos projectOntoXZ() {
		return y(0);
	}

	public Pos projectOntoYZ() {
		return x(0);
	}

	public Pos projectOntoX() {
		return projectOnto(UNIT_X);
	}

	public Pos projectOntoY() {
		return projectOnto(UNIT_Y);
	}

	public Pos projectOntoZ() {
		return projectOnto(UNIT_Z);
	}

	public Pos negate() {
		return scale(-1);
	}

	public Pos negateByXY() {
		return z(-z());
	}

	public Pos negateByXZ() {
		return y(-y());
	}

	public Pos negateByYZ() {
		return x(-x());
	}

	public Pos negateByX() {
		return negateByXY().negateByXZ();
	}

	public Pos negateByY() {
		return negateByXY().negateByYZ();
	}

	public Pos negateByZ() {
		return negateByXZ().negateByYZ();
	}

	public Pos rotate(Quaternion quaternion) {
		double x = x(), y = y(), z = z(), w = quaternion.w(), i = quaternion.x(), j = quaternion.y(), k = quaternion.z(),
				w2 = w * w, i2 = i * i, j2 = j * j, k2 = k * k;

		double xRotated = x * (w2 + i2 - j2 - k2) + y * 2 * (i * j - w * k) + z * 2 * (i * k + w * j);
		double yRotated = x * 2 * (i * j + w * k) + y * (w2 - i2 + j2 - k2) + z * 2 * (j * k - w * i);
		double zRotated = x * 2 * (i * k - w * j) + y * 2 * (j * k + w * i) + z * (w2 - i2 - j2 + k2);

		return new Pos(dimension(), xRotated, yRotated, zRotated);
	}

	public Pos rotateAround(Pos pivot, Quaternion quaternion) {
		return subtract(pivot).rotate(quaternion).add(pivot);
	}

	public Pos min(Pos another) {
		return new Pos(dimension(), Math.min(x(), another.x()), Math.min(y(), another.y()), Math.min(z(), another.z()));
	}

	public Pos max(Pos another) {
		return new Pos(dimension(), Math.max(x(), another.x()), Math.max(y(), another.y()), Math.max(z(), another.z()));
	}

	public Pos clamp(Pos min, Pos max) {
		return min.max(this).min(max);
	}

	public Pos minByMagnitude(Pos another) {
		return magnitude() <= another.magnitude() ? this : another;
	}

	public Pos maxByMagnitude(Pos another) {
		return magnitude() >= another.magnitude() ? this : another;
	}

	public Pos interpolate(Pos another, double factor) {
		return add(another.subtract(this).scale(factor));
	}

	public Pos sphericalInterpolate(Pos another, double factor) {
		Pos unit = normalize(), anotherUnit = another.normalize();
		double angle = angleTo(another);
		double sin = Math.sin(angle), sinFactor = Math.sin(factor * angle), oneMinusSinFactor = Math.sin((1 - factor) * angle);

		return new Pos(
				dimension(),
				(unit.x() * oneMinusSinFactor + another.x() * sin) / sin,
				(unit.y() * oneMinusSinFactor + another.y() * sin) / sin,
				(unit.z() * oneMinusSinFactor + another.z() * sin) / sin
		);
	}

	public Pos cameraProjection(double yaw, double pitch) {
		return rotate(Quaternion.rotationXDegrees(yaw)).rotate(Quaternion.rotationX(-pitch));
	}

	// Links

	public Vec3d toVec3d() {
		return new Vec3d(x(), y(), z());
	}

	public Vec3i toVec3i() {
		return new Vec3i((int) x(), (int) y(), (int) z());
	}

	public BlockPos toBlockPos() {
		return new BlockPos(toVec3i());
	}

	// Object Methods

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean precisely) {
		return getClass().getSimpleName()
					   + (isZero()
								  ? "[zero]"
								  : precisely
											? String.format("[x=%f, y=%f, z=%f]", x(), y(), z())
											: String.format("[x=%.5f, y=%.5f, z=%.5f]", x(), y(), z())
													  + (!hasDimension()
																 ? ""
																 : ("-(dimension=" + dimension() + ")")));
	}
}
