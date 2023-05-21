package net.krlite.equator.test;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.input.Keyboard;
import net.krlite.equator.input.Mouse;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.render.vanilla.VanillaWidgets;
import net.krlite.equator.visual.animation.Interpolation;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Colorspace;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

public class CanvasScreen extends Screen {
	public CanvasScreen() {
		super(Text.of("Canvas"));
	}

	private final Interpolation interpolation = new Interpolation(0, 0, 1, 120);
	private Box box = Box.fromCartesian(0, 0, 0, 0);
	private Paragraph.Alignment horizontal = Paragraph.Alignment.LEFT;
	private Section.Alignment vertical = Section.Alignment.TOP;

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
				if (key == Keyboard.LEFT) {
					horizontal = horizontal.previous();
				}

				if (key == Keyboard.RIGHT) {
					horizontal = horizontal.next();
				}

				if (key == Keyboard.UP) {
					vertical = vertical.previous();
				}

				if (key == Keyboard.DOWN) {
					vertical = vertical.next();
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

		//VanillaWidgets.Tooltip.render(matrixStack, box);

		Box another = FrameInfo.scaled().topLeft(box.bottomRight()).squareInner().alignBottomRight(FrameInfo.scaled());

		/*
		new Flat(matrixStack, 0, another).new Oval()
				.addColor(0, AccurateColor.MAGENTA)
				.addColor(Math.PI / 2, AccurateColor.CYAN)
				.addColor(3 * Math.PI / 2, AccurateColor.YELLOW)
				.ovalMode(Flat.Oval.OvalMode.FILL)
				.mixMode(ColorStandard.MixMode.PIGMENT)
				.render();

		*/

		/*
		new Flat(matrixStack, 0, box).new Text(
				section -> section
								   .appendTitle("TITLE")
								   .append("Write me!")
								   .appendSpacing()
								   .appendSpacing()
								   .appendSpacing()
								   .appendSubtitle("SUBTITLE")
								   .append("§aTooltips are cool!")
								   .appendTitle("§fTITLE")
		).color(Palette.rainbow(interpolation.value() * Math.PI * 2))
				.horizontalAlignment(horizontal).verticalAlignment(vertical).enableCulling().new Tooltip(Flat.Text.Tooltip.TooltipSnap.BOTH).render();

		 */

		FrameInfo.scaled().render(matrixStack, 0, flat -> flat.new Rectangle(AccurateColor.WHITE));

		Box.fromCartesian(0, 0, 100, 100).render(matrixStack,
				flat -> flat
								.new Rectangle()
								.colorTopLeft(AccurateColor.RED)
								.colorBottomLeft(AccurateColor.BLACK)
								.colorBottomRight(AccurateColor.BLACK)
								.colorTopRight(AccurateColor.WHITE)
		);

		Box.fromCartesian(100, 0, 100, 100).render(matrixStack,
				flat -> flat
								.new Rectangle(Colorspace.HSV)
								.colorTopLeft(AccurateColor.RED.colorspace(Colorspace.HSV))
								.colorBottomLeft(AccurateColor.RED.colorspace(Colorspace.HSV).value(0))
								.colorBottomRight(AccurateColor.RED.colorspace(Colorspace.HSV).value(0).saturation(0))
								.colorTopRight(AccurateColor.RED.colorspace(Colorspace.HSV).saturation(0))
		);

		Box box = Box.fromCartesian(50, 50).center(FrameInfo.scaled());

		box.shift(0, 3).scaleCenter(0.95).render(matrixStack,
				flat -> flat.new Rectangle()
								.colors(AccurateColor.BLACK.opacity(0.7))
								.new Outlined(Vector.fromCartesian(7, 7), Flat.Rectangle.Outlined.OutliningMode.NORMAL, Flat.Rectangle.Outlined.OutliningStyle.EDGE_FADED)
		);

		box.render(matrixStack, flat -> flat.new Rectangle().colors(AccurateColor.GREEN));
	}
}
