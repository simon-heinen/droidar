package v2.simpleUi.util;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;

public class BGUtils {

	/**
	 * Try Orientation.TL_BR or Orientation.TOP_BOTTOM and so on
	 */
	private Orientation gradientOrientation;
	/**
	 * is final because the colors in the theme can be changed but not the
	 * ColorTheme itself
	 */
	private float[] cornerRadii;
	private int[] colorsInGradient;

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

	/**
	 * @param colorString
	 *            #RRGGBB or #AARRGGBB or... read
	 *            {@link Color#parseColor(String)}
	 * @return
	 */
	public static int toARGB(String colorString) {
		return android.graphics.Color.parseColor(colorString);
	}

	public static int[] createGrayGradient1() {
		int[] c = new int[3];
		int f = -20;
		c[0] = toARGB(255, 90 + f, 90 + f, 90 + f);
		c[1] = toARGB(255, 80 + f, 80 + f, 80 + f);
		c[2] = toARGB(255, 70 + f, 70 + f, 70 + f);
		return c;
	}

	public static int[] createGrayGradient2() {
		int[] c = new int[3];
		int f = -40;
		c[0] = toARGB(255, 190 + f, 190 + f, 190 + f);
		c[1] = toARGB(255, 185 + f, 185 + f, 185 + f);
		c[2] = toARGB(255, 160 + f, 160 + f, 160 + f);
		return c;
	}

	public static int[] createGrayGradient3() {
		int[] c = new int[3];
		int f = -40;
		c[0] = toARGB(0, 160 + f, 160 + f, 160 + f);
		c[1] = toARGB(255, 185 + f, 185 + f, 185 + f);
		c[2] = toARGB(0, 160 + f, 160 + f, 160 + f);
		return c;
	}

	public static int[] createGreenGradient() {
		int[] colorArray = new int[5];
		int alpha = 160;
		colorArray[0] = toARGB(alpha, 0, 95, 0);
		colorArray[1] = toARGB(alpha, 0, 95, 0);
		colorArray[2] = toARGB(alpha, 0, 110, 0);
		colorArray[3] = toARGB(alpha, 0, 115, 0);
		colorArray[4] = toARGB(alpha, 0, 120, 0);
		return colorArray;
	}

	public static int[] createRedGradient() {
		int[] colorArray = new int[5];
		int alpha = 160;
		int x = 120;
		colorArray[0] = toARGB(alpha, x + 95, 0, 0);
		colorArray[1] = toARGB(alpha, x + 95, 0, 0);
		colorArray[2] = toARGB(alpha, x + 110, 0, 0);
		colorArray[3] = toARGB(alpha, x + 115, 0, 0);
		colorArray[4] = toARGB(alpha, x + 120, 0, 0);
		return colorArray;
	}

	public void applyTo(View v) {
		if (colorsInGradient != null && gradientOrientation != null
				&& cornerRadii != null) {
			GradientDrawable s = new GradientDrawable(gradientOrientation,
					colorsInGradient);
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

	public static float[] genCornerArray(int cornerSize) {
		return genCornerArray(cornerSize, cornerSize, cornerSize, cornerSize);
	}

	public static BGUtils newGrayBackground() {
		return new BGUtils(Orientation.BL_TR, BGUtils.createGrayGradient1(),
				BGUtils.genCornerArray(15));
	}

	public static BGUtils newRedBackground() {
		return new BGUtils(Orientation.BL_TR, BGUtils.createRedGradient(),
				BGUtils.genCornerArray(10));
	}

	public static BGUtils newGreenBackground() {
		return new BGUtils(Orientation.BL_TR, BGUtils.createGreenGradient(),
				BGUtils.genCornerArray(10));
	}

	/**
	 * @param o
	 *            use the {@link Orientation} class
	 * @param colorsInGradient
	 *            see {@link BGUtils#createGrayGradient1()} for implementation
	 *            details
	 * @param cornerRadii
	 *            use {@link BGUtils#genCornerArray(int)}
	 */
	public BGUtils(Orientation o, int[] colorsInGradient, float[] cornerRadii) {
		this.gradientOrientation = o;
		this.colorsInGradient = colorsInGradient;
		this.cornerRadii = cornerRadii;
	}

}
