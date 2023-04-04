package net.krlite.equator.test;

import net.krlite.equator.input.Mouse;
import net.krlite.equator.visual.animation.Interpolation;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CanvasScreen extends Screen {
	public CanvasScreen() {
		super(Text.of("Canvas"));
	}

	private final Interpolation interpolation = new Interpolation(0, 1);

	{
		Interpolation.Callbacks.Complete.EVENT.register(i -> i.targetValue(1 - i.targetValue()));

		Mouse.Callbacks.Click.EVENT.register((button, event, mods) -> {
			if (event == Mouse.Action.PRESS)
				interpolation.switchPauseResume();
		});
	}

	@Override
	protected void init() {
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		renderBackground(matrixStack);

		//System.out.println("Interpolation value: " + interpolation.value() + ", " + interpolation.targetValue());
		//FrameInfo.Scaled.fullScreen().scale(interpolation.value()).ready(AccurateColor.CYAN).render(matrixStack);
	}
}
