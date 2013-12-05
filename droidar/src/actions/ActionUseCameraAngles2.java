package actions;

import listeners.eventManagerListeners.OrientationChangedListener;
import setup.ArSetup;
import system.EventManager;
import android.hardware.SensorManager;
import android.view.Surface;

/**
 * Register it in the
 * {@link EventManager#addOnOrientationChangedAction(OrientationChangedListener)}
 * and you will get updates whenever the angles change.
 * 
 * @author Simon Heinen
 * 
 */
public abstract class ActionUseCameraAngles2 implements
		OrientationChangedListener {

	private static final int MATRIX_SIZE = 16;
	private static final int CIRCLE = 360;
	private static final int HALF_CIRCLE = CIRCLE / 2;
	private static final int MIN_MATRIX_SIZE = 3;
	private static final int FOURTH_OF_CIRCLE_DEG = 90;
	
	private float[] mMag;
	private float[] mAccel;
	private boolean mSensorRead;
	private float[] mR = new float[MATRIX_SIZE];
	private float[] mOutR = new float[MATRIX_SIZE];
	private float[] mI = new float[MATRIX_SIZE];
	private int mScreenRotation;

	/**
	 * Constructor.
	 * 
	 */
	public ActionUseCameraAngles2() {
		mScreenRotation = ArSetup.getScreenOrientation();
	}

	@Override
	public boolean onMagnetChanged(float[] values) {
		mMag = values;
		mSensorRead = true;
		calcMatrix();
		return true;
	}

	@Override
	public boolean onAccelChanged(float[] values) {
		mAccel = values;
		calcMatrix();
		return true;
	}

	@Override
	public boolean onOrientationChanged(float[] values) {
		// TODO
		return true;
	}

	private void calcMatrix() {
		if (mMag != null && mAccel != null && mSensorRead) {
			mSensorRead = false;
			SensorManager.getRotationMatrix(mR, mI, mAccel, mMag);
			onRotationMatrixUpdated(mR);
		}
	}

	/**
	 * @return the correctly rotated matrix which can then be used in OpenGL
	 *         e.g.
	 */
	public float[] getRotateMatrixAccordingToDeviceOrientation() {
		if (EventManager.isTabletDevice) {
			/*
			 * change accel sensor data according to http://code.google.com/p
			 * /libgdx/source/browse/trunk/backends/gdx -backend-android/src/com
			 * /badlogic/gdx/backends/android/AndroidInput.java
			 */
			SensorManager.remapCoordinateSystem(mR, SensorManager.AXIS_X,
					SensorManager.AXIS_Y, mOutR);
		} else {
			/*
			 * TODO do this for all 4 rotation possibilities!
			 */
			if (mScreenRotation == Surface.ROTATION_90) {
				// then rotate it according to the screen rotation:
				SensorManager.remapCoordinateSystem(mR, SensorManager.AXIS_Y,
						SensorManager.AXIS_MINUS_X, mOutR);
			} else {
				mOutR = mR;
			}
		}
		return mOutR;
	}

	private final float mRad2deg = HALF_CIRCLE / (float) Math.PI;
	private float[] mO = new float[MIN_MATRIX_SIZE];

	/**
	 * you can also use
	 * {@link ActionUseCameraAngles2#getRotateMatrixAccordingToDeviceOrientation()}
	 * here to rotate R correctly automatically. so this method body might look
	 * like this: <br>
	 * <br>
	 * glCamera.setRotationMatrix(getRotateMatrixAccordingToDeviceOrientation(),
	 * 0);
	 * 
	 * @param updatedRotationMatrix - float[] contains the updated matrix
	 */
	public void onRotationMatrixUpdated(float[] updatedRotationMatrix) {
		SensorManager.getOrientation(updatedRotationMatrix, mO);
		float magnet = mO[0] * mRad2deg + FOURTH_OF_CIRCLE_DEG;
		if (magnet < 0) {
			magnet += CIRCLE;
		}
		onAnglesUpdated(mO[1] * mRad2deg, -mO[2] * mRad2deg, magnet);
	}

	/**
	 * @param pitch
	 *            pitch is the rotation around the x axis. when the device would
	 *            be a steering wheel, this method would indicate how much it is
	 *            rotated. 0 means the car drives straight forward, positive
	 *            values (0 to 90) mean that the car turns left, negative values
	 *            mean that the car turns right
	 * @param roll
	 *            the roll is the rotation around the y axis. if the device
	 *            would be your head 0 would mean you are looking on the ground,
	 *            90 would mean you look in front of you and 180 would mean you
	 *            look in the sky. from 0 to 360. 0 means the camera targets the
	 *            ground, 180 the camera looks into the sky
	 * @param compassAzimuth
	 *            0=north 90=east 180=south 270=west
	 */
	public abstract void onAnglesUpdated(float pitch, float roll,
			float compassAzimuth);

}
