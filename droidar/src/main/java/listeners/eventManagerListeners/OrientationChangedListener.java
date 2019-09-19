package listeners.eventManagerListeners;

import android.hardware.SensorEventListener;

public interface OrientationChangedListener {
	/**
	 * see
	 * {@link SensorEventListener#onSensorChanged(android.hardware.SensorEvent)}
	 * 
	 * @param values
	 * @return
	 */
    boolean onOrientationChanged(float[] values);

	/**
	 * see
	 * {@link SensorEventListener#onSensorChanged(android.hardware.SensorEvent)}
	 * 
	 * @param values
	 * @return
	 */
    boolean onMagnetChanged(float[] values);

	/**
	 * see
	 * {@link SensorEventListener#onSensorChanged(android.hardware.SensorEvent)}
	 * 
	 * @param values
	 * @return
	 */
    boolean onAccelChanged(float[] values);
}
