package net.krlite.equator.visual.text;

import net.krlite.equator.base.Cyclic;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.visual.color.AccurateColor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * <h1>Section</h1>
 * Represents a section of text.
 * @param fontSize			The font size.
 * @param titleScalar		The scalar to multiply the font size by for titles.
 * @param subtitleScalar	The scalar to multiply the font size by for subtitles.
 * @param lineSpacing		The spacing between lines.
 * @param paragraphSpacing	The spacing between paragraphs.
 * @param paragraphs		The paragraphs of text.
 * @see Paragraph
 */
public record Section(
		double fontSize, double titleScalar, double subtitleScalar,
		double lineSpacing, double paragraphSpacing, Paragraph... paragraphs
) {
	public enum Alignment implements Cyclic.Enum<Alignment> {
		TOP((box, height) -> box),
		CENTER((box, height) -> box.top(box.yCenter()).shift(0, -height / 2)),
		BOTTOM((box, height) -> box.top(box.bottom()).shift(0, -height));

		@FunctionalInterface
		interface AlignmentFunction {
			Box apply(Box box, double height);
		}

		private final AlignmentFunction function;

		Alignment(AlignmentFunction function) {
			this.function = function;
		}

		public Box apply(Box box, double height) {
			return function.apply(box, height);
		}
	}

	public static final Section DEFAULT = new Section();

	public static Section build(UnaryOperator<Section> builder) {
		return builder.apply(DEFAULT);
	}

	public Section(double fontSize, double titleScalar, double subtitleScalar, double lineSpacing, double paragraphSpacing) {
		this(fontSize, titleScalar, subtitleScalar, lineSpacing, paragraphSpacing, new Paragraph[0]);
	}

	public Section(double fontSize, double titleScalar, double subtitleScalar) {
		this(fontSize, titleScalar, subtitleScalar, DEFAULT_LINE_SPACING, DEFAULT_PARAGRAPH_SPACING);
	}

	public Section(double fontSize) {
		this(fontSize, DEFAULT_TITLE_SCALAR, DEFAULT_SUBTITLE_SCALAR);
	}

	public Section() {
		this(DEFAULT_FONT_SIZE);
	}

	Section(Section section, Paragraph... paragraphs) {
		this(section.fontSize(), section.titleScalar(), section.subtitleScalar(), section.lineSpacing(), section.paragraphSpacing(),
				section.isEmpty() ? paragraphs : Stream.concat(Stream.of(section.paragraphs()), Stream.of(paragraphs)).toArray(Paragraph[]::new));
	}

	public static final double DEFAULT_FONT_SIZE = 1, DEFAULT_TITLE_SCALAR = 1.5, DEFAULT_SUBTITLE_SCALAR = 1.2, DEFAULT_LINE_SPACING = 1.0 / 10.0, DEFAULT_PARAGRAPH_SPACING = 1;

	// fontSize() is a record method

	// titleScalar() is a record method

	// subtitleScalar() is a record method

	// lineSpacing() is a record method

	// paragraphSpacing() is a record method

	// paragraphs() is a record method

	public Section fontSize(double fontSize) {
		return new Section(fontSize, titleScalar(), subtitleScalar(), lineSpacing(), paragraphSpacing(), paragraphs());
	}

	public Section lineSpacing(double lineSpacing) {
		return new Section(fontSize(), titleScalar(), subtitleScalar(), lineSpacing, paragraphSpacing(), paragraphs());
	}

	public Section paragraphSpacing(double paragraphSpacing) {
		return new Section(fontSize(), titleScalar(), subtitleScalar(), lineSpacing(), paragraphSpacing, paragraphs());
	}

	public boolean isEmpty() {
		return paragraphs().length == 0 || Arrays.stream(paragraphs()).allMatch(Paragraph::isSpacing);
	}

	public double width() {
		return isEmpty() ? 0 : Arrays.stream(paragraphs()).mapToDouble(paragraph -> paragraph.width(fontSize())).max().orElse(0);
	}

	public double height() {
		return isEmpty() ? 0 : Arrays.stream(paragraphs()).mapToDouble(paragraph -> paragraph.height(fontSize(), lineSpacing())).sum();
	}

	public double wrappedHeight(double width) {
		return isEmpty() ? 0 : Arrays.stream(paragraphs()).mapToDouble(paragraph -> paragraph.wrappedHeight(fontSize(), lineSpacing(), width)).sum();
	}

	private Section appendParagraphRaw(Paragraph paragraph) {
		return new Section(this, paragraph);
	}

	public Section appendSpacing(double spacing) {
		return appendParagraphRaw(Paragraph.spacing(spacing));
	}

	public Section appendSpacing() {
		return appendSpacing(1);
	}

	private Section appendParagraph(Paragraph paragraph, double spacing) {
		return Arrays.stream(paragraphs()).skip(Math.max(0, paragraphs().length - 1)).findFirst().map(lastParagraph -> {
			if (paragraph.isSpacing() || lastParagraph.isSpacing()) {
				return appendParagraphRaw(paragraph);
			}
			else {
				return appendSpacing(spacing).appendParagraphRaw(paragraph);
			}
		}).orElseGet(() -> appendParagraphRaw(paragraph));
	}

	private Section appendParagraph(Paragraph paragraph) {
		return appendParagraph(paragraph, 1);
	}

	public Section append(Paragraph paragraph, boolean hasSpacing) {
		return hasSpacing ? appendParagraph(paragraph) : appendParagraphRaw(paragraph);
	}

	public Section append(Paragraph paragraph) {
		return append(paragraph, true);
	}

	public Section append(Text text, double scalar, boolean hasSpacing) {
		return append(new Paragraph(text, scalar), hasSpacing);
	}

	public Section append(String text, double scalar, boolean hasSpacing) {
		return append(Text.of(text), scalar, hasSpacing);
	}

	public Section append(Text text, boolean hasSpacing) {
		return append(text, 1, hasSpacing);
	}

	public Section append(String text, boolean hasSpacing) {
		return append(Text.of(text), hasSpacing);
	}

	public Section append(Text text, double scalar) {
		return append(text, scalar, true);
	}

	public Section append(String text, double scalar) {
		return append(Text.of(text), scalar);
	}

	public Section append(Text text) {
		return append(text, 1);
	}

	public Section append(String text) {
		return append(Text.of(text));
	}

	public Section appendTitle(Text text, boolean bold, boolean hasSpacing) {
		return append(new Paragraph(text.copy().styled(style -> style.withBold(bold)), titleScalar()), hasSpacing);
	}

	public Section appendTitle(String text, boolean bold, boolean hasSpacing) {
		return appendTitle(Text.of(text), bold, hasSpacing);
	}

	public Section appendTitle(Text text, boolean bold) {
		return appendTitle(text, bold, true);
	}

	public Section appendTitle(String text, boolean bold) {
		return appendTitle(Text.of(text), bold);
	}

	public Section appendTitle(Text text) {
		return appendTitle(text, false);
	}

	public Section appendTitle(String text) {
		return appendTitle(Text.of(text));
	}

	public Section appendSubtitle(Text text, boolean bold, boolean hasSpacing) {
		return append(new Paragraph(text.copy().styled(style -> style.withBold(bold)), subtitleScalar()), hasSpacing);
	}

	public Section appendSubtitle(String text, boolean bold, boolean hasSpacing) {
		return appendSubtitle(Text.of(text), bold, hasSpacing);
	}

	public Section appendSubtitle(Text text, boolean bold) {
		return appendSubtitle(text, bold, true);
	}

	public Section appendSubtitle(String text, boolean bold) {
		return appendSubtitle(Text.of(text), bold);
	}

	public Section appendSubtitle(Text text) {
		return appendTitle(text, false);
	}

	public Section appendSubtitle(String text) {
		return appendTitle(Text.of(text));
	}

	public void render(Box box, DrawContext context, TextRenderer textRenderer, AccurateColor color, Alignment vertical, Paragraph.Alignment horizontal, boolean shadow) {
		render(new LinkedList<>(Arrays.stream(paragraphs()).toList()), vertical.apply(box, wrappedHeight(box.w())), context, textRenderer, color, vertical, horizontal, shadow);
	}

	private void render(LinkedList<Paragraph> paragraphs, Box box, DrawContext context, TextRenderer textRenderer, AccurateColor color, Alignment vertical, Paragraph.Alignment horizontal, boolean shadow) {
		Paragraph paragraph = paragraphs.poll();

		if (paragraph == null) return;

		if (paragraphs.peek() != null) {
			render(paragraphs, box.shift(0, paragraph.wrappedHeight(fontSize(), lineSpacing(), box.w())),
					context, textRenderer, color, vertical, horizontal, shadow);
		}

		paragraph.render(fontSize(), lineSpacing(), box, context, textRenderer, color, horizontal, shadow);
	}

	public void print(boolean withFormattingPattern) {
		Arrays.stream(paragraphs()).forEach(paragraph -> paragraph.print(withFormattingPattern));
	}

	public void print() {
		print(true);
	}

	public void print(double width, boolean withFormattingPattern) {
		Arrays.stream(paragraphs()).forEach(paragraph -> paragraph.print(fontSize(), width, withFormattingPattern));
	}

	public void print(double width) {
		print(width, true);
	}
}
