package actions;

import gl.GLCamera;
import worldData.Updateable;

/**
 * this class is for debugging purpose only at the moment and has to be modified
 * to work. it extracts the the yaw, pitch and roll from the sensor data arrays
 * 
 * @author Spobo
 * 
 */
@Deprecated
public class ActionRotateCameraBufferedDebug extends Action {

	private Updateable myCamera;

	public ActionRotateCameraBufferedDebug(GLCamera camera) {
		myCamera = camera;
	}

	@Override
	public boolean onAccelChanged(float[] values) {

		float pitch = (float) +Math.toDegrees(Math.atan2(-values[1],
				Math.sqrt(values[2] * values[2] + values[0] * values[0])));
		float roll = (float) -Math.toDegrees(Math.atan2(values[0], -values[2]));

		return true;
	}

	@Override
	public boolean onMagnetChanged(float[] values) {

		float yaw = (float) -Math.toDegrees(Math.atan2(-values[0],
				Math.sqrt(values[1] * values[1] + values[2] * values[2])));
		float pitch = (float) -Math
				.toDegrees(Math.atan2(-values[2], values[1]));

		return true;
	}

	@Override
	public boolean onOrientationChanged(float[] values) {
		// TODO find right order:
		float yaw = values[0];
		float pitch = values[1];
		float roll = values[2];
		return true;
	}

}
