package gui.simpleUI.modifiers;

import gui.simpleUI.AbstractModifier;
import gui.simpleUI.SimpleUIv1;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class TextModifier extends AbstractModifier {
	private EditText e;

	public abstract String getVarName();

	public abstract String load();

	public abstract boolean save(String newValue);

	@Override
	public View getView(Context context) {

		LinearLayout l = new LinearLayout(context);
		l.setGravity(Gravity.CENTER_VERTICAL);
		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2);
		LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);

		TextView t = new TextView(context);
		t.setText(getVarName());
		t.setLayoutParams(p);
		l.addView(t);
		e = new EditText(context);
		e.setLayoutParams(p2);
		e.setText(load());
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
	public final boolean save() {
		return save(e.getText().toString());
	}

}