package net.krlite.equator.visual.color;

import net.krlite.equator.math.algebra.Theory;

/**
 * <h1>Palette</h1>
 * Contains <b>coloring utilities</b> along with <b>sets of colors</b> that can be used in a variety of contexts.
 * @see AccurateColor
 */
public class Palette {
	public static AccurateColor rainbow(double radians) {
		return new AccurateColor(Colorspace.HSV, new double[]{ Math.toDegrees(radians), 1, 0.72}, 1);
	}
	
	public static final AccurateColor BROWN = new AccurateColor(0.5, 0.25, 0, 1), TEAL = new AccurateColor(0, 0.5, 0.5, 1),
			GOLD = new AccurateColor(1, 0.75, 0, 1), SILVER = new AccurateColor(0.75, 0.75, 0.75, 1), NAVY = new AccurateColor(0, 0, 0.5, 1),
			MAROON = new AccurateColor(0.5, 0, 0, 1), OLIVE = new AccurateColor(0.5, 0.5, 0, 1), LIME = new AccurateColor(0, 1, 0, 1),
			AQUA = new AccurateColor(0, 1, 1, 1), FUCHSIA = new AccurateColor(1, 0, 1, 1), TURQUOISE = new AccurateColor(0.25, 0.875, 0.8125, 1),
			INDIGO = new AccurateColor(0.29, 0, 0.51, 1), VIOLET = new AccurateColor(0.93, 0.51, 0.93, 1), CRIMSON = new AccurateColor(0.86, 0.08, 0.24, 1),
			CHARTREUSE = new AccurateColor(0.5, 1, 0, 1), CORAL = new AccurateColor(1, 0.5, 0.31, 1), DEEP_PINK = new AccurateColor(1, 0.08, 0.58, 1),
			DEEP_SKY_BLUE = new AccurateColor(0, 0.75, 1, 1), DIM_GRAY = new AccurateColor(0.41, 0.41, 0.41, 1), DODGER_BLUE = new AccurateColor(0.12, 0.56, 1, 1),
			FOREST_GREEN = new AccurateColor(0.13, 0.55, 0.13, 1), GAINSBORO = new AccurateColor(0.86, 0.86, 0.86, 1), GOLDEN_ROD = new AccurateColor(0.85, 0.65, 0.13, 1),
			HOT_PINK = new AccurateColor(1, 0.41, 0.71, 1), KHAKI = new AccurateColor(0.94, 0.9, 0.55, 1);

	/**
	 * <h2>Translucent</h2>
	 * Basic colors with <b>50% opacity.</b>
	 */
	public static class Translucent {
		public static final AccurateColor BLACK = new AccurateColor(0, 0, 0, 0.5), WHITE = new AccurateColor(1, 1, 1, 0.5),
				GRAY = new AccurateColor(0.5, 0.5, 0.5, 0.5), LIGHT_GRAY = new AccurateColor(0.75, 0.75, 0.75, 0.5), DARK_GRAY = new AccurateColor(0.25, 0.25, 0.25, 0.5),
				RED = new AccurateColor(1, 0, 0, 0.5), GREEN = new AccurateColor(0, 1, 0, 0.5), BLUE = new AccurateColor(0, 0, 1, 0.5),
				CYAN = new AccurateColor(0, 1, 1, 0.5), YELLOW = new AccurateColor(1, 1, 0, 0.5), MAGENTA = new AccurateColor(1, 0, 1, 0.5),
				ORANGE = new AccurateColor(1, 0.5, 0, 0.5), PINK = new AccurateColor(1, 0.75, 0.75, 0.5);
	}

	/**
	 * <h2>Pantone</h2>
	 * Colors from the <b>Pantone Color of the Year 2000~2023.</b>
	 */
	public static class Pantone {
		public static final AccurateColor VIVA_MAGENTA_2023 = AccurateColor.fromRGBA(187, 38, 73);
		public static final AccurateColor VERY_PERI_2022_COTTON = AccurateColor.fromRGBA(102, 103, 171), VERY_PERI_2022_PAPER = AccurateColor.fromRGBA(105, 106, 173);
		public static final AccurateColor ILLUMINATING_2021 = AccurateColor.fromRGBA(245, 223, 76), ULTIMATE_GRAY_2021 = AccurateColor.fromRGBA(153, 154, 157);
		public static final AccurateColor CLASSIC_BLUE_2020 = AccurateColor.fromRGBA(15, 76, 129);
		public static final AccurateColor LIVING_CORAL_2019 = AccurateColor.fromRGBA(253, 111, 97);
		public static final AccurateColor ULTRA_VIOLET_2018 = AccurateColor.fromRGBA(97, 74, 139);
		public static final AccurateColor GREENERY_2017 = AccurateColor.fromRGBA(135, 177, 75);
		public static final AccurateColor ROSE_QUARTZ_2016 = AccurateColor.fromRGBA(248, 202, 202), SERENITY_2016 = AccurateColor.fromRGBA(146, 168, 206);
		public static final AccurateColor MARSALA_2015 = AccurateColor.fromRGBA(150, 80, 76);
		public static final AccurateColor RADIANT_ORCHID_2014 = AccurateColor.fromRGBA(171, 94, 154);
		public static final AccurateColor EMERALD_2013 = AccurateColor.fromRGBA(4, 149, 115);
		public static final AccurateColor TANGERINE_TANGO_2012 = AccurateColor.fromRGBA(221, 64, 36);
		public static final AccurateColor HONEYSUCKLE_2011 = AccurateColor.fromRGBA(216, 79, 112);
		public static final AccurateColor TURQUOISE_2010 = AccurateColor.fromRGBA(69, 180, 170);
		public static final AccurateColor MIMOSA_2009 = AccurateColor.fromRGBA(239, 189, 84);
		public static final AccurateColor BLUE_IRIS_2008 = AccurateColor.fromRGBA(85, 78, 155);
		public static final AccurateColor CHILI_PEPPER_2007 = AccurateColor.fromRGBA(146, 14, 40);
		public static final AccurateColor SAND_DOLLAR_2006 = AccurateColor.fromRGBA(222, 204, 190);
		public static final AccurateColor BLUE_TURQUOISE_2005 = AccurateColor.fromRGBA(84, 175, 174);
		public static final AccurateColor TIGERLILY_2004 = AccurateColor.fromRGBA(225, 88,63);
		public static final AccurateColor AQUA_SKY_2003 = AccurateColor.fromRGBA(122, 196, 195);
		public static final AccurateColor TRUE_RED_2002 = AccurateColor.fromRGBA(190, 25, 49);
		public static final AccurateColor FUCHSIA_ROSE_2001 = AccurateColor.fromRGBA(199, 66, 118);
		public static final AccurateColor CERULEAN_BLUE_2000 = AccurateColor.fromRGBA(152, 179, 209);
	}
	
