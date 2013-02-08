package actions.algos;

import util.Log;

public class DoubleMeanSquareRootBuffer {

	private static final boolean DEBUG_SHOW_VALUES = false;
	private float mybufferValue;
	private int bufferCount;
	private float minimumBufferValue;

	/**
	 * uses double mean square root method and sets lower and upper bounds to
	 * buffer values
	 * 
	 * @param target
	 * @param values
	 * @param buffer
	 */
	private synchronized void rootMeanSquareBuffer4(float[] target,
			float[] values, float buffer) {

		/*
		 * dont set amplification under 150
		 */
		final float amplification = 200;
		mybufferValue = (bufferCount * mybufferValue + buffer)
				/ (1 + bufferCount);
		if (mybufferValue < minimumBufferValue) {
			mybufferValue = minimumBufferValue;
		}
		if (DEBUG_SHOW_VALUES)
			Log.d("Eventmanager", "buffer values: " + mybufferValue);

		target[0] += amplification;
		target[1] += amplification;
		target[2] += amplification;
		values[0] += amplification;
		values[1] += amplification;
		values[2] += amplification;

		target[0] = (float) (Math.sqrt(Math.sqrt((target[0] * target[0]
				* target[0] * target[0] * mybufferValue + values[0] * values[0]
				* values[0] * values[0])
				/ (1 + mybufferValue))));
		target[1] = (float) (Math.sqrt(Math.sqrt((target[1] * target[1]
				* target[1] * target[1] * mybufferValue + values[1] * values[1]
				* values[1] * values[1])
				/ (1 + mybufferValue))));
		target[2] = (float) (Math.sqrt(Math.sqrt((target[2] * target[2]
				* target[2] * target[2] * mybufferValue + values[2] * values[2]
				* values[2] * values[2])
				/ (1 + mybufferValue))));

		target[0] -= amplification;
		target[1] -= amplification;
		target[2] -= amplification;
		values[0] -= amplification;
		values[1] -= amplification;
		values[2] -= amplification;
	}

}
