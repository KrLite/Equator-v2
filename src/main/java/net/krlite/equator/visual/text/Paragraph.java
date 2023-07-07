package net.krlite.equator.visual.text;

import net.krlite.equator.base.Cyclic;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.visual.color.AccurateColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.davidmoten.text.utils.WordWrap;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>Paragraph</h1>
 * Represents a paragraph of text.
 * @param text		The text of the paragraph.
 * @param scalar	The scalar to multiply the font size by.
 */
public record Paragraph(Text text, double scalar) {
	public enum Alignment implements Cyclic.Enum<Alignment> {
		LEFT((box, text, textRenderer, fontSize, scalar) -> box.topLeft()),
		CENTER((box, text, textRenderer, fontSize, scalar) -> box.topCenter().add(-fontSize * scalar * textRenderer.getWidth(text) / 2.0, 0)),
		RIGHT((box, text, textRenderer, fontSize, scalar) -> box.topRight().add(-fontSize * scalar * textRenderer.getWidth(text), 0));

		@FunctionalInterface
		interface AlignmentFunction {
			Vector apply(Box box, Text text, TextRenderer textRenderer, double fontSize, double scalar);
		}

		private final AlignmentFunction function;

		Alignment(AlignmentFunction function) {
			this.function = function;
		}

		public Vector apply(Box box, Text text, TextRenderer textRenderer, double fontSize, double scalar) {
			return function.apply(box, text, textRenderer, fontSize, scalar);
		}
	}

	public static final Pattern NEWLINE_PATTERN = Pattern.compile("\\r?\\n"), FORMATTING_PATTERN = Pattern.compile("§(?<code>[0-9a-fk-or])");
	public static final String NEWLINE = "\n";
	public static final Paragraph EMPTY = new Paragraph("", 0);

	public static Paragraph build(UnaryOperator<Paragraph> builder) {
		return builder.apply(EMPTY);
	}

	public static Paragraph spacing(double scalar) {
		return new Paragraph("", scalar);
	}

	public static Paragraph spacing() {
		return spacing(1);
	}

	public Paragraph(String text, double scalar) {
		this(Text.of(text), scalar);
	}

	public Paragraph(Text text) {
		this(text, 1);
	}

	public Paragraph(String text) {
		this(Text.of(text));
	}

	// text() is a record method

	// scalar() is a record method

	public Paragraph text(Text text) {
		return new Paragraph(text, scalar());
	}

	public Paragraph scalar(double scalar) {
		return new Paragraph(text(), scalar);
	}

	public boolean isSpacing() {
		return text().getString().isEmpty();
	}

	public double width(double fontSize) {
		return fontSize * scalar() * MinecraftClient.getInstance().textRenderer.getWidth(text());
	}

	public double height(double fontSize, double lineSpacing) {
		return fontSize * scalar() * MinecraftClient.getInstance().textRenderer.fontHeight * (isSpacing() ? 1 : (1 + lineSpacing));
	}

	public double wrappedHeight(double fontSize, double lineSpacing, double width) {
		return height(fontSize, lineSpacing) * countLines(fontSize, width);
	}

	public Text[] wrap(double fontSize, double width) {
		return isSpacing() ? new Text[] { text() } : Arrays.stream(concatFormatting(NEWLINE_PATTERN.matcher(
				WordWrap.from(text().getString())
						.breakWords(true)
						.insertHyphens(true)
						.maxWidth(width)
						.newLine(NEWLINE)
						.includeExtraWordChars("0123456789")
						.includeExtraWordChars("§")
						.stringWidth(charSequence -> fontSize * scalar() * MinecraftClient.getInstance().textRenderer.getWidth(
								Text.literal(FORMATTING_PATTERN.matcher(charSequence).replaceAll("")).setStyle(text().getStyle()))
						).wrap()
		).replaceAll(NEWLINE).split(NEWLINE))).map(s -> Text.literal(s).setStyle(text().getStyle())).toArray(Text[]::new);
	}

	private String[] concatFormatting(String[] lines) {
		String[] result = new String[lines.length];
		String currentFormatting = "";
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			result[i] = currentFormatting + line;

			Matcher matcher = FORMATTING_PATTERN.matcher(line);

			while (matcher.find()) {
				currentFormatting = matcher.group();
			}
		}
		return result;
	}

	public int countLines(double fontSize, double width) {
		return width <= 0 ? 0 : wrap(fontSize, width).length;
	}

	public void render(double fontSize, double lineSpacing, Box box, DrawContext context, TextRenderer textRenderer, AccurateColor color, Alignment alignment, boolean shadow) {
		if (box.w() <= 0) return;
		render(new LinkedList<>(Arrays.stream(wrap(fontSize, box.w())).toList()), fontSize, lineSpacing, box, context, textRenderer, color, alignment, shadow);
	}

	private void render(LinkedList<Text> lines, double fontSize, double lineSpacing, Box box, DrawContext context, TextRenderer textRenderer, AccurateColor color, Alignment alignment, boolean shadow) {
		Text line = lines.poll();

		if (line == null) return;

		if (lines.peek() != null) {
			lines.set(0, lines.peek().copy().styled(style -> style.withParent(line.getStyle())));
			render(lines, fontSize, lineSpacing, box.shift(0, height(fontSize, lineSpacing)), context, textRenderer, color, alignment, shadow);
		}

		context.getMatrices().push();

		Vector aligned = alignment.apply(box, line, textRenderer, fontSize, scalar());
		context.getMatrices().translate(aligned.x(), aligned.y(), 0);
		context.getMatrices().scale((float) (fontSize * scalar()), (float) (fontSize * scalar()), 1);

		context.drawText(textRenderer, line, 0, 0, color.toInt(), shadow);

		context.getMatrices().pop();
	}

	private void print(String text, boolean withFormattingPattern) {
		if (withFormattingPattern) {
			System.out.println(text);
		} else {
			System.out.println(FORMATTING_PATTERN.matcher(text).replaceAll(""));
		}
	}

	public void print(boolean withFormattingPattern) {
		print(text().getString(), withFormattingPattern);
	}

	public void print() {
		print(true);
	}

	public void print(double fontSize, double width, boolean withFormattingPattern) {
		Arrays.stream(wrap(fontSize, width)).forEach(text -> print(text.getString(), withFormattingPattern));
	}

	public void print(double fontSize, double width) {
		print(fontSize, width, true);
	}
}
