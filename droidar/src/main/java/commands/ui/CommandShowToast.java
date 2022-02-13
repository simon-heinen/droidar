package commands.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import commands.Command;

public class CommandShowToast extends Command {

	private String myTextToDisplay;
	private Context myContext;

	private LinearLayout myLinLayout;
	private EditText myEditText;

	public CommandShowToast(Context c, String textToDisplay) {
		myTextToDisplay = textToDisplay;
		myContext = c;
	}

	@Override
	public boolean execute() {

		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(myContext, myTextToDisplay, Toast.LENGTH_LONG)
						.show();
			}
		});

		return true;
	}

	@Override
	public boolean execute(Object transfairObject) {

		if (transfairObject == myLinLayout) {
			final String s = myEditText.getText().toString();

			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(myLinLayout.getContext(), s,
							Toast.LENGTH_LONG).show();
				}
			});
			return true;
		}

		return execute();
	}

	@Override
	public View getMyListItemView(View viewToUseIfNotNull, ViewGroup parentView) {
		myLinLayout = new LinearLayout(parentView.getContext());
		myLinLayout.setFocusable(false);
		myLinLayout.setOrientation(LinearLayout.VERTICAL);

		TextView t = new TextView(parentView.getContext());
		t.setText("Show Toast:");
		t.setTextSize(25);
		t.setFocusable(false);
		myLinLayout.addView(t);

		myEditText = new EditText(parentView.getContext());
		myLinLayout.addView(myEditText);

		final Button b = new Button(parentView.getContext());
		b.setText("Lock Input");
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myEditText.setFocusable(!myEditText.isFocusable());
				b.setFocusable(false);
				b.setVisibility(View.GONE);
				myEditText.setEnabled(false);
			}
		});

		myLinLayout.addView(b);

		return myLinLayout;
	}

	/**
	 * Only a wrapper method to make life easier ;)
	 * 
	 * @param myTargetActivity
	 * @param text
	 */
	public static void show(Activity myTargetActivity, String text) {
		new CommandShowToast(myTargetActivity, text).execute();
	}
}
