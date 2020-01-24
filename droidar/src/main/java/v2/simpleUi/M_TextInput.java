package v2.simpleUi;

import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class M_TextInput implements ModifierInterface, UiDecoratable {

	private EditText editText;
	private UiDecorator myDecorator;
	private boolean editable = true;
	private boolean isLongText;
	private boolean horizontalScrollable;
	private float weightOfDescription = 1;
	private float weightOfInputText = 1;
	private static Handler myHandler = new Handler(Looper.getMainLooper());
	private String additionalInfoText;

	public void setWeightOfDescription(float weightOfDescription) {
		this.weightOfDescription = weightOfDescription;
	}

	public void setWeightOfInputText(float weightOfInputText) {
		this.weightOfInputText = weightOfInputText;
	}

	public M_TextInput() {
	}

	public M_TextInput(boolean editable, boolean isLongText,
			boolean horizontalScrollable) {
		setLongText(isLongText);
		setEditable(editable);
		setHorizontalScrollable(horizontalScrollable);
	}

	public void setLongText(boolean isLongText) {
		this.isLongText = isLongText;
	}

	public boolean isLongText() {
		return isLongText;
	}

	public void setHorizontalScrollable(boolean horizontalScrollable) {
		this.horizontalScrollable = horizontalScrollable;
	}

	public boolean isHorizontalScrollable() {
		return horizontalScrollable;
	}

	public void setInfoText(String infoText) {
		this.additionalInfoText = infoText;
		myHandler.post(new Runnable() {
			@Override
			public void run() {
				if (editText != null) {
					editText.setHint(additionalInfoText);
				}
			}
		});
	}

	@Override
	public View getView(Context context) {
		LinearLayout container = new LinearLayout(context);
		container.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);

		container.setGravity(Gravity.CENTER_VERTICAL);
		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfDescription);
		LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfInputText);
		if (isLongText()) {
			int m = 8;
			p2.setMargins(2 * m, m, 2 * m, m);
			container.setOrientation(LinearLayout.VERTICAL);
		}

		TextView nameText = new TextView(context);
		nameText.setText(getVarName());
		nameText.setLayoutParams(p);
		container.addView(nameText);

		editText = new EditText(context) {
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				if (horizontalScrollable) {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
				return super.onTouchEvent(event);
			}
		};
		editText.setLayoutParams(p2);
		editText.setText(load());
		editText.setEnabled(editable);
		editText.setFocusable(editable);
		editText.setFocusableInTouchMode(editable);
		setInfoText(additionalInfoText);
		if (isHorizontalScrollable()) {
			editText.setHorizontallyScrolling(true);
		}

		container.addView(editText);

		if (myDecorator != null) {
			int currentLevel = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, nameText, currentLevel + 1,
					UiDecorator.TYPE_INFO_TEXT);
			myDecorator.decorate(context, editText, currentLevel + 1,
					UiDecorator.TYPE_EDIT_TEXT);
		}
		return container;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		if (editText != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					editText.setEnabled(isEditable());
					editText.setFocusable(isEditable());
					editText.setFocusableInTouchMode(isEditable());
				}
			});
		}
	}

	public boolean isEditable() {
		return editable;
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	@Override
	public boolean save() {
		if (!editable) {
			return true;
		}
		return save(editText.getText().toString());
	}

	public abstract String load();

	public abstract String getVarName();

	public abstract boolean save(String newText);

}
