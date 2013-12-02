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

public class ArFragment extends Fragment implements ISetupEntry {
	
	
	private ArSetup mSetup;
	private AugmentedView mOverlayView;	
	private ArType mType = ArType.FRAGMENT;
	
	public ArFragment(){
		//mSetup = new StaticDemoSetup(this);
//		mSetup = new StaticDemoSetup(this);
//		mSetup.onCreate();
	}
	
	@Override
	public ArType getType(){
		return mType;
	}

	@Override
	public AugmentedView getAugmentedView() {
		return mOverlayView;
	}	
	
	////////////////////////////////////////////////
	// Android Life Cycle For Fragments
	// Notes: onCreateView is called every time
	//        the fragment is made visible
	//        onDestoryView is called every time
	//        the fragment is put into the backstack
	/////////////////////////////////////////////////
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
		mSetup = new StaticDemoSetup(this);
		mOverlayView = new AugmentedView(getActivity());
		mOverlayView.getRenderer().setUseLightning(mSetup._a2_initLightning(mOverlayView.getRenderer().getMyLights()));
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

	//////////////////////////////////////////////
	// Android Overrides
	//////////////////////////////////////////////
	

}
