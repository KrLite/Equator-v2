package net.krlite.equator.render.renderer;

import net.minecraft.client.util.math.MatrixStack;

public abstract class Basic {
	protected Basic(MatrixStack matrixStack) {
		this.matrixStack = matrixStack;
	}

	private final MatrixStack matrixStack;

	public MatrixStack matrixStack() {
		return matrixStack;
	}
}
