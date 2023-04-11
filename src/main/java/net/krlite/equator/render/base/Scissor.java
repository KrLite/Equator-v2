package net.krlite.equator.render.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.math.geometry.Box;

import java.util.function.UnaryOperator;

public record Scissor(Box box) {
	// box() is a record method

	public Scissor box(Box box) {
		return new Scissor(box);
	}

	public Scissor box(UnaryOperator<Box> box) {
		return new Scissor(box.apply(box()));
	}

	public void snipOn() {
		// Need to fit the box into the OpenGL Coordinate System
		Box fitted = box().fitToOpenGL();
		RenderSystem.enableScissor((int) fitted.x(), (int) fitted.y(), (int) fitted.w(), (int) fitted.h());
	}

	public void snipOff() {
		RenderSystem.disableScissor();
	}
}
