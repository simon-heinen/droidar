package v2.simpleUi;

import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class M_Caption implements ModifierInterface, UiDecoratable {

	private String myText;
	private float mySizeFactor = 1.1f;
	private UiDecorator myDecorator;
	private OnClickListener myOnClickListener;
	private LinearLayout container;

	public M_Caption(String text) {
		myText = text;
	}

	/**
	 * @param text
	 * @param sizeFactor
	 *            default is 1.1f (of normal size)
	 */
	public M_Caption(String text, float sizeFactor) {
		this(text);
		mySizeFactor = sizeFactor;
	}

	public void setMyText(String myText) {
		this.myText = myText;
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.myOnClickListener = onClickListener;
	}

	@Override
	public View getView(Context context) {

		int bottomAndTopPadding = 4;
		int textPadding = 7;

		container = new LinearLayout(context);
		container.setGravity(Gravity.CENTER);

		container.setPadding(0, bottomAndTopPadding, 0, bottomAndTopPadding);

		TextView t = new TextView(context);
		t.setText(myText);
		t.setPadding(textPadding, textPadding, textPadding, textPadding);
		t.setGravity(Gravity.CENTER_HORIZONTAL);
		if (myOnClickListener != null) {
			t.setOnClickListener(myOnClickListener);
		}
		container.addView(t);

		if (mySizeFactor != 1) {
			t.setTextSize(t.getTextSize() * mySizeFactor);
		}

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, container, level + 1,
					UiDecorator.TYPE_CONTAINER);
			myDecorator.decorate(context, t, level + 1,
					UiDecorator.TYPE_CAPTION);
		}

		return container;
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	@Override
	public boolean save() {
		return true;
	}

	public int getHeightInPixels() {
		return container.getHeight();
	}

}