package net.krlite.equator.test;

import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.input.Keyboard;
import net.krlite.equator.input.Mouse;
import net.krlite.equator.visual.animation.Interpolation;
import net.krlite.equator.visual.color.AccurateColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class CanvasScreen extends Screen {
	public CanvasScreen() {
		super(Text.of("Canvas"));
	}

	private final Interpolation interpolation = new Interpolation(0, 1, 0, 120);

	{
		Mouse.Callbacks.Click.EVENT.register((button, action, mods) -> {
			if (MinecraftClient.getInstance().currentScreen != this) return;

			if (action.isPress()) {
				interpolation.reset();
				System.out.println("Mouse click: " + button);
			}
		});

		Keyboard.Callbacks.Key.EVENT.register((key, scanCode, action, mods) -> {
			if (MinecraftClient.getInstance().currentScreen != this) return;
			
			if (action.isPress()) {
				if (key == Keyboard.E) {
					if (client != null) {
						client.setScreen(new LayerScreen());
					}
				}
			}
		});
	}

	@Override
	protected void init() {
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		renderBackground(matrixStack);

		FrameInfo.scaled().ready(AccurateColor.YELLOW.opacity(interpolation.value())).render(matrixStack);
	}
}
