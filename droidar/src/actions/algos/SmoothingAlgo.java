package actions.algos;


public class SmoothingAlgo extends Algo {
	
	private static final float ALPHA = 0.04f;
	private static final float BETA = 0.03f;
	private FilterType mFilter = FilterType.NONE;

	//variables needed for lowpass filter
	private float[] mPrevValues;
	
	//variables needed for highpass filter
	private float[] mCurrentValues   = new float[3];
	private float[] mLastValues      = new float[3];
	private float[] mHighPassValues  = new float[3];
	
	public enum FilterType {
		NONE,
		LOWPASS,
		LOWPASSV2,
		HIGHPASS,
		BAND,
		BANDV2,
		KORMAN,
	}
	
	public SmoothingAlgo(FilterType pFilter){
		mFilter = pFilter;
	}
	
	@Override
	public float[] execute(float[] pNewData) {
		float[] retValue = pNewData.clone();
		switch(mFilter){
		case NONE:
			break;
		case LOWPASS:
			mPrevValues = lowPass(pNewData,mPrevValues);
			retValue = mPrevValues.clone();
			break;
		case LOWPASSV2:
			mPrevValues = lowPass(pNewData,mPrevValues);
			retValue = mPrevValues.clone();
			break;
		case HIGHPASS:
			mCurrentValues = pNewData.clone();
			mHighPassValues = highPass(mCurrentValues,mLastValues,mHighPassValues);
			mLastValues = mCurrentValues.clone();
			retValue = mHighPassValues;
			break;
		case BAND:
			mCurrentValues = pNewData.clone();
			mHighPassValues = highPass(mCurrentValues,mLastValues,mHighPassValues);
			mLastValues = mCurrentValues.clone();
			mPrevValues = lowPass(mHighPassValues,mPrevValues);
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
	
	
	private float[] lowPass(float[] current, float[] last){
		if(last == null){
			last = new float[current.length];
		}
		
		for(int i = 0; i < current.length; i++){
			if(mFilter == FilterType.LOWPASS|| mFilter == FilterType.BAND){
				last[i] = last[i] * (1.0f - ALPHA) + current[i] * ALPHA;
			}else{
				//Assume LOWPASSV2
				last[i] = current[i] * (1.0f - ALPHA) + last[i] * ALPHA;
			}
		}
		
		return last;
	}
	
	private float[] highPass(float[] current, float[] last, float[] filtered){
		if(last == null || filtered == null ){
			last = new float[current.length];
			filtered = new float[current.length];
		}
		
		for(int i = 0; i < current.length; i++){
			filtered[i] = BETA * (filtered[i] + current[i] + last[i]);
			last[i] = current[i];
		}
		return filtered;
	}
	
//	public static float[] filterLowPass(float[] input, float[] prev, float alpha) {
//		if (input == null || prev == null)
//			throw new NullPointerException("input/prev needs to be inititialzied");
//		if (input.length != prev.length)
//			throw new IllegalArgumentException("Input arguemtments have different lengths");
//
//		for (int i = 0; i < input.length; i++) {
//			prev[i] = prev[i] + alpha * (input[i] - prev[i]);
//		}
//		return prev;
//	}
//	
//	public static float[] filterHighPass(float[] input, float[] prev, float alpha) {
//		if (input == null || prev == null)
//			throw new NullPointerException("input/prev needs to be inititialzied");
//		if (input.length != prev.length)
//			throw new IllegalArgumentException("Input arguemtments have different lengths");
//
//		for (int i = 0; i < input.length; i++) {
//			prev[i] = (input[i] * ALPHA) + (prev[i] * (1.0f - ALPHA));
//		}
//
//		return prev;
//	}
}
