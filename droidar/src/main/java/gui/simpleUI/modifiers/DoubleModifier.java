package gui.simpleUI.modifiers;

import gui.simpleUI.AbstractModifier;
import gui.simpleUI.SimpleUIv1;
import util.Log;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class DoubleModifier extends AbstractModifier {
	private EditText e;

	public abstract double load();

	public abstract String getVarName();

	public abstract boolean save(double newValue);

	@Override
	public View getView(Context context) {
		LinearLayout l = new LinearLayout(context);
		l.setGravity(Gravity.CENTER_VERTICAL);

		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2);
		LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);

		TextView t = new TextView(context);
		t.setLayoutParams(p);
		t.setText(this.getVarName());

		l.addView(t);

		// TODO replace by better view representative:
		e = new EditText(context);
		e.setLayoutParams(p2);
		e.setText("" + load());

		l.addView(e);
		l.setPadding(SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING,
				SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING);

		if (getTheme() != null) {
			getTheme().applyOuter1(l);
			getTheme().applyNormal1(t);
			getTheme().applyNormal1(e);
		}

		return l;
	}

	@Override
	public boolean save() {
		try {
			return save(Double.parseDouble(e.getText().toString()));
		} catch (NumberFormatException e) {
			// TODO show toast?
			Log.e("EditScreen", "The entered value for " + getVarName()
					+ " was no number!");
		}
		return false;
	}

}