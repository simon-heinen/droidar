package nativeLib;

public class NativeLib {

	/**
	 * Detect markers in the current frame and return the detected data.
	 * @param frame The frame that should be analyzed.
	 * @param mat Float array that will be used to return the data.
	 * @param height Height of the current frame.
	 * @param width Width of the current frame.
	 * @param calibrateNext Boolean which specifies of this frame should be used
	 * for calibration or not. 
	 * @return The number of calibration frames left until the calibration can be started.
	 */
	public native int detectMarkers(byte[] frame, float[] mat, int height,
			int width, boolean calibrateNext);

	/**
	 * Start the calibration.
	 * @param height Height of the frames which are used.
	 * @param width Width of the frames which are used.
	 * @return True if the calibration succeeded
	 */
	public native boolean calibrate(int height, int width);

	/**
	 * Get the calibration data.
	 * @param cameraMat The double array which is used to store the intrinsic camera parameters. (double[9])
	 * @param distortionMat The double array which is used to store the distortion coefficients. (double[5])
	 * @return True if the data was written into the arrays correctly.
	 */
	public native boolean getCalibration(double[] cameraMat,
			double[] distortionMat);

	/**
	 * Set the calibration data.
	 * @param cameraMat The double array which stores the intrinsic camera parameters. (double[9])
	 * @param distortionMat The double array which stores the distortion coefficients. (double[5])
	 * @return True if the data was read from the arrays correctly.
	 */
	public native boolean setCalibration(double[] cameraMat,
			double[] distortionMat);
	
	/**
	 * Initialize the native code and retrieve some constants defined in the native code. 
	 * @param configParams An int[2] array in which the constants will be stored. The first entry stores the maximum number of possible markers. The second entry stores the number of frames used for the calibration.
	 * @param cameraMat The double array which is used to store the intrinsic camera parameters. (double[9])
	 * @param distortionMat The double array which is used to store the distortion coefficients. (double[5])
	 * @return True if everything succeeded.
	 */
	public native boolean initThread(int[] configParams, double[] cameraMat,
			double[] distortionMat);

	static {
		System.loadLibrary("opencv");
	}

}
