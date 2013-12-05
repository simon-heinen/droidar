package actions;

import gl.GLCamera;
import android.view.MotionEvent;

/**
 *  Perform unbuffered processing when moving the camera.  
 *  //TODO: Determine if this class is needed. 
 *
 */
@Deprecated
public class ActionMoveCameraUnbuffered extends Action {

	private GLCamera mCamera;
	private float mFactor;

	/**
	 * Constructor.
	 * @param camera - {@link gl.GLCamera}
	 * @param factor
	 *            should be around 2-15
	 */
	public ActionMoveCameraUnbuffered(GLCamera camera, float factor) {
		mCamera = camera;
		mFactor = factor;
	}

	@Override
	public boolean onTrackballEvent(float x, float y, MotionEvent event) {
		mCamera.changePositionUnbuffered(y * mFactor, x * mFactor);
		return true;
	}

}
