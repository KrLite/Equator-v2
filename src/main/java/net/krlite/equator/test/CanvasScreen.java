package net.krlite.equator.test;

import net.krlite.equator.frame.FrameInfo;
import net.krlite.equator.input.Mouse;
import net.krlite.equator.math.algebra.Curves;
import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.visual.animation.Interpolation;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CanvasScreen extends Screen {
	public CanvasScreen() {
		super(Text.of("Canvas"));
	}

	private final Interpolation interpolation = new Interpolation(0, 1, 120);

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

		Box box = FrameInfo.Scaled.fullScreen().translateRight(-0.9 * interpolation.value()).translateTop(0.5 * interpolation.value());

		box.ready(AccurateColor.BLACK).render(matrixStack);
		/*
		box.translateTop(0.625).ready(AccurateColor.DARK_GRAY).render(matrixStack);
		box.translateTop(0.75).ready(AccurateColor.GRAY).render(matrixStack);

		Paragraph.title(Text.of("Hello, world!")).render(box.translateTop(0.5), textRenderer, matrixStack, AccurateColor.GRAY, false);
		Paragraph.subtitle(Text.of("Subtitle")).render(box.translateTop(0.625), textRenderer, matrixStack, AccurateColor.LIGHT_GRAY, false);
		Paragraph.of(Text.of("Paragraph with wrapped text. Powered by word-wrap!")).render(box.translateTop(0.75), textRenderer, matrixStack, AccurateColor.WHITE, false);

		 */

		new Section(Text.of("Hello, world!"), 1 + 2.3 * (1 - interpolation.value())).fontSize(1 - 0.6 * interpolation.value())
				.append(Text.of("§oP§4a§5ragraph A - write something here.").copy().styled(style -> style.withBold(true)))
				.append(Text.of("Paragraph B - another paragraph."))
				.appendSubtitle(Text.of("Subtitle Without Spacing at Top"), false)
				.render(box, textRenderer, matrixStack, Palette.rainbow(Curves.SINE.apply(0.5, 1, System.currentTimeMillis() / 1000.0)), false);
	}
}
