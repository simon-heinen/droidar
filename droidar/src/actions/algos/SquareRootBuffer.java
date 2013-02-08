package actions.algos;

public class SquareRootBuffer extends Algo {

	/*
	 * dont set amplification under 150
	 */
	final float AMPLIFIC = 150;

	@Override
	public boolean execute(float[] target, float[] values, float bufferSize) {
		return rootMeanSquareBuffer2(target, values, bufferSize);
	}

	private synchronized boolean rootMeanSquareBuffer2(float[] target,
			float[] values, float mybufferValue) {

		target[0] += AMPLIFIC;
		target[1] += AMPLIFIC;
		target[2] += AMPLIFIC;
		values[0] += AMPLIFIC;
		values[1] += AMPLIFIC;
		values[2] += AMPLIFIC;

		target[0] = (float) (Math
				.sqrt((target[0] * target[0] * mybufferValue + values[0]
						* values[0])
						/ (1 + mybufferValue)));
		target[1] = (float) (Math
				.sqrt((target[1] * target[1] * mybufferValue + values[1]
						* values[1])
						/ (1 + mybufferValue)));
		target[2] = (float) (Math
				.sqrt((target[2] * target[2] * mybufferValue + values[2]
						* values[2])
						/ (1 + mybufferValue)));

		target[0] -= AMPLIFIC;
		target[1] -= AMPLIFIC;
		target[2] -= AMPLIFIC;
		values[0] -= AMPLIFIC;
		values[1] -= AMPLIFIC;
		values[2] -= AMPLIFIC;
		return true;
	}

}
