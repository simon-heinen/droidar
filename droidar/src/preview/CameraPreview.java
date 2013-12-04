package preview;

/**
 * Callback interface for communicating frame information.
 */
public interface CameraPreview {
	
	/**
	 * Used as a callback method to re-add a frame with modified data. 
	 * @param frame - byte array the contains frame information
	 */
	void reAddCallbackBuffer(byte[] frame);

}
