package net.krlite.equator.test;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.input.Keyboard;
import net.krlite.equator.input.Mouse;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.render.vanilla.VanillaWidgets;
import net.krlite.equator.visual.animation.Interpolation;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
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

			if (action.isPress()) {
				if (key == Keyboard.E) {
					if (client != null) {
						//client.setScreen(new LayerScreen());
						client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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

		VanillaWidgets.Tooltip.render(matrixStack, box);

		Box another = FrameInfo.scaled().topLeft(box.bottomRight());

		new Flat(matrixStack, 0, another).new Rectangle(Palette.Minecraft.TOOLTIP_BACKGROUND).render();
	}
}
