package net.krlite.equator.render.renderer;

import net.krlite.equator.math.geometry.volume.Pos;
import net.krlite.equator.render.renderer.base.Basic;
import net.minecraft.client.gui.DrawContext;

public class Volume extends Basic {
	// Constructors

	protected Volume(DrawContext context, Pos pos) {
		super(context);
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
		return new Volume(context(), pos);
	}
}
