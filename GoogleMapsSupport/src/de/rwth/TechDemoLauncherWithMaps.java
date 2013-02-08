package de.rwth;

import system.Setup;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import de.rwth.setups.ARNavigatorSetup;
import de.rwth.setups.AccuracyTestsSetup;

public class TechDemoLauncherWithMaps extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.demoselector);
		

	}

	@Override
	protected void onResume() {
		super.onResume();
		((LinearLayout) findViewById(R.id.demoScreenLinView)).removeAllViews();
		showSetup("Accuracy Tests Setup", new AccuracyTestsSetup());
		showSetup("AR Navigator", new ARNavigatorSetup());
		
	}
	
	private void showSetup(String string, final Setup aSetupInstance) {
		((LinearLayout) findViewById(R.id.demoScreenLinView))
				.addView(new SimpleButton(string) {
					@Override
					public void onButtonPressed() {
						Activity theCurrentActivity = TechDemoLauncherWithMaps.this;
						ARActivityPlusMaps.startWithSetup(theCurrentActivity,
								aSetupInstance);
					}
				});
	}

	private abstract class SimpleButton extends Button {
		public SimpleButton(String text) {
			super(TechDemoLauncherWithMaps.this);
			setText(text);
			setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onButtonPressed();
				}
			});
		}

		public abstract void onButtonPressed();
	}

}
