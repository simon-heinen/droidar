package entry;

import preview.AugmentedView;
import setup.ArSetup;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import de.rwth.setups.StaticDemoSetup;

public class ArActivity extends Activity  implements ISetupEntry {
	
	private static final String LOG_TAG = "ArActivity";
	
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSetup = createSetup();	
		mOverlayView = createAugmentedView(this);
		mOverlayView.getRenderer().setUseLightning(mSetup._a2_initLightning(mOverlayView.getRenderer().getMyLights()));
		mSetup.onCreate();	
		setContentView(mOverlayView);
	}
	
	
	@Override
	protected void onRestart() {
		super.onRestart();
		mSetup.onResume();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSetup.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mSetup.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mSetup.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSetup.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSetup.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((mSetup != null)
				&& (mSetup.onKeyDown(this, keyCode, event)))
			return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (((mSetup != null) && mSetup.onCreateOptionsMenu(menu)))
			return true;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (mSetup != null)
			return mSetup.onMenuItemSelected(featureId, item);
		return super.onMenuItemSelected(featureId, item);
	}
	
	protected ArSetup createSetup(){
		return new StaticDemoSetup(this);
	}
	
	protected AugmentedView createAugmentedView(Activity activity){
		return new AugmentedView(activity);
	}
}
