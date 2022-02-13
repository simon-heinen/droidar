package gui.simpleUI.modifiers;

import gui.simpleUI.AbstractModifier;
import gui.simpleUI.Theme;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class ColorModifier extends AbstractModifier {
	private EditText eAlpha;
	private EditText eRed;
	private EditText eGreen;
	private EditText eBlue;

	@Override
	public View getView(Context context) {

		LinearLayout l = new LinearLayout(context);

		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2);
		LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);

		TextView t = new TextView(context);
		t.setText(getVarName());
		t.setLayoutParams(p);

		l.addView(t);

		LinearLayout l2 = new LinearLayout(context);
		l2.setLayoutParams(p2);

		OnKeyListener k = new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				updateColor();
				return false;
			}
		};

		eAlpha = new EditText(context);
		eAlpha.setLayoutParams(p2);
		eAlpha.setOnKeyListener(k);
		l2.addView(eAlpha);

		eRed = new EditText(context);
		eRed.setLayoutParams(p2);
		eRed.setOnKeyListener(k);
		l2.addView(eRed);

		eGreen = new EditText(context);
		eGreen.setLayoutParams(p2);
		eGreen.setOnKeyListener(k);
		l2.addView(eGreen);

		eBlue = new EditText(context);
		eBlue.setLayoutParams(p2);
		eBlue.setOnKeyListener(k);
		l2.addView(eBlue);

		eAlpha.setText(getAlpha());
		eRed.setText(getRed());
		eGreen.setText(getGreen());
		eBlue.setText(getBlue());
		updateColor();

		l.addView(l2);

		if (getTheme() != null) {
			getTheme().applyOuter1(l);
			getTheme().applyNormal1(t);
			getTheme().applyNormal2(eAlpha);
			getTheme().applyNormal2(eBlue);
			getTheme().applyNormal2(eGreen);
			getTheme().applyNormal2(eRed);
		}

		return l;

	}

	private void updateColor() {
		try {
			int a = Integer.parseInt(eAlpha.getText().toString());
			int r = Integer.parseInt(eRed.getText().toString());
			int g = Integer.parseInt(eGreen.getText().toString());
			int b = Integer.parseInt(eBlue.getText().toString());
			int c = Theme.ThemeColors.toARGB(a, r, g, b);
			eAlpha.setTextColor(c);
			eRed.setTextColor(c);
			eGreen.setTextColor(c);
			eBlue.setTextColor(c);

		} catch (NumberFormatException e) {
			// e.printStackTrace();
		}
	}

	public abstract String getVarName();

	@Override
	public boolean save() {
		try {
			int a = Integer.parseInt(eAlpha.getText().toString());
			int r = Integer.parseInt(eRed.getText().toString());
			int g = Integer.parseInt(eGreen.getText().toString());
			int b = Integer.parseInt(eBlue.getText().toString());
			return save(a, r, g, b);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return false;
	}

	public abstract boolean save(int a, int r, int g, int b);

	public abstract String getAlpha();

	public abstract String getRed();

	public abstract String getGreen();

	public abstract String getBlue();
}