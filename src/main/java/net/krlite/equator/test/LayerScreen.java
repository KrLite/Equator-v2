package net.krlite.equator.test;

import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.visual.color.AccurateColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class LayerScreen extends Screen {
	protected LayerScreen() {
		super(Text.of("Layer"));
	}

	@Nullable
	private final Screen parent = MinecraftClient.getInstance().currentScreen;

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		if (parent != null) {
			parent.render(matrixStack, mouseX, mouseY, delta);
		}

		renderBackground(matrixStack);
	}

	@Override
	public void renderBackground(MatrixStack matrixStack) {
	}
}
