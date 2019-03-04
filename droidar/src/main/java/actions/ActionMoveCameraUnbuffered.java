package actions;

import gl.GLCamera;
import android.view.MotionEvent;

@Deprecated
public class ActionMoveCameraUnbuffered extends Action {

	private GLCamera myCamera;
	private float myFactor;

	/**
	 * @param camera
	 * @param factor
	 *            should be around 2-15
	 */
	public ActionMoveCameraUnbuffered(GLCamera camera, float factor) {
		myCamera = camera;
		myFactor = factor;
	}

	@Override
	public boolean onTrackballEvent(float x, float y, MotionEvent event) {
		myCamera.changePositionUnbuffered(y * myFactor, x * myFactor);
		return true;
	}

}
