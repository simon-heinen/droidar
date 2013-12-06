package listeners;
/**
 * Interface for listening for new frames. 
 * Subject to change. 
 */
public interface FrameReceivedListener {
	/**
	 * This is called when a new frame has been received. 
	 * @param frame - byte[] that contains the frame being viewed
	 * @param mat - byte[] that contains data ??TODO: ??
	 * @param frameWidth - current frame width
	 * @param frameHeight - current frame height
	 */
	void onFrameReceived(byte[] frame, float[] mat, int frameWidth, int frameHeight);
	
	/**
	 * This is called when a new frame has been parsed.
	 * @param mat - byte[] array that contains the information that has been parsed.
	 */
	void onFrameParsed(float[] mat);
	
	/**
	 * Force the renderer to redraw.
	 * TODO: This may be removed. 
	 */
	void onForceRefresh();
	
}
