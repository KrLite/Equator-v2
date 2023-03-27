package net.krlite.equator.visual.texture;

import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.math.geometry.Vector;
import net.minecraft.util.Identifier;

public class Texture {
	private static String combinePaths(String... paths) {
		return "textures/" + String.join("/", paths) + ".png";
	}

	public static Texture fromIdentifier(Identifier texture) {
		return new Texture(texture);
	}

	public static Texture fromNamespacePath(String namespace, String... paths) {
		return new Texture(new Identifier(namespace, combinePaths(paths)));
	}

	public static Texture fromPath(String... paths) {
		return new Texture(new Identifier(combinePaths(paths)));
	}

	protected Texture(Identifier identifier, Box uvBox, boolean flippedX, boolean flippedY) {
		this.identifier = identifier;
		this.uvBox = uvBox;
		this.flippedX = flippedX;
		this.flippedY = flippedY;
	}

	public Texture(Identifier identifier, Box uvBox) {
		this(identifier, uvBox, false, false);
	}

	public Texture(Identifier identifier) {
		this(identifier, Box.UNIT);
	}

	public Texture(Identifier identifier, double uMin, double vMin, double uMax, double vMax) {
		this(identifier, new Box(uMin, vMin, uMax, vMax));
	}

	public Texture(Identifier identifier, int textureWidth, int textureHeight, int x, int y, int width, int height) {
		this(identifier, (double) x / textureWidth, (double) y / textureHeight, (double) (x + width) / textureWidth, (double) (y + height) / textureHeight);
	}

	public Texture(Identifier identifier, int textureSize, int x, int y, int size) {
		this(identifier, textureSize, textureSize, x, y, size, size);
	}

	private final Box uvBox;
	private final Identifier identifier;
	private final boolean flippedX, flippedY;

	public Identifier identifier() {
		return identifier;
	}

	public Texture identifier(Identifier identifier) {
		return new Texture(identifier, uvBox());
	}

	public Box uvBox() {
		return uvBox;
	}

	public Texture uvBox(Box uvBox) {
		return new Texture(identifier(), uvBox);
	}

	public Texture uvBox(double uMin, double vMin, double uMax, double vMax) {
		return uvBox(new Box(uMin, vMin, uMax, vMax));
	}

	public Texture uvBox(int textureWidth, int textureHeight, int x, int y, int width, int height) {
		return uvBox((double) x / textureWidth, (double) y / textureHeight, (double) (x + width) / textureWidth, (double) (y + height) / textureHeight);
	}

	public Texture uvBox(int textureSize, int x, int y, int size) {
		return uvBox(textureSize, textureSize, x, y, size, size);
	}

	public Vector uvTopLeft() {
		return flippedY() ? flippedX() ? uvBox().bottomRight() : uvBox().topRight() : flippedX() ? uvBox().bottomLeft() : uvBox().topLeft();
	}

	public Vector uvBottomLeft() {
		return flippedY() ? flippedX() ? uvBox().topRight() : uvBox().bottomRight() : flippedX() ? uvBox().topLeft() : uvBox().bottomLeft();
	}

	public Vector uvBottomRight() {
		return flippedY() ? flippedX() ? uvBox().topLeft() : uvBox().bottomLeft() : flippedX() ? uvBox().topRight() : uvBox().bottomRight();
	}

	public Vector uvTopRight() {
		return flippedY() ? flippedX() ? uvBox().bottomLeft() : uvBox().topLeft() : flippedX() ? uvBox().bottomRight() : uvBox().topRight();
	}

	public boolean flippedX() {
		return flippedX;
	}

	public boolean flippedY() {
		return flippedY;
	}

	public Texture flippedX(boolean flippedX) {
		return new Texture(identifier(), uvBox(), flippedX, flippedY());
	}

	public Texture flippedY(boolean flippedY) {
		return new Texture(identifier(), uvBox(), flippedX(), flippedY);
	}

	public Texture flipX() {
		return flippedX(!flippedX());
	}

	public Texture flipY() {
		return flippedY(!flippedY());
	}

	public Texture zoomIn(double scalar) {
		return uvBox(uvBox().scaleCentered(1 / scalar));
	}

	public Texture zoomOut(double scalar) {
		return uvBox(uvBox().scaleCentered(scalar));
	}

	public Texture shift(Vector shift) {
		return uvBox(uvBox().shift(shift.remainder(1)));
	}

	public Texture shift(double uOffset, double vOffset) {
		return shift(new Vector(uOffset, vOffset));
	}
}
