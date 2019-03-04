package de.rwth;

import system.ArActivity;
import system.ErrorHandler;
import system.EventManager;
import system.Setup;
import tests.AndroidDeviceOnlyTests;
import tests.EfficientListTests;
import tests.GameLogicTests;
import tests.GeoTests;
import tests.GlTests;
import tests.IOTests;
import tests.SystemTests;
import tests.WorldTests;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import commands.ui.CommandShowToast;

import de.rwth.setups.CollectItemsSetup;
import de.rwth.setups.DebugSetup;
import de.rwth.setups.FarAwayPOIScenarioSetup;
import de.rwth.setups.FastChangingTextSetup;
import de.rwth.setups.GameDemoSetup;
import de.rwth.setups.GeoPosTestSetup;
import de.rwth.setups.GraphCreationSetup;
import de.rwth.setups.GraphMovementTestSetup;
import de.rwth.setups.LargeWorldsSetup;
import de.rwth.setups.LightningSetup;
import de.rwth.setups.PlaceObjectsSetup;
import de.rwth.setups.PlaceObjectsSetupTwo;
import de.rwth.setups.PositionTestsSetup;
import de.rwth.setups.SensorTestSetup;
import de.rwth.setups.StaticDemoSetup;

public class TechDemoLauncher extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.demoselector);

	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("DemoScreen onResume");
		LinearLayout l = ((LinearLayout) findViewById(R.id.demoScreenLinView));
		l.removeAllViews();

		showSetup("GeoPosTestSetup", new GeoPosTestSetup());
		showSetup("Demo Setup", new StaticDemoSetup());
		showSetup("Animation Demo", new DebugSetup());
		showSetup("Game Demo", new GameDemoSetup());
		showSetup("'Too far away' scenario", new FarAwayPOIScenarioSetup());
		showSetup("Large worlds", new LargeWorldsSetup());
		showSetup("Changing text Demo", new FastChangingTextSetup());
		showSetup("Lightning Demo", new LightningSetup());
		showSetup("Collecting Items Demo", new CollectItemsSetup());
		showSetup("Placing objects Demo", new PlaceObjectsSetup());
		showSetup("Placing objects Demo 2", new PlaceObjectsSetupTwo());
		showSetup("Graph Movement Test", new GraphMovementTestSetup());
		showSetup("Graph creation Test", new GraphCreationSetup());
		showSetup("Sensor Processing Demo", new SensorTestSetup());
		showSetup("Position tests", new PositionTestsSetup());

		l.addView(new SimpleButton(
				"deviceHasLargeScreenAndOrientationFlipped = "
						+ EventManager
								.deviceHasLargeScreenAndOrientationFlipped(this)) {
			@Override
			public void onButtonPressed() {

			}
		});

		l.addView(new SimpleButton("Run tests") {
			@Override
			public void onButtonPressed() {
				runTests();
			}
		});

		l.addView(new SimpleButton("Load test UI") {
			@Override
			public void onButtonPressed() {
				setContentView(R.layout.test_layout);
			}
		});

	}

	private void showSetup(String string, final Setup aSetupInstance) {
		((LinearLayout) findViewById(R.id.demoScreenLinView))
				.addView(new SimpleButton(string) {
					@Override
					public void onButtonPressed() {
						Activity theCurrentActivity = TechDemoLauncher.this;
						ArActivity.startWithSetup(theCurrentActivity,
								aSetupInstance);
					}
				});
	}

	private abstract class SimpleButton extends Button {
		public SimpleButton(String text) {
			super(TechDemoLauncher.this);
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

	private void runTests() {
		// execute all tests defined in the ARTestSuite:
		try {

			system.EventManager.getInstance().registerListeners(this, true);

			// new ThreadTest().run();
			// new MemoryAllocationTests().run();
			// new NetworkTests().run();

			new SystemTests().run();
			new EfficientListTests().run();
			new GeoTests().run();
			new IOTests(this).run();
			new WorldTests().run();
			new AndroidDeviceOnlyTests(this).run();
			new GameLogicTests().run();
			new GlTests().run();

			new CommandShowToast(this, "All tests succeded on this device :)")
					.execute();
		} catch (Exception e) {
			e.printStackTrace();
			ErrorHandler.showErrorLog(this, e, true);
		}
	}

}
