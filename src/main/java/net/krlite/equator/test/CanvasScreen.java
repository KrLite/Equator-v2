package net.krlite.equator.test;

import net.krlite.equator.util.InputEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class CanvasScreen extends Screen {
	public CanvasScreen() {
		super(Text.of("Canvas"));
	}

	@Override
	protected void init() {
		InputEvents.InputCallbacks.Mouse.EVENT.register((event, button, mods) -> {
			if (event == InputEvents.InputEvent.MOUSE_PRESSED && button == 0)
				System.out.println("Left mouse button pressed");
		});
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		renderBackground(matrixStack);
	}
}
