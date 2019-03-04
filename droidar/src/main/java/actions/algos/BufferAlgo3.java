package actions.algos;

public class BufferAlgo3 extends Algo {

	private float myBuffer;
	private float a;
	private float b;

	/**
	 * @param buffer
	 *            has to be between 0 and 1 (try 0.2)
	 */
	public BufferAlgo3(float buffer, float a, float b) {
		myBuffer = buffer;
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean execute(float[] target, float[] values, float bufferSize) {
		target[0] = morph(target[0], values[0]);
		target[1] = morph(target[1], values[1]);
		target[2] = morph(target[2], values[2]);
		return true;
	}

	private float morph(float t, float v) {
		float dist = v - t;
		if (-a < dist && dist < a) {
			return t;
		}
		if (dist < -b || b < dist) {
			return v;
		}
		return t + (dist) * myBuffer;
	}

}
