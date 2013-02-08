package actions;

import listeners.eventManagerListeners.OrientationChangedListener;
import system.EventManager;
import system.Setup;
import android.hardware.SensorManager;
import android.view.Surface;

/**
 * Register it in the
 * {@link EventManager#addOnOrientationChangedAction(OrientationChangedListener)}
 * and you will get updates whenever the angles change
 * 
 * @author Simon Heinen
 * 
 */
public abstract class ActionUseCameraAngles2 implements
		OrientationChangedListener {

	private float[] mag;
	private float[] accel;
	private boolean sensorRead;
	float[] R = new float[16];
	float[] outR = new float[16];
	float[] I = new float[16];
	private int screenRotation;

	public ActionUseCameraAngles2() {
		screenRotation = Setup.getScreenOrientation();
	}

	@Override
	public boolean onMagnetChanged(float[] values) {
		mag = values;
		sensorRead = true;
		calcMatrix();
		return true;
	}

	@Override
	public boolean onAccelChanged(float[] values) {
		accel = values;
		calcMatrix();
		return true;
	}

	@Override
	public boolean onOrientationChanged(float[] values) {
		// TODO
		return true;
	}

	private void calcMatrix() {
		if (mag != null && accel != null && sensorRead) {
			sensorRead = false;
			SensorManager.getRotationMatrix(R, I, accel, mag);
			onRotationMatrixUpdated(R);
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
			SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X,
					SensorManager.AXIS_Y, outR);
		} else {
			/*
			 * TODO do this for all 4 rotation possibilities!
			 */
			if (screenRotation == Surface.ROTATION_90) {
				// then rotate it according to the screen rotation:
				SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Y,
						SensorManager.AXIS_MINUS_X, outR);
			} else {
				outR = R;
			}
		}
		return outR;
	}

	private final float rad2deg = 180 / (float) Math.PI;
	private float[] o = new float[3];

	/**
	 * you can also use
	 * {@link ActionUseCameraAngles2#getRotateMatrixAccordingToDeviceOrientation()}
	 * here to rotate R correctly automatically. so this method body might look
	 * like this: <br>
	 * <br>
	 * glCamera.setRotationMatrix(getRotateMatrixAccordingToDeviceOrientation(),
	 * 0);
	 * 
	 * @param updatedRotationMatrix
	 */
	public void onRotationMatrixUpdated(float[] updatedRotationMatrix) {
		SensorManager.getOrientation(updatedRotationMatrix, o);
		float magnet = o[0] * rad2deg + 90;
		if (magnet < 0)
			magnet += 360;
		onAnglesUpdated(o[1] * rad2deg, -o[2] * rad2deg, magnet);
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
