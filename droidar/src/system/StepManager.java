package system;

import java.util.ArrayList;
import java.util.List;

import actions.ActionUseCameraAngles2;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.util.FloatMath;

/**
 * @author Paul Smith code@uvwxy.de
 * 
 */
public class StepManager implements SensorEventListener {

	private SensorManager sensorManager;

	private List<OnStepListener> listeners;

	private int minTimeBetweenSteps = 866;
	private double minStepPeakSize = 0.9;
	private double stepLengthInMeter = 0.9;

	private Handler handler = new Handler();
	private long handler_delay_millis = 1000 / 30;
	boolean handler_is_running = false;

	private float[] last_acc_events = { 0f, 0f, 0f };
	private long last_step_ms;
	private float orientation = 0.0f;
	private static final int vhSize = 6;

	private static final String LOG_TAG = "StepManager";
	private float[][] stepDetecWindow = new float[vhSize][];
	private int vhPointer = 0;

	public interface OnStepListener {

		public void onStep(double compassAngle, double steplength);
	}

	private void registerSensors(Context context) {
		// register acceleraion sensor

		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);

		Sensor magnetSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorManager.registerListener(this, magnetSensor,
				SensorManager.SENSOR_DELAY_GAME);
		Sensor accelSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelSensor,
				SensorManager.SENSOR_DELAY_GAME);

	}

	private void start() {
		handler_is_running = true;
		handler.removeCallbacks(handlerStepDetection);
		handler.postDelayed(handlerStepDetection, handler_delay_millis);

	}

	private void stop() {
		handler.removeCallbacks(handlerStepDetection);
		handler_is_running = false;
	}

	private void unregisterSensors() {
		sensorManager.unregisterListener(this);
	}

	public void registerStepListener(Context context, OnStepListener l) {
		if (listeners == null) {
			listeners = new ArrayList<OnStepListener>();
			registerSensors(context);
			start();
		}
		listeners.add(l);
	}

	public void unRegisterStepListener(OnStepListener l) {
		listeners.remove(l);
		if (listeners.isEmpty()) {
			stop();
			unregisterSensors();
			listeners = null;
		}
	}

	private void addCurrentSensorData() {
		stepDetecWindow[vhPointer % vhSize] = last_acc_events;
		vhPointer++;
		vhPointer = vhPointer % vhSize;
	}

	private boolean checkIfStepHappend() {
		// Add value to values_history
		int lookahead = 5;
		for (int t = 1; t <= lookahead; t++) {
			int x0 = (vhPointer - 1 - t + vhSize + vhSize) % vhSize;
			int x1 = (vhPointer - 1 + vhSize) % vhSize;
			if (stepDetecWindow[(vhPointer - 1 - t + vhSize + vhSize) % vhSize] != null) {
				double check = FloatMath
						.sqrt((stepDetecWindow[x0][0] - stepDetecWindow[x1][0])
								* (stepDetecWindow[x0][0] - stepDetecWindow[x1][0])
								+ (stepDetecWindow[x0][1] - stepDetecWindow[x1][1])
								* (stepDetecWindow[x0][1] - stepDetecWindow[x1][1])
								+ (stepDetecWindow[x0][2] - stepDetecWindow[x1][2])
								* (stepDetecWindow[x0][2] - stepDetecWindow[x1][2]));
				if (check >= minStepPeakSize) {
					// Log.i(LOG_TAG, "Detected step with t = " + t +
					// ", peakSize = " + minStepPeakSize + " < " + check);
					return true;
				}
			}

		}
		return false;
	}

	// Handler code

	/**
	 * Takes a location and updates its position according to the step
	 * 
	 * @param l
	 * @param d
	 * @param bearing
	 * @return
	 */
	public static Location newLocationOneStepFurther(Location l, double d,
			double bearing) {
		bearing = Math.toRadians(bearing);
		double R = 6378100; // m equatorial radius
		double lat1 = Math.toRadians(l.getLatitude());
		double lon1 = Math.toRadians(l.getLongitude());
		double dr = d / R;
		double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dr) + Math.cos(lat1)
				* Math.sin(dr) * Math.cos(bearing));
		double lon2 = lon1
				+ Math.atan2(
						Math.sin(bearing) * Math.sin(d / R) * Math.cos(lat1),
						Math.cos(d / R) - Math.sin(lat1) * Math.sin(lat2));

		Location ret = new Location("LOCMOV");
		ret.setLatitude(Math.toDegrees(lat2));
		ret.setLongitude(Math.toDegrees(lon2));
		ret.setAccuracy((float) (2.0f * d));
		ret.setAltitude(0);
		ret.setTime(System.currentTimeMillis());
		return ret;
	}

	private Runnable handlerStepDetection = new Runnable() {

		public void run() {
			// if start is called twice: we have "two" threads, i.e clean this
			handler.removeCallbacks(handlerStepDetection);
			addCurrentSensorData();
			long t = System.currentTimeMillis();
			if (t - last_step_ms > minTimeBetweenSteps && checkIfStepHappend()) {
				for (int i = 0; i < listeners.size(); i++) {
					listeners.get(i).onStep(orientation, stepLengthInMeter);
				}
				last_step_ms = t;
			}

			// no movements if no steps/jumps detected at all, or not in the
			// standing_timeout_ms intervall

			if (handler_is_running)
				handler.postDelayed(this, handler_delay_millis);
		}
	};

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	final float alpha = 0.8f;
	float[] bufferedAccel = { .0f, .0f, .0f };

	final float magnetoalpha = 0.8f;
	float[] bufferedMagneto = { .0f, .0f, .0f };

	ActionUseCameraAngles2 compassAzimuthCalcer = new ActionUseCameraAngles2() {

		@Override
		public void onAnglesUpdated(float pitch, float roll,
				float compassAzimuth) {
			orientation = compassAzimuth;
		}
	};

	@Override
	public void onSensorChanged(SensorEvent event) {

		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			bufferedAccel[0] = alpha * bufferedAccel[0] + (1 - alpha)
					* event.values[0];
			bufferedAccel[1] = alpha * bufferedAccel[1] + (1 - alpha)
					* event.values[1];
			bufferedAccel[2] = alpha * bufferedAccel[2] + (1 - alpha)
					* event.values[2];

			float x = event.values[0] - bufferedAccel[0];
			float y = event.values[1] - bufferedAccel[1];
			float z = event.values[2] - bufferedAccel[2];
			float[] b = { x, y, z };

			last_acc_events = b;
			compassAzimuthCalcer.onAccelChanged(bufferedAccel);
			break;

		case Sensor.TYPE_MAGNETIC_FIELD:
			bufferedMagneto[0] = magnetoalpha * bufferedMagneto[0]
					+ (1 - magnetoalpha) * event.values[0];
			bufferedMagneto[1] = magnetoalpha * bufferedMagneto[1]
					+ (1 - magnetoalpha) * event.values[1];
			bufferedMagneto[2] = magnetoalpha * bufferedMagneto[2]
					+ (1 - magnetoalpha) * event.values[2];
			compassAzimuthCalcer.onMagnetChanged(bufferedMagneto);
			break;
		}
	}

	public void setMinTimeBetweenSteps(int minTimeBetweenSteps) {
		this.minTimeBetweenSteps = minTimeBetweenSteps;
	}

	public void setMinStepPeakSize(double minStepPeakSize) {
		this.minStepPeakSize = minStepPeakSize;
	}

	public void setStepLengthInMeter(double stepLengthInMeter) {
		this.stepLengthInMeter = stepLengthInMeter;
	}

	public int getMinTimeBetweenSteps() {
		return minTimeBetweenSteps;
	}

	public double getMinStepPeakSize() {
		return minStepPeakSize;
	}

	public double getStepLengthInMeter() {
		return stepLengthInMeter;
	}

}