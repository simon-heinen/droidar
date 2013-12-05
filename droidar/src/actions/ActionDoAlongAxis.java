package actions;

import gl.GLCamera;
import util.Vec;
import android.view.MotionEvent;

/**
 * This uses the virtual camera rotation to map input from the touchscreen or
 * the trackball etc and do something along the virtual camera axes (eg camera
 * movement or object movement or anything else). without mapping it to the
 * current camera rotation, a x+10 movement would always be along the virtual x
 * axis and not along the current camera x axis.
 * 
 * @author Spobo
 * 
 */
public abstract class ActionDoAlongAxis extends Action {

	private GLCamera mTargetCamera;
	private float mTrackballFactor;
	private final float mTouchscreenReductionFactor;
	private Vec mMovementVec = new Vec();
	
	private static final int CIRCLE = 360;

	/**
	 * Constructor.
	 * @param camera - {@link gl.GLCamera}
	 * @param trackballFactor
	 *            should be around 2-15
	 * @param touchscreenFactor
	 *            25 would be good value to start.The higher the value the
	 *            slower the movement
	 */
	public ActionDoAlongAxis(GLCamera camera, float trackballFactor,
			float touchscreenFactor) {
		mTargetCamera = camera;
		mTrackballFactor = trackballFactor;
		mTouchscreenReductionFactor = touchscreenFactor;
	}

	@Override
	public boolean onTrackballEvent(float x, float y, MotionEvent event) {
		alignAcordingToViewAxes(x * mTrackballFactor, -y * mTrackballFactor);

		return true;
	}

	@Override
	public boolean onTouchMove(MotionEvent e1, MotionEvent e2,
			float screenDeltaX, float screenDeltaY) {

		alignAcordingToViewAxes(screenDeltaX / mTouchscreenReductionFactor,
				-screenDeltaY / mTouchscreenReductionFactor);
		return true;
	}

	/**
	 * This is where the magic happens. The input movement is mapped according
	 * to the virtual camera rotation around the z axis to do the movement
	 * "along the axes"
	 */
	private void alignAcordingToViewAxes(float x, float y) {
		mMovementVec.x = x;
		mMovementVec.y = y;
		mMovementVec.rotateAroundZAxis(CIRCLE - (mTargetCamera
				.getCameraAnglesInDegree()[0]));
		doAlongViewAxis(mMovementVec.x, mMovementVec.y);

	}
	
	/**
	 * Get the target camera.
	 * @return - {@link gl.GLCamera}
	 */
	public GLCamera getTargetCamera() {
		return mTargetCamera;
	}

	/**
	 * Perform some action along the view axis.
	 * @param x - axis
	 * @param y - axis
	 */
	abstract void doAlongViewAxis(float x, float y);

}
