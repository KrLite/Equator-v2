package net.krlite.equator.visual.text;

import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.visual.color.AccurateColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.davidmoten.text.utils.WordWrap;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Paragraph(Text text, double fontSize, double lineSpacing) {
	public static final Pattern NEWLINE_PATTERN = Pattern.compile("\\r?\\n"), FORMATTING_PATTERN = Pattern.compile("§(?<code>[0-9a-fk-or])");
	public static final String NEWLINE = "\n";

	public static Paragraph of(Text text, double fontSize, double lineSpacing) {
		return new Paragraph(text, fontSize, lineSpacing);
	}

	public static Paragraph of(Text text) {
		return new Paragraph(text, Section.DEFAULT_FONT_SIZE, Section.DEFAULT_LINE_SPACING);
	}

	public static Paragraph title(Text text, double fontSize, double lineSpacing, double titleScaling) {
		return new Paragraph(text, fontSize * titleScaling, lineSpacing);
	}

	public static Paragraph title(Text text) {
		return title(text, Section.DEFAULT_FONT_SIZE, Section.DEFAULT_LINE_SPACING, Section.DEFAULT_TITLE_SCALING);
	}

	public static Paragraph subtitle(Text text, double fontSize, double lineSpacing, double subtitleScaling) {
		return new Paragraph(text, fontSize * subtitleScaling, lineSpacing);
	}

	public static Paragraph subtitle(Text text) {
		return subtitle(text, Section.DEFAULT_FONT_SIZE, Section.DEFAULT_LINE_SPACING, Section.DEFAULT_SUBTITLE_SCALING);
	}

	public static Paragraph spacing(double fontSize, double lineSpacing, double paragraphSpacing) {
		return new Paragraph(Text.of(""), fontSize * paragraphSpacing, lineSpacing);
	}

	public static Paragraph spacing(double paragraphSpacing) {
		return spacing(Section.DEFAULT_FONT_SIZE, Section.DEFAULT_LINE_SPACING, paragraphSpacing);
	}

	public static Paragraph spacing() {
		return spacing(Section.DEFAULT_PARAGRAPH_SPACING);
	}

	public Paragraph(Text text) {
		this(text, Section.DEFAULT_FONT_SIZE, Section.DEFAULT_LINE_SPACING);
	}

	// text() is a record method

	// fontSize() is a record method

	// lineSpacing() is a record method

	public Paragraph text(Text text) {
		return new Paragraph(text, fontSize(), lineSpacing());
	}

	public Paragraph fontSize(double fontSize) {
		return new Paragraph(text(), fontSize, lineSpacing());
	}

	public Paragraph lineSpacing(double lineSpacing) {
		return new Paragraph(text(), fontSize(), lineSpacing);
	}

	public boolean isSpacing() {
		return text().getString().isEmpty();
	}

	public double height(boolean withSpacing) {
		return fontSize() * MinecraftClient.getInstance().textRenderer.fontHeight * (withSpacing ? 1 + lineSpacing : 1);
	}

	public double height() {
		return height(true);
	}

	public double heightAuto() {
		return height(!isSpacing());
	}

	public double totalHeight(double width, boolean withSpacing) {
		return height(withSpacing) * countLines(width);
	}

	public double totalHeight(double width) {
		return totalHeight(width, true);
	}

	public double totalHeightAuto(double width) {
		return totalHeight(width, !isSpacing());
	}

	public Text[] wrap(double width) {
		return isSpacing() ? new Text[]{text()} : Arrays.stream(concatFormatting(NEWLINE_PATTERN.matcher(
				WordWrap.from(text().getString())
						.breakWords(true)
						.insertHyphens(true)
						.maxWidth(width)
						.newLine(NEWLINE)
						.includeExtraWordChars("0123456789")
						.includeExtraWordChars("§")
						.stringWidth(charSequence -> fontSize() * MinecraftClient.getInstance().textRenderer.getWidth(
								Text.literal(FORMATTING_PATTERN.matcher(charSequence).replaceAll("")).setStyle(text().getStyle())))
						.wrap()
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

	public int countLines(double width) {
		return wrap(width).length;
	}

	public void render(Box box, TextRenderer textRenderer, MatrixStack matrixStack, AccurateColor color, boolean shadow) {
		render(wrap(box.width().magnitude()), box, textRenderer, matrixStack, color, shadow);
	}

	private void render(Text[] lines, Box box, TextRenderer textRenderer, MatrixStack matrixStack, AccurateColor color, boolean shadow) {
		Text line = lines[0];

		if (lines.length > 1) {
			Text[] copied = Arrays.copyOfRange(lines, 1, lines.length);
			copied[0] = copied[0].copy().styled(style -> style.withParent(line.getStyle()));
			render(copied, box.shift(0, heightAuto()), textRenderer, matrixStack, color, shadow);
		}

		matrixStack.push();

		matrixStack.translate(box.topLeft().x(), box.topLeft().y(), 0);
		matrixStack.scale((float) fontSize(), (float) fontSize(), 1);

		if (shadow) {
			textRenderer.drawWithShadow(matrixStack, line, 0, 0, color.toInt());
		} else {
			textRenderer.draw(matrixStack, line, 0, 0, color.toInt());
		}

		matrixStack.pop();
	}
}
