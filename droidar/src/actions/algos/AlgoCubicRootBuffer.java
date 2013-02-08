package actions.algos;

public class AlgoCubicRootBuffer extends Algo {

	/**
	 * uses cubic root but doesnt work fine..
	 * 
	 * @param target
	 * @param values
	 * @param bufferSize
	 */
	@Override
	public boolean execute(float[] target, float[] values, float bufferSize) {

		target[0] = (float) (Math.cbrt((target[0] * target[0] * target[0]
				* bufferSize + values[0] * values[0] * values[0])
				/ (bufferSize + 1)));
		target[1] = (float) (Math.cbrt((target[1] * target[1] * target[1]
				* bufferSize + values[1] * values[1] * values[1])
				/ (bufferSize + 1)));
		target[2] = (float) (Math.cbrt((target[2] * target[2] * target[2]
				* bufferSize + values[2] * values[2] * values[2])
				/ (bufferSize + 1)));
		return true;
	}

}
