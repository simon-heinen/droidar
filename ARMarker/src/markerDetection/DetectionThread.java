package markerDetection;

import gl.MarkerObject;

import java.util.HashMap;

import nativeLib.NativeLib;

import preview.Preview;

import android.opengl.GLSurfaceView;
import android.os.SystemClock;

public class DetectionThread extends Thread {
	private int frameWidth;
	private int frameHeight;
	private byte[] frame = null;
	public boolean busy = false;
	private boolean stopRequest = false;
	private GLSurfaceView openglView;
	private Preview preview;
	private NativeLib nativelib;
	private float[] mat;
	private long start = 0;
	private long now = 0;
	private int fcount = 0;
	private double fps = 0;
	private boolean calcFps = false;
	private HashMap<Integer, MarkerObject> markerObjectMap;
	private UnrecognizedMarkerListener unrecognizedMarkerListener;

	public DetectionThread(NativeLib nativeLib, GLSurfaceView openglView,
			HashMap<Integer, MarkerObject> markerObjectMap,
			UnrecognizedMarkerListener unrecognizedMarkerListener) {
		this.openglView = openglView;
		this.markerObjectMap = markerObjectMap;
		this.nativelib = nativeLib;
		this.unrecognizedMarkerListener = unrecognizedMarkerListener;

		// TODO make size dynamically after the init function.
		mat = new float[1 + 18 * 5];

		// application will exit even if this thread remains active.
		setDaemon(true);

	}

	@Override
	public synchronized void run() {
		while (true) {
			while (busy == false || stopRequest == true) {
				try {
					wait();// wait for a new frame
				} catch (InterruptedException e) {
				}
			}

			if (stopRequest == true) {
				// do nothing
			} else {
				if (calcFps) {
					// calculate the fps
					if (start == 0) {
						start = SystemClock.uptimeMillis();
					}
					fcount++;
					if (fcount == 30) {
						now = SystemClock.uptimeMillis();
						fps = 30 / ((now - start) / 1000.0);
						// Log.i("AR", "fps:" + fps);
						start = 0;
						fcount = 0;
					}
				}
				// Pass the frame to the native code and find the
				// marker information.
				// The false at the end is a remainder of the calibration.
				nativelib.detectMarkers(frame, mat, frameHeight, frameWidth,
						false);

				// Needs to be reworked to. Either just deleted, or changed into
				// some timer delay
				openglView.requestRender();

				// Write all current information of the detected markers into
				// the marker hashmap and notify markers they are recognized.
				int startIdx, endIdx, rotIdx, idIdx = 0;
				for (int i = 0; i < (int) mat[0]; i++) {
					startIdx = (1 + i * 18);
					endIdx = startIdx + 15;
					rotIdx = endIdx + 1;
					idIdx = rotIdx + 1;

					// Log.d(LOG_TAG, "StartIdx");

					MarkerObject markerObj = markerObjectMap
							.get((int) mat[idIdx]);

					if (markerObj != null) {
						markerObj.OnMarkerPositionRecognized(mat, startIdx,
								endIdx);
					} else {
						if (unrecognizedMarkerListener != null) {
							unrecognizedMarkerListener
									.onUnrecognizedMarkerDetected(
											(int) mat[idIdx], mat, startIdx,
											endIdx, (int) mat[rotIdx]);
						}
					}
				}

				busy = false;
				preview.reAddCallbackBuffer(frame);
			}

			yield();
		}

	}

	public synchronized void nextFrame(byte[] data) {
		if (busy == false /* this.getState() == Thread.State.WAITING */) {
			// ok, we are ready for a new frame:
			busy = true;
			this.frame = data;
			// do the work:
			synchronized (this) {
				this.notify();
			}
		} else {
			// the Thread is busy, we just ignore the frame and go on.
		}
	}

	public void setImageSizeAndRun(int height, int width) {
		frameHeight = height;
		frameWidth = width;

		if (stopRequest == true) {
			// this means the thread is active, no starting is needed,
			// just reset the flag.
			stopRequest = false;
		} else {
			// this is a new thread.
			super.start();
		}

	}

	public void stopThread() {
		stopRequest = true;
	}

	public double getFps() {
		return fps;
	}

	public void setPreview(Preview preview) {
		this.preview = preview;
	}

	public void calculateFrameRate() {
		calcFps = true;
	}

	public void setMarkerObjectMap(
			HashMap<Integer, MarkerObject> markerObjectMap) {
		this.markerObjectMap = markerObjectMap;
	}
}
