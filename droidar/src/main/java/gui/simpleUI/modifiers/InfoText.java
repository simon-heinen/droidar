package gui.simpleUI.modifiers;

import gui.simpleUI.AbstractModifier;
import gui.simpleUI.SimpleUIv1;
import gui.simpleUI.Theme;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoText extends AbstractModifier {

	private String myText;
	private String myName;
	private int myGravity;

	public InfoText(String name, String text) {
		myName = name;
		myText = text;
	}

	/**
	 * This constructor produces a comment like text-representation which gets a
	 * Normal2 style (see {@link Theme} for more informations)
	 * 
	 * @param text
	 * @param gravity
	 *            use values from android.Gravity (e.g. Gravity.RIGHT)
	 */
	public InfoText(String text, int gravity) {
		myText = text;
		myGravity = gravity;
	}

	@Override
	public View getView(Context context) {

		if (myName != null) {
			LinearLayout l = new LinearLayout(context);
			LayoutParams p = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);
			LayoutParams p2 = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);
			TextView t = new TextView(context);
			t.setText(myName);
			t.setLayoutParams(p);
			l.addView(t);

			TextView e = new TextView(context);
			e.setLayoutParams(p2);
			e.setText(myText);
			l.addView(e);
			l.setPadding(SimpleUIv1.DEFAULT_PADDING,
					SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING,
					SimpleUIv1.DEFAULT_PADDING);

			if (getTheme() != null) {
				getTheme().applyOuter1(l);
				getTheme().applyNormal1(t);
				getTheme().applyNormal1(e);
			}

			return l;
		} else {
			TextView t = new TextView(context);
			t.setText(myText);
			t.setGravity(myGravity);
			t.setPadding(SimpleUIv1.DEFAULT_PADDING,
					SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING,
					SimpleUIv1.DEFAULT_PADDING);

			if (getTheme() != null)
				getTheme().applyNormal2(t);
			return t;
		}

	}

	@Override
	public boolean save() {
		return true;
	}

}