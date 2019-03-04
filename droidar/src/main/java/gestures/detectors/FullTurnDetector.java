package gestures.detectors;

import java.util.Arrays;

import android.hardware.SensorManager;
import gestures.PhoneGesture;
import gestures.PhoneGestureDetector;
import gestures.SensorData;
import gestures.StandardPhoneGesture;

/**
 * The FullTurnDetector can be used to determine whether the player has
 * performed a turn of roughly 360 degrees while holding the phone. Detection of
 * this gesture is based on sampling of the compass coordinates. The gesture
 * allows a certain slowness of the movement but resets itself if there has not
 * been a large change in the compass direction for more than 1.5s.
 * 
 * @author Rene Glebke <rene.glebke@rwth-aachen.de>
 * @version 1.0pre - 2013-01-13
 * 
 */
public class FullTurnDetector implements PhoneGestureDetector {

	/**
	 * Controls how sensitive the detector shall be.
	 */
	public static final float TIME_BEFORE_RESET_IN_MS = 1500;
	public static final int NUMBER_OF_AZIMUTH_BINS = 42; // For detection
	public static final int NUMBER_OF_COARSE_BINS = 8; // For resetting

	private static final float ORIENTATION_DIVIDER = 360f / NUMBER_OF_AZIMUTH_BINS;
	private static final float ORIENTATION_DIVIDER_COARSE = 360f / NUMBER_OF_COARSE_BINS;

	/**
	 * Preallocated members for the matrices used by the SensorManager.
	 */
	private float rotationMatrix[] = new float[9];
	private float orientationMatrix[] = new float[3];

	/**
	 * The field of seen "binned" readings and of seen different readings.
	 */
	private boolean[] seenReadings = new boolean[NUMBER_OF_AZIMUTH_BINS];
	private int seenDifferentReadings = 0;

	/**
	 * Orientation changes.
	 */
	private int lastSeenCoarseOrientation = 0;
	private long timeOfLastSeenCoarseOrientationChange = System
			.currentTimeMillis();

	@Override
	public PhoneGesture getType() {
		return StandardPhoneGesture.FULL_TURN;
	}

	@Override
	public double getProbability() {
		/*
		 * The probability of this event is the number of seen different
		 * readings.
		 */
		// Log.d("Full Turn Probability",
		// Double.toString((double)this.seenDifferentReadings /
		// NUMBER_OF_AZIMUTH_BINS));
		return (double) this.seenDifferentReadings / NUMBER_OF_AZIMUTH_BINS;
	}

	public void reset() {
		Arrays.fill(this.seenReadings, false);
		this.seenDifferentReadings = 0;
		this.lastSeenCoarseOrientation = 0;
		this.timeOfLastSeenCoarseOrientationChange = System.currentTimeMillis();
	}

	@Override
	public void feedSensorEvent(SensorData sensorData) {
		// Step 1: Get the orientation according to the new (API >= 8) method
		// (see Sensor class documentation)
		SensorManager.getRotationMatrix(rotationMatrix, null,
				sensorData.gravity, sensorData.mag);
		SensorManager.getOrientation(rotationMatrix, orientationMatrix);

		/*
		 * Step 2: The current azimuth is given by the 1st entry in the
		 * orientation matrix; we normalize it to the value range 0..360.
		 */
		float orientation = (float) Math.toDegrees(orientationMatrix[0]);
		if (orientation < 0) {
			orientation += 360;
		}

		/*
		 * Step 3: Check if the player is facing the same direction for a longer
		 * period of time. In this case, reset the instance (there is no motion
		 * going on at the moment).
		 */
		int orientationIntCoarse = (int) (orientation / ORIENTATION_DIVIDER_COARSE);
		if ((Math.abs(this.lastSeenCoarseOrientation - orientationIntCoarse) % NUMBER_OF_COARSE_BINS) <= 1) {
			if (System.currentTimeMillis()
					- this.timeOfLastSeenCoarseOrientationChange >= FullTurnDetector.TIME_BEFORE_RESET_IN_MS) {
				this.reset();
			}
		} else {
			this.lastSeenCoarseOrientation = orientationIntCoarse;
			this.timeOfLastSeenCoarseOrientationChange = System
					.currentTimeMillis();
		}

		// Step 4: Set the bin of the seen reading to true
		int orientationInt = (int) (orientation / ORIENTATION_DIVIDER);
		if (!this.seenReadings[orientationInt]) {
			++this.seenDifferentReadings;
			this.seenReadings[orientationInt] = true;
		}
	}
}
