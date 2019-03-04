package gestures;

import gestures.detectors.DummyDetector;
import gestures.detectors.FullTurnDetector;
import gestures.detectors.LoggingDetector;
import gestures.detectors.LookingDetector;
import gestures.detectors.SlashDetector;
import gestures.detectors.UppercutDetector;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * The PhoneGestureSensor is a sensor which sits on top of the raw hardware
 * sensors and detects special gestures that are made with the phone. Gestures
 * are detected by choosing the most probable one at every point in time. You
 * can extend the sensor's abilities by adding detector classes implementing the
 * {@link PhoneGestureDetector} interface. The sensor notifies all its listeners
 * whenever the most probable gesture changes to a new type.
 * 
 * Create new instances using the PhoneGestureSensor.Builder.
 * 
 * @author marmat (Martin Matysiak)
 */
public class PhoneGestureSensor implements SensorEventListener {

	/**
	 * A builder to create new instances of the PhoneGestureSensor.
	 * 
	 * @author marmat (Martin Matysiak)
	 */
	public static class Builder {
		private PhoneGestureSensor sensor;

		/**
		 * Creates a new builder instance.
		 * 
		 * @param context
		 *            The context in which to run the sensor.
		 */
		public Builder(Context context) {
			sensor = new PhoneGestureSensor(context);
		}

		/**
		 * Sets the minimal amount of time that will be waited between to sensor
		 * events.
		 * 
		 * @param timeout
		 *            The timeout in milliseconds.
		 * @return The builder instance for method chaining.
		 */
		public Builder setGestureTimeout(long timeout) {
			sensor.gestureTimeout = timeout;
			return this;
		}

		/**
		 * Adds a dummy detector such that the NONE gesture will be detected
		 * whenever there is no other gesture with a probability higher than the
		 * given threshold.
		 * 
		 * @param confidence
		 *            The minimal needed confidence for a gesture to be
		 *            detected.
		 * @return The builder instance for method chaining.
		 */
		public Builder withMinimumConfidence(float confidence) {
			sensor.addDetector(new DummyDetector(StandardPhoneGesture.NONE, confidence));
			return this;
		}

		/**
		 * Adds a detector for the SLASH gesture.
		 * 
		 * @return The builder instance for method chaining.
		 */
		public Builder withSlashDetection() {
			sensor.addDetector(new SlashDetector());
			return this;
		}

		/**
		 * Adds a detector for the LOOKING gesture.
		 * 
		 * @return The builder instance for method chaining.
		 */
		public Builder withLookingDetection() {
			sensor.addDetector(new LookingDetector());
			return this;
		}

		/**
		 * Adds a detector for the UPPERCUT gesture.
		 * 
		 * @return The builder instance for method chaining.
		 */
		public Builder withUppercutDetection() {
			sensor.addDetector(new UppercutDetector());
			return this;
		}

		/**
		 * Adds a detector for the FULL_TURN gesture.
		 * 
		 * @return The builder instance for method chaining.
		 */
		public Builder withFullTurnDetection() {
			sensor.addDetector(new FullTurnDetector());
			return this;
		}

		/**
		 * Will cause the measured values to be printed to the debug log
		 * whenever a sensor event occurs.
		 * 
		 * @return The builder instance for method chaining.
		 */
		public Builder withLogging() {
			sensor.addDetector(new LoggingDetector(LoggingDetector.LOG_ALL));
			return this;
		}

		/**
		 * Will cause the measured values to be printed to the debug log
		 * whenever a sensor event occurs.
		 * 
		 * @param logMask
		 *            A bitmask specifying the messages to log. See
		 *            {@link LoggingDetector}.
		 * @return The builder instance for method chaining.
		 */
		public Builder withLogging(int logMask) {
			sensor.addDetector(new LoggingDetector(logMask));
			return this;
		}

		/**
		 * Adds an arbitrary detector to the currently built sensor.
		 * 
		 * @param detector
		 *            The detector to add.
		 * @return The builder instance for method chaining.
		 */
		public Builder withDetector(PhoneGestureDetector detector) {
			sensor.addDetector(detector);
			return this;
		}

		public PhoneGestureSensor build() {
			sensor.resume();
			return sensor;
		}
	}

	/**
	 * A list of registered event listeners.
	 */
	private final List<PhoneGestureListener> phoneGestureListeners;

	/**
	 * A list of gesture detectors that will be used to choose the most probable
	 * gesture.
	 */
	private final List<PhoneGestureDetector> phoneGestureDetectors;

	/**
	 * The SensorManager to which this sensor is connected to.
	 */
	private final SensorManager sensorManager;

	/**
	 * Timestamp of the last propagated event in milliseconds.
	 */
	private long lastEvent;

	/**
	 * The last propagated gesture.
	 */
	private PhoneGesture lastGesture = StandardPhoneGesture.NONE;

	/**
	 * The gravity vector indicating the measured values of gravity along the
	 * phone's axes.
	 */
	private float[] gravity = { 0, 0, 0 };

	/**
	 * The weight for the high pass filter when filtering out gravity.
	 */
	private final float alpha = 0.8f;

