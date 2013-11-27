package listeners;

public interface FrameReceivedListener {
	
	public void onFrameReceived(byte[] frame, float[] mat, int frameWidth, int frameHeight);
	
	public void onFrameParsed(float[] mat);
	
	public void onForceRefresh();
	
}
