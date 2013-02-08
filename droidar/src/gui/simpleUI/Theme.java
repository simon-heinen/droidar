package gui.simpleUI;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Theme implements EditItem {

	private Theme.ThemeBackground background1;
	private Theme.TextStyle headerText1;
	private Theme.TextStyle normalText1;

	private Theme.ThemeBackground backgroundForText1;
	private Theme.ThemeBackground backgroundButtons1;

	private Theme.ThemeBackground background2;
	private Theme.TextStyle headerText2;
	private Theme.TextStyle normalText2;
	private Theme.ThemeBackground backgroundForText2;
	private Theme.ThemeBackground backgroundButtons2;

	private final Theme.ThemeColors myColors;

	public Theme(Theme.ThemeColors colors) {
		myColors = colors;
	}

	public static Theme A(Context a, Theme.ThemeColors colors) {
		Theme theme = new Theme(colors);
		// theme.backgroundMainContainer = ThemeBackground.B1();
		theme.background1 = ThemeBackground.B1();
		theme.setAllTextStylesTo(TextStyle.DefaultAndroidStyle());
		theme.backgroundButtons1 = ThemeBackground.B2();
		return theme;
	}

	public void setAllTextStylesTo(Theme.TextStyle t) {
		headerText1 = t;
		headerText2 = t;
		normalText1 = t;
		normalText2 = t;
	}

	public static Theme B(Context a, Theme.ThemeColors colors) {
		Theme theme = new Theme(colors);
		// theme.backgroundMainContainer = ThemeBackground.B2();
		theme.backgroundButtons1 = ThemeBackground.B1();
		theme.setAllTextStylesTo(TextStyle.Pony(a, 40));
		theme.headerText1 = TextStyle.Pony(a, 40);
		theme.headerText2 = TextStyle.Pony(a, 40);
		theme.normalText1 = TextStyle.Pony(a, 25);
		theme.normalText2 = TextStyle.Pony(a, 15);
		return theme;
	}

	public static Theme C(Context a, Theme.ThemeColors colors) {
		Theme theme = new Theme(colors);
		// theme.backgroundMainContainer = ThemeBackground.B2();
		theme.backgroundButtons1 = ThemeBackground.B1();
		theme.headerText1 = TextStyle.Disko(a, 40);
		theme.headerText2 = TextStyle.Disko(a, 40);
		theme.normalText1 = TextStyle.Disko(a, 25);
		theme.normalText2 = TextStyle.Disko(a, 15);
		return theme;
	}

	public static Theme D(Context a, Theme.ThemeColors colors) {
		Theme theme = new Theme(colors);
		// theme.backgroundMainContainer = ThemeBackground.B2();
		theme.headerText1 = TextStyle.Gothic(a, 45);
		theme.headerText2 = TextStyle.Gothic(a, 45);
		theme.normalText1 = TextStyle.Gothic(a, 30);
		theme.normalText2 = TextStyle.Gothic(a, 20);
		theme.backgroundButtons1 = ThemeBackground.B2();
		return theme;
	}

	public static Theme E(Context a, Theme.ThemeColors colors) {
		Theme theme = new Theme(colors);
		// theme.backgroundMainContainer = ThemeBackground.B2();
		theme.headerText1 = TextStyle.GiantHead(a, 45);
		theme.headerText2 = TextStyle.GiantHead(a, 45);
		theme.normalText1 = TextStyle.GiantHead(a, 30);
		theme.normalText2 = TextStyle.GiantHead(a, 20);
		theme.backgroundButtons1 = ThemeBackground.B2();
		return theme;
	}

	public static Theme F(Context a, Theme.ThemeColors colors) {
		Theme theme = new Theme(colors);
		// theme.backgroundMainContainer = ThemeBackground.B2();
		theme.headerText1 = TextStyle.GiantHead2(a, 45);
		theme.headerText2 = TextStyle.GiantHead2(a, 45);
		theme.normalText1 = TextStyle.GiantHead2(a, 30);
		theme.normalText2 = TextStyle.GiantHead2(a, 20);
		theme.backgroundButtons1 = ThemeBackground.B2();
		return theme;
	}

	public static Theme G(Context a, Theme.ThemeColors colors) {
		Theme theme = new Theme(colors);
		// theme.backgroundMainContainer = ThemeBackground.B2();
		theme.headerText1 = TextStyle.Scars(a, 45);
		theme.headerText2 = TextStyle.Scars(a, 45);
		theme.normalText1 = TextStyle.Scars(a, 30);
		theme.normalText2 = TextStyle.Scars(a, 20);
		theme.backgroundButtons1 = ThemeBackground.B2();
		return theme;
	}

	public static Theme H(Context a, Theme.ThemeColors colors) {
		Theme theme = new Theme(colors);
		// theme.backgroundMainContainer = ThemeBackground.B2();
		theme.headerText1 = TextStyle.Scars2(a, 45);
		theme.headerText2 = TextStyle.Scars2(a, 45);
		theme.normalText1 = TextStyle.Scars2(a, 30);
		theme.normalText2 = TextStyle.Scars2(a, 20);
		theme.backgroundButtons1 = ThemeBackground.B2();
		return theme;
	}

	public static Theme I(Context a, Theme.ThemeColors colors) {
		Theme theme = new Theme(colors);
		// theme.backgroundMainContainer = ThemeBackground.B2();
		theme.headerText1 = TextStyle.Grumble(a, 45);
		theme.headerText2 = TextStyle.Grumble(a, 45);
		theme.normalText1 = TextStyle.Grumble(a, 30);
		theme.normalText2 = TextStyle.Grumble(a, 20);
		theme.backgroundButtons1 = ThemeBackground.B2();
		return theme;
	}

	public static class ThemeColors {
		private static final int DEFAULT_ALPHA = 220;
		public static final int[] Ggray = initGradientGray();
		public static int whiteB = toARGB(DEFAULT_ALPHA, 255, 255, 255);
		public static int whiteD = toARGB(DEFAULT_ALPHA, 200, 200, 200);
		public static int redD = toARGB(DEFAULT_ALPHA, 100, 0, 0);
		public static int redB = toARGB(DEFAULT_ALPHA, 240, 35, 13);
		public static int greenD = toARGB(DEFAULT_ALPHA, 0, 80, 0);
		public static int greenB = toARGB(DEFAULT_ALPHA, 0, 110, 0);
		public static int blueD = toARGB(DEFAULT_ALPHA, 0, 0, 100);
		public static int blueB = toARGB(DEFAULT_ALPHA, 32, 32, 150);
		public static int orangeD = toARGB(DEFAULT_ALPHA, 255, 100, 0);
		public static int orangeB = toARGB(DEFAULT_ALPHA, 255, 162, 21);
		public static int grayD = toARGB(DEFAULT_ALPHA, 75, 75, 75);
		public static int grayB = toARGB(DEFAULT_ALPHA, 150, 150, 150);
		public static int blackD = toARGB(DEFAULT_ALPHA, 10, 10, 10);
		public static int blackB = toARGB(DEFAULT_ALPHA, 50, 50, 50);
		public static int blackT1 = toARGB(100, 0, 0, 0);
		public static int blackT2 = toARGB(200, 0, 0, 0);

		private int headerText1;
		private int headerShadow1;
		private int[] outer1BG;
		private int headerText2;
		private int headerShadow2;
		private int[] outer2BG;
		private int normalText1;
		private int normalShadow1;
		private int[] normalBG1;
		private int normalText2;
		private int normalShadow2;
		private int[] normalBG2;
		private int buttonText1;
		private int buttonShadow1;
		private int[] buttonBG1;
		private int[] buttonBG2;
		private int buttonShadow2;

		/**
		 * @param alpha
		 *            [0..255]
		 * @param red
		 *            [0..255]
		 * @param green
		 *            [0..255]
		 * @param blue
		 *            [0..255]
		 * @return the calculated argb value
		 */
		public static int toARGB(int alpha, int red, int green, int blue) {
			return android.graphics.Color.argb(alpha, red, green, blue);
		}

		private static int[] initGradientGray() {
			int[] c = new int[3];
			int f = -20;
			c[0] = toARGB(255, 90 + f, 90 + f, 90 + f);
			c[1] = toARGB(255, 85 + f, 85 + f, 85 + f);
			c[2] = toARGB(255, 80 + f, 80 + f, 80 + f);
			return c;
		}

		private static int[] initGradientGray2() {
			int[] c = new int[3];
			int f = -40;
			c[0] = toARGB(255, 190 + f, 190 + f, 190 + f);
			c[1] = toARGB(255, 185 + f, 185 + f, 185 + f);
			c[2] = toARGB(255, 160 + f, 160 + f, 160 + f);
			return c;
		}

		public static Theme.ThemeColors initToRed() {
			Theme.ThemeColors c = new ThemeColors();
			int[] colorArray = new int[2];
			colorArray[0] = ThemeColors.redB;
			colorArray[1] = ThemeColors.redD;
			c.applyToAllTextColors(ThemeColors.blackT1);
			c.applyToImportantBackgrounds(colorArray);
			c.applyToAllShadows(ThemeColors.redB);
			return c;
		}

		public static Theme.ThemeColors initToGreen() {
			Theme.ThemeColors c = new ThemeColors();
			int[] colorArray = new int[5];
			int alpha = 160;
			colorArray[0] = toARGB(alpha, 0, 95, 0);
			colorArray[1] = toARGB(alpha, 0, 95, 0);
			colorArray[2] = toARGB(alpha, 0, 110, 0);
			colorArray[3] = toARGB(alpha, 0, 95, 0);
			colorArray[4] = toARGB(alpha, 0, 95, 0);
			c.applyToAllTextColors(ThemeColors.whiteB);
			c.applyToImportantBackgrounds(colorArray);
			c.applyToAllShadows(ThemeColors.blackB);
			return c;
		}

		public static Theme.ThemeColors initToBlue() {
			Theme.ThemeColors c = new ThemeColors();
			int[] colorArray = new int[2];
			colorArray[0] = ThemeColors.blueD;
			colorArray[1] = ThemeColors.blueB;
			c.applyToAllTextColors(toARGB(255, 200, 200, 200));
			c.applyToImportantBackgrounds(colorArray);
			c.applyToAllShadows(toARGB(255, 0, 0, 0));
			return c;
		}

		public static Theme.ThemeColors initToBlack() {
			Theme.ThemeColors c = new ThemeColors();
			c.applyToAllTextColors(ThemeColors.whiteB);
			c.applyToImportantBackgrounds(initGradientGray());
			c.buttonBG1 = initGradientGray2();
			c.applyToAllShadows(Color.GRAY);
			return c;
		}

		public void applyToAllShadows(int color) {
			normalShadow1 = color;
			normalShadow2 = color;
			buttonShadow1 = color;
			buttonShadow2 = color;
			headerShadow1 = color;
			headerShadow2 = color;
		}

		public void applyToAllBackgrounds(int[] colorGradient) {
			buttonBG1 = colorGradient;
			buttonBG2 = colorGradient;
			normalBG1 = colorGradient;
			normalBG2 = colorGradient;
			outer1BG = colorGradient;
			outer2BG = colorGradient;
		}

		public void applyToImportantBackgrounds(int[] colorGradient) {
			buttonBG1 = colorGradient;
			buttonBG2 = colorGradient;
			outer1BG = colorGradient;
		}

		public void applyToAllTextColors(int color) {
			normalText1 = color;
			normalText2 = color;
			headerText1 = color;
			headerText2 = color;
			buttonText1 = color;
		}

		public void applyNormal1(TextView v) {
			if (normalText1 != 0)
				v.setTextColor(normalText1);
		}

		public void applyNormal1(Button v) {
			if (buttonText1 != 0)
				v.setTextColor(buttonText1);
		}

		public int[] getBackgroundColors() {
			return outer1BG;
		}

		public void applyNormal2(TextView v) {
			if (normalText2 != 0)
				v.setTextColor(normalText2);
		}

		public void applyHeaderC1(TextView v) {
			if (headerText1 != 0)
				v.setTextColor(headerText1);
		}

		public void applyHeaderC2(TextView v) {
			if (headerText2 != 0)
				v.setTextColor(headerText2);
		}

	}

	public static class TextStyle {
		private Typeface textTypeface;
		private float textSize = 0;
		private float shadowSize = 1;
		private float shadowXPos = 1;
		private float shadowYPos = 1;

		/**
		 * @param a
		 * @param fontName
		 *            fonts have to be located in the assets/fonts folder and
		 *            fontName should be something like "MyFont.otf". Free fonts
		 *            are available at <a
		 *            href="http://www.bvfonts.com/fonts/fonts.php?show=free"
		 *            >http://www.bvfonts.com/fonts/fonts.php?show=free</a> for
		 *            example
		 */
		public void setTextFont(Context a, String fontName) {
			textTypeface = Typeface.createFromAsset(a.getAssets(), fontName);

		}

		public void setTextSize(int textSize) {
			this.textSize = textSize;
		}

		public static Theme.TextStyle DefaultAndroidStyle() {
			Theme.TextStyle s = new TextStyle();
			return s;
		}

		/**
		 * @param a
		 * @param size
		 *            use 40 for big letters and 20 for small ones
		 * @param color
		 * @return
		 */
		public static Theme.TextStyle GiantHead(Context a, float size) {
			Theme.TextStyle s = new TextStyle();
			s.setTextFont(a, "giant_head_regular_tt.ttf");
			s.textSize = size;
			return s;
		}

		public static Theme.TextStyle Pony(Context a, float size) {
			Theme.TextStyle s = new TextStyle();
			s.setTextFont(a, "one_trick_pony_tt.ttf");
			s.textSize = size;
			return s;
		}

		public static Theme.TextStyle Gothic(Context a, float size) {
			Theme.TextStyle s = new TextStyle();
			s.setTextFont(a, "gothic_ultra_tt.ttf");
			s.textSize = size;
			return s;
		}

		public static Theme.TextStyle Disko(Context a, float size) {
			Theme.TextStyle s = new TextStyle();
			s.setTextFont(a, "disko_tt.ttf");
			s.textSize = size;
			return s;
		}

		public static Theme.TextStyle GiantHead2(Context a, float size) {
			Theme.TextStyle s = new TextStyle();
			s.setTextFont(a, "giant_head_two_tt.ttf");
			s.textSize = size;
			return s;
		}

		public static Theme.TextStyle Grumble(Context a, float size) {
			Theme.TextStyle s = new TextStyle();
			s.setTextFont(a, "grumble_tt.ttf");
			s.textSize = size;
			return s;
		}

		public static Theme.TextStyle Scars(Context a, float size) {
			Theme.TextStyle s = new TextStyle();
			s.setTextFont(a, "myscars_tt.ttf");
			s.textSize = size;
			return s;
		}

		public static Theme.TextStyle Scars2(Context a, float size) {
			Theme.TextStyle s = new TextStyle();
			s.setTextFont(a, "mybleedingscars_tt.ttf");
			s.textSize = size;
			return s;
		}

		public void applyTo(TextView v, int shadowColor) {
			if (textTypeface != null)
				v.setTypeface(textTypeface);
			if (textSize != 0)
				v.setTextSize(textSize);
			if (shadowColor != 0)
				v.setShadowLayer(shadowSize, shadowXPos, shadowYPos,
						shadowColor);

		}

		// public void applyTo(Button v) {
		// if (textTypeface != null)
		// v.setTypeface(textTypeface);
		// if (textSize != 0)
		// v.setTextSize(textSize);
		//
		// }

	}

	public static class ThemeBackground {

		private Orientation gradientOrientation;
		/**
		 * is final because the colors in the theme can be changed but not the
		 * ColorTheme itself
		 */
		private float[] cornerRadii;

		// private int leftPadding = SimpleUI.DEFAULT_PADDING;
		// private int topPadding = SimpleUI.DEFAULT_PADDING;
		// private int rightPadding = SimpleUI.DEFAULT_PADDING;
		// private int bottomPadding = SimpleUI.DEFAULT_PADDING;

		public static Theme.ThemeBackground B1() {
			Theme.ThemeBackground b = new ThemeBackground();
			b.gradientOrientation = Orientation.TL_BR;
			b.cornerRadii = genCornerArray(10);
			return b;
		}

		public static Theme.ThemeBackground B2() {
			Theme.ThemeBackground b = new ThemeBackground();
			b.gradientOrientation = Orientation.TOP_BOTTOM;
			b.cornerRadii = genCornerArray(5);
			return b;
		}

		public static Theme.ThemeBackground B3() {
			Theme.ThemeBackground b = new ThemeBackground();
			b.gradientOrientation = Orientation.LEFT_RIGHT;
			b.cornerRadii = genCornerArray(10);
			return b;
		}

		public void applyTo(View v, int[] c) {

			if (c != null && gradientOrientation != null) {
				GradientDrawable s = new GradientDrawable(gradientOrientation,
						c);
				s.setCornerRadii(cornerRadii);
				v.setBackgroundDrawable(s);
			}

		}

		/**
		 * @param leftTop
		 * @param rightTop
		 * @param rightBottom
		 * @param leftBottom
		 * @return
		 */
		public static float[] genCornerArray(int leftTop, int rightTop,
				int rightBottom, int leftBottom) {
			float[] a = new float[8];
			int i = 0;
			a[i++] = leftTop;
			a[i++] = leftTop;
			a[i++] = rightTop;
			a[i++] = rightTop;
			a[i++] = rightBottom;
			a[i++] = rightBottom;
			a[i++] = leftBottom;
			a[i++] = leftBottom;
			return a;
		}

		private static float[] genCornerArray(int cornerSize) {
			return genCornerArray(cornerSize, cornerSize, cornerSize,
					cornerSize);
		}

		public void generateGUI(ModifierGroup group, Object message) {
			// group.addModifier(new ListModifier());
		}

	}

	public void applyNormal1(View v) {
		if (background1 != null)
			background1.applyTo(v, myColors.normalBG1);
	}

	public void applyNormal1(EditText v) {
		if (backgroundForText1 != null)
			backgroundForText1.applyTo(v, myColors.normalBG1);
		if (normalText1 != null)
			normalText1.applyTo(v, myColors.normalShadow1);
		if (myColors != null)
			myColors.applyNormal1(v);
	}

	public void applyNormal1(TextView v) {
		if (backgroundForText1 != null)
			backgroundForText1.applyTo(v, myColors.normalBG1);
		if (normalText1 != null)
			normalText1.applyTo(v, myColors.normalShadow1);
		if (myColors != null)
			myColors.applyNormal1(v);
	}

	public void applyNormal1(Button v) {
		if (backgroundButtons1 != null)
			backgroundButtons1.applyTo(v, myColors.buttonBG1);
		if (normalText1 != null)
			normalText1.applyTo(v, myColors.buttonShadow1);
		if (myColors != null)
			myColors.applyNormal1(v);
	}

	public void applyNormal1(ImageButton v) {
		if (backgroundButtons1 != null)
			backgroundButtons1.applyTo(v, myColors.buttonBG1);
		// if (normalText1 != null)
		// normalText1.applyTo(v, myColors.buttonShadow1);
		// if (myColors != null)
		// myColors.applyNormal1(v);
		// TODO
	}

	public void applyNormal2(View v) {
		if (background2 != null)
			background2.applyTo(v, myColors.normalBG2);
	}

	public void applyNormal2(TextView v) {
		if (backgroundForText2 != null)
			backgroundForText2.applyTo(v, myColors.normalBG2);
		if (normalText2 != null)
			normalText2.applyTo(v, myColors.normalShadow2);
		if (myColors != null)
			myColors.applyNormal2(v);
	}

	public void applyNormal2(Button v) {
		if (backgroundButtons2 != null)
			backgroundButtons2.applyTo(v, myColors.buttonBG2);
		if (normalText2 != null)
			normalText2.applyTo(v, myColors.buttonShadow2);
		if (myColors != null)
			myColors.applyNormal2(v);
	}

	// public void applyToMainContainer(View v) {
	// if (backgroundMainContainer != null)
	// backgroundMainContainer.applyTo(v, myColors.outer1BG);
	// }

	public void applyOuter1(View v) {
		if (background1 != null)
			background1.applyTo(v, myColors.outer1BG);
	}

	public void applyOuter1(TextView v) {
		if (background1 != null)
			background1.applyTo(v, myColors.outer1BG);
		if (headerText1 != null)
			headerText1.applyTo(v, myColors.headerShadow1);
		if (myColors != null)
			myColors.applyHeaderC1(v);
	}

	public void applyOuter2(View v) {
		if (background2 != null)
			background2.applyTo(v, myColors.outer2BG);
	}

	public void applyOuter2(TextView v) {
		if (background2 != null)
			background2.applyTo(v, myColors.outer2BG);
		if (headerText2 != null)
			headerText2.applyTo(v, myColors.headerShadow2);
		if (myColors != null)
			myColors.applyHeaderC2(v);
	}

	@Override
	public void customizeScreen(ModifierGroup group, Object message) {
		this.background1.generateGUI(group, message);
	}

}