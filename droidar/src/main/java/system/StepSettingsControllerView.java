package system;

import v2.simpleUi.M_Checkbox;
import v2.simpleUi.M_Container;
import v2.simpleUi.M_Double;
import v2.simpleUi.M_Integer;
import android.content.Context;

public class StepSettingsControllerView extends M_Container {

	public StepSettingsControllerView(Context context) {

		this.add(new M_Checkbox() {

			@Override
			public boolean save(boolean newValue) {
				SimpleLocationManager.setStepDetectionEnabled(newValue);
				return true;
			}

			@Override
			public boolean loadVar() {
				return SimpleLocationManager.isStepDetectionEnabled();
			}

			@Override
			public CharSequence getVarName() {
				return "StepDetectionEnabled";
			}
		});

		this.add(new M_Double() {

			@Override
			public boolean save(double newValue) {
				SimpleLocationManager
						.setMinimumAverageAccuracy((float) newValue);
				return true;
			}

			@Override
			public double load() {
				return SimpleLocationManager.getMinimumAverageAccuracy();
			}

			@Override
			public String getVarName() {
				return "MinimumAverageAccuracy";
			}
		});
		this.add(new M_Integer() {

			@Override
			public boolean save(int newValue) {
				SimpleLocationManager
						.setNumberOfSimulatedStepsInSameDirection(newValue);
				return true;
			}

			@Override
			public int load() {
				return SimpleLocationManager
						.getNumberOfSimulatedStepsInSameDirection();
			}

			@Override
			public String getVarName() {
				return "NumberOfSimulatedStepsInSameDirection";
			}
		});
		final StepManager sm = SimpleLocationManager.getInstance(context)
				.getStepManager();
		if (sm != null) {

			this.add(new M_Double() {

				@Override
				public boolean save(double newValue) {
					sm.setMinStepPeakSize(newValue);
					return true;
				}

				@Override
				public double load() {
					return sm.getMinStepPeakSize();
				}

				@Override
				public String getVarName() {
					return "MinStepPeakSize";
				}
			});

			this.add(new M_Double() {

				@Override
				public boolean save(double newValue) {
					sm.setStepLengthInMeter(newValue);
					return true;
				}

				@Override
				public double load() {
					return sm.getStepLengthInMeter();
				}

				@Override
				public String getVarName() {
					return "StepLengthInMeter";
				}
			});
			this.add(new M_Integer() {

				@Override
				public boolean save(int newValue) {
					sm.setMinTimeBetweenSteps(newValue);
					return true;
				}

				@Override
				public int load() {
					return sm.getMinTimeBetweenSteps();
				}

				@Override
				public String getVarName() {
					return "MinTimeBetweenSteps";
				}
			});

		}
	}
}
