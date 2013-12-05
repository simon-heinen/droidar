package actions.algos;

/**
 * Perform different kinds of filtering based on the {@link actions.algos.SmoothingAlgo.FilterType}. 
 */
public class SmoothingAlgo extends Algo {
	
	private static final int SENSOR_ARRAY_SIZE = 3;
	private static final float ALPHA = 0.04f;
	private static final float BETA = 0.03f;
	private FilterType mFilter = FilterType.NONE;

	//variables needed for lowpass filter
	private float[] mPrevValues;
	
	//variables needed for highpass filter
	private float[] mCurrentValues   = new float[SENSOR_ARRAY_SIZE];
	private float[] mLastValues      = new float[SENSOR_ARRAY_SIZE];
	private float[] mHighPassValues  = new float[SENSOR_ARRAY_SIZE];
	
	/**
	 * Different filters that can be used they {@link actions.algos.SmoothingAlgo}. 
	 */
	public enum FilterType {
		NONE,
		LOWPASS,
		LOWPASSV2,
		HIGHPASS,
		BAND,
		BANDV2,
		KORMAN,
	}
	
	/**
	 * Constructor.
	 * @param pFilter - {@link actions.algos.SmoothingAlgo.FilterType}
	 */
	public SmoothingAlgo(FilterType pFilter) {
		mFilter = pFilter;
	}
	
	@Override
	public float[] execute(float[] pNewData) {
		float[] retValue = pNewData.clone();
		switch(mFilter) {
		case NONE:
			break;
		case LOWPASS:
			mPrevValues = lowPass(pNewData, mPrevValues);
			retValue = mPrevValues.clone();
			break;
		case LOWPASSV2:
			mPrevValues = lowPass(pNewData, mPrevValues);
			retValue = mPrevValues.clone();
			break;
		case HIGHPASS:
			mCurrentValues = pNewData.clone();
			mHighPassValues = highPass(mCurrentValues, mLastValues, mHighPassValues);
			mLastValues = mCurrentValues.clone();
			retValue = mHighPassValues;
			break;
		case BAND:
			mCurrentValues = pNewData.clone();
			mHighPassValues = highPass(mCurrentValues, mLastValues, mHighPassValues);
			mLastValues = mCurrentValues.clone();
			mPrevValues = lowPass(mHighPassValues, mPrevValues);
			retValue = mPrevValues.clone();
			break;
		case BANDV2:
			break;
		case KORMAN:
			break;
		default:
			break;
		}
		
		return retValue;
	}
	
	
	private float[] lowPass(float[] current, float[] last) {
		if (last == null) {
			last = new float[current.length];
		}
		
		for (int i = 0; i < current.length; i++) {
			if (mFilter == FilterType.LOWPASS || mFilter == FilterType.BAND) {
				last[i] = last[i] * (1.0f - ALPHA) + current[i] * ALPHA;
			} else {
				//Assume LOWPASSV2
				last[i] = current[i] * (1.0f - ALPHA) + last[i] * ALPHA;
			}
		}
		
		return last;
	}
	
	private float[] highPass(float[] current, float[] last, float[] filtered) {
		if (last == null || filtered == null) {
			last = new float[current.length];
			filtered = new float[current.length];
		}
		
		for (int i = 0; i < current.length; i++) {
			filtered[i] = BETA * (filtered[i] + current[i] + last[i]);
			last[i] = current[i];
		}
		return filtered;
	}
}
