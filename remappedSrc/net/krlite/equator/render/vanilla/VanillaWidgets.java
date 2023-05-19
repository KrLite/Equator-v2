package net.krlite.equator.render.vanilla;

import net.krlite.equator.base.Cyclic;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.render.renderer.Flat.Rectangle;
import net.krlite.equator.render.renderer.Flat.Rectangle.NineSliced;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.texture.Texture;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;

public class VanillaWidgets {
	public static class Button {
		public enum State implements Cyclic.Enum<State> {
			UNAVAILABLE,
			AVAILABLE,
			FOCUSED;

			private final int y;

			State() {
				this.y = 46 + ordinal() * 20;
			}

			public int y() {
				return y;
			}
		}

		public static void render(MatrixStack matrixStack, Box box, State state) {
			new Flat(matrixStack, 0, box)
					.new Rectangle(Texture.fromIdentifier(ClickableWidget.WIDGETS_TEXTURE).uvBox(256, 256, 0, state.y(), 200, 20))
					.new NineSliced(20, 20, 4, 4, 200, 20)
					.render();
		}
	}

	public static class Tooltip {
		public static void render(MatrixStack matrixStack, Box box) {
			Box bleed = box.expand(-1);
			Flat flat = new Flat(matrixStack, 0, box);

			// Rectangle
			flat.new Rectangle(Palette.Minecraft.TOOLTIP_BACKGROUND).render();

			// Background border
			flat.box(bleed.height(1))
					.new Rectangle(Palette.Minecraft.TOOLTIP_BACKGROUND).render();

			flat.box(bleed.height(1).alignBottom(box))
					.new Rectangle(Palette.Minecraft.TOOLTIP_BACKGROUND).render();

			flat.box(bleed.width(1).alignLeft(box))
					.new Rectangle(Palette.Minecraft.TOOLTIP_BACKGROUND).render();

			flat.box(bleed.width(1).alignRight(box))
					.new Rectangle(Palette.Minecraft.TOOLTIP_BACKGROUND).render();

			// Border
			flat.box(bleed.height(1).alignTop(box.top() + 1))
					.new Rectangle(Palette.Minecraft.TOOLTIP_BORDER_LIGHT).render();

			flat.box(bleed.height(1).alignBottom(box.bottom() - 1))
					.new Rectangle(Palette.Minecraft.TOOLTIP_BORDER_DARK).render();

			flat.box(bleed.expand(-1).width(1).alignLeft(box.left() + 1))
					.new Rectangle()
					.colorTop(Palette.Minecraft.TOOLTIP_BORDER_LIGHT)
					.colorBottom(Palette.Minecraft.TOOLTIP_BORDER_DARK)
					.render();

			flat.box(bleed.expand(-1).width(1).alignRight(box.right() - 1))
					.new Rectangle()
					.colorTop(Palette.Minecraft.TOOLTIP_BORDER_LIGHT)
					.colorBottom(Palette.Minecraft.TOOLTIP_BORDER_DARK)
					.render();
		}
	}
}
