package v2.simpleUi;

import java.util.ArrayList;
import java.util.regex.Pattern;

import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * to allow or ban certain providers you can use
 * {@link M_EmailInput#setProviderBlackList(ArrayList)} and
 * {@link M_EmailInput#setProviderWhiteList(ArrayList)} and put Strings like
 * "@gmail.com" in there <br>
 * <br>
 * 
 * Use {@link M_EmailInput#sendMail(Context, String, String, String)} to send
 * mails from your application via the mail application
 * 
 * @author Simon Heinen
 * 
 */
public abstract class M_EmailInput implements ModifierInterface, UiDecoratable {

	private EditText editText;
	private UiDecorator myDecorator;
	private ArrayList<String> providerWhiteList;
	private ArrayList<String> providerBlackList;
	int originalEditTextColor;

	public void setProviderBlackList(ArrayList<String> providerBlackList) {
		this.providerBlackList = providerBlackList;
	}

	public void setProviderWhiteList(ArrayList<String> providerWhiteList) {
		this.providerWhiteList = providerWhiteList;
	}

	@Override
	public View getView(Context context) {
		LinearLayout container = new LinearLayout(context);
		container.setGravity(Gravity.CENTER_VERTICAL);
		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2);
		LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);

		TextView nameText = new TextView(context);
		nameText.setText(getVarName());
		nameText.setLayoutParams(p);
		container.addView(nameText);

		editText = new EditText(context);
		editText.setLayoutParams(p2);
		editText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		if (myDecorator != null) {
			int currentLevel = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, nameText, currentLevel + 1,
					UiDecorator.TYPE_INFO_TEXT);
			myDecorator.decorate(context, editText, currentLevel + 1,
					UiDecorator.TYPE_EDIT_TEXT);
		}

		originalEditTextColor = editText.getCurrentTextColor();

		editText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (emailIsOk(editText.getText().toString())) {
					setEditTextToNormal(editText);
				} else {
					setEditTextToError(editText);
				}
				return false;
			}

		});

		editText.setText(load());

		container.addView(editText);
		container.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);

		return container;
	}

	private void setEditTextToError(EditText editText) {
		editText.setTextColor(Color.argb(255, 255, 0, 0));
	}

	private void setEditTextToNormal(EditText editText) {
		editText.setTextColor(originalEditTextColor);
	}

	final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[^@ ]+[@][^@ ]+[.][^@ ]+");

	private boolean emailIsOk(String email) {

		boolean looksLikeEmail = EMAIL_ADDRESS_PATTERN.matcher(email).matches();
		boolean isInWhiteList = true;
		if (providerWhiteList != null)
			isInWhiteList = isInProviderList(email, providerWhiteList);
		boolean isInBlackList = false;
		if (providerBlackList != null)
			isInBlackList = isInProviderList(email, providerBlackList);
		return looksLikeEmail && isInWhiteList && (!isInBlackList);
	}

	private boolean isInProviderList(String email,
			ArrayList<String> providerList) {
		boolean isInProviderList = false;
		for (String provider : providerList) {
			isInProviderList |= email.endsWith(provider);
		}
		return isInProviderList;
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	@Override
	public boolean save() {
		String email = editText.getText().toString();
		if (emailIsOk(email))
			return save(email);
		else
			return false;
	}

	public static void sendMail(Context c, String emailAddress,
			String myMailSubject, String mailText) {
		final Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { emailAddress });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				myMailSubject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mailText);
		c.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

	public abstract String load();

	public abstract String getVarName();

	public abstract boolean save(String validEmailAddress);

}
