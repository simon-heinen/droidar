package gui.simpleUI.modifiers;

import gui.simpleUI.AbstractModifier;
import gui.simpleUI.SimpleUIv1;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class BoolModifier extends AbstractModifier {

	private CheckBox e;

	@Override
	public View getView(Context context) {
		LinearLayout l = new LinearLayout(context);
		l.setGravity(Gravity.CENTER_VERTICAL);
		l.setPadding(SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING,
				SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING);

		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2);
		LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);

		TextView t = new TextView(context);
		t.setLayoutParams(p);
		t.setText(this.getVarName());

		l.addView(t);

		// TODO replace by better view representative:
		e = new CheckBox(context);
		e.setLayoutParams(p2);
		e.setChecked(loadVar());

		l.addView(e);

		if (getTheme() != null) {
			getTheme().applyOuter1(l);
			getTheme().applyNormal1(t);
			getTheme().applyNormal1(e);
		}

		return l;
	}

	public abstract boolean loadVar();

	public abstract CharSequence getVarName();

	public abstract boolean save(boolean newValue);

	@Override
	public boolean save() {
		return save(e.isChecked());
	}

}