package gl;

import java.nio.FloatBuffer;

import util.Vec;

/**
 * The values for the color channels (eg {@link Color#red} have to be between 0
 * and 1!
 * 
 * @author Spobo
 * 
 */
public class Color {

	/**
	 * has to be between 0 and 1
	 */
	public float red;

	/**
	 * see {@link Color#red}
	 */
	public float green;
	/**
	 * see {@link Color#red}
	 */
	public float blue;
	/**
	 * see {@link Color#red}
	 */
	public float alpha;

	/**
	 * the values should be between 0 and 1, for example new Color(1,0,0,1)
	 * would be red
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public Color(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	/**
	 * @param string
	 *            #ff11ff for example
	 */
	public Color(String string) {
		int c = android.graphics.Color.parseColor(string);
		red = (float) android.graphics.Color.red(c) / 255;
		green = (float) android.graphics.Color.green(c) / 255;
		blue = (float) android.graphics.Color.blue(c) / 255;
		alpha = (float) android.graphics.Color.alpha(c) / 255;
	}

	/**
	 * @param color
	 *            the color value you could get from
	 *            {@link android.graphics.Color} for example
	 */
	public Color(int color) {
		red = (float) android.graphics.Color.red(color) / 255;
		green = (float) android.graphics.Color.green(color) / 255;
		blue = (float) android.graphics.Color.blue(color) / 255;
		alpha = (float) android.graphics.Color.alpha(color) / 255;
	}

	/**
	 * transforms the color object into its appendant integer value
	 * 
	 * blue will become 0xff0000ff (argb value) for example
	 * 
	 * @return
	 */
	public int toIntARGB() {
		// android.graphics.Color.parseColor("#66000000"); //would be
		// transparent black TODO use somehow
		return android.graphics.Color.argb((int) (alpha * 255),
				(int) (red * 255), (int) (green * 255), (int) (blue * 255));
	}

	public int toIntRGB() {
		return android.graphics.Color.argb(255, (int) (red * 255),
				(int) (green * 255), (int) (blue * 255));
	}

	public static Color white() {
		return new Color(1, 1, 1, 1);
	}

	public static Color green() {
		return new Color(0f, 1f, 0f, 1f);
	}

	public static Color red() {
		return new Color(1f, 0f, 0f, 1f);
	}

	public static Color redTransparent() {
		return new Color(1f, 0f, 0f, 0.5f);
	}

	public static Color blue() {
		return new Color(0f, 0f, 1f, 1f);
	}

	public static Color blueTransparent() {
		return new Color(0f, 0f, 1f, 0.6f);
	}

	public static Color greenTransparent() {
		return new Color(0f, 1f, 0f, 0.6f);
	}

	public FloatBuffer toFloatBuffer() {
		float[] values = new float[4];
		values[0] = red;
		values[1] = green;
		values[2] = blue;
		values[3] = alpha;
		return GLUtilityClass.createAndInitFloatBuffer(values);
	}

	public Color copy() {
		return new Color(red, green, blue, alpha);
	}

	/**
	 * @param target
	 * @param values
	 * @param speed
	 * @return the remaining distances between the two colors (channels
	 *         seperated, use {@link Vec#getLength()} eg). Will return the
	 *         0-Vector if incorrect parameters are passed
	 */
	public static Vec morphToNewColor(Color target, Color values, float speed) {
		Vec d = new Vec();
		if (target != null && values != null) {
			d.x = values.red - target.red;
			target.red = target.red + d.x * speed;
			if (target.red < 0)
				target.red = 0;
			if (target.red > 1)
				target.red = 1;
			d.y = values.green - target.green;
			target.green = target.green + d.y * speed;
			if (target.green < 0)
				target.green = 0;
			if (target.green > 1)
				target.green = 1;
			d.z = values.blue - target.blue;
			target.blue = target.blue + d.z * speed;
			if (target.blue < 0)
				target.blue = 0;
			if (target.blue > 1)
				target.blue = 1;
			target.alpha = target.alpha + (values.alpha - target.alpha) * speed;
			if (target.alpha < 0)
				target.alpha = 0;
			if (target.alpha > 1)
				target.alpha = 1;
		}
		return d;
	}

	@Override
	public String toString() {
		return "(r:" + this.red + ",g:" + this.green + ",b:" + this.blue
				+ ",a:" + this.alpha + ")";
	}

	public void copyValues(Color source) {
		red = source.red;
		green = source.green;
		blue = source.blue;
		alpha = source.alpha;
	}

	public static Color getRandomARGBColor() {
		return new Color(getRandomFloatFrom0To1(), getRandomFloatFrom0To1(),
				getRandomFloatFrom0To1(), getRandomFloatFrom0To1());
	}

	public static Color getRandomRGBColor() {
		return new Color(getRandomFloatFrom0To1(), getRandomFloatFrom0To1(),
				getRandomFloatFrom0To1(), 1);
	}

	private static float getRandomFloatFrom0To1() {
		return (float) Math.random();
	}

	public static Color whiteTransparent() {
		return new Color(1, 1, 1, 0.5f);
	}

	public static Color silver1() {
		return new Color("#C0C0C0");
	}

	public static Color silver2() {
		return new Color("#C9C0BB");
	}

	public static Color blackTransparent() {
		return new Color(0, 0, 0, 0.6f);
	}

	public static Color battleshipGrey() {
		return new Color("#848482");
	}

	public static Color TrolleyGrey() {
		return new Color("#808080");
	}

	public float[] toFloatArray() {
		float[] c = { red, green, blue, alpha };
		return c;
	}

	public void setTo(Color c) {
		alpha = c.alpha;
		green = c.green;
		blue = c.blue;
		red = c.red;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Color ? this.toIntARGB() == ((Color) other)
				.toIntARGB() : false;
	}

	@Override
	public int hashCode() {
		return this.toIntARGB();
	}

}
