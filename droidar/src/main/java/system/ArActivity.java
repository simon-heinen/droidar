package system;

import util.Log;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This is an example activity which demonstrates how to use a Setup object. It
 * wraps the Setup object and forwards all needed events to it.
 * 
 * @author Simon Heinen
 * 
 */
public class ArActivity extends Activity {

	private static final String LOG_TAG = "ArActivity";

	private static Setup staticSetupHolder;

	private Setup mySetupToUse;

	/**
	 * Called when the activity is first created.
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "main onCreate");
		if (staticSetupHolder != null) {
			mySetupToUse = staticSetupHolder;
			staticSetupHolder = null;
			runSetup();
		} else {
			Log.e(LOG_TAG, "There was no Setup specified to use. "
					+ "Please use ArActivity.show(..) when you "
					+ "want to use this way of starting the AR-view!");
			this.finish();
		}
	}

	public static void startWithSetup(Activity currentActivity, Setup setupToUse) {
		ArActivity.staticSetupHolder = setupToUse;
		currentActivity.startActivity(new Intent(currentActivity,
				ArActivity.class));
	}

	public static void startWithSetupForResult(Activity currentActivity,
			Setup setupToUse, int requestCode) {
		ArActivity.staticSetupHolder = setupToUse;
		currentActivity.startActivityForResult(new Intent(currentActivity,
				ArActivity.class), requestCode);
	}

	private void runSetup() {
		mySetupToUse.run(this);
	}

	@Override
	protected void onRestart() {
		if (mySetupToUse != null)
			mySetupToUse.onRestart(this);
		super.onRestart();
	}

	@Override
	protected void onResume() {
		if (mySetupToUse != null)
			mySetupToUse.onResume(this);
		super.onResume();
	}

	@Override
	protected void onStart() {
		if (mySetupToUse != null)
			mySetupToUse.onStart(this);
		super.onStart();
	}

	@Override
	protected void onStop() {
		if (mySetupToUse != null)
			mySetupToUse.onStop(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (mySetupToUse != null)
			mySetupToUse.onDestroy(this);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (mySetupToUse != null)
			mySetupToUse.onPause(this);
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((mySetupToUse != null)
				&& (mySetupToUse.onKeyDown(this, keyCode, event)))
			return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (((mySetupToUse != null) && mySetupToUse.onCreateOptionsMenu(menu)))
			return true;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (mySetupToUse != null)
			return mySetupToUse.onMenuItemSelected(featureId, item);
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d(LOG_TAG, "main onConfigChanged");
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
			Log.d(LOG_TAG, "orientation changed to landscape");
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
			Log.d(LOG_TAG, "orientation changed to portrait");
		super.onConfigurationChanged(newConfig);
	}

}