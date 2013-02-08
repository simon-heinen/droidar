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
	public abstract boolean onOrientationChanged(float[] values);

	/**
	 * see
	 * {@link SensorEventListener#onSensorChanged(android.hardware.SensorEvent)}
	 * 
	 * @param values
	 * @return
	 */
	public boolean onMagnetChanged(float[] values);

	/**
	 * see
	 * {@link SensorEventListener#onSensorChanged(android.hardware.SensorEvent)}
	 * 
	 * @param values
	 * @return
	 */
	public abstract boolean onAccelChanged(float[] values);
}
