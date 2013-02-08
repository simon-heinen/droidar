package gestures;

/**
 * A wrapper class for all data that will be collected by the PhoneGestureSensor
 * and passed on to the detectors. Calculates a few properties out of the given
 * sensor data that is commonly used across detectors.
 * 
 * @author marmat (Martin Matysiak)
 */
public class SensorData {
	/**
	 * Time of the sensor event in nanoseconds.
	 */
	public final long timestamp;

	/**
	 * Gravity along the three phone axes in m/s^2.
	 */
	public final float[] gravity;

	/**
	 * Linear acceleration along the three phone axes (i.e. excluding influence
	 * of gravity) in m/s^2.
	 */
	public final float[] linearAcceleration;

	/**
	 * Total acceleration (euclidean norm) in m/s^2.
	 */
	public final double absoluteAcceleration;

	/**
	 * The readings of the gravity sensor.
	 */
	public final float[] mag;

	public SensorData(float[] gravity, float[] linearAcceleration, float[] mag) {
		this.timestamp = System.nanoTime();
		this.gravity = gravity;
		this.linearAcceleration = linearAcceleration;
		this.absoluteAcceleration = Math.sqrt(Math
				.pow(linearAcceleration[0], 2)
				+ Math.pow(linearAcceleration[1], 2)
				+ Math.pow(linearAcceleration[2], 2));
		this.mag = mag;
	}
}