	/**
	 * The minimal amount of time between to gestures in milliseconds.
	 */
	public long gestureTimeout = 500;

	/**
	 * Creates a new sensor instance that will be looking for phone gestures.
	 * 
	 * @param context
	 *            The application context in which to run.
	 */
	private PhoneGestureSensor(Context context) {
		phoneGestureListeners = new ArrayList<PhoneGestureListener>();
		phoneGestureDetectors = new ArrayList<PhoneGestureDetector>();
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
	}

	/**
	 * Registers the given listener with this sensor, or does nothing if the
	 * given listener is already registered.
	 * 
	 * @param listener
	 *            The listener to register.
	 */
	public void addListener(PhoneGestureListener listener) {
		if (!phoneGestureListeners.contains(listener)) {
			phoneGestureListeners.add(listener);
		}
	}

	/**
	 * Removes the given listener from events of this sensor, or does nothing if
	 * the listener wasn't previously registered.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(PhoneGestureListener listener) {
		phoneGestureListeners.remove(listener);
	}

	/**
	 * Adds the given detector to this sensor. The detector will then receive
	 * sensor events as long as this sensor is active.
	 * 
	 * @param detector
	 *            The detector to add.
	 */
	public void addDetector(PhoneGestureDetector detector) {
		if (!phoneGestureDetectors.contains(detector)) {
			phoneGestureDetectors.add(detector);
		}
	}

	/**
	 * Removes the given detector if it was previously registered to this
	 * sensor.
	 * 
	 * @param detector
	 *            The detector to remove.
	 */
	public void removeDetector(PhoneGestureDetector detector) {
		phoneGestureDetectors.remove(detector);
	}

	/**
	 * Removes effects of gravity or offset and returns a vector with pure
	 * linear acceleration.
	 * 
	 * @param event
	 *            The SensorEvent that occurred.
	 * @return The linear acceleration vector along the phone's axes.
	 */
	private float[] getLinearAcceleration(SensorEvent event) {
		float[] result = { 0, 0, 0 };

		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			// High pass filtering to remove gravity (see also goo.gl/dl85N)
			gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
			gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
			gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

			result[0] = event.values[0] - gravity[0];
			result[1] = event.values[1] - gravity[1];
			result[2] = event.values[2] - gravity[2];
			break;
		// TODO: Add support for Sensor.TYPE_LINEAR_ACCELERATION
		}

		return result;
	}

	/**
	 * Gets the current readings of the magnetic field sensor.
	 * 
	 * @param event
	 *            The SensorEvent that occurred.
	 * @return The readings of the magnetic sensor.
	 */
	private float[] getMagneticField(SensorEvent event) {
		float[] result = { 0, 0, 0 };

		switch (event.sensor.getType()) {
		case Sensor.TYPE_MAGNETIC_FIELD:
			result[0] = event.values[0];
			result[1] = event.values[1];
			result[2] = event.values[2];
		}

		return result;
	}

	/**
	 * Notifies all registered listeners of the event that occurred.
	 * 
	 * @param phoneGesture
	 *            The gesture that has been detected.
	 */
	private void propagateEvent(PhoneGesture phoneGesture) {
		long currentTime = System.currentTimeMillis();

		if ((lastGesture != StandardPhoneGesture.NONE && currentTime - lastEvent < gestureTimeout)
				|| lastGesture == phoneGesture) {
			return;
		}

		lastEvent = currentTime;
		lastGesture = phoneGesture;

		// We copy the current list of listeners into an array to achieve
		// thread safety since new listeners could be registered while
		// propagating the current event.
		PhoneGestureListener[] listenersToCall = new PhoneGestureListener[phoneGestureListeners
				.size()];
		listenersToCall = phoneGestureListeners.toArray(listenersToCall);
		for (PhoneGestureListener listener : listenersToCall) {
			listener.onPhoneGesture(phoneGesture);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO: check if we should handle this

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] linearAcceleration = getLinearAcceleration(event);
		float[] mag = getMagneticField(event);

		SensorData data = new SensorData(gravity, linearAcceleration, mag);
		PhoneGestureDetector mostProbableDetector = null;
		for (PhoneGestureDetector detector : phoneGestureDetectors) {
			detector.feedSensorEvent(data);
			if (mostProbableDetector == null
					|| detector.getProbability() > mostProbableDetector
							.getProbability()) {
				mostProbableDetector = detector;
			}
		}

		propagateEvent(mostProbableDetector != null ? mostProbableDetector
				.getType() : StandardPhoneGesture.NONE);
	}

	/**
	 * Pauses detection of gestures for all registered listeners. Use with
	 * caution.
	 */
	public void pause() {
		sensorManager.unregisterListener(this);
	}

	/**
	 * Resumes detection of gestures.
	 */
	public void resume() {
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME);

	}

	/**
	 * Stops listening to events once and for all. Use with caution! Should be
	 * only called when it's completely sure that the user won't return to the
	 * activity.
	 */
	public void stop() {
		sensorManager.unregisterListener(this);
		phoneGestureListeners.clear();
	}
}