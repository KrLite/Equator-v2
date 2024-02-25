package net.krlite.equator.test;

import net.krlite.equator.Equator;
import net.krlite.equator.input.Mouse;
import net.krlite.equator.math.algebra.Curves;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.visual.animation.animated.AnimatedDouble;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CanvasScreen extends Screen {
	public CanvasScreen() {
		super(Text.of("Canvas"));
	}

	private Box box = Box.fromCartesian(0, 0, 0, 0);
	private Paragraph.Alignment horizontal = Paragraph.Alignment.LEFT;
	private Section.Alignment vertical = Section.Alignment.TOP;
	private final AnimatedDouble animation = new AnimatedDouble(1, 2, 500, Curves.Back.IN);
	private boolean a = false, b = false;

	{
		animation.speedNegate();
		animation.sensitive(true);

		animation.onTermination(() -> {
			if (a != b) {
				a = b;
				animation.speedNegate();
			}
		});

		Mouse.Callbacks.Click.EVENT.register(((button, action, modifiers) -> {
			if (MinecraftClient.getInstance().currentScreen != this) return;

			if (button == Mouse.LEFT) {
				if (action.isPress()) {
					b = !b;
					animation.speedNegate();
				}
			}
		}));
	}

	/*
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

	 */

	@Override
	protected void init() {
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		renderBackground(context);

		//VanillaWidgets.Tooltip.render(context, box);

		Box another = FrameInfo.scaled().topLeft(box.bottomRight()).alignBottomRight(FrameInfo.scaled());

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
		).color(Palette.rainbow(interpolation.value() * 2 * Math.PI))
				.horizontalAlignment(horizontal).verticalAlignment(vertical).enableCulling().new Tooltip(Flat.Text.Tooltip.TooltipSnap.BOTH).render();

		 */

		/*
		FrameInfo.scaled().render(context, 0, flat -> flat.new Rectangle(Palette.WHITE));

		Box.fromCartesian(0, 0, 100, 100).render(context,
				flat -> flat
								.new Rectangle()
								.colorTopLeft(AccurateColor.RED)
								.colorBottomLeft(AccurateColor.BLACK)
								.colorBottomRight(AccurateColor.BLACK)
								.colorTopRight(Palette.WHITE)
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

		// FrameInfo.scaled().scaleCenter(0.3 * animation.value()).render(context, flat -> flat.new Rectangle().colors(Palette.MAGENTA));

		/*
		FrameInfo.scaled().squareInner().scale(0.4).scale(animation.value()).leftCenter(FrameInfo.scaled().scaleCenter(0.7)).render(context,
				flat -> flat.new Block(Blocks.BLUE_BED.getDefaultState(), Palette.BLACK, Quaternion.rotationXYZDegrees(90 + 45 * Curves.Sinusoidal.EASE.rewind().apply(-1, 1, (System.currentTimeMillis() % 2000) / 2000.0), 45, 0))
		);

		FrameInfo.scaled().squareInner().scale(0.4).scale(animation.value()).rightCenter(FrameInfo.scaled().scaleCenter(0.7)).render(context,
				flat -> flat.new Block(Blocks.AMETHYST_BLOCK.getDefaultState(), Palette.BLACK, Quaternion.rotationXYZDegrees(90 + 45 * Curves.Sinusoidal.EASE.rewind().apply(-1, 1, (System.currentTimeMillis() % 2000) / 2000.0), 45, 0))
		);

		FrameInfo.scaled().translateRight(-0.5).render(context,
				flat -> flat.new Text(section -> section.append("Block Entity Model")).color(Palette.Minecraft.AQUA).verticalAlignment(Section.Alignment.BOTTOM).horizontalAlignment(Paragraph.Alignment.CENTER)
		);

		FrameInfo.scaled().translateLeft(0.5).render(context,
				flat -> flat.new Text(section -> section.append("Block Model")).color(Palette.Minecraft.LIGHT_PURPLE).verticalAlignment(Section.Alignment.BOTTOM).horizontalAlignment(Paragraph.Alignment.CENTER)
		);

		 */

		/*
		FrameInfo.scaled().scaleCenter(0.5).render(context, flat -> flat.new Oval(Palette.CORAL)
				.mode(Flat.Oval.OvalMode.FILL_GRADIANT_OUT)
				.addColor(0, Palette.WHITE)
				.addColor(Math.PI, Palette.CYAN)
				.outlineDynamic(Flat.Oval.VertexProvider.INNER, 0.25)
				//.offset(-0.5)
				.arc(2 * Curves.Sinusoidal.EASE.apply(-Math.PI, Math.PI, System.currentTimeMillis() / 1000.0))
		);
		 */

		for (int i = 0; i < 100; i++) {
			int speed = i + 1;
			FrameInfo.scaled().squareInner().scaleCenter(1 - i / 100.0).render(context, flat -> flat.new Oval(Palette.CORAL)
					.outline(Flat.Oval.VertexProvider.INNER)
					.breadth(new Flat.Oval.Breadth.Dynamic(0.1))
					.mode(Flat.Oval.OvalMode.FILL_GRADIANT_OUT)
					.addColor(0, Palette.CYAN)
					.addColor(Math.PI, Palette.CRIMSON)
					.offset(2 * Curves.Sinusoidal.EASE.apply(-Math.PI, Math.PI, System.currentTimeMillis() / 1000.0 * speed / 100.0))
			);

		}

		FrameInfo.scaled().render(context, flat -> flat.new Text()
				.section(section -> section.append(String.format("%.2f", Equator.fps().orElse(0.0))))
				.horizontalAlignment(Paragraph.Alignment.CENTER)
				.verticalAlignment(Section.Alignment.CENTER)
		);
	}
}
