package gui.simpleUI.modifiers;

import gui.simpleUI.AbstractModifier;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public abstract class ButtonModifier extends AbstractModifier {

	private String myText;

	public ButtonModifier(String buttonText) {
		myText = buttonText;
	}

	@Override
	public View getView(Context context) {

		LinearLayout l = new LinearLayout(context);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		l.setLayoutParams(params);

		Button b = new Button(context);
		b.setLayoutParams(params);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ButtonModifier.this.onClick();
			}
		});
		b.setText(myText);

		if (getTheme() != null)
			getTheme().applyNormal1(b);

		int p = 12;
		b.setPadding(p, p, p, p);

		l.addView(b);
		int p2 = 4;
		l.setPadding(p2, p2, p2, p2);

		return l;
	}

	@Override
	public boolean save() {
		return true;
	}

	public abstract void onClick();

}