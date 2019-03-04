package v2.simpleUi;

import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class M_Checkbox implements ModifierInterface, UiDecoratable {

	private CheckBox e;
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

	@Override
	public View getView(final Context context) {
		LinearLayout l = new LinearLayout(context);
		l.setGravity(Gravity.CENTER_VERTICAL);
		l.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);
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

		t.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (e != null && e.isEnabled()) {
					e.setChecked(!e.isChecked());
				}
			}
		});

		// TODO replace by better view representative:
		e = new CheckBox(context);

		e.setChecked(loadVar());
		e.setEnabled(editable);
		e.setFocusable(editable);
		e.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				M_Checkbox.this.onCheckedChanged(context, e, isChecked);
			}

		});

		LinearLayout l2 = new LinearLayout(context);
		l2.setLayoutParams(p2);
		l2.setGravity(Gravity.RIGHT);
		l2.addView(e);
		l.addView(l2);

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, t, level, UiDecorator.TYPE_INFO_TEXT);
			myDecorator.decorate(context, e, level, UiDecorator.TYPE_EDIT_TEXT);
		}

		return l;
	}

	public void onCheckedChanged(Context context, CheckBox e, boolean isChecked) {
		// on default do not react to this
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
					e.setEnabled(isEditable());
				}
			});
	}

	public void setBoolValueOfViewIfPossible(final boolean newValue) {
		if (e != null)
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					e.setChecked(newValue);
				}
			});
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	public abstract boolean loadVar();

	public abstract CharSequence getVarName();

	public abstract boolean save(boolean newValue);

	@Override
	public boolean save() {
		if (!editable)
			return true;
		return save(e.isChecked());
	}

}