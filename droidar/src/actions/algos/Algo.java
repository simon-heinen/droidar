package actions.algos;

import util.Log;

/**
 *  Sensor filter/buffer algo base class. 
 */
public abstract class Algo {

	/**
	 * @param values - new float[] that contains the new values from the sensors
	 * @return - new float[] that contains the buffered/filtered values
	 */
	public float[] execute(float[] values) {
		Log.e("algo class error", "execute(one param) not catched");
		return null;
	}

	/**
	 * @param targetValues - new values will be placed in this array
	 * @param newValues - new float[] data
	 * @param bufferSize - buffer/filter constant 
	 * @return - true if boo
	 */
	public boolean execute(float[] targetValues, float[] newValues,
			float bufferSize) {
		Log.e("algo class error", "execute(3 params) not catched");
		return false;
	}

}
