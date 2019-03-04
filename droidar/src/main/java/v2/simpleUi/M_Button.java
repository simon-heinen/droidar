package v2.simpleUi;

import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public abstract class M_Button implements ModifierInterface, UiDecoratable {

	private String myText;
	private UiDecorator myDecorator;

	public M_Button(String buttonText) {
		myText = buttonText;
	}

	@Override
	public View getView(final Context context) {

		final Button b = new Button(context);
		b.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				M_Button.this.onClick(context, b);
			}
		});
		b.setText(myText);

		int p = 12;
		b.setPadding(p, p, p, p);

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator
					.decorate(context, b, level + 1, UiDecorator.TYPE_BUTTON);
		}

		return b;
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

	public abstract void onClick(Context context, Button clickedButton);

}