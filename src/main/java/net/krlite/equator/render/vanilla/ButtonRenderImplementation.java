package net.krlite.equator.render.vanilla;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import net.krlite.equator.base.Cyclic;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.visual.texture.Texture;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;

public class ButtonRenderImplementation {
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

	public static void renderButton(MatrixStack matrixStack, Box box, State state) {
		box.ready(Texture.fromIdentifier(ClickableWidget.WIDGETS_TEXTURE).uvBox(256, 256, 0, state.y(), 200, 20))
				.renderNineSliced(matrixStack, 20, 20, 4, 4, 200, 20);
	}
}
