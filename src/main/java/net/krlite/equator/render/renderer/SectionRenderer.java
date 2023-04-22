package net.krlite.equator.render.renderer;

import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.math.geometry.Vector;
import net.krlite.equator.render.base.Renderable;
import net.krlite.equator.render.base.Scissor;
import net.krlite.equator.render.vanilla.TooltipRenderImplementation;
import net.krlite.equator.visual.color.AccurateColor;
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

/**
 * <h1>SectionRenderer</h1>
 * Based on {@link Box} and contains a {@link Section} to hold the text to render. An {@link AccurateColor} can be
 * applied to the text and a {@link Quaterniondc Quaternion} modifier can be applied during rendering.
 * @see Box
 * @see Quaterniondc
 * @see Section
 * @see AccurateColor
 * @param box			The {@link Box} to render the text in.
 * @param modifier		The {@link Quaterniondc Quaternion} modifier to apply during rendering.
 * @param section		The {@link Section} to hold the text to render.
 * @param color			The {@link AccurateColor} to apply to the text.
 * @param vertical		The {@link Section.Alignment} which determines the vertical alignment of the text.
 * @param horizontal	The {@link Paragraph.Alignment} which determines the horizontal alignment of the text.
 * @param shadow		Whether the text should be rendered with a shadow.
 */
public record SectionRenderer(
		Box box, Quaterniondc modifier, Section section, @Nullable AccurateColor color,
		Section.Alignment vertical, Paragraph.Alignment horizontal, boolean shadow
) implements Renderable {
	public SectionRenderer(Box box, Section section) {
		this(box, new Quaterniond(), section, null, Section.Alignment.TOP, Paragraph.Alignment.LEFT, false);
	}

	@Override
	public Box box() {
		return box;
	}

	@Override
	public Quaterniondc modifier() {
		return modifier;
	}

	@Override
	public Section section() {
		return section;
	}

	@Override
	public AccurateColor color() {
		return color;
	}

	@Override
	public Section.Alignment vertical() {
		return vertical;
	}

	@Override
	public Paragraph.Alignment horizontal() {
		return horizontal;
	}

	@Override
	public boolean shadow() {
		return shadow;
	}

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
		if (!isRenderable()) return;

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

		TooltipRenderImplementation.render(matrixStack, !ignoreVerticalAlignment ? box() : box().height(actualHeight + 2 * bleeding));
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

	public void print(boolean withFormattingPattern) {
		section().print(withFormattingPattern);
	}

	public void print() {
		print(true);
	}
}
