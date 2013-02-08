package markerDetection;

import gl.GLFactory;
import gl.GLRenderer;
import gl.MarkerObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import nativeLib.NativeLib;

import preview.Preview;
import preview.PreviewPost2_0;
import preview.PreviewPre2_0;

import system.EventManager;
import system.Setup;
import util.CameraCalibration;
import util.IO;
import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

public abstract class MarkerDetectionSetup extends Setup {

	// /**
	// * This is the ID for the MarkerObject that defines which object to
	// display
	// * for all markers if no specific object for a marker was defined.
	// */
	// public final static int UNDIFINED_MARKER_OBJECT = -1;

	private final String CALIB_PATH = "ARCameraCalibration";
	private Preview cameraPreview;
	// private GLSurfaceView openglView;

	// protected MarkerMatrixContainer matrixContainer;
	private DetectionThread myThread;

	// private ProgressDialog pd;
	// public int MAX_CALIB_FRAMES;
	private NativeLib nativeLib = new NativeLib();
	private CameraCalibration calib = null;
	private Camera.Size cameraSize;
	private LayoutParams optimalLayoutParams;

	@Override
	public void initializeCamera() {

		MarkerObjectMap markerObjectMap = new MarkerObjectMap();
		DisplayMetrics displayMetrics = myTargetActivity.getResources()
				.getDisplayMetrics();
		int weight = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;
		int apiLevel = Integer.parseInt(android.os.Build.VERSION.SDK);

		Camera mCamera = Camera.open();
		Camera.Parameters parameters = mCamera.getParameters();
		cameraSize = parameters.getPreviewSize();

		// Check the OS version to determine what kind of preview to use.
		if (apiLevel < 5) {
			weight = preSdkV5(height);
		} else {
			weight = postSdkV5(parameters, weight, height);
		}
		optimalLayoutParams = new LayoutParams(weight, height);

		mCamera.release();
		tryToLoadCameraSettings();

		// initialize native code.
		int[] constants = new int[2];
		nativeLib.initThread(constants, calib.cameraMatrix,
				calib.distortionMatrix);

		myThread = new DetectionThread(nativeLib, myGLSurfaceView,
				markerObjectMap, _a2_getUnrecognizedMarkerListener());
		if (apiLevel <= 5) {
			cameraPreview = new PreviewPre2_0(myTargetActivity, myThread,
					cameraSize);
			Log.d("AR", "API Level: " + apiLevel + " Created Preview Pre2.1");
		} else {
			cameraPreview = new PreviewPost2_0(myTargetActivity, myThread,
					cameraSize);
			Log.d("AR", "API Level: " + apiLevel + " Created Preview Post2.1");
		}

		_a3_registerMarkerObjects(markerObjectMap);

	}

	@Override
	public void addCameraOverlay() {
		myTargetActivity.addContentView(cameraPreview, optimalLayoutParams);
	}

	@Override
	public void addGLSurfaceOverlay() {
		myTargetActivity.addContentView(myGLSurfaceView, optimalLayoutParams);
	}

	private int preSdkV5(int h) {
		int w;
		// These needs to be checked. Find a "standard" value for the camera
		// resolution and set the display aspect ration accordingly.
		// Height stays the same, but width needs to be changed according to
		// the aspect ratio of the camera resolution.
		w = h * 240 / 160;
		cameraSize.width = 240;
		cameraSize.height = 160;
		return w;
	}

	private int postSdkV5(Camera.Parameters parameters, int w, int h) {
		double aspectRatio = (double) w / (double) h;
		Camera.Size currentSizeChoice;
		List<Camera.Size> supportedSizes = null;
		Method method = null;
		try {
			method = parameters.getClass().getDeclaredMethod(
					"getSupportedPreviewSizes", (Class[]) null);
			Object o = method.invoke(parameters, (Object[]) null);
			supportedSizes = (List<Size>) o;

		} catch (Exception e) {
			e.printStackTrace();
		}
		cameraSize = supportedSizes.get(0);
		// Find the smallest camera resolution;
		for (int i = 0; i < supportedSizes.size(); i++) {
			if (supportedSizes.get(i).width < cameraSize.width) {
				cameraSize = supportedSizes.get(i);
			}
		}

		for (int i = 1; i < supportedSizes.size(); i++) {
			currentSizeChoice = supportedSizes.get(i);
			if (((Math.abs(aspectRatio
					- ((double) cameraSize.width / (double) cameraSize.height))) > Math
					.abs((aspectRatio - ((double) currentSizeChoice.width / (double) currentSizeChoice.height))))
					&& (currentSizeChoice.height <= 240)) {
				cameraSize = currentSizeChoice;
			}
		}
		// Height stays the same, but width needs to be changed according to
		// the aspect ratio of the camera resolution.
		w = h * cameraSize.width / cameraSize.height;
		return w;
	}

	private void tryToLoadCameraSettings() {
		// Load previously stored calibrations
		SharedPreferences settings = myTargetActivity.getSharedPreferences(
				CALIB_PATH, 0);
		String fileName = settings.getString("calibration", null);
		if (fileName != null) {
			try {
				calib = (CameraCalibration) IO.loadSerializable("/sdcard/"
						+ CALIB_PATH + "/" + fileName + ".txt");
				Log.d("AR", "using old calibration");
			} catch (Exception e) {
				Log.d("AR", "default value, file is empty");
				calib = CameraCalibration.defaultCalib(cameraSize.width,
						cameraSize.height);
			}
		} else {
			Log.d("AR", "default value, file not found");
			calib = CameraCalibration.defaultCalib(cameraSize.width,
					cameraSize.height);
		}
	}

	// public static Object loadSerializable(String filename) throws
	// IOException,
	// ClassNotFoundException {
	//
	// FileInputStream fiStream = new FileInputStream(filename);
	// GZIPInputStream gzipStream = new GZIPInputStream(fiStream);
	// ObjectInputStream inStream = new ObjectInputStream(gzipStream);
	// Object loadedObject = inStream.readObject();
	// inStream.close();
	// return loadedObject;
	//
	// }

	public abstract UnrecognizedMarkerListener _a2_getUnrecognizedMarkerListener();

	public abstract void _a3_registerMarkerObjects(
			MarkerObjectMap markerObjectMap);

	@Override
	public void onDestroy(Activity a) {
		super.onDestroy(a);
		if (cameraPreview != null)
			cameraPreview.releaseCamera();
		// Ensure app is gone after back button is pressed!
		if (myThread != null)
			myThread.stopThread();
	}

	@Override
	public void releaseCamera() {
		super.releaseCamera();
		if (cameraPreview != null)
			cameraPreview.releaseCamera();
	}

}
