package net.krlite.equator.test;

import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.math.geometry.Vector;
import net.krlite.equator.input.Keyboard;
import net.krlite.equator.input.Mouse;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.vanilla.ButtonRenderImplementation;
import net.krlite.equator.visual.animation.Interpolation;
import net.krlite.equator.visual.color.AccurateColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class CanvasScreen extends Screen {
	public CanvasScreen() {
		super(Text.of("Canvas"));
	}

	private final Interpolation interpolation = new Interpolation(0, 0, 1, 120);
	private Box box = Box.fromCartesian(0, 0, 0, 0);

	{
		Interpolation.Callbacks.Complete.EVENT.register(interpolation -> {
			if (interpolation == this.interpolation) {
				this.interpolation.reverse();
			}
		});

		Mouse.Callbacks.Move.EVENT.register((position) -> {
			if (MinecraftClient.getInstance().currentScreen != this) return;

			box = new Box(position);
		});

		Keyboard.Callbacks.Key.EVENT.register((key, scanCode, action, mods) -> {
			if (MinecraftClient.getInstance().currentScreen != this) return;

			/*
			if (action.isPress()) {
				if (key == Keyboard.E) {
					if (client != null) {
						client.setScreen(new LayerScreen());
					}
				}
			}

			 */
		});
	}

	@Override
	protected void init() {
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		renderBackground(matrixStack);

		box.ready(AccurateColor.RED.opacity(0.1)).render(matrixStack);
		ButtonRenderImplementation.renderButton(matrixStack, box, ButtonRenderImplementation.State.FOCUSED);
	}
}
