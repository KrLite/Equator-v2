package net.krlite.equator.visual.text;

import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.visual.color.AccurateColor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.stream.Stream;

public class Section {
	public Section(double fontSize, double titleScaling, double subtitleScaling, double lineSpacing, double paragraphSpacing, Paragraph... paragraphs) {
		this.fontSize = fontSize;
		this.titleScaling = titleScaling;
		this.subtitleScaling = subtitleScaling;
		this.lineSpacing = lineSpacing;
		this.paragraphSpacing = paragraphSpacing;
		this.paragraphs = paragraphs;
	}

	public Section(Text title, double fontSize, double titleScaling, double subtitleScaling, double lineSpacing, double paragraphSpacing) {
		this(fontSize, titleScaling, subtitleScaling, lineSpacing, paragraphSpacing, Paragraph.title(title, fontSize, lineSpacing, titleScaling));
	}

	public Section(Text title, double fontSize) {
		this(title, fontSize, DEFAULT_TITLE_SCALING, DEFAULT_SUBTITLE_SCALING, DEFAULT_LINE_SPACING, DEFAULT_PARAGRAPH_SPACING);
	}

	public Section(Text title) {
		this(title, DEFAULT_FONT_SIZE);
	}

	Section(Section section, Paragraph... paragraphs) {
		this(section.fontSize(), section.titleScaling(), section.subtitleScaling(), section.lineSpacing(), section.paragraphSpacing(),
				section.isEmpty() ? paragraphs : Stream.concat(Stream.of(section.paragraphs()), Stream.of(paragraphs)).toArray(Paragraph[]::new));
	}

	public static final double DEFAULT_FONT_SIZE = 1, DEFAULT_TITLE_SCALING = 1.5, DEFAULT_SUBTITLE_SCALING = 1.2, DEFAULT_LINE_SPACING = 1.0 / 10.0, DEFAULT_PARAGRAPH_SPACING = 1;
	private final double fontSize, titleScaling, subtitleScaling, lineSpacing, paragraphSpacing;
	private final Paragraph[] paragraphs;

	public double fontSize() {
		return fontSize;
	}

	public double titleScaling() {
		return titleScaling;
	}

	public double subtitleScaling() {
		return subtitleScaling;
	}

	public double lineSpacing() {
		return lineSpacing;
	}

	public double paragraphSpacing() {
		return paragraphSpacing;
	}

	public Paragraph[] paragraphs() {
		return paragraphs;
	}

	public Section fontSize(double fontSize) {
		return new Section(fontSize, titleScaling(), subtitleScaling(), lineSpacing(), paragraphSpacing(), paragraphs());
	}

	public Section titleScaling(double titleScaling) {
		return new Section(fontSize(), titleScaling, subtitleScaling(), lineSpacing(), paragraphSpacing(), paragraphs());
	}

	public Section subtitleScaling(double subtitleScaling) {
		return new Section(fontSize(), titleScaling(), subtitleScaling, lineSpacing(), paragraphSpacing(), paragraphs());
	}

	public Section lineSpacing(double lineSpacing) {
		return new Section(fontSize(), titleScaling(), subtitleScaling(), lineSpacing, paragraphSpacing(), paragraphs());
	}

	public Section paragraphSpacing(double paragraphSpacing) {
		return new Section(fontSize(), titleScaling(), subtitleScaling(), lineSpacing(), paragraphSpacing, paragraphs());
	}

	public boolean isEmpty() {
		return paragraphs().length == 0 || Arrays.stream(paragraphs()).allMatch(Paragraph::isSpacing);
	}

	public double height() {
		return isEmpty() ? 0 : Arrays.stream(paragraphs()).mapToDouble(Paragraph::heightAuto).sum();
	}

	public double height(double width) {
		return isEmpty() ? 0 : Arrays.stream(paragraphs()).mapToDouble(paragraph -> paragraph.totalHeightAuto(width)).sum();
	}

	public Section appendSpacing(double spacing) {
		return appendParagraphRaw(Paragraph.spacing(fontSize(), lineSpacing(), paragraphSpacing() * spacing));
	}

	public Section appendSpacing() {
		return appendSpacing(1);
	}

	private Section appendParagraphRaw(Paragraph paragraph) {
		return new Section(this, paragraph);
	}

	private Section appendParagraph(Paragraph paragraph, double spacing) {
		return Arrays.stream(paragraphs()).skip(Math.max(0, paragraphs().length - 1)).findFirst().map(lastParagraph -> {
			if (paragraphs().length == 0 || paragraph.isSpacing() || lastParagraph.isSpacing()) {
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

	public Section append(Text text, boolean hasSpacing) {
		return append(Paragraph.of(text, fontSize(), lineSpacing()), hasSpacing);
	}

	public Section append(Text text) {
		return append(text, true);
	}

	public Section appendTitle(Text text, boolean hasSpacing) {
		return append(Paragraph.title(text, fontSize(), lineSpacing(), titleScaling()), hasSpacing);
	}

	public Section appendTitle(Text text) {
		return appendTitle(text, true);
	}

	public Section appendSubtitle(Text text, boolean hasSpacing) {
		return append(Paragraph.subtitle(text, fontSize(), lineSpacing(), subtitleScaling()), hasSpacing);
	}

	public Section appendSubtitle(Text text) {
		return appendSubtitle(text, true);
	}

	public void render(Box box, TextRenderer textRenderer, MatrixStack matrixStack, AccurateColor color, boolean shadow) {
		render(paragraphs(), box, textRenderer, matrixStack, color, shadow);
	}

	private void render(Paragraph[] paragraphs, Box box, TextRenderer textRenderer, MatrixStack matrixStack, AccurateColor color, boolean shadow) {
		Paragraph paragraph = paragraphs[0];

		if (paragraphs.length > 1) {
			render(Arrays.copyOfRange(paragraphs, 1, paragraphs.length), box.shift(0, paragraph.isSpacing() ? paragraph.height() : paragraph.totalHeightAuto(box.width().magnitude())),
					textRenderer, matrixStack, color, shadow);
		}

		paragraph.render(box, textRenderer, matrixStack, color, shadow);
	}
}
