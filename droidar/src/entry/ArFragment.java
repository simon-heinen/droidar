package entry;

import preview.AugmentedView;
import setup.ArSetup;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.rwth.setups.StaticDemoSetup;

/**
 * Container that acts as the glue between the {@link setup.ArSetup} and the {@link preview.AugmentedView}.
 * Manages the life cycle and acts the entry point to the android application.  
 * To implement your own {@link setup.ArSetup} extend this class and override {@link entry.ArFragment#createSetup()}.
 * To implement your own {@link preview.AugmentedView} extend this class and 
 * override {@link entry.ArFragment#createAugmentedView(Activity)}.
 */
public class ArFragment extends Fragment implements ISetupEntry {	
	private ArSetup mSetup;
	private AugmentedView mOverlayView;	
	private ArType mType = ArType.FRAGMENT;
	
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
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return mOverlayView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mSetup = createSetup();
		if (mSetup.getActivity() == null) {
			mSetup.setEntry(this);
		}
		mOverlayView = createAugmentedView(getActivity());
		mOverlayView.getRenderer().setUseLightning(mSetup.initLightning(mOverlayView.getRenderer().getMyLights()));
		mSetup.onCreate();
		mSetup.onStart();
		mSetup.run(this);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		super.onStart();	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mSetup.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mSetup.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		mSetup.onStop();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	protected ArSetup createSetup() {
		return new StaticDemoSetup(this);
	}
	
	protected AugmentedView createAugmentedView(Activity activity) {
		return new AugmentedView(activity);
	}
}
