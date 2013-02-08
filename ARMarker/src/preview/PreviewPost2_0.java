package preview;

import java.lang.reflect.Method;

import markerDetection.DetectionThread;


import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

public class PreviewPost2_0 extends Preview{
	
	public PreviewPost2_0(Context context, DetectionThread thread, Camera.Size size) {
		super(context, thread, size);
	}


	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		
		w= optimalSize.width; 
		h= optimalSize.height;
		Camera.Parameters parameters = mCamera.getParameters(); 
		parameters.setPreviewSize(w, h); 
        mCamera.setParameters(parameters);
        PixelFormat p = new PixelFormat();
        PixelFormat.getPixelFormatInfo(parameters.getPreviewFormat(),p);
        
        
        //Must call this before calling addCallbackBuffer to get all the
        // reflection variables setup
        initAddCallbackBufferMethod();
        int bufSize = ((h*w)*p.bitsPerPixel)/8;
        Log.d("AR","Camera parameters: Size: "+bufSize+", Height: "+h+", Width: "+w+", pixelformat: "+p.toString());
        if(first){
        	myThread.setImageSizeAndRun(h,w);
            first=false;
        }
        //Add a buffer for the detection. This will be added to the queue again once it is free to use
        //otherwise the frames will be discarded.
        byte[] buffer = new byte[bufSize];
        addCallbackBuffer(buffer);   

        setPreviewCallbackWithBuffer(); 
        
        mCamera.startPreview();
    }


	//Variables used during the reflections for the callback methods 
	Method addCallbackBufferMethod;
	Object[] argumentList;

	private void initAddCallbackBufferMethod() {
		try {
			@SuppressWarnings("rawtypes")
			Class mC = Class.forName("android.hardware.Camera");

			@SuppressWarnings("rawtypes")
			Class[] mPartypes = new Class[1];
			mPartypes[0] = (new byte[1]).getClass(); 
			addCallbackBufferMethod = mC.getMethod("addCallbackBuffer", mPartypes);

			argumentList = new Object[1];
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 *The normal addCallbackBuffer method as specified by the Android API but here it is accessed
	 *with reflections.
	 */
	private void addCallbackBuffer(byte[] b) {
		// Check to be sure initForACB has been called to setup
		// addCallbackBufferMethod and argumentList
		if (argumentList == null) {
			initAddCallbackBufferMethod();
		}

		argumentList[0] = b;
		try {
			addCallbackBufferMethod.invoke(mCamera, argumentList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *The normal setPreviewCallbackWithBuffer method as specified by the Android API but here it is accessed
	 *with reflections.
	 */
	private void setPreviewCallbackWithBuffer() {
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName("android.hardware.Camera");

			Method setPreviewCallbackWithBuffer = c.getDeclaredMethod(
					"setPreviewCallbackWithBuffer", new Class[] { Camera.PreviewCallback.class });
			Object[] arglist = new Object[1];
			arglist[0] = this;
			setPreviewCallbackWithBuffer.invoke(mCamera, arglist);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *The normal onPreviewFrame method as specified by the Android API but here it is accessed
	 *with reflections.
	 */
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (myThread.busy == false) {
			myThread.nextFrame(data);
		} else if(!paused){
			//Add the buffer back into the queue.
			addCallbackBuffer(data);
		}

	}


	
	@Override
	public void reAddCallbackBuffer(byte[] data) {
		if(!paused){
			addCallbackBuffer(data);
		}		
	}




}
