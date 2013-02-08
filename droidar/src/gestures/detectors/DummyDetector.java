package gestures.detectors;

import gestures.PhoneGesture;
import gestures.PhoneGestureDetector;
import gestures.SensorData;

/**
 * A dummy detector that will always return the given constant probability for
 * the given gesture type. This detector can be used to create thresholds
 * without needing to do any special case implementation.
 * 
 * @author marmat (Martin Matysiak)
 * 
 */
public class DummyDetector implements PhoneGestureDetector {

	private final PhoneGesture gestureType;
	private final double gestureProbability;

	public DummyDetector(PhoneGesture gestureType, double gestureProbability) {
		this.gestureType = gestureType;
		this.gestureProbability = gestureProbability;
	}

	@Override
	public PhoneGesture getType() {
		return gestureType;
	}

	@Override
	public double getProbability() {
		return gestureProbability;
	}

	@Override
	public void feedSensorEvent(SensorData sensorData) {
		// do nothing
	}
}
