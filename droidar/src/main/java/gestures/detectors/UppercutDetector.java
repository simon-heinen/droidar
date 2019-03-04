package gestures.detectors;

import gestures.PhoneGesture;
import gestures.PhoneGestureDetector;
import gestures.SensorData;
import gestures.StandardPhoneGesture;
import util.Vec;

public class UppercutDetector implements PhoneGestureDetector {

	private static final float VELOCITY_DECAY = 0.98f;
	private static final float MINIMUM_VELOCITY = 0.2f;
	private static final float PROBABILITY_DECAY = 0.8f;

	private Vec velocity = new Vec();
	private long lastTimestamp = 0;
	private double probability = 0;
	private double angle = 0;

	@Override
	public PhoneGesture getType() {
		return StandardPhoneGesture.UPPERCUT;
	}

	@Override
	public double getProbability() {
		return Math.min(1, probability);
	}

	@Override
	public void feedSensorEvent(SensorData sensorData) {
		// We need at least two events to calculate velocity, the very first
		// event is only used to obtain an initial timestamp.
		if (lastTimestamp == 0) {
			lastTimestamp = sensorData.timestamp;
			return;
		}

		Vec linearAcceleration = new Vec(
				(float) sensorData.linearAcceleration[0],
				(float) sensorData.linearAcceleration[1],
				(float) sensorData.linearAcceleration[2]);

		Vec gravity = new Vec((float) sensorData.gravity[0],
				(float) sensorData.gravity[1], (float) sensorData.gravity[2]);

		// Integrate to obtain velocity vector. We divide by 1,000,000,000 in
		// order to obtain the velocity in m/s instead of m/ns.
		velocity.add(Vec.mult(
				(sensorData.timestamp - lastTimestamp) / 1000000000f,
				linearAcceleration));

		// Let the velocity decay minimally to avoid problems of
		// sensor drifting.
		velocity.mult(VELOCITY_DECAY);

		// Calculate angle between velocity and gravity vector to determine
		// gesture probability.
		double scalarProduct = gravity.x * velocity.x + gravity.y * velocity.y
				+ gravity.z * velocity.z;
		double currentAngle = Math.acos(scalarProduct
				/ (gravity.getLength() * velocity.getLength()));

		this.angle = 0.2 * this.angle + 0.8 * currentAngle;

		if (velocity.getLength() > MINIMUM_VELOCITY) {
			// Push the value through a sigmoid function (which might overshoot
			// a little bit for perfect vertical movements (which is rarely the
			// case, though). Nevertheless, that's why a min function is used in
			// getProbability().
			probability = 1.5 / (1 + Math.exp(-10
					* (this.angle / Math.PI - 0.7)));
		} else {
			probability *= PROBABILITY_DECAY;
		}

		lastTimestamp = sensorData.timestamp;
	}
}
