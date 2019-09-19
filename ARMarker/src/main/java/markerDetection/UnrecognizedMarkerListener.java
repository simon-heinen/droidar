package markerDetection;

public interface UnrecognizedMarkerListener {

	/**
	 * @param markerCode int
	 * @param mat float[]
	 * @param startIdx int
	 * @param endIdx int
	 * @param rotationValue int
	 *            0 90 180 or 270
	 */
	void onUnrecognizedMarkerDetected(int markerCode, float[] mat, int startIdx, int endIdx, int rotationValue);

}
