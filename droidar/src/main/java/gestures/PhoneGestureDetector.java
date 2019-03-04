package gestures;

/**
 * A class that is able to detect a specific gesture type by calculating its
 * probability based on data gathered from the phone's sensor readings. When
 * implementing a new Detector, make sure to add the appropriate type to the
 * {@link PhoneGesture} enum.
 * 
 * Gestures can of course be parameterized using their specific constructors.
 * 
 * @author marmat (Martin Matysiak)
 */
public interface PhoneGestureDetector {

	/**
	 * Returns the type detected by the implementing detector. Return value
	 * should not change during the detector's lifetime.
	 * 
	 * @return The gesture type detected by this class.
	 */
	public PhoneGesture getType();

	/**
	 * Returns the probability with which the gesture represented by the
	 * implementing class is currently occurring.
	 * 
	 * @return A value between [0, 1] indicating the probability with which the
	 *         detected gesture is currently happening.
	 */
	public double getProbability();

	/**
	 * Will be called from the Sensor whenever new accelerometer readings are
	 * incoming.
	 * 
	 * TODO: Additional to linear acceleration, pass a global movement history
	 * (the reconstructed 3d path that Simon mentioned), so that the
	 * implementing classes have to do that on their own.
	 * 
	 * @param sensorData
	 *            Data collected by the PhoneGestureSensor.
	 */
	public void feedSensorEvent(SensorData sensorData);
}
