package actions.algos;

/**
 * 
 * accel-sensor: has normal values from -11 to +11 and shake values from -19 to
 * 19.
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
	
	private static final int ARRAY_SIZE = 3;
	private float[] mOldV = new float[ARRAY_SIZE];
	private float mBarrier;

	/**
	 * @param barrier - barrier value.
	 */
	public SensorAlgo1(float barrier) {
		mBarrier = barrier;
	}

	@Override
	public float[] execute(float[] v) {
		mOldV[0] = checkAndCalc(mOldV[0], v[0]);
		mOldV[1] = checkAndCalc(mOldV[1], v[1]);
		mOldV[2] = checkAndCalc(mOldV[2], v[2]);
		return mOldV;
	}

	private float checkAndCalc(float oldV, float newV) {
		float delta = oldV - newV;
		/*
		 * if the new value is very different from the old one, morph to the new
		 * one
		 */
		if (delta < -mBarrier || mBarrier < delta) {
			return (oldV + newV) / 2;
		}
		/*
		 * else reuse the old one
		 */
		return oldV;
	}

}
