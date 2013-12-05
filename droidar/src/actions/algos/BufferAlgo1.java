package actions.algos;

/**
 * implementation of the function. <br>
 * 
 * <pre>
 *       /              0  		for       |x| < a   
 *  f(x)=| 1/(b-a)*|x|+a/(a-b)	for  a <= |x| < b  
 *       \              1  		for  b <= |x|
 *  
 *  graph of f(x):
 *  
 *   ^          _________
 *  1|         /| 
 *   |        / |
 *   |       /  |
 *   |      /   |
 *   |     /    |
 *   |____/     |        
 *  0 ----a-----b-------->
 * </pre>
 * 
 * @author Spobo
 * 
 */
public class BufferAlgo1 extends Algo {

	private final float mA;
	private final float mB;
	private final float mM;
	private final float mN;

	/**
	 * @param a - upper bound buffer
	 * @param b - lower bound buffer
	 */
	public BufferAlgo1(float a, float b) {
		this.mA = a;
		this.mB = b;
		mM = 1f / (b - a);
		mN = a / (a - b);
	}

	@Override
	public boolean execute(float[] target, float[] values, float bufferSize) {
		target[0] = morph(target[0], values[0]);
		target[1] = morph(target[1], values[1]);
		target[2] = morph(target[2], values[2]);
		return true;
	}

	/**
	 * @param v
	 * @param newV
	 * @return newT=t+f(|v-t|)
	 */
	private float morph(float v, float newV) {
		float x = newV - v;
		if (x >= 0) {
			if (x < mA) {
				return v; // v+0*x
			}
			if (mB <= x) {
				return newV; // v+1*x
			}
			return v + x * mM + mN;
		} else {
			if (-x < mA) {
				return v;
			}
			if (mB <= -x) {
				return newV;
			}
			return v + x * mM + mN;
		}
	}

}
