package gestures.detectors;

import gestures.PhoneGesture;
import gestures.PhoneGestureDetector;
import gestures.SensorData;
import gestures.StandardPhoneGesture;

/**
 * A detector to detect "Slashing" movements by simple peak detection.
 * 
 * @author marmat (Martin Matysiak)
 * 
 */
public class SlashDetector implements PhoneGestureDetector {

	/**
	 * Parameter which influences how fast we sudden movements will influence
	 * the gesture probability.
	 */
	private static final double ALPHA = 0.5;

	/**
	 * The acceleration in m/s^2 for a move being detected as a slash.
	 */
	private static final double SLASH_THRESHOLD = 10;

	/**
	 * The current gesture probability.
	 */
	private double gestureProbability = 0;

	@Override
	public PhoneGesture getType() {
		return StandardPhoneGesture.SLASH;
	}

	@Override
	public double getProbability() {
		return gestureProbability;
	}

	@Override
	public void feedSensorEvent(SensorData sensorData) {
		gestureProbability = (1 - ALPHA) * gestureProbability + ALPHA
				* (sensorData.absoluteAcceleration > SLASH_THRESHOLD ? 1 : 0);
	}
}
