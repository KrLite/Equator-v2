package net.krlite.equator.render.renderer.base;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public abstract class Basic {
	protected Basic(DrawContext context) {
		this.context = context;
	}

	private final DrawContext context;

	public DrawContext context() {
		return context;
	}

	public MatrixStack matrixStack() {
		return context.getMatrices();
	}
}
