package v2.simpleUi.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

public class TextUtils {
	private Typeface textTypeface;
	private float textSize = 0;
	private int shadowColor = 0;
	private float shadowSize = 1;
	private float shadowXPos = 1;
	private float shadowYPos = 1;

	/**
	 * @param context
	 * @param fontName
	 *            fonts have to be located in the assets/fonts folder and
	 *            fontName should be something like "MyFont.otf". Free fonts are
	 *            available at <a
	 *            href="http://www.bvfonts.com/fonts/fonts.php?show=free"
	 *            >http://www.bvfonts.com/fonts/fonts.php?show=free</a> for
	 *            example
	 */
	public void setTextFont(Context context, String fontName) {
		textTypeface = Typeface.createFromAsset(context.getAssets(), fontName);

	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public static TextUtils TextWithwhiteShadow(Context context) {
		TextUtils s = new TextUtils();
		int x = 255;
		s.shadowColor = Color.argb(130, x, x, x);
		s.shadowSize = 1;
		s.shadowXPos = 1;
		s.shadowYPos = 1;
		return s;
	}

	public static TextUtils TextWithBlackShadow(Context context) {
		TextUtils s = new TextUtils();
		int x = 50;
		s.shadowColor = Color.argb(130, x, x, x);
		s.shadowSize = 1;
		s.shadowXPos = 1;
		s.shadowYPos = 1;
		return s;
	}

	public static TextUtils GiantHeadTextFont(Context a, float size) {
		TextUtils s = new TextUtils();
		s.setTextFont(a, "giant_head_regular_tt.ttf");
		s.textSize = size;
		return s;
	}

	public void applyTo(TextView v) {
		if (textTypeface != null)
			v.setTypeface(textTypeface);
		if (textSize != 0)
			v.setTextSize(textSize);
		if (shadowColor != 0)
			v.setShadowLayer(shadowSize, shadowXPos, shadowYPos, shadowColor);

	}

}
