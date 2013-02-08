package gui.simpleUI.modifiers;

import gui.simpleUI.AbstractModifier;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Headline extends AbstractModifier {

	private String myText;
	private int myColor = -1;
	private Bitmap myIcon;
	private int myIconId;
	private float myTextSize;

	public Headline(String text) {
		this(null, text, -1);
	}

	public Headline(String text, int backgroundColor) {
		this(null, text, backgroundColor);
	}

	public Headline(Bitmap icon, String shortDescr) {
		this(icon, shortDescr, -1);
	}

	public Headline(Bitmap icon, String text, int backgroundColor) {
		myIcon = icon;
		myText = text;
		myColor = backgroundColor;
	}

	public Headline(int iconId, String text) {
		myIconId = iconId;
		myText = text;
		myColor = -1;
	}

	public Headline(int iconId, String text, float manualTextSize) {
		this(iconId, text);
		myTextSize = manualTextSize;
	}

	@Override
	public View getView(Context myContext) {

		int bottomAndTopPadding = 4;
		int textPadding = 7;
		int iconPadding = 10;

		LinearLayout l = new LinearLayout(myContext);
		l.setGravity(Gravity.CENTER_VERTICAL);

		l.setPadding(0, bottomAndTopPadding, 0, bottomAndTopPadding);

		if (myIcon != null) {
			ImageView i = new ImageView(myContext);

			i.setPadding(0, iconPadding, iconPadding, iconPadding);
			i.setImageBitmap(myIcon);
			l.addView(i);
		} else if (myIconId != 0) {
			ImageView i = new ImageView(myContext);

			i.setPadding(0, iconPadding, iconPadding, iconPadding);
			i.setImageResource(myIconId);
			l.addView(i);
		}

		TextView t = new TextView(myContext);
		t.setText(myText);
		t.setPadding(textPadding, textPadding, textPadding, textPadding);
		if (myIconId == 0)
			t.setGravity(Gravity.CENTER_HORIZONTAL);
		l.addView(t);

		if (getTheme() != null) {
			getTheme().applyOuter1(l);
			// getTheme().applyOuter1(t);
		}
		if (myTextSize != 0)
			t.setTextSize(myTextSize);
		if (myColor != -1) {
			l.setBackgroundColor(myColor);
		}

		return l;
	}

	@Override
	public boolean save() {
		return true;
	}

}