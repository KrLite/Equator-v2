package net.krlite.equator.render.renderer;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.base.Renderable;
import net.krlite.equator.visual.color.AccurateColor;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
public record OvalRenderer(
		Box box, double offset, double radians,
		AccurateColor centerColor, ImmutableMap<Double, AccurateColor> colorMap, boolean mixing
) implements Renderable {
	public OvalRenderer(Box box, double offset, double radians, AccurateColor centerColor, ImmutableMap<Double, AccurateColor> colorMap, boolean mixing) {
		this.box = box;
		this.offset = offset;
		this.radians = radians;
		this.centerColor = centerColor;
		this.colorMap = ImmutableMap.copyOf(sortColorMap(colorMap));
		this.mixing = mixing;
	}

	public OvalRenderer(Box box, AccurateColor color) {
		this(box, 0, 2 * Math.PI, color, ImmutableMap.of(), false);
	}

	public OvalRenderer(Box box) {
		this(box, AccurateColor.TRANSPARENT);
	}

	@Override
	public Box box() {
		return box;
	}

	@Override
	public double offset() {
		return offset;
	}

	@Override
	public double radians() {
		return radians;
	}

	@Override
	public AccurateColor centerColor() {
		return centerColor;
	}

	@Override
	public ImmutableMap<Double, AccurateColor> colorMap() {
		return ImmutableMap.copyOf(mutableColorMap());
	}

	public Map<Double, AccurateColor> mutableColorMap() {
		return sortColorMap(colorMap);
	}

	public ImmutableMap<Double, AccurateColor> safeColorMap() {
		Map<Double, AccurateColor> originalMap = mutableColorMap();

		if (firstColor() != lastColor()) {
			if (!colorMap().containsKey(0.0) && !colorMap().containsKey(2 * Math.PI)) {
				AccurateColor color = firstColor();

				double firstOffset = originalMap.keySet().stream().findFirst().orElse(0.0);
				double offset = Math.abs(firstOffset - originalMap.keySet().stream().reduce((first, second) -> second).orElse(0.0));
				double factor = Theory.isZero(offset) ? 0 : (firstOffset / offset);
				color = mixing() ? color.mix(lastColor(), factor) : color.blend(lastColor(), factor);

				originalMap.put(0.0, color);
				originalMap.put(2 * Math.PI, color);
			} else if (colorMap().containsKey(0.0)) {
				originalMap.put(2 * Math.PI, colorMap().get(0.0));
			} else if (colorMap().containsKey(2 * Math.PI)) {
				originalMap.put(0.0, colorMap().get(2 * Math.PI));
			}
		}

		return ImmutableMap.copyOf(sortColorMap(originalMap));
	}

	public boolean mixing() {
		return mixing;
	}

	public OvalRenderer offset(double offset) {
		return new OvalRenderer(box(), offset, radians(), centerColor(), colorMap(), mixing());
	}

	public OvalRenderer radians(double radians) {
		return new OvalRenderer(box(), offset(), radians, centerColor(), colorMap(), mixing());
	}

	public OvalRenderer centerColor(AccurateColor centerColor) {
		return new OvalRenderer(box(), offset(), radians(), centerColor, colorMap(), mixing());
	}

	public OvalRenderer colorMap(Map<Double, AccurateColor> colorMap) {
		return new OvalRenderer(box(), offset(), radians(), centerColor(), ImmutableMap.copyOf(colorMap), mixing());
	}

	public OvalRenderer addColor(double offset, AccurateColor color) {
		return colorMap(ImmutableMap.<Double, AccurateColor>builder()
						.putAll(colorMap())
						.put(modOffset(offset), color)
						.build());
	}

	public OvalRenderer addColor(Map.Entry<? extends Double, ? extends AccurateColor> entry) {
		return addColor(entry.getKey(), entry.getValue());
	}

	public OvalRenderer mixing(boolean mixing) {
		return new OvalRenderer(box(), offset(), radians(), centerColor(), colorMap(), mixing);
	}

	public boolean existsColor() {
		return centerColor().hasColor() || colorMap().values().stream().anyMatch(AccurateColor::hasColor);
	}

	private static LinkedHashMap<Double, AccurateColor> sortColorMap(Map<Double, AccurateColor> colorMap) {
		return new LinkedHashMap<>(colorMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	private static double modOffset(double offset) {
		return Theory.mod(offset, 2 * Math.PI);
	}

	@Override
	public boolean isRenderable() {
		return Renderable.isLegal(box) && existsColor() && Theory.looseGreater(radians(), 0);
	}

	public AccurateColor firstColor() {
		return !colorMap().isEmpty() ? colorMap().values().stream().findFirst().orElseThrow() : existsColor() ? centerColor() : AccurateColor.TRANSPARENT;
	}

	public AccurateColor lastColor() {
		return !colorMap().isEmpty() ? colorMap().values().stream().reduce((first, second) -> second).orElseThrow() : existsColor() ? centerColor() : AccurateColor.TRANSPARENT;
	}

	public Map.Entry<Double, AccurateColor> nearestPrevious(double offset) {
		return !safeColorMap().isEmpty() ? safeColorMap().entrySet().stream().filter(entry -> entry.getKey() <= modOffset(offset)).reduce((first, second) -> second).orElseThrow() : new AbstractMap.SimpleEntry<>(0.0, existsColor() ? centerColor() : AccurateColor.TRANSPARENT);
	}

	public Map.Entry<Double, AccurateColor> nearestNext(double offset) {
		return !safeColorMap().isEmpty() ? safeColorMap().entrySet().stream().filter(entry -> entry.getKey() >= modOffset(offset)).findFirst().orElseThrow() : new AbstractMap.SimpleEntry<>(2 * Math.PI, existsColor() ? centerColor() : AccurateColor.TRANSPARENT);
	}

	public AccurateColor getColor(double offset) {
		if (!existsColor()) {
			return AccurateColor.TRANSPARENT;
		}

		if (colorMap().isEmpty()) {
			return centerColor();
		}

		if (safeColorMap().containsKey(offset)) {
			return safeColorMap().get(offset);
		}

		offset = modOffset(offset);

		final Map.Entry<Double, AccurateColor> prev = nearestPrevious(offset), next = nearestNext(offset);
		if (prev.getValue().looseEquals(next.getValue())) {
			return prev.getValue();
		}

		double weight = (offset - prev.getKey()) / Math.abs(next.getKey() - prev.getKey());

		return mixing()
					   ? prev.getValue().mix(next.getValue(), weight)
					   : prev.getValue().blend(next.getValue(), weight);
	}

	public double eccentricity() {
		return Math.sqrt(Math.abs(Math.pow(box().w(), 2) - Math.pow(box().h(), 2))) / Math.max(box().w(), box().h());
	}

	private Vector getVertex(double offset, double radiusFactor) {
		offset = modOffset(offset);
		return box().center().add(Vector.fromCartesian(
				radiusFactor * box().w() / 2 * Math.cos(offset),
				radiusFactor * box().h() / 2 * Math.sin(offset)
		));
	}

	private void renderVertex(BufferBuilder builder, Matrix4f matrix, Vector vertex, AccurateColor color, float z) {
		builder.vertex(matrix, (float) vertex.x(), (float) vertex.y(), z)
				.color(color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(), color.opacityAsFloat())
				.next();
	}

	private void renderVertex(BufferBuilder builder, Matrix4f matrix, double offset, double radiusFactor, AccurateColor color, float z) {
		renderVertex(builder, matrix, getVertex(offset, radiusFactor), color, z);
	}

	private static final double delta = 0.01;

	public enum ColoringMode {
		FILL((ovalRenderer, offset, radiusFactor) -> ovalRenderer.getColor(offset)),
		GRADIANT_OUT((ovalRenderer, offset, radiusFactor) ->
							 ovalRenderer.mixing()
									 ? ovalRenderer.centerColor().mix(ovalRenderer.getColor(offset), radiusFactor)
									 : ovalRenderer.centerColor().blend(ovalRenderer.getColor(offset), radiusFactor)),
		GRADIANT_IN((ovalRenderer, offset, radiusFactor) ->
								ovalRenderer.mixing()
										? ovalRenderer.getColor(offset).mix(ovalRenderer.centerColor(), radiusFactor)
										: ovalRenderer.getColor(offset).blend(ovalRenderer.centerColor(), radiusFactor)),
		FILL_GRADIANT_OUT((ovalRenderer, offset, radiusFactor) -> Theory.looseEquals(radiusFactor, 1)
																		  ? FILL.getColor(ovalRenderer, offset, radiusFactor)
																		  : ovalRenderer.centerColor()),
		FILL_GRADIANT_IN((ovalRenderer, offset, radiusFactor) -> !Theory.looseEquals(radiusFactor, 1)
																		 ? FILL.getColor(ovalRenderer, offset, radiusFactor)
																		 : ovalRenderer.centerColor());

		interface ColorFunction {
			AccurateColor getColor(OvalRenderer ovalRenderer, double offset, double radiusFactor);
		}

		private final ColorFunction colorFunction;

		ColoringMode(ColorFunction colorFunction) {
			this.colorFunction = colorFunction;
		}

		public AccurateColor getColor(OvalRenderer ovalRenderer, double offset, double radiusFactor) {
			return colorFunction.getColor(ovalRenderer, offset, radiusFactor);
		}
	}

	public void render(MatrixStack matrixStack, double radiusFactor, ColoringMode coloringMode, float z) {
		if (!isRenderable()) return;

		if (Theory.looseEquals(radiusFactor, 1)) return;

		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		RenderSystem.disableCull(); // Prevents triangles from being culled

		BufferBuilder builder = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();

		builder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

		for (double offset = offset(); offset < offset() + radians() + delta; offset += delta) {
			double clampedOffset = Math.min(offset, offset() + radians()); // Prevents offset from exceeding the end of the arc
			Vector vertex = getVertex(clampedOffset, 1);

			// Render the inner (center) vertex
			if (Theory.isZero(radiusFactor)) {
				renderVertex(builder, matrix, box().center(), centerColor(), z);
			} else {
				renderVertex(builder, matrix, getVertex(clampedOffset, radiusFactor),
						coloringMode.getColor(this, clampedOffset - offset(), radiusFactor), z);
			}

			// Render the outer vertex
			renderVertex(builder, matrix, vertex,
					coloringMode.getColor(this, clampedOffset - offset(), 1), z);
		}

		BufferRenderer.drawWithGlobalProgram(builder.end());

		RenderSystem.disableBlend();
		RenderSystem.enableCull();
	}

	public void render(MatrixStack matrixStack, ColoringMode coloringMode, float z) {
		render(matrixStack, 0, coloringMode, z);
	}

	public void render(MatrixStack matrixStack, ColoringMode coloringMode) {
		render(matrixStack, coloringMode, 0);
	}

	public void render(MatrixStack matrixStack, float z) {
		render(matrixStack, ColoringMode.GRADIANT_OUT, z);
	}

	public void render(MatrixStack matrixStack) {
		render(matrixStack, 0);
	}

	public enum OutliningMode {
		INWARD((ovalRenderer, offset, thickness) -> ovalRenderer.getVertex(offset, 1 - thickness / ovalRenderer.getVertex(offset, 1).distanceTo(ovalRenderer.box().center())),
				(ovalRenderer, offset, thickness) -> ovalRenderer.getVertex(offset, 1)),
		OUTWARD((ovalRenderer, offset, thickness) -> ovalRenderer.getVertex(offset, 1),
				(ovalRenderer, offset, thickness) -> ovalRenderer.getVertex(offset, 1 + thickness / ovalRenderer.getVertex(offset, 1).distanceTo(ovalRenderer.box().center()))),
		MIDDLE((ovalRenderer, offset, thickness) -> ovalRenderer.getVertex(offset, 1 - thickness / 2 / ovalRenderer.getVertex(offset, 1).distanceTo(ovalRenderer.box().center())),
				(ovalRenderer, offset, thickness) -> ovalRenderer.getVertex(offset, 1 + thickness / 2 / ovalRenderer.getVertex(offset, 1).distanceTo(ovalRenderer.box().center())));

		interface OutliningFunction {
			Vector getInner(OvalRenderer ovalRenderer, double offset, double thickness);
		}

		private final OutliningFunction innerFunction, outerFunction;

		OutliningMode(OutliningFunction innerFunction, OutliningFunction outerFunction) {
			this.innerFunction = innerFunction;
			this.outerFunction = outerFunction;
		}

		public Vector getInner(OvalRenderer ovalRenderer, double offset, double thickness) {
			return innerFunction.getInner(ovalRenderer, offset, thickness);
		}

		public Vector getOuter(OvalRenderer ovalRenderer, double offset, double thickness) {
			return outerFunction.getInner(ovalRenderer, offset, thickness);
		}
	}

	public void renderOutline(MatrixStack matrixStack, double thickness, OutliningMode outliningMode, float z) {
		if (!isRenderable()) return;

		if (Theory.isZero(thickness)) return;

		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		RenderSystem.disableCull(); // Prevents triangles from being culled

		BufferBuilder builder = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();

		builder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

		for (double offset = offset(); offset < offset() + radians() + delta; offset += delta) {
			double clampedOffset = Math.min(offset, offset() + radians()); // Prevents offset from exceeding the end of the arc

			// Render the inner vertex
			renderVertex(builder, matrix, outliningMode.getInner(this, clampedOffset, thickness),
					ColoringMode.FILL.getColor(this, clampedOffset - offset(), 1), z);

			// Render the outer vertex
			renderVertex(builder, matrix, outliningMode.getOuter(this, clampedOffset, thickness),
					ColoringMode.FILL.getColor(this, clampedOffset - offset(), 1), z);
		}

		BufferRenderer.drawWithGlobalProgram(builder.end());

		RenderSystem.disableBlend();
		RenderSystem.enableCull();
	}

	public void renderOutline(MatrixStack matrixStack, double thickness, OutliningMode outliningMode) {
		renderOutline(matrixStack, thickness, outliningMode, 0);
	}

	public void renderOutline(MatrixStack matrixStack, double thickness, float z) {
		renderOutline(matrixStack, thickness, OutliningMode.INWARD, z);
	}

	public void renderOutline(MatrixStack matrixStack, double thickness) {
		renderOutline(matrixStack, thickness, 0);
	}
}

 */
