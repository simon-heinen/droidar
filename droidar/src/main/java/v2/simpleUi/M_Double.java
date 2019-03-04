package v2.simpleUi;

import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class M_Double implements ModifierInterface, UiDecoratable {
	private EditText e;
	private UiDecorator myDecorator;
	private boolean editable = true;
	private float weightOfDescription = 1;
	private float weightOfInputText = 1;

	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public void setWeightOfDescription(float weightOfDescription) {
		this.weightOfDescription = weightOfDescription;
	}

	public void setWeightOfInputText(float weightOfInputText) {
		this.weightOfInputText = weightOfInputText;
	}

	public abstract double load();

	public abstract String getVarName();

	public abstract boolean save(double newValue);

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
		e.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		e.setText("" + load());
		e.setEnabled(editable);
		e.setFocusable(editable);
		e.setFocusableInTouchMode(editable);
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

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		if (e != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					e.setEnabled(isEditable());
					e.setFocusable(isEditable());
				}
			});
		}
	}

	@Override
	public boolean save() {
		if (!editable)
			return true;
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