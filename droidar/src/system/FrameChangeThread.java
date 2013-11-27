package system;

import java.util.LinkedList;
import java.util.List;

import preview.CameraPreview;
import listeners.FrameReceivedListener;
import android.os.SystemClock;

public class FrameChangeThread extends Thread {
	
	private int mFrameWidth;
	private int mFrameHeight;
	private byte[] mFrame = null;
	
	private boolean mBusy = false;
	private boolean mStopRequest = false;
	
	private List<FrameReceivedListener> mFrameReceivers = new LinkedList<FrameReceivedListener>();
	private CameraPreview mPreview;
	
	private float[] mMat;
	private long mStartFps;
	private int mFpsCount;
	private long mEndFps;
	private double mFps;
	
	public FrameChangeThread(CameraPreview preview){
		mPreview = preview;
		mMat = new float[1 + 18 * 5];
		setDaemon(true);
	}
	
	public synchronized void run(){
		while(true){
			
			waitForFrame();
			
			if(!mStopRequest){
				calculateFPS();
				onFrameReceived();
				onForcedRefresh();
				onFrameParsed();
				mBusy = false;
				mPreview.reAddCallbackBuffer(mFrame);
			}

			yield();
		}
	}
	
	/**
	 * 
	 * @param preview
	 */
	public void setPreview(CameraPreview preview){
		mPreview = preview;
	}
	
	/**
	 * Retrieve calculated FPS. 
	 * @return
	 */
	public double getFps(){
		return mFps;
	}
	
	/**
	 * Stops the thread from processing frames.
	 */
	public void stopThread(){
		mStopRequest = true;
	}
	
	/**
	 * This will initialize the frame width and height with the 
	 * given paramaters.  It will also cause this thread to start running
	 * @param width
	 * @param height
	 */
	public void setSizeAndRun(int width, int height){
		mFrameWidth = width;
		mFrameHeight = height;
		
		if(mStopRequest) {
			mStopRequest = false;
		}else{
			start();
		}
	}
	
	/**
	 * This will notify the thread that the next frame is available
	 * @param frame
	 */
	public synchronized void nextFrame(byte[] frame){
		if(!mBusy){
			mBusy = true;
			mFrame = frame;
			synchronized(this){
				notify();
			}
		}
	}
	
	/**
	 * Notify listeners when a forced refresh is requested
	 */
	private void onForcedRefresh(){
		for(FrameReceivedListener listener : mFrameReceivers){
			listener.onForceRefresh();
		}
	}
	
	/**
	 * Notify listeners when the frame has been parsed
	 */
	private void onFrameParsed(){
		for(FrameReceivedListener listener : mFrameReceivers){
			listener.onFrameParsed(mMat);
		}
	}
	
	/**
	 * Notify listeners when the frame has been received
	 */
	private void onFrameReceived(){
		for(FrameReceivedListener listener : mFrameReceivers){
			listener.onFrameReceived(mFrame, mMat, mFrameWidth, mFrameHeight);
		}
	}
	
	/**
	 * Calculates the FPS based on the uptime and frames
	 * that get processed. 
	 */
	private void calculateFPS(){
		if(mStartFps == 0){
			mStartFps = SystemClock.uptimeMillis();
		}
		mFpsCount++;
		if(mFpsCount == 30){
			mEndFps = SystemClock.uptimeMillis();
			mFps = 30 / ((mEndFps - mStartFps) / 1000.0);
			mStartFps = 0;
			mFpsCount = 0;
		}
	}
	
	/**
	 * Causes the thread to wait until the next available frame
	 */
	private void waitForFrame(){
		while( mBusy == false || mStopRequest == true){
			try{
				wait();
			}catch(Exception e){}
		}
	}
	
	/**
	 * Added on frame change listener
	 * @param listener
	 */
	public void addFrameReceivedListener(FrameReceivedListener listener){
		mFrameReceivers.add(listener);
	}
	
	/**
	 * Remove on frame change listener
	 * @param listener
	 */
	public void removeFrameReceiviedListener(FrameReceivedListener listener){
		mFrameReceivers.remove(listener);
	}
	
	/**
	 * Check to see if this thread can process another frame
	 * @return
	 */
	public boolean isAvailable(){
		return !mBusy;
	}

}
