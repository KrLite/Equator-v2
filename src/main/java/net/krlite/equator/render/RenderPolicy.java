package net.krlite.equator.render;

import net.krlite.equator.math.logic.flat.FlatGate;
import net.krlite.equator.math.logic.flat.FlatTransform;
import net.minecraft.client.util.math.MatrixStack;

public record RenderPolicy(MatrixStack matrixStack) {
	public class Flat {


		public class Subdivision {
			// Constructors

			public Subdivision(FlatTransform modifier, FlatGate limiter) {
				this.modifier = modifier;
				this.limiter = limiter;
			}

			// Fields

			private final FlatTransform modifier;
			private final FlatGate limiter;

			// Accessors

			public FlatTransform modifier() {
				return modifier;
			}

			public FlatGate limiter() {
				return limiter;
			}

			// 'Subdivision'
		}

		// 'Flat'
	}

	// 'RenderPolicy'
}
