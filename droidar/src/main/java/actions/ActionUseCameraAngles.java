package actions;

import gl.GLCamera;
import worldData.Updateable;
import android.hardware.SensorManager;

/**
 * Use
 * 
 * Children of this class can access the angles of the specified camera
 * 
 * 
 * 
 * @author Spobo
 * 
 */
@Deprecated
public abstract class ActionUseCameraAngles extends Action {

	@Deprecated
	private int accelCounter;
	@Deprecated
	private int accelThreshold = 10;
	/**
	 * sould represent {@link SensorManager#getOrientation(float[], float[])}
	 */
	@Deprecated
	private float[] myAngles = new float[3];
	private GLCamera myCamera;

	public ActionUseCameraAngles(GLCamera camera) {
		myCamera = camera;
	}

	/**
	 * this affects how often the updatePitch() and updateRoll() methods are
	 * called.
	 * 
	 * @param threshold
	 *            1 means update on every event 10 means update every 10 events.
	 *            default is 10
	 */
	public void setUpdateThreshold(int threshold) {
		this.accelThreshold = threshold;
	}

	@Override
	public boolean onAccelChanged(float[] values) {
		accelCounter++;
		if (accelCounter > accelThreshold) {
			accelCounter = 0;
			/*
			 * missing documentation for the following calculations.. TODO
			 */
			float pitch = (float) Math.toDegrees(Math.atan2(-values[1],
					Math.sqrt(values[2] * values[2] + values[0] * values[0])));
			myAngles[1] = pitch;
			updatePitch(pitch);
			float roll = 180 + (float) -Math.toDegrees(Math.atan2(values[0],
					-values[2]));
			myAngles[2] = pitch;
			updateRoll(roll);
		}
		return true;
	}

	/**
	 * pitch is the rotation around the x axis. when the device would be a
	 * steering wheel, this method would indicate how much it is rotated
	 * 
	 * @param pitchAngle
	 *            0 means the car drives straight forward, positive values (0 to
	 *            90) mean that the car turns left, negative values mean that
	 *            the car turns right
	 */
	public abstract void updatePitch(float pitchAngle);

	/**
	 * the roll is the rotation around the y axis. if the device would be your
	 * head 0 would mean you are looking on the ground, 90 would mean you look
	 * in front of you and 180 would mean you look in the sky
	 * 
	 * @param rollAngle
	 *            from 0 to 360. 0 means the camera targets the ground, 180 the
	 *            camera looks into the sky
	 */
	public abstract void updateRoll(float rollAngle);

	/**
	 * @param azimuth
	 *            0=north 90=east 180=south 270=west
	 */
	public abstract void updateCompassAzimuth(float azimuth);

	@Override
	public boolean onOrientationChanged(float[] values) {
		/*
		 * the use of the orientation sensor results in other values then the
		 * normal magnetometer and accelerometer values. Therefore it was
		 * disabled for the moment.
		 */
		// float pitch = -xAngle;
		// float roll = -yAngle;
		// updateCompassDirection(-zAngle);
		return true;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		float[] v = myCamera.getCameraAnglesInDegree();

		float azimuth = v[0];
		if (myCamera.getRotation() != null)
			azimuth += myCamera.getRotation().z;
		if (azimuth >= 360)
			azimuth -= 360;
		myAngles[0] = azimuth;
		updateCompassAzimuth(azimuth);

		return true;
	}

}
