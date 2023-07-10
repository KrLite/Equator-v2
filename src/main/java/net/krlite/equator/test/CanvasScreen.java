package net.krlite.equator.test;

import net.krlite.equator.input.Keyboard;
import net.krlite.equator.input.Mouse;
import net.krlite.equator.math.algebra.Curves;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.visual.animation.ValueAnimation;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class CanvasScreen extends Screen {
	public CanvasScreen() {
		super(Text.of("Canvas"));
	}

	private Box box = Box.fromCartesian(0, 0, 0, 0);
	private Paragraph.Alignment horizontal = Paragraph.Alignment.LEFT;
	private Section.Alignment vertical = Section.Alignment.TOP;
	private final ValueAnimation animation = new ValueAnimation(1, 1.1, 100, Curves.Sinusoidal.EASE);

	{
		animation.sensitive(true);
		animation.speedNegate();
	}

	{
		Mouse.Callbacks.Click.EVENT.register((button, action, modifiers) -> {
			if (MinecraftClient.getInstance().currentScreen != this) return;

			if (button == Mouse.LEFT) {
				if (client != null) {
					animation.speedNegate();
				}
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
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		renderBackground(context);

		//VanillaWidgets.Tooltip.render(context, box);

		Box another = FrameInfo.scaled().topLeft(box.bottomRight()).squareInner().alignBottomRight(FrameInfo.scaled());

		/*
		new Flat(context, 0, another).new Oval()
				.addColor(0, AccurateColor.MAGENTA)
				.addColor(Math.PI / 2, AccurateColor.CYAN)
				.addColor(3 * Math.PI / 2, AccurateColor.YELLOW)
				.ovalMode(Flat.Oval.OvalMode.FILL)
				.mixMode(ColorStandard.MixMode.PIGMENT)
				.render();

		*/

		/*
		new Flat(context, 0, box).new Text(
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

		/*
		FrameInfo.scaled().render(context, 0, flat -> flat.new Rectangle(AccurateColor.WHITE));

		Box.fromCartesian(0, 0, 100, 100).render(context,
				flat -> flat
								.new Rectangle()
								.colorTopLeft(AccurateColor.RED)
								.colorBottomLeft(AccurateColor.BLACK)
								.colorBottomRight(AccurateColor.BLACK)
								.colorTopRight(AccurateColor.WHITE)
		);

		Box.fromCartesian(100, 0, 100, 100).render(context,
				flat -> flat
								.new Rectangle(Colorspace.HSV)
								.colorTopLeft(AccurateColor.RED.colorspace(Colorspace.HSV))
								.colorBottomLeft(AccurateColor.RED.colorspace(Colorspace.HSV).value(0))
								.colorBottomRight(AccurateColor.RED.colorspace(Colorspace.HSV).value(0).saturation(0))
								.colorTopRight(AccurateColor.RED.colorspace(Colorspace.HSV).saturation(0))
		);

		Box box = Box.fromCartesian(50, 50).center(FrameInfo.scaled());

		box.render(context,
				flat -> flat.new Rectangle()
								.colors(AccurateColor.MAGENTA.opacity(0.5))
								.new Outlined(Vector.fromCartesian(240, 240).scale(interpolation.value()), Flat.Rectangle.Outlined.OutliningMode.NORMAL, Flat.Rectangle.Outlined.OutliningStyle.EDGE_FADED)
		);

		box.render(context, flat -> flat.new Rectangle().colors(AccurateColor.MAGENTA));
		 */

		FrameInfo.scaled().scaleCenter(0.5 * animation.value()).render(context, flat -> flat.new Rectangle().colors(AccurateColor.MAGENTA));
	}
}