	/**
	 * <h2>Minecraft</h2>
	 * Colors from <b>Minecraft.</b>
	 */
	public static class Minecraft {
		/**
		 * <b>Mojang</b>
		 * <br />
		 * The red color used in the <b>Mojang logo.</b>
		 */
		public static final AccurateColor MOJANG_RED = new AccurateColor(1, 0, 0.5);

		/**
		 * <b>Missing Texture</b>
		 * <br />
		 * The purple and black colors used when <b>the texture is missing.</b>
		 */
		public static final AccurateColor MISSING_TEXTURE_PURPLE = AccurateColor.fromRGBA(0xF800F8), MISSING_TEXTURE_BLACK = AccurateColor.fromRGBA(0x000000);

		/**
		 * <b>Foreground</b>
		 * <br />
		 * Colors capable of being used in the <b>foreground,</b> such as on the <b>text.</b>
		 */
		public static final AccurateColor MINECOIN_GOLD = AccurateColor.fromRGBA(0xDDD605), BLACK = AccurateColor.fromRGBA(0x000000),
				DARK_BLUE = AccurateColor.fromRGBA(0x0000AA), DARK_GREEN = AccurateColor.fromRGBA(0x00AA00), DARK_AQUA = AccurateColor.fromRGBA(0x00AAAA),
				DARK_RED = AccurateColor.fromRGBA(0xAA0000), DARK_PURPLE = AccurateColor.fromRGBA(0xAA00AA), GOLD = AccurateColor.fromRGBA(0xFFAA00),
				GRAY = AccurateColor.fromRGBA(0xAAAAAA), DARK_GRAY = AccurateColor.fromRGBA(0x555555), BLUE = AccurateColor.fromRGBA(0x5555FF),
				GREEN = AccurateColor.fromRGBA(0x55FF55), AQUA = AccurateColor.fromRGBA(0x55FFFF), RED = AccurateColor.fromRGBA(0xFF5555),
				LIGHT_PURPLE = AccurateColor.fromRGBA(0xFF55FF), YELLOW = AccurateColor.fromRGBA(0xFFFF55), WHITE = AccurateColor.fromRGBA(0xFFFFFF);

		/**
		 * <b>Background</b>
		 * <br />
		 * Colors capable of being used in the <b>background,</b> such as on the <b>shadows</b> behind the <b>text.</b>
		 */
		public static final AccurateColor BACKGROUND_MINECOIN_GOLD = AccurateColor.fromRGBA(0x373501), BACKGROUND_BLACK = AccurateColor.fromRGBA(0x000000),
				BACKGROUND_DARK_BLUE = AccurateColor.fromRGBA(0x00002A), BACKGROUND_DARK_GREEN = AccurateColor.fromRGBA(0x002A00),
				BACKGROUND_DARK_AQUA = AccurateColor.fromRGBA(0x002A2A), BACKGROUND_DARK_RED = AccurateColor.fromRGBA(0x2A0000),
				BACKGROUND_DARK_PURPLE = AccurateColor.fromRGBA(0x2A002A), BACKGROUND_GOLD = AccurateColor.fromRGBA(0x2A2A00),
				BACKGROUND_GOLD_BEDROCK = AccurateColor.fromRGBA(0x402A00), BACKGROUND_GRAY = AccurateColor.fromRGBA(0x2A2A2A),
				BACKGROUND_DARK_GRAY = AccurateColor.fromRGBA(0x151515), BACKGROUND_BLUE = AccurateColor.fromRGBA(0x15153F),
				BACKGROUND_GREEN = AccurateColor.fromRGBA(0x153F15), BACKGROUND_AQUA = AccurateColor.fromRGBA(0x153F3F),
				BACKGROUND_RED = AccurateColor.fromRGBA(0x3F1515), BACKGROUND_LIGHT_PURPLE = AccurateColor.fromRGBA(0x3F153F),
				BACKGROUND_YELLOW = AccurateColor.fromRGBA(0x3F3F15), BACKGROUND_WHITE = AccurateColor.fromRGBA(0x3F3F3F);

		/**
		 * <b>Tooltip</b>
		 * <br />
		 * Colors used in the <b>tooltip.</b>
		 */
		public static final AccurateColor TOOLTIP_BORDER_LIGHT = AccurateColor.fromRGBA(0x505000FFL), TOOLTIP_BORDER_DARK = AccurateColor.fromRGBA(0x5028007FL),
				TOOLTIP_BACKGROUND = AccurateColor.fromRGBA(0xF0100010L);
	}
}
