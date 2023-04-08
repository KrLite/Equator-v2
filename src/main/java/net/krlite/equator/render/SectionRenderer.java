package net.krlite.equator.render;

import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.math.geometry.Vector;
import net.krlite.equator.render.base.Renderable;
import net.krlite.equator.render.base.Scissor;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;

import java.util.function.UnaryOperator;

public record SectionRenderer(Box box, Quaterniondc modifier, Section section, @Nullable AccurateColor color, Section.Alignment vertical, Paragraph.Alignment horizontal, boolean shadow) implements Renderable {
	public SectionRenderer(Box box, Section section) {
		this(box, new Quaterniond(), section, null, Section.Alignment.TOP, Paragraph.Alignment.LEFT, false);
	}

	// box() is a record method

	// modifier() is a record method

	// section() is a record method

	// color() is a record method

	// vertical() is a record method

	// horizontal() is a record method

	// shadow() is a record method

	public SectionRenderer modifier(Quaterniondc modifier) {
		return new SectionRenderer(box(), modifier, section(), color(), vertical(), horizontal(), shadow());
	}

	public SectionRenderer modifier(UnaryOperator<Quaterniondc> modifier) {
		return modifier(modifier.apply(modifier()));
	}

	public SectionRenderer removeModifier() {
		return modifier(new Quaterniond());
	}

	public SectionRenderer section(Section section) {
		return new SectionRenderer(box(), modifier(), section, color(), vertical(), horizontal(), shadow());
	}

	public SectionRenderer section(UnaryOperator<Section> section) {
		return section(section.apply(section()));
	}

	public SectionRenderer color(AccurateColor color) {
		return new SectionRenderer(box(), modifier(), section(), color, vertical(), horizontal(), shadow());
	}

	public SectionRenderer color(UnaryOperator<AccurateColor> color) {
		return color(color.apply(color()));
	}

	public SectionRenderer removeColor() {
		return color(color -> null);
	}

	public SectionRenderer vertical(Section.Alignment vertical) {
		return new SectionRenderer(box(), modifier(), section(), color(), vertical, horizontal(), shadow());
	}

	public SectionRenderer cycleVertical(boolean reverse) {
		return vertical(vertical().fromBoolean(reverse));
	}

	public SectionRenderer horizontal(Paragraph.Alignment horizontal) {
		return new SectionRenderer(box(), modifier(), section(), color(), vertical(), horizontal, shadow());
	}

	public SectionRenderer cycleHorizontal(boolean reverse) {
		return horizontal(horizontal().fromBoolean(reverse));
	}

	public SectionRenderer shadow(boolean shadow) {
		return new SectionRenderer(box(), modifier(), section(), color(), vertical(), horizontal(), shadow);
	}

	public SectionRenderer withShadow() {
		return shadow(true);
	}

	public SectionRenderer withoutShadow() {
		return shadow(false);
	}

	private SectionRenderer preserve(Box box) {
		return new SectionRenderer(box, modifier(), section(), color(), vertical(), horizontal(), shadow());
	}

	public double actualHeight() {
		return section().actualHeight(box().w());
	}

	@Override
	public boolean isRenderable() {
		return Renderable.isBoxLegal(box()) && !section().isEmpty();
	}

	public void render(MatrixStack matrixStack, TextRenderer textRenderer, boolean cut) {
		if (!isRenderable()) {
			return;
		}

		Scissor scissor = box().scissor();

		if (cut) {
			scissor.snipOn();
		}

		matrixStack.push();
		matrixStack.translate(box().x(), box().y(), 0);
		matrixStack.multiply(new Quaternionf(modifier()));

		section().render(box().alignTopLeft(Vector.ZERO), matrixStack, textRenderer, color(), vertical(), horizontal(), shadow());

		matrixStack.pop();

		if (cut) {
			scissor.snipOff();
		}
	}

	public void render(MatrixStack matrixStack, TextRenderer textRenderer) {
		render(matrixStack, textRenderer, false);
	}

	public void render(MatrixStack matrixStack, boolean cut) {
		render(matrixStack, MinecraftClient.getInstance().textRenderer, cut);
	}

	public void render(MatrixStack matrixStack) {
		render(matrixStack, false);
	}

	public void renderTooltip(MatrixStack matrixStack, TextRenderer textRenderer, double bleeding, boolean ignoreVerticalAlignment) {
		Box preserved = box().expand(-bleeding);
		double actualHeight = preserve(preserved).actualHeight();

		renderTooltipBackground(matrixStack, !ignoreVerticalAlignment ? box() : box().height(actualHeight + 2 * bleeding));
		preserve(!ignoreVerticalAlignment ? preserved : preserved.height(actualHeight)).render(matrixStack, textRenderer, true);
	}

	public void renderTooltip(MatrixStack matrixStack, double bleeding, boolean ignoreVerticalAlignment) {
		renderTooltip(matrixStack, MinecraftClient.getInstance().textRenderer, bleeding, ignoreVerticalAlignment);
	}

	public void renderTooltip(MatrixStack matrixStack, TextRenderer textRenderer) {
		renderTooltip(matrixStack, textRenderer, 3, true);
	}

	public void renderTooltip(MatrixStack matrixStack) {
		renderTooltip(matrixStack, MinecraftClient.getInstance().textRenderer);
	}

	private void renderTooltipBackground(MatrixStack matrixStack, Box box) {
		Box bleed = box.expand(-1);

		// Rectangle
		bleed.ready(Palette.Minecraft.TOOLTIP_BACKGROUND).render(matrixStack);

		// Background border
		bleed.height(1).alignTop(box.top()).ready(Palette.Minecraft.TOOLTIP_BACKGROUND)
				.render(matrixStack);

		bleed.height(1).alignBottom(box.bottom()).ready(Palette.Minecraft.TOOLTIP_BACKGROUND)
				.render(matrixStack);

		bleed.width(1).alignLeft(box.left()).ready(Palette.Minecraft.TOOLTIP_BACKGROUND)
				.render(matrixStack);

		bleed.width(1).alignRight(box.right()).ready(Palette.Minecraft.TOOLTIP_BACKGROUND)
				.render(matrixStack);

		// Border
		bleed.height(1).alignTop(box.top() + 1).ready(Palette.Minecraft.TOOLTIP_BORDER_LIGHT)
				.render(matrixStack);

		bleed.height(1).alignBottom(box.bottom() - 1).ready(Palette.Minecraft.TOOLTIP_BORDER_DARK)
				.render(matrixStack);

		bleed.expand(-1).width(1).alignLeft(box.left() + 1).readyGradiant()
				.top(Palette.Minecraft.TOOLTIP_BORDER_LIGHT)
				.bottom(Palette.Minecraft.TOOLTIP_BORDER_DARK)
				.render(matrixStack);

		bleed.expand(-1).width(1).alignRight(box.right() - 1).readyGradiant()
				.top(Palette.Minecraft.TOOLTIP_BORDER_DARK)
				.bottom(Palette.Minecraft.TOOLTIP_BORDER_LIGHT)
				.render(matrixStack);
	}
}
