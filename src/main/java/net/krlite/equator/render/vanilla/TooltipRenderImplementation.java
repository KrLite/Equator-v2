package net.krlite.equator.render.vanilla;

import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.visual.color.Palette;
import net.minecraft.client.util.math.MatrixStack;

public class TooltipRenderImplementation {
	/**
	 * Renders a tooltip background with the default Minecraft(vanilla) style.
	 * @param matrixStack	The {@link MatrixStack} to render to.
	 * @param box			The {@link Box} to render the tooltip in.
	 */
	public static void render(MatrixStack matrixStack, Box box) {
		Box bleed = box.expand(-1);

		// Rectangle
		bleed.ready(Palette.Minecraft.TOOLTIP_BACKGROUND)
				.render(matrixStack);

		// Background border
		bleed.height(1).alignTop(box)
				.ready(Palette.Minecraft.TOOLTIP_BACKGROUND)
				.render(matrixStack);

		bleed.height(1).alignBottom(box)
				.ready(Palette.Minecraft.TOOLTIP_BACKGROUND)
				.render(matrixStack);

		bleed.width(1).alignLeft(box)
				.ready(Palette.Minecraft.TOOLTIP_BACKGROUND)
				.render(matrixStack);

		bleed.width(1).alignRight(box)
				.ready(Palette.Minecraft.TOOLTIP_BACKGROUND)
				.render(matrixStack);

		// Border
		bleed.height(1).alignTop(box.top() + 1)
				.ready(Palette.Minecraft.TOOLTIP_BORDER_LIGHT)
				.render(matrixStack);

		bleed.height(1).alignBottom(box.bottom() - 1)
				.ready(Palette.Minecraft.TOOLTIP_BORDER_DARK)
				.render(matrixStack);

		bleed.expand(-1).width(1).alignLeft(box.left() + 1).readyGradiant(
				renderer -> renderer
									.top(Palette.Minecraft.TOOLTIP_BORDER_LIGHT)
									.bottom(Palette.Minecraft.TOOLTIP_BORDER_DARK)
		).render(matrixStack);

		bleed.expand(-1).width(1).alignRight(box.right() - 1).readyGradiant(
				renderer -> renderer
									.top(Palette.Minecraft.TOOLTIP_BORDER_DARK)
									.bottom(Palette.Minecraft.TOOLTIP_BORDER_LIGHT)
		).render(matrixStack);
	}
}
