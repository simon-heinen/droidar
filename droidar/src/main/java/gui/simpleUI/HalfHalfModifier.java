package gui.simpleUI;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class HalfHalfModifier extends AbstractModifier {

	private ModifierInterface myLeft;
	private ModifierInterface myRight;

	public HalfHalfModifier(ModifierInterface left, ModifierInterface right) {
		myLeft = left;
		myRight = right;
		updateTheme();
	}

	private void updateTheme() {
		if (myLeft instanceof AbstractModifier) {
			((AbstractModifier) myLeft).setTheme(getTheme());
		}
		if (myRight instanceof AbstractModifier) {
			((AbstractModifier) myRight).setTheme(getTheme());
		}
	}

	@Override
	public void setTheme(Theme myTheme) {
		super.setTheme(myTheme);
		updateTheme();
	}

	@Override
	public View getView(Context context) {
		LinearLayout l = new LinearLayout(context);
		View left = myLeft.getView(context);
		View right = myRight.getView(context);
		LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);
		left.setLayoutParams(params);
		right.setLayoutParams(params);
		l.addView(left);
		l.addView(right);
		return l;
	}

	@Override
	public boolean save() {
		return myLeft.save() && myRight.save();
	}

}
