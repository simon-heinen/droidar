package gui.simpleUI.modifiers;

import gui.simpleUI.AbstractModifier;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public abstract class ImageButtonModifier extends AbstractModifier {

	private int myText;
	private LinearLayout l;
	private ImageButton b;

	public ImageButtonModifier(int iconId) {
		myText = iconId;
	}

	@Override
	public View getView(Context context) {

		l = new LinearLayout(context);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		l.setLayoutParams(params);

		b = new ImageButton(context);
		b.setLayoutParams(params);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageButtonModifier.this.onClick();
			}
		});
		b.setImageResource(myText);

		if (getTheme() != null)
			getTheme().applyNormal1(b);

		int p = 6;
		b.setPadding(p, p, p, p);

		l.addView(b);
		int p2 = 2;
		l.setPadding(p2, p2, p2, p2);

		return l;
	}

	@Override
	public boolean save() {
		return true;
	}

	public void disableButton() {
		l.removeView(b);

	}

	public abstract void onClick();

}