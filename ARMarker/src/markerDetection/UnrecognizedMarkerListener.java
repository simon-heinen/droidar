package markerDetection;

public interface UnrecognizedMarkerListener {

	/**
	 * @param markerCode
	 * @param mat
	 * @param startIdx
	 * @param endIdx
	 * @param rotationValue
	 *            0 90 180 or 270
	 */
	void onUnrecognizedMarkerDetected(int markerCode, float[] mat,
			int startIdx, int endIdx, int rotationValue);

}
