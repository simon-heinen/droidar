package gestures.detectors;

import gestures.PhoneGesture;
import gestures.PhoneGestureDetector;
import gestures.SensorData;
import gestures.StandardPhoneGesture;

/**
 * A detector which detects if the user is holding the phone stable for a
 * certain amount of time.
 * 
 * @author marmat (Martin Matysiak)
 * 
 */
public class LookingDetector implements PhoneGestureDetector {

	/**
	 * The rate with which the gesture probability will increase if the phone is
	 * being held stable.
	 */
	private static final double PROBABILITY_INCREASE = 0.01;

	/**
	 * The rate with which the probability will decrease after the phone has
	 * stopped being in a stable position;
	 */
	private static final double PROBABILITY_DECAY = 0.8;

	/**
	 * The maximum absolute acceleration in m/s^2 that will still be considered
	 * as "stable".
	 */
	private static final double STABILITY_THRESHOLD = 3;

	private double gestureProbability;

	@Override
	public PhoneGesture getType() {
		return StandardPhoneGesture.LOOKING;
	}

	@Override
	public double getProbability() {
		return gestureProbability;
	}

	@Override
	public void feedSensorEvent(SensorData sensorData) {
		if (sensorData.absoluteAcceleration <= STABILITY_THRESHOLD) {
			/*
			 * A few notes to the following equation:
			 * 
			 * The term (STABILITY_THRESHOLD - absoluteAcceleration) /
			 * STABILITY_THRESHOLD makes sure that the closer the absolute
			 * acceleration is to the threshold, the smaller the influence on a
			 * probability increase will be.
			 * 
			 * This is multiplied by PROBABILITY_INCREASE which is the global
			 * parameter that can be used to influence the rate of increase.
			 * 
			 * The whole thing is then multiplied by (1 - gestureProbability) to
			 * make sure that we will never have a larger total probability than
			 * 1.0 (the rate will approach 1 but never quite reach it).
			 */

			gestureProbability += (STABILITY_THRESHOLD - sensorData.absoluteAcceleration)
					/ STABILITY_THRESHOLD
					* PROBABILITY_INCREASE
					* (1 - gestureProbability);
		} else {
			gestureProbability *= (1 - PROBABILITY_DECAY);
		}
	}

}
