package gui.simpleUI.modifiers;

import gui.simpleUI.AbstractModifier;
import gui.simpleUI.SimpleUIv1;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class PlusMinusModifier extends AbstractModifier {

	private TextView valueText;
	private int myMinusImage;
	private int myPlusImage;

	public PlusMinusModifier(int minusImage, int plusImage) {
		myMinusImage = minusImage;
		myPlusImage = plusImage;
	}

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

		if (myMinusImage != -1) {
			Button minus = new Button(context);
			minus.setBackgroundResource(myMinusImage);

			minus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					valueText.setText("" + minusEvent(getCurrentValue()));
				}

			});
			LinearLayout l3 = new LinearLayout(context);
			l3.setLayoutParams(p2);
			l3.setGravity(Gravity.CENTER_HORIZONTAL);
			l3.addView(minus);
			l2.addView(l3);
		}
		valueText = new TextView(context);
		valueText.setText("" + load());

		LinearLayout l4 = new LinearLayout(context);
		l4.setLayoutParams(p2);
		l4.setGravity(Gravity.CENTER_HORIZONTAL);
		l4.addView(valueText);

		l2.addView(l4);

		if (myPlusImage != -1) {
			Button plus = new Button(context);
			// plus.setLayoutParams(p2);
			plus.setBackgroundResource(myPlusImage);
			plus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					valueText.setText("" + plusEvent(getCurrentValue()));
				}

			});
			LinearLayout l5 = new LinearLayout(context);
			l5.setLayoutParams(p2);
			l5.setGravity(Gravity.CENTER_HORIZONTAL);
			l5.addView(plus);
			l2.addView(l5);
		}
		l.addView(l2);
		l.setPadding(SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING,
				SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING);

		if (getTheme() != null) {
			getTheme().applyOuter1(l);
			getTheme().applyNormal1(t);
			getTheme().applyNormal1(valueText);
		}
		return l;
	}

	private double getCurrentValue() {
		try {
			return Double.parseDouble(valueText.getText().toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean save() {
		return save(getCurrentValue());
	}

	public abstract String getVarName();

	public abstract double load();

	public abstract double minusEvent(double currentValue);

	public abstract double plusEvent(double currentValue);

	public abstract boolean save(double currentValue);

}