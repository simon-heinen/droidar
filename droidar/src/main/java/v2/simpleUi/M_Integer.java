package v2.simpleUi;

import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class M_Integer implements ModifierInterface, UiDecoratable {
	private EditText e;
	private UiDecorator myDecorator;
	private boolean editable = true;
	private float weightOfDescription = 1;
	private float weightOfInputText = 1;
	private static Handler myHandler = new Handler(Looper.getMainLooper());
	private Integer minimumValue;
	private Integer maximumValue;
	private OnClickListener myNotEditableInfo;

	public void setMinimumAndMaximumValue(int minValue, int maxValue) {
		minimumValue = minValue;
		maximumValue = maxValue;
	}

	public Integer getMaximumValue() {
		return maximumValue;
	}

	public Integer getMinimumValue() {
		return minimumValue;
	}

	public void setWeightOfDescription(float weightOfDescription) {
		this.weightOfDescription = weightOfDescription;
	}

	public void setWeightOfInputText(float weightOfInputText) {
		this.weightOfInputText = weightOfInputText;
	}

	public abstract int load();

	public abstract String getVarName();

	public abstract boolean save(int newValue);

	@Override
	public View getView(Context context) {
		LinearLayout l = new LinearLayout(context);
		l.setGravity(Gravity.CENTER_VERTICAL);

		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfDescription);
		LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfInputText);

		TextView t = new TextView(context);
		t.setLayoutParams(p);
		t.setText(this.getVarName());

		l.addView(t);

		// TODO replace by better view representative:
		e = new EditText(context);
		e.setLayoutParams(p2);
		e.setText("" + load());
		e.setInputType(InputType.TYPE_CLASS_NUMBER);
		e.setKeyListener(new DigitsKeyListener(true, false));
		setEditable(isEditable());

		if (minimumValue != null && maximumValue != null)
			setMinMaxFilterFor(e, minimumValue, maximumValue);

		l.addView(e);
		l.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, t, level + 1,
					UiDecorator.TYPE_INFO_TEXT);
			myDecorator.decorate(context, e, level + 1,
					UiDecorator.TYPE_EDIT_TEXT);
		}

		return l;
	}

	private static void setMinMaxFilterFor(EditText e, final Integer min,
			final Integer max) {
		e.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!"".equals(s.toString())) {
					try {
						long v = Long.parseLong(s.toString());
						if (v < min) {
							s.clear();
							s.append("" + min);
						} else if (v > max) {
							s.clear();
							s.append("" + max);
						}
					} catch (NumberFormatException e) {
					}
				}
			}
		});
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		if (e != null)
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					if (!isEditable()) {
						if (myNotEditableInfo == null) {
							e.setOnClickListener(null);
							e.setEnabled(false);
						} else {
							e.setEnabled(true);
							e.getBackground().setColorFilter(Color.GRAY,
									PorterDuff.Mode.MULTIPLY);
							e.setOnClickListener(myNotEditableInfo);
						}
					} else {
						e.setOnClickListener(null);
						e.getBackground().setColorFilter(Color.WHITE,
								PorterDuff.Mode.MULTIPLY);
						e.setEnabled(true);
					}

					e.setFocusable(isEditable());
					e.setFocusableInTouchMode(isEditable());

				}
			});
	}

	public void setToMaxValue() {
		if (e != null && isEditable() && getMaximumValue() != null)
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					e.setText("" + getMaximumValue());
				}
			});
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	@Override
	public boolean save() {
		if (!editable)
			return true;
		try {
			return save(Integer.parseInt(e.getText().toString()));
		} catch (NumberFormatException e) {
			// TODO show toast?
			Log.e("EditScreen", "The entered value for " + getVarName()
					+ " was no number!");
		}
		e.requestFocus();
		return false;
	}

	public void setNotEditableInfo(OnClickListener onClickListener) {
		myNotEditableInfo = onClickListener;
	}

}