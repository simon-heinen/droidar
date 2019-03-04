package v2.simpleUi;

import java.util.Date;
import java.util.HashMap;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

/**
 * Don't forget to add<br>
 * <br>
 * < activity android:name="v2.simpleUi.SimpleUI" android:theme=
 * "@android:style/Theme.Translucent"/> <br>
 * <br>
 * to your Manifest.xml file! <br>
 * <br>
 * If you want to use the {@link SimpleUI} activity by implementing your own
 * subactivity of {@link SimpleUI} then override the
 * {@link SimpleUI#loadStaticElementToDisplay()} and return a modifier or a view
 * to be displayed. This has some advantages over extending the normal
 * {@link Activity} but is not required to be able to use
 * {@link ModifierInterface}s.
 * 
 * @author Simon Heinen
 * 
 */
public class SimpleUI extends Activity {

	private static final String TRANSFAIR_KEY_ID = "transfairKey";
	private static final String LOG_TAG = "SimpleUI v2";
	private static final boolean DEBUG = false;
	private static SimpleUiApplication application;

	private View ViewToShow;
	private ModifierInterface myModifier;

	/**
	 * Will save changes when the close button is pressed
	 * 
	 * @param context
	 * @param closeButtonText
	 *            e.g. "Save & Close"
	 * @param itemsToDisplay
	 * @return
	 */
	public static boolean showInfoDialog(Context context,
			String closeButtonText, final M_Container itemsToDisplay) {
		if (itemsToDisplay == null) {
			Log.e(LOG_TAG, "itemsToDisplay object was null");
			return false;
		}
		itemsToDisplay.add(new M_Button(closeButtonText) {

			@Override
			public void onClick(Context context, Button clickedButton) {
				if (itemsToDisplay.save() && context instanceof Activity)
					((Activity) context).finish();
			}
		});
		return showUi(context, itemsToDisplay);
	}

	public static boolean showCancelOkDialog(Context context,
			String cancelText, String okText, final M_Container itemsToDisplay) {

		ModifierInterface left = new M_Button(cancelText) {

			@Override
			public void onClick(Context context, Button clickedButton) {
				if (context instanceof Activity)
					((Activity) context).finish();
			}
		};
		ModifierInterface right = new M_Button(okText) {

			@Override
			public void onClick(Context context, Button clickedButton) {
				if (itemsToDisplay.save() && context instanceof Activity)
					((Activity) context).finish();
			}
		};
		itemsToDisplay.add(new M_HalfHalf(left, right));
		return showUi(context, itemsToDisplay);
	}

	/**
	 * @param currentActivity
	 * @param contentToShow
	 *            e.g. a {@link M_Container} which is filled with all the items
	 * @return
	 */
	public static boolean showUi(Context context,
			ModifierInterface modifierToDisplay) {
		if (modifierToDisplay != null) {
			Intent intent = new Intent(context, SimpleUI.class);
			try {
				String key = storeObjectInTransfairList(context,
						modifierToDisplay);
				/*
				 * The key to the object will be stored in the extras of the
				 * intent:
				 */
				intent.putExtra(TRANSFAIR_KEY_ID, key);
			} catch (Exception e) {
				e.printStackTrace();
			}
			startActivity(context, intent);
			return true;
		}
		return false;
	}

	public static void startActivity(Context context, Intent intent) {
		if (context instanceof Activity) {
			context.startActivity(intent);
		} else {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (DEBUG)
			Log.i(LOG_TAG, "onPause" + " by " + this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (DEBUG)
			Log.v(LOG_TAG, "onResume" + " by " + this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (DEBUG)
			Log.v(LOG_TAG, "onRestart" + " by " + this);
	}

	@Override
	protected void onDestroy() {
		if (DEBUG)
			Log.v(LOG_TAG, "onDestroy" + " by " + this);
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (DEBUG)
			Log.v(LOG_TAG, "onStart" + " by " + this);
	}

	@Override
	protected void onStop() {
		if (DEBUG)
			Log.v(LOG_TAG, "onStop" + " by " + this);
		super.onStop();
	}

	/**
	 * @param itemToDisplay
	 * @return the key for the location where it is stored
	 */
	private static String storeObjectInTransfairList(Context c,
			Object itemToDisplay) {

		String newKey = new Date().toString() + itemToDisplay.toString();
		getApplication(c).getTransferList().put(newKey, itemToDisplay);
		if (DEBUG)
			getApplication(c).getTransferList();
		return newKey;
	}

	public static SimpleUiApplication getApplication(Context c) {
		SimpleUiApplication app = null;
		if (c instanceof Activity) {
			try {
				app = ((SimpleUiApplication) ((Activity) c).getApplication());
			} catch (Exception e) {
				Log.v(LOG_TAG, "The used android.app.Application was not a "
						+ "SimpleUiApplication. Please "
						+ "change this in the manifest!");
			}
			if (app != null) {
				HashMap<String, Object> tr = null;
				if (application != null && app != application) {
					if (DEBUG)
						Log.w(LOG_TAG, "new application and already "
								+ "loaded application were not "
								+ "equal! Replacing old reference");
					tr = application.getTransferList();
				}
				application = app;
				// try to resque all the objects from the old list:
				if (tr != null)
					application.getTransferList().putAll(tr);
			}
		}
		if (application == null) {
			// create the backup singleton
			application = new SimpleUiApplication();
		}
		return application;
	}

	private Object loadObjectFromTransfairList(String key) {
		HashMap<String, Object> transfairList = getApplication(this)
				.getTransferList();
		if (key == null) {
			if (DEBUG)
				Log.i(LOG_TAG, "passed key was null, will"
						+ " try to load content from static method");
			return null;
		}
		if (transfairList == null) {
			if (DEBUG)
				Log.i(LOG_TAG, "transfairList object was null, so "
						+ "storeObjectInTransfairList was "
						+ "never called before!");
			return null;
		}
		Object o = transfairList.get(key);
		if (DEBUG)
			Log.v(LOG_TAG, "Returning " + o + " for the passed key=" + key);
		// transfairList.remove(key);
		return o;
	}

	// private View myView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (DEBUG)
			Log.i(LOG_TAG, "onCreate" + " by " + this);
		try {

			String key = null;
			if (savedInstanceState != null)
				key = savedInstanceState.getString(TRANSFAIR_KEY_ID);
			else if (getIntent() != null && getIntent().getExtras() != null)
				key = getIntent().getExtras().getString(TRANSFAIR_KEY_ID);
			else
				Log.i(LOG_TAG, "On create got no information what to display");

			if (DEBUG)
				Log.i(LOG_TAG, "onCreate got key=" + key);

			ViewToShow = loadContentToViewField(key);
			if (DEBUG)
				Log.d(LOG_TAG, "Loaded " + ViewToShow);
			if (ViewToShow != null) {
				try {
					((ViewGroup) ViewToShow.getParent()).removeView(ViewToShow);
				} catch (Exception e) {
				}
				setContentView(ViewToShow);
			} else {
				M_Container c = createErrorInfo();
				setContentView(c.getView(this));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private View loadContentToViewField(String key) {
		Object o = loadObjectFromTransfairList(key);
		if (o == null)
			o = loadStaticElementToDisplay();
		if (o instanceof ModifierInterface) {
			myModifier = ((ModifierInterface) o);
			return ((ModifierInterface) o).getView(this);
		}
		if (o instanceof View)
			return (View) o;
		return null;
	}

	/**
	 * Override this method in your subclass to get all the features of the
	 * {@link SimpleUI} class but with a static content
	 * 
	 * @return a {@link View} or a {@link ModifierInterface} to be displayed
	 */
	public Object loadStaticElementToDisplay() {
		return null;
	}

	/**
	 * This can happen if the complete application is killed by the system, the
	 * content is loaded dynamically so this is necessary to switch the
	 * application back to a valid state.
	 * 
	 * @return
	 */
	public M_Container createErrorInfo() {
		M_Container c = new M_Container();
		c.add(new M_InfoText(R.drawable.ic_dialog_alert,
				"The application was closed by Android, it has to be reopened! "
						+ "Please reopen the application by "
						+ "clicking the icon in the application list."));
		c.add(new M_Button("Restart App") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUI.this.finish();
				Intent i = new Intent(Intent.ACTION_MAIN);
				PackageManager manager = getPackageManager();
				i = manager.getLaunchIntentForPackage(context.getPackageName());
				i.addCategory(Intent.CATEGORY_LAUNCHER);
				int FLAG_ACTIVITY_CLEAR_TASK = 32768;
				i.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
						| Intent.FLAG_ACTIVITY_NEW_TASK
						| FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
				// if (DEBUG)
				// Log.w(LOG_TAG, "Killing complete process");
				// System.gc();
				// android.os.Process.killProcess(android.os.Process.myPid());
				// System.exit(1);
			}
		});
		return c;
	}

	/**
	 * It is possible to listen to the onActivityResult from any
	 * {@link ModifierInterface} class you want, just implement this interface
	 * as well, pass the {@link ModifierInterface} to the
	 * {@link SimpleUI#showUi(Context, ModifierInterface)} e.g. and it will be
	 * notified when the {@link SimpleUI} gets an onActivityResult event
	 * 
	 * @author Simon Heinen
	 * 
	 */
	public interface SimpleUiActivityResultListener {
		/**
		 * read also {@link Activity#onActivityResult}
		 * 
		 * @param a
		 * @param requestCode
		 * @param resultCode
		 * @param data
		 */
		void onActivityResult(Activity a, int requestCode, int resultCode,
				Intent data);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (myModifier instanceof SimpleUiActivityResultListener) {
			((SimpleUiActivityResultListener) myModifier).onActivityResult(
					this, requestCode, resultCode, data);
		} else
			super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (DEBUG)
			Log.v(LOG_TAG, "onSaveInstanceState" + " by " + this);
		if (ViewToShow != null) {
			/*
			 * http://stackoverflow.com/questions/151777/how-do-i-save-an-android
			 * - applications-state
			 */
			String key = storeObjectInTransfairList(this, ViewToShow);
			if (DEBUG)
				Log.i(LOG_TAG, "onSaveInstanceState - storing the UI ("
						+ ViewToShow + ") via the key: " + key);
			outState.putString(TRANSFAIR_KEY_ID, key);
		} else {
			if (DEBUG)
				Log.e(LOG_TAG, "Could not save the modifierToShow "
						+ "field because it was null!");
		}
		super.onSaveInstanceState(outState);
	}

}
