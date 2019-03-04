package v3;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public abstract class M_DoubleModifier extends M_TextModifier {
	private Double minimumValue;
	private Double maximumValue;

	public void setMinimumAndMaximumValue(Double minValue, Double maxValue) {
		minimumValue = minValue;
		maximumValue = maxValue;
	}

	public Double getMaximumValue() {
		return maximumValue;
	}

	public Double getMinimumValue() {
		return minimumValue;
	}

	@Override
	public void applyTextFilterIfNeeded(EditText e) {
		e.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
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

	private static void setMinMaxFilterFor(EditText e, final Double min,
			final Double max) {
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
						double v = Double.parseDouble(s.toString());
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
			return saveDouble(Double.parseDouble(newValue));
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
		return "" + loadDouble();
	}

	public abstract double loadDouble();

	public abstract boolean saveDouble(double doubleValue);
}
