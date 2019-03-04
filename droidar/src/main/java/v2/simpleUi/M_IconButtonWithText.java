package v2.simpleUi;

import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public abstract class M_IconButtonWithText implements ModifierInterface,
		UiDecoratable {

	private String myText;
	private UiDecorator myDecorator;
	private int myIconId;
	private ImageView imageButton;

	public M_IconButtonWithText(int iconId) {
		myIconId = iconId;
	}

	public M_IconButtonWithText(int iconId, String buttonText) {
		myIconId = iconId;
		myText = buttonText;
	}

	@Override
	public View getView(final Context context) {

		LinearLayout l = new LinearLayout(context);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		l.setOrientation(LinearLayout.VERTICAL);
		l.setLayoutParams(params);

		// l.setGravity(Gravity.CENTER_HORIZONTAL);

		imageButton = new ImageView(context);
		LayoutParams imparams = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		int p = 3;
		imparams.setMargins(p, p, p, p);
		imageButton.setLayoutParams(imparams);

		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				M_IconButtonWithText.this.onClick(context, imageButton);
			}
		});
		imageButton.setImageResource(myIconId);
		l.addView(imageButton);

		TextView t = null;
		if (myText != null) {
			t = new TextView(context);
			t.setText(myText);
			t.setGravity(Gravity.CENTER_HORIZONTAL);
			l.addView(t);
		}

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, imageButton, level + 1,
					UiDecorator.TYPE_ICON);
			if (t != null) {
				myDecorator.decorate(context, t, level + 1,
						UiDecorator.TYPE_INFO_TEXT);
			}
		}

		return l;

	}

	public ImageView getImageButton() {
		return imageButton;
	}

	@Override
	public boolean save() {
		return true;
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	public abstract void onClick(Context context, ImageView clickedButton);

	public void setIconId(int iconId) {
		myIconId = iconId;
		if (imageButton != null) {
			imageButton.setImageResource(myIconId);
		}
	}

}