package actions.algos;

public class BufferAlgo2 extends Algo {

	private float myBuffer;

	/**
	 * @param buffer
	 *            has to be between 0 and 1 (try 0.2)
	 */
	public BufferAlgo2(float buffer) {
		myBuffer = buffer;
	}

	@Override
	public boolean execute(float[] target, float[] values, float bufferSize) {
		target[0] = morph(target[0], values[0]);
		target[1] = morph(target[1], values[1]);
		target[2] = morph(target[2], values[2]);
		return true;
	}

	private float morph(float t, float v) {
		return t + (v - t) * myBuffer;
	}

}
