package gui;

import system.Setup;
import system.TaskManager;
import util.Log;
import util.Wrapper;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import commands.Command;
import commands.logic.CommandSetWrapperToValue;
import commands.system.CommandDeviceVibrate;

import de.rwth.R;

public class GuiSetup {

	private static final String LOG_TAG = "GuiSetup";
	private static final long VIBRATION_DURATION_IN_MS = 20;
	// private static final int BUTTON_BACKGROUND =
	// android.R.drawable.alert_light_frame;
	private LinearLayout topOuter;
	private LinearLayout bottomOuter;
	private LinearLayout leftOuter;
	private LinearLayout rightOuter;
	private LinearLayout bottomView;
	private LinearLayout topView;
	private LinearLayout leftView;
	private LinearLayout rightView;
	private LinearLayout bottomRightView;

	private RelativeLayout main;
	private Setup mySetup;
	/**
	 * will be set to true in {@link GuiSetup} constructor on default
	 */
	private boolean vibrationEnabled;
	private CommandDeviceVibrate vibrateCommand;

	/**
	 * @param setup
	 * @param source
	 *            the xml layout converted into a view
	 */
	public GuiSetup(Setup setup, View source) {

		mySetup = setup;
		Log.d(LOG_TAG, "GuiSetup init");
		setVibrationFeedbackEnabled(true);

		main = (RelativeLayout) source.findViewById(R.id.main_view);

		bottomOuter = (LinearLayout) source.findViewById(R.id.LLA_bottom);
		topOuter = (LinearLayout) source.findViewById(R.id.LLA_top);
		leftOuter = (LinearLayout) source.findViewById(R.id.LLA_left);
		rightOuter = (LinearLayout) source.findViewById(R.id.LLA_right);

		bottomView = (LinearLayout) source.findViewById(R.id.LinLay_bottom);
		topView = (LinearLayout) source.findViewById(R.id.LinLay_top);
		leftView = (LinearLayout) source.findViewById(R.id.LinLay_left);
		rightView = (LinearLayout) source.findViewById(R.id.LinLay_right);

		bottomRightView = (LinearLayout) source
				.findViewById(R.id.LinLay_bottomRight);

	}

	public void addButtonToBottomView(Command a, String buttonText) {
		addButtonToView(bottomView, a, buttonText);
	}

	public void addButtonToLeftView(Command a, String buttonText) {
		addButtonToView(leftView, a, buttonText);
	}

	public void addButtonToRightView(Command a, String buttonText) {
		addButtonToView(rightView, a, buttonText);
	}

	public void addImangeButtonToRightView(int imageId, Command command) {
		addImageButtonToView(rightView, command, imageId);
	}

	public void addImangeButtonToTopView(int imageId, Command command) {
		addImageButtonToView(topView, command, imageId);
	}

	public void addButtonToTopView(Command a, String buttonText) {
		addButtonToView(topView, a, buttonText);
	}

