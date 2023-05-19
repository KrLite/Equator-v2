package net.krlite.equator.render.renderer;

import net.krlite.equator.math.geometry.volume.Pos;
import net.krlite.equator.render.renderer.base.Basic;
import net.minecraft.client.util.math.MatrixStack;

public class Volume extends Basic {
	// Constructors

	protected Volume(MatrixStack matrixStack, Pos pos) {
		super(matrixStack);
		this.pos = pos;
	}

	// Fields

	private final Pos pos;

	// Accessors

	public Pos pos() {
		return pos;
	}

	// Mutators

	public Volume pos(Pos pos) {
		return new Volume(matrixStack(), pos);
	}
}
