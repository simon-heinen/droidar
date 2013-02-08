package v3;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.EditText;

public abstract class M_LongModifier extends M_TextModifier {
	private Long minimumValue;
	private Long maximumValue;

	public void setMinimumAndMaximumValue(Long minValue, Long maxValue) {
		minimumValue = minValue;
		maximumValue = maxValue;
	}

	public Long getMaximumValue() {
		return maximumValue;
	}

	public Long getMinimumValue() {
		return minimumValue;
	}

	@Override
	public void applyTextFilterIfNeeded(EditText e) {
		e.setInputType(InputType.TYPE_CLASS_NUMBER);
		e.setKeyListener(new DigitsKeyListener(true, false));
		if (minimumValue != null && maximumValue != null) {
			setMinMaxFilterFor(e, minimumValue, maximumValue);
		}
	}

	public void setToMaxValue() {
		if (getEditText() != null && isEditable() && getMaximumValue() != null) {
			getMyHandler().post(new Runnable() {
				@Override
				public void run() {
					getEditText().setText("" + getMaximumValue());
				}
			});
		}
	}

	private static void setMinMaxFilterFor(EditText e, final Long min,
			final Long max) {
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

	@Override
	public boolean save(String newValue) {
		try {
			return saveLong(Long.parseLong(newValue));
		} catch (NumberFormatException e) {
			// TODO show toast?
			Log.e("EditScreen", "The entered value for " + getVarName()
					+ " was no number!");
		}
		getEditText().requestFocus();
		return false;
	}

	@Override
	public String load() {
		return "" + loadLong();
	}

	public abstract long loadLong();

	public abstract boolean saveLong(long longValue);
}
