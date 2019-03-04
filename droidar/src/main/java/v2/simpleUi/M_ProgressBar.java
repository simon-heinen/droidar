package v2.simpleUi;

import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class M_ProgressBar implements ModifierInterface, UiDecoratable {

	private ProgressBar progressBar;
	private UiDecorator myDecorator;
	private TextView nameText;
	private LinearLayout container;

	public void hide() {
		if (container != null) {
			container.setVisibility(View.GONE);
		}
	}

	public void showAgain() {
		if (container != null) {
			container.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public View getView(Context context) {
		container = new LinearLayout(context);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setGravity(Gravity.CENTER_VERTICAL);
		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2);
		LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);

		nameText = new TextView(context);
		nameText.setText(getVarName());
		nameText.setLayoutParams(p);
		container.addView(nameText);

		progressBar = new ProgressBar(context, null,
				android.R.attr.progressBarStyleHorizontal);
		progressBar.setLayoutParams(p2);
		progressBar.setProgress(loadInitValue());
		progressBar.setMax(loadMaxValue());

		container.addView(progressBar);
		container.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);

		if (myDecorator != null) {
			int currentLevel = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, nameText, currentLevel + 1,
					UiDecorator.TYPE_INFO_TEXT);
			myDecorator.decorate(context, progressBar, currentLevel + 1,
					UiDecorator.TYPE_EDIT_TEXT);
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

	public abstract int loadInitValue();

	public abstract int loadMaxValue();

	public abstract String getVarName();

	private Handler mHandler = new Handler(Looper.getMainLooper());

	public void updateValue(final int newProgressValue, final String updatedText) {
		// do it from the UI thread:
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				progressBar.setProgress(newProgressValue);
				if (updatedText != null)
					nameText.setText(updatedText);
			}
		});
	}

}
