package actions.algos;

/**
 * 
 * accel-sensor: has normal values from -11 to +11 and shake values from -19 to
 * 19
 * 
 * magnet-sensor: has normal values from -60 to +60 and metal/magnet values from
 * -120 to 120
 * 
 * This values were measured on a G1, so they might differ on other devices
 * 
 * @author Spobo
 * 
 */
public class SensorAlgo1 extends Algo {

	private float[] oldV = new float[3];
	private float myBarrier;

	public SensorAlgo1(float barrier) {
		myBarrier = barrier;
	}

	@Override
	public float[] execute(float[] v) {
		oldV[0] = checkAndCalc(oldV[0], v[0]);
		oldV[1] = checkAndCalc(oldV[1], v[1]);
		oldV[2] = checkAndCalc(oldV[2], v[2]);
		return oldV;
	}

	private float checkAndCalc(float oldV, float newV) {
		float delta = oldV - newV;
		/*
		 * if the new value is very different from the old one, morph to the new
		 * one
		 */
		if (delta < -myBarrier || myBarrier < delta) {
			return (oldV + newV) / 2;
		}
		/*
		 * else reuse the old one
		 */
		return oldV;
	}

}
