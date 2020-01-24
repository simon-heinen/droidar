package gestures.detectors;

import android.util.Log;
import gestures.PhoneGesture;
import gestures.PhoneGestureDetector;
import gestures.SensorData;
import gestures.StandardPhoneGesture;

/**
 * A dummy detector that's simply printing out the measured sensor data to the
 * debug log.
 * 
 * @author marmat (Martin Matysiak)
 * 
 */
public class LoggingDetector implements PhoneGestureDetector {

	public static final int LOG_LINEAR_ACCELERATION = 1;
	public static final int LOG_ABSOLUTE_ACCELERATION = 2;
	public static final int LOG_GRAVITY = 4;
	public static final int LOG_ALL = 0xFF;

	private final int logMask;

	/**
	 * Creates a detector that will log sensor data whenever it occurs.
	 * 
	 * @param logMask
	 *            A bitmask specifying which messages to log. Create it by
	 *            combining LoggingDetector.LOG_* constants using OR.
	 */
	public LoggingDetector(int logMask) {
		this.logMask = logMask;
	}

	@Override
	public PhoneGesture getType() {
		return StandardPhoneGesture.NONE;
	}

	@Override
	public double getProbability() {
		return 0;
	}

	@Override
	public void feedSensorEvent(SensorData sensorData) {
		StringBuilder builder = new StringBuilder();

		if ((logMask & LOG_ABSOLUTE_ACCELERATION) != 0) {
			builder.append("Abs: ").append(sensorData.absoluteAcceleration)
					.append("\n");
		}

		if ((logMask & LOG_GRAVITY) != 0) {
			builder.append("Gra: ").append(formatArray(sensorData.gravity))
					.append("\n");
		}

		if ((logMask & LOG_LINEAR_ACCELERATION) != 0) {
			builder.append("Lin: ")
					.append(formatArray(sensorData.linearAcceleration))
					.append("\n");
		}

		Log.d("LoggingDetector", builder.toString());
	}

	private String formatArray(float[] array) {
		StringBuilder builder = new StringBuilder("[ ");
		for (double value : array) {
			builder.append(String.format("% 8.4f ", value));
		}
		return builder.append("]").toString();
	}
}