	public void addImageButtonToView(LinearLayout target, final Command c,
			int imageId) {
		if (target != null) {
			ImageButton b = new ImageButton(target.getContext());
			// b.setBackgroundResource(BUTTON_BACKGROUND);
			b.setImageResource(imageId);
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isVibrationFeedbackEnabled() && vibrateCommand != null) {
						vibrateCommand.execute();
					}
					c.execute();
				}
			});
			target.addView(b);
		} else {
			Log.e(LOG_TAG, "No target specified (was null) "
					+ "to add the image-button to.");
		}
	}

	/**
	 * @param target
	 * @param onClickCommand
	 * @param buttonText
	 */
	public void addButtonToView(LinearLayout target,
			final Command onClickCommand, String buttonText) {
		if (target != null) {
			Button b = new Button(target.getContext());
			// b.setBackgroundResource(BUTTON_BACKGROUND);
			// b.setTextColor(gl.Color.blackTransparent().toIntRGB());
			b.setText(buttonText);
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isVibrationFeedbackEnabled() && vibrateCommand != null) {
						vibrateCommand.execute();
					}
					onClickCommand.execute();
				}

			});
			target.addView(b);
		} else {
			// TODO then main.xml damaged or changed?
		}
	}

	private boolean isVibrationFeedbackEnabled() {
		return vibrationEnabled;
	}

	public void setVibrationFeedbackEnabled(boolean vibrate) {
		this.vibrationEnabled = vibrate;
		if (vibrate && vibrateCommand == null) {
			try {
				Log.d(LOG_TAG,
						"Trying to enable vibration feedback for UI actions");
				vibrateCommand = new CommandDeviceVibrate(
						mySetup.myTargetActivity, VIBRATION_DURATION_IN_MS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addCheckBoxToView(LinearLayout v, String text,
			boolean initFlag, final Command isCheckedCommand,
			final Command isNotCheckedCommand) {
		CheckBox c = new CheckBox(v.getContext());

		c.setText(text);
		c.setChecked(initFlag);
		c.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					isCheckedCommand.execute();
				} else {
					isNotCheckedCommand.execute();
				}

			}
		});
		v.addView(c);
	}

	public void addCheckBoxToView(LinearLayout v, String string,
			Wrapper wrapperWithTheBooleanToSwitchInside) {
		CommandSetWrapperToValue setTrue = new CommandSetWrapperToValue(
				wrapperWithTheBooleanToSwitchInside, true);
		CommandSetWrapperToValue setFalse = new CommandSetWrapperToValue(
				wrapperWithTheBooleanToSwitchInside, false);
		addCheckBoxToView(v, string,
				wrapperWithTheBooleanToSwitchInside.getBooleanValue(), setTrue,
				setFalse);
	}

	/**
	 * @param v
	 * @param weight
	 *            2 or 3 is a good value
	 * @param height
	 *            <150
	 * @return
	 */
	public void addViewToBottomRight(View v, float weight, int heightInPixels) {
		bottomRightView.addView(v);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, heightInPixels);
		p.weight = weight;
		bottomRightView.setLayoutParams(p);
	}

	public EditText addSearchbarToView(LinearLayout v,
			final Command commandOnSearch, String clearText) {
		final EditText t = new EditText(v.getContext());
		t.setHint(clearText);
		t.setHintTextColor(Color.GRAY);
		t.setMinimumWidth(200);
		t.setSingleLine();
		t.setSelectAllOnFocus(true);
		t.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					String text = t.getText().toString();
					if (text.length() > 0) {
						t.setText("");
						Log.d(LOG_TAG, "Gui-searchbar fiering text: '" + text
								+ "'(length=" + text.length() + ")!");
						return commandOnSearch.execute(text);
					}
				}
				return false;
			}
		});
		v.addView(t);
		return t;
	}

	public void addTaskmanagerToView(LinearLayout v) {
		addTaskmanagerToView(v, "", " <", "/", "> ");
	}

	/**
	 * @param v
	 * @param idleText
	 * @param workingPrefix
	 *            the text in front of the current working progress. Example:
	 *            The < in <2/10>
	 * @param workingMiddleText
	 *            the text in the middle of the current working progress.
	 *            Example: The / in <2/10>
	 * @param workingSuffix
	 *            the text at the end of the current working progress. Example:
	 *            The > in <2/10>
	 */
	public void addTaskmanagerToView(LinearLayout v, String idleText,
			String workingPrefix, String workingMiddleText, String workingSuffix) {
		v.addView(TaskManager.getInstance().getProgressWheel(v.getContext()));
		v.addView(TaskManager.getInstance().getProgressTextView(v.getContext(),
				idleText, workingPrefix));
		v.addView(TaskManager.getInstance().getProgressSizeText(v.getContext(),
				idleText, workingMiddleText, workingSuffix));
	}

	public void addViewToBottom(View v) {
		bottomView.addView(v);
	}

	public void addViewToTop(View v) {
		topView.addView(v);
	}

	public void addViewToRight(View v) {
		rightView.addView(v);
	}

	public View getMainContainerView() {
		return main;
	}

	public LinearLayout getLeftView() {
		return leftView;
	}

	public LinearLayout getRightView() {
		return rightView;
	}

	public LinearLayout getBottomView() {
		return bottomView;
	}

	public LinearLayout getTopView() {
		return topView;
	}

	public void setBackroundColor(LinearLayout target, int color) {
		target.setBackgroundColor(color);
	}

	public void setBottomBackroundColor(int color) {
		setBackroundColor(bottomOuter, color);
	}

	public void setBottomMinimumHeight(int height) {
		setMinimumHeight(bottomOuter, height);
	}

	public void setBottomViewCentered() {
		/*
		 * TODO doesnt work anymore because of the additional linlayout in the
		 * right bottom corner! fix it
		 */
		bottomOuter.setGravity(Gravity.CENTER);
	}

	public void setLeftBackroundColor(int color) {
		setBackroundColor(leftOuter, color);
	}

	public void setLeftViewCentered() {
		leftOuter.setGravity(Gravity.CENTER);
	}

	public void setLeftWidth(int width) {
		setMinimumWidth(leftView, width);
	}

	public void setMinimumHeight(LinearLayout target, int height) {
		target.setMinimumHeight(height);
	}

	public void setMinimumWidth(LinearLayout target, int width) {
		target.setMinimumWidth(width);
	}

	public void setRightBackroundColor(int color) {
		setBackroundColor(rightOuter, color);
	}

	public void setRightViewCentered() {
		rightOuter.setGravity(Gravity.CENTER);
	}

	public void setRightViewAllignBottom() {
		rightOuter.setGravity(Gravity.BOTTOM);
	}

	public void setRightWidth(int width) {
		setMinimumWidth(rightView, width);
	}

	public void setTopBackroundColor(int color) {
		setBackroundColor(topOuter, color);
	}

	public void setTopHeight(int height) {
		setMinimumHeight(topView, height);
	}

	public void setTopViewAllignRight() {
		topOuter.setGravity(Gravity.RIGHT);
	}

	public void setTopViewCentered() {
		topOuter.setGravity(Gravity.CENTER);
	}

	/**
	 * This method does the same thing as
	 * {@link Setup#addItemToOptionsMenu(Command, String)}!
	 * 
	 * @param commandToAdd
	 * @param menuItemText
	 */
	public void addItemToOptionsMenu(Command commandToAdd, String menuItemText) {
		mySetup.addItemToOptionsMenu(commandToAdd, menuItemText);
	}

}
