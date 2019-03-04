package v2.simpleUi;

import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class M_PlusMinus implements ModifierInterface, UiDecoratable {

	private TextView valueText;
	private int myMinusImageId;
	private int myPlusImageId;
	private UiDecorator myDecorator;

	public M_PlusMinus(int minusImage, int plusImage) {
		myMinusImageId = minusImage;
		myPlusImageId = plusImage;
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	@Override
	public View getView(Context context) {
		LinearLayout l = new LinearLayout(context);
		l.setGravity(Gravity.CENTER);

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
		l2.setGravity(Gravity.CENTER);
		final ImageButton minusBtn = new ImageButton(context);
		if (myMinusImageId != -1) {
			minusBtn.setImageResource(myMinusImageId);
			minusBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					valueText.setText(minusEvent(minusBtn, getCurrentValue()));
				}

			});
			LinearLayout l3 = new LinearLayout(context);
			l3.setLayoutParams(p2);
			l3.setGravity(Gravity.CENTER_HORIZONTAL);
			l3.addView(minusBtn);
			l2.addView(l3);
		}
		valueText = new TextView(context);
		valueText.setText("" + load());

		LinearLayout l4 = new LinearLayout(context);
		l4.setLayoutParams(p2);
		l4.setGravity(Gravity.CENTER_HORIZONTAL);
		l4.addView(valueText);

		l2.addView(l4);
		final ImageButton plusBtn = new ImageButton(context);
		if (myPlusImageId != -1) {
			plusBtn.setImageResource(myPlusImageId);
			plusBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					valueText.setText(plusEvent(plusBtn, getCurrentValue()));
				}

			});
			LinearLayout l5 = new LinearLayout(context);
			l5.setLayoutParams(p2);
			l5.setGravity(Gravity.CENTER_HORIZONTAL);
			l5.addView(plusBtn);
			l2.addView(l5);
		}
		l.addView(l2);
		l.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			if (plusBtn != null)
				myDecorator.decorate(context, plusBtn, level + 1,
						UiDecorator.TYPE_BUTTON);
			if (minusBtn != null)
				myDecorator.decorate(context, minusBtn, level + 1,
						UiDecorator.TYPE_BUTTON);
			myDecorator.decorate(context, t, level + 1,
					UiDecorator.TYPE_INFO_TEXT);
			myDecorator.decorate(context, valueText, level + 1,
					UiDecorator.TYPE_INFO_TEXT);
		}

		return l;
	}

	private String getCurrentValue() {
		return valueText.getText().toString();
	}

	@Override
	public boolean save() {
		return save(getCurrentValue());
	}

	public abstract String getVarName();

	public abstract String load();

	public abstract String minusEvent(ImageButton minusButton,
			String currentValue);

	public abstract String plusEvent(ImageButton plusButton, String currentValue);

	/**
	 * @param currentValue
	 * @return true if the save procedure was successful
	 */
	public abstract boolean save(String currentValue);

}