package entry;

import logger.ARLogger;
import preview.AugmentedView;
import setup.ArSetup;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import de.rwth.setups.StaticDemoSetup;

/**
 * Container that acts as the glue between the {@link setup.ArSetup} and the {@link preview.AugmentedView}.
 * Manages the life cycle and acts the entry point to the android application. 
 * To implement your own {@link setup.ArSetup} extend this class and override {@link entry.ArActivity#createSetup()}.
 * To implement your own {@link preview.AugmentedView} extend this class and 
 * override {@link entry.ArActivity#createAugmentedView(Activity)}.
 *
 */
public class ArActivity extends Activity  implements ISetupEntry {
	
	private static final String LOG_TAG = "ArActivity";
	private static boolean USE_STATIC_SETUP = false;
	private static ArSetup  STATIC_SETUP;
	
	private ArSetup mSetup;
	private AugmentedView mOverlayView;	
	private ArType mType = ArType.ACTIVITY;
	
	
	@Override
	public Activity getActivity() {
		return this;
	}
	
	@Override
	public ArType getType() {
		return mType;
	}
	
	@Override
	public AugmentedView getAugmentedView() {
		return mOverlayView;
	}
	
	private void determainSetupToUse() {
		if (USE_STATIC_SETUP) {
			mSetup = STATIC_SETUP;
		} else {
			mSetup = createSetup();
		}
	}
	
	private void setEntryIfNeeded() {
		if (mSetup.getActivity() == null) {
			mSetup.setEntry(this);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		determainSetupToUse();
		setEntryIfNeeded();
		mOverlayView = createAugmentedView(this);
		mOverlayView.getRenderer().setUseLightning(mSetup.initLightning(mOverlayView.getRenderer().getMyLights()));
		mSetup.onCreate();
		mSetup.onStart();
		mSetup.run(this);
		setContentView(mOverlayView);
	}
	
	@Override
	protected void onRestart() {
		ARLogger.debug(LOG_TAG, "onRestart");
		super.onRestart();
		mSetup.onResume();
	}

	@Override
	protected void onResume() {
		ARLogger.debug(LOG_TAG, "onResume");
		super.onResume();
		mSetup.onResume();
	}

	@Override
	protected void onStart() {
		ARLogger.debug(LOG_TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		ARLogger.debug(LOG_TAG, "onStop");
		super.onStop();
		mSetup.onPause();
	}

	@Override
	protected void onDestroy() {
		ARLogger.debug(LOG_TAG, "onDestory");
		super.onDestroy();
		mSetup.onStop();
	}

	@Override
	protected void onPause() {
		ARLogger.debug(LOG_TAG, "onPause");
		super.onPause();
		mSetup.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((mSetup != null)
				&& (mSetup.onKeyDown(this, keyCode, event))) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (((mSetup != null) && mSetup.onCreateOptionsMenu(menu))) {
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (mSetup != null) {
			return mSetup.onMenuItemSelected(featureId, item);
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	/**
	 * Simple way to kick off the ArActivity. 
	 * @param currentActivity - {@link android.app.Activity} base
	 * @param setupToUse - {@link setup.ArSetup}
	 */
	public static void startWithSetup(Activity currentActivity, ArSetup setupToUse) {
		USE_STATIC_SETUP = true;
		STATIC_SETUP = setupToUse;
		currentActivity.startActivity(new Intent(currentActivity,
				ArActivity.class));
	}

	protected ArSetup createSetup() {
		return new StaticDemoSetup();
	}
	
	protected AugmentedView createAugmentedView(Activity activity) {
		return new AugmentedView(activity);
	}
}
