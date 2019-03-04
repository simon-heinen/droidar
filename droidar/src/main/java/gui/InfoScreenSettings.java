package gui;

import gl.Color;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * You can use {@link InfoScreenSettings#addText(String)} and
 * {@link InfoScreenSettings#addTextWithIcon(int, String)} to display some
 * custom information to the user
 * 
 * @author Spobo
 * 
 */
public class InfoScreenSettings {

	private LinearLayout myLinLayout;
	private Context myContext;
	public Color backgroundColor;
	private String myCloseButtonText = "Close";
	public boolean hasCloseButton = true;
	public int padding = 10;
	public int iconPadding = 3;
	public int spacerPadding = 0;
	private String myLoadingText = " Loading... ";
	private boolean closeInstantly;

	public InfoScreenSettings(Context c) {
		myContext = c;
		myLinLayout = new LinearLayout(c);
		backgroundColor = new Color(0, 0, 0, 0.5f);
		myLinLayout.setOrientation(LinearLayout.VERTICAL);
		myLinLayout.setPadding(padding, padding, padding, padding);
		// myLinLayout.setLayoutParams(params)
	}

	public LinearLayout getLinLayout() {
		return myLinLayout;
	}

	public void addView(View v) {
		myLinLayout.addView(v);
	}

	public void addText(String text) {
		TextView t = new TextView(myContext);
		t.setText(text);
		t.setPadding(0, spacerPadding, 0, spacerPadding);
		myLinLayout.addView(t);
	}

	public void addTextWithIcon(int iconId, String text) {
		LinearLayout l = new LinearLayout(myContext);
		l.setGravity(Gravity.CENTER_VERTICAL);
		ImageView i = new ImageView(myContext);
		i.setPadding(0, iconPadding, iconPadding * 2, iconPadding);
		i.setImageResource(iconId);
		l.addView(i);
		TextView t = new TextView(myContext);
		t.setText(text);
		l.addView(t);
		l.setPadding(0, spacerPadding, 0, spacerPadding);
		myLinLayout.addView(l);
	}

	public String getLoadingText() {
		return myLoadingText;
	}

	public String getCloseButtonText() {
		return myCloseButtonText;
	}

	public void setCloseButtonText(String closeButtonText) {
		this.myCloseButtonText = closeButtonText;
	}

	public void setLoadingText(String myLoadingText) {
		this.myLoadingText = myLoadingText;
	}

	public boolean closeInstantly() {
		return closeInstantly;
	}

	public void setCloseInstantly() {
		closeInstantly = true;
	}

}
