package listeners.eventManagerListeners;

/**
 * Listener for sensors. 
 */
public interface OrientationChangedListener {
	/**
	 * see
	 * {@link android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)}.
	 * 
	 * @param values - values returned from sensor.
	 * @return - process successful
	 */
	boolean onOrientationChanged(float[] values);

	/**
	 * see
	 * {@link android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)}.
	 * 
	 * @param values - values returned from sensor
	 * @return - process successful
	 */
	boolean onMagnetChanged(float[] values);

	/**
	 * see
	 * {@link android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)}.
	 * 
	 * @param values - values returned from sensor
	 * @return - process successful
	 */
	boolean onAccelChanged(float[] values);
}
