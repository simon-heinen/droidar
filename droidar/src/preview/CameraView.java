package preview;

import java.io.IOException;
import java.util.List;

import logger.ARLogger;
import system.EventManager;
import system.FrameChangeThread;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements 
				SurfaceHolder.Callback,
				Camera.PreviewCallback,
				CameraPreview {
	
	private static final String LOG_TAG = "CameraView";
	
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;
	private FrameChangeThread mOnFrameChange;
	private boolean mPause = false;
	
	public CameraView(Context context) {
		super(context);
		mOnFrameChange = new FrameChangeThread(this);
		intiCameraView(context);
	}

	public CameraView(Context context,FrameChangeThread thread, AttributeSet attrs) {
		super(context, attrs);
		mOnFrameChange = new FrameChangeThread(this);
		intiCameraView(context);
	}

	private void intiCameraView(Context context) {
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		ARLogger.debug(LOG_TAG,"Camera surface holder created.");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//TODO: Put this in a thread as it may slow the UI down due to calling
		//       <code>android.hardware.Camera.onPreviewFrame(Byte b)</code>
		mCamera = Camera.open();
		ARLogger.debug(LOG_TAG, "Camera opened.");
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			releaseCamera();
			ARLogger.exception(LOG_TAG, "Unable to set camear preview display", e);
		}
		if(showInPortrait()){
			mCamera.setDisplayOrientation(90);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		setCameraParameters(width, height);
		setCallBackBuffer(width, height);
		addPreviewCallback();
		resumeCamera();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}
	
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if(mOnFrameChange.isAvailable()){
			mOnFrameChange.nextFrame(data);
		}else if(!mPause){
			mCamera.addCallbackBuffer(data);
		}
		
	}


	/**
	 * Sets the camera's parameters. Sets the optimal preview size
	 * based on the width and height given.
	 */
	public void setCameraParameters(int width, int height) {

		Parameters parameters = mCamera.getParameters();
		List<Size> sizes = parameters.getSupportedPreviewSizes();
		Size optimalSize = getOptimalPreviewSize(sizes, width, height);

		parameters.setPreviewSize(optimalSize.width, optimalSize.height);
		
		adjustOrientation(parameters);
		
		try {
			mCamera.setParameters(parameters);
		} catch (Exception e) {
			ARLogger.exception(LOG_TAG, "Couldn't set camera parameters", e);
		}
	}
	
	private void addPreviewCallback(){
			mCamera.setPreviewCallbackWithBuffer(this);
	}
	
	private void setCallBackBuffer(int width, int height){
		Camera.Parameters parameters = mCamera.getParameters();
		PixelFormat pixelFormat = new PixelFormat();
		PixelFormat.getPixelFormatInfo(
				parameters.getPreviewFormat(), 
				pixelFormat);
		
		int bufSize = ((width*height)*pixelFormat.bitsPerPixel)/8;
		byte[] buffer = new byte[bufSize];
		mCamera.addCallbackBuffer(buffer);
		
        ARLogger.debug(LOG_TAG,"Camera parameters: Size: "+bufSize+", Height: "+height+", Width: "+width+", pixelformat: "+pixelFormat.toString() );
	}

	/**
	 * Properly resume the Camera
	 */
	public void resumeCamera() {
		mPause = false;
		if (mCamera != null) {
			mCamera.startPreview();
			ARLogger.debug(LOG_TAG, "Camera preview started.");
		} else {
			ARLogger.warn(LOG_TAG, "Camera preview unable to be started.");
		}
	}

	
	public void onPause() {
		mPause = true;
		if (mCamera != null) {
			mCamera.stopPreview();
			ARLogger.debug(LOG_TAG, "Camera preview stopped.");
		}
	}

	/**
	 * Properly stop and release the camera
	 */
	public void releaseCamera() {
		mPause = true;
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			ARLogger.debug(LOG_TAG,"Camera released.");
		}
	}
	
	
	/**
	 * Returns the optimal preview size given the width and height
	 * @param sizes
	 * @param width
	 * @param height
	 * @return
	 */
	private Size getOptimalPreviewSize(List<Size> sizes, int width, int height) {
		Size result = null;
		for (Camera.Size size : sizes) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;
					
					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return result;
	}
	
	private boolean showInPortrait(){
		boolean ret = false;
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT &&
				!EventManager.isTabletDevice){
			ret = true;
		}
		
		return ret;
	}
	
	private void adjustOrientation(Parameters params){
		if(showInPortrait()){
			params.set("orientation", "portrait");
			params.set("rotation", "90");
		}
	}

	@Override
	public void reAddCallbackBuffer(byte[] frame) {
		if(!mPause){
			mCamera.addCallbackBuffer(frame);
		}
	}
	
	public FrameChangeThread getFrameUpdater(){
		return mOnFrameChange;
	}

}
