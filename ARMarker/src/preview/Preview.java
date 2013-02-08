package preview;

import java.io.IOException;

import markerDetection.DetectionThread;


import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class Preview extends SurfaceView implements
		SurfaceHolder.Callback, Camera.PreviewCallback {
	protected boolean paused = false;
	protected boolean first = true;
	protected Message message;
	private SurfaceHolder mHolder;
	protected Camera mCamera;
	protected DetectionThread myThread;
	protected Camera.Size optimalSize;
	protected double aspectRatio;

	public Preview(Context context, Thread thread, Camera.Size size) {
		super(context);
		optimalSize = size;
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		myThread = (DetectionThread) thread;
		myThread.setPreview(this);
	}

	public Preview(Context context) {
		super(context);
	}

	@Override
	public abstract void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
			int arg3);

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		Log.d("AR", "opening camera.");
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			// Surface will be destroyed when we return, so stop the preview.
			// Because the CameraDevice object is not a shared resource, it's
			// very
			// important to release it when the activity is paused.
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public abstract void onPreviewFrame(byte[] arg0, Camera arg1);

	public abstract void reAddCallbackBuffer(byte[] data);

	public void pause() {
		paused = true;

	}

	public void resume() {
		paused = false;

	}

	public void releaseCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

}
