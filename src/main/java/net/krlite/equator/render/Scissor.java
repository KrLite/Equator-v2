package net.krlite.equator.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.util.FrameInfo;
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

	public void cut() {
		// Need to fit the box into the OpenGL Coordinate System
		Box fitted = FrameInfo.Convertor.scaledToOpenGL(box());
		RenderSystem.enableScissor((int) fitted.topLeft().x(), (int) fitted.topLeft().y(), (int) fitted.width().magnitude(), (int) fitted.height().magnitude());
	}

	public void release() {
		RenderSystem.disableScissor();
	}
}
