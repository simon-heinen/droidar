package gui.simpleUI;

import gui.simpleUI.Theme.ThemeColors;
import gui.simpleUI.modifiers.ButtonModifier;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;

/**
 * 
 * This is an old version of simpleui.googlecode.com which will be removed from
 * this project soon!! <br>
 * <br>
 * <br>
 * <br>
 * 
 * Don't forget to add<br>
 * 
 * < activity android:name="SmartUI" android:theme=
 * "@android:style/Theme.Translucent"> < /activity> <br>
 * to your Manifest.xml file!
 * 
 * @author Spobo
 * 
 */
@Deprecated
public class SimpleUIv1 extends Activity {

	public static final int DEFAULT_PADDING = 4;

	private static final String MESSAGE = "MESSAGE";
	private static final String CONFIG = "CONFIG";
	private static final int MOST_OUTER_PADDING = 10;
	private static HashMap<String, Object> transfairList;
	private static final int OUTER_BACKGROUND_DIMMING_COLOR = Theme.ThemeColors.blackT2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String key = getIntent().getExtras().getString("key");

		if (transfairList != null) {
			EditItem o = (EditItem) transfairList.get(key);
			if (o != null) {
				Object message = transfairList.get(key + MESSAGE);
				UIConfig config = (UIConfig) transfairList.get(key + CONFIG);
				showInContext(this, o, message, config);
			}
		} else {
			// TODO
			finish();
		}
	}

	private static void showInContext(Activity target, EditItem o,
			Object message, UIConfig config) {
		LinearLayout mostOuterBox = generateViewFor(target, o, message, config);
		/*
		 * setting the theme here does not work, so it has to be set in the
		 * manifest.. this.setTheme(android.R.style.Theme_Translucent);
		 */
		target.requestWindowFeature(Window.FEATURE_NO_TITLE);
		target.setContentView(mostOuterBox);

	}

	private static LinearLayout generateViewFor(Activity target, EditItem o,
			Object optionalMessage, UIConfig config) {
		ModifierGroup group = new ModifierGroup();

		Theme configTheme = config.loadTheme();
		if (configTheme != null) {
			group.setTheme(configTheme);
		}

		o.customizeScreen(group, optionalMessage);
		group.addModifier(config.loadCloseButtonsFor(target, group));

		LayoutParams layParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);

		LinearLayout containerForAllItems = new LinearLayout(target);
		containerForAllItems.setLayoutParams(layParams);
		containerForAllItems.setPadding(MOST_OUTER_PADDING, MOST_OUTER_PADDING,
				MOST_OUTER_PADDING, MOST_OUTER_PADDING);
		containerForAllItems.setOrientation(LinearLayout.VERTICAL);
		containerForAllItems.addView(group.getView(target));

		if (group.getTheme() != null) {
			group.getTheme().applyOuter1(containerForAllItems);
		}

		LinearLayout mostOuterBox = new LinearLayout(target);
		mostOuterBox.setGravity(Gravity.CENTER);
		mostOuterBox.setBackgroundColor(OUTER_BACKGROUND_DIMMING_COLOR);
		mostOuterBox.setPadding(MOST_OUTER_PADDING, MOST_OUTER_PADDING,
				MOST_OUTER_PADDING, MOST_OUTER_PADDING);
		mostOuterBox.addView(containerForAllItems);
		return mostOuterBox;
	}

	public static void showEditScreen(final Activity currentActivity,
			EditItem objectToEdit, Object optionalMessage) {
		UIConfig config = new UIConfig() {

			@Override
			public Theme loadTheme() {
				return getDefaultTheme(currentActivity);
			}

			@Override
			public ModifierInterface loadCloseButtonsFor(
					final Activity currentActivity, final ModifierGroup group) {
				ButtonModifier cancel = new ButtonModifier("Cancel") {

					@Override
					public void onClick() {
						currentActivity.finish();
					}
				};
				ButtonModifier ok = new ButtonModifier("Ok") {

					@Override
					public void onClick() {
						group.save();
						currentActivity.finish();
					}
				};
				return new HalfHalfModifier(cancel, ok);
			}

		};
		show(currentActivity, objectToEdit, optionalMessage, config);
	}

	public static void show(Context context, EditItem itemToDisplay,
			Object optionalMessage, UIConfig config) {
		Intent intent = new Intent(context, SimpleUIv1.class);
		if (itemToDisplay != null) {
			String key = addTransfairObject(context, itemToDisplay,
					optionalMessage, config);
			// The key to the object will be stored in the extras of the
			// activity:
			intent.putExtra("key", key);
		}
		context.startActivity(intent);
	}

	private static String addTransfairObject(Context context,
			EditItem itemToDisplay, Object message, UIConfig config) {
		if (transfairList == null)
			transfairList = new HashMap<String, Object>();
		String newKey = context.toString();
		transfairList.put(newKey, itemToDisplay);
		transfairList.put(newKey + MESSAGE, message);
		transfairList.put(newKey + CONFIG, config);
		return newKey;
	}

	public static void showInfoScreen(final Activity currentActivity,
			EditItem itemToDisplay, Object optionalMessage) {
		UIConfig config = new UIConfig() {

			@Override
			public Theme loadTheme() {
				return getDefaultTheme(currentActivity);
			}

			@Override
			public ModifierInterface loadCloseButtonsFor(
					final Activity currentActivity, final ModifierGroup group) {
				return new ButtonModifier("Close") {
					@Override
					public void onClick() {
						group.save();
						currentActivity.finish();
					}
				};
			}
		};
		show(currentActivity, itemToDisplay, optionalMessage, config);
	}

	public static Theme getDefaultTheme(Activity currentActivity) {
		// TODO Auto-generated method stub
		return Theme.A(currentActivity, ThemeColors.initToBlack());
	}

}
