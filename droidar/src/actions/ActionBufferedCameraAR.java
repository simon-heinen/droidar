package actions;

import gl.GLCamera;
import android.view.MotionEvent;

/**
 * This action provides two useful features when using AR. First the virtual
 * camera can be rotated when dragging horizontal and second the cameras height
 * will be adjusted on vertical dragging. this allows a better overview
 * 
 * @author Spobo
 * 
 */
public class ActionBufferedCameraAR extends Action {

	private static final float DEFAULT_X_TOUCH_SENSITY = 2;
	private static final float DEFAULT_Y_TOUCH_SENSITY = 36;
	private final float mXTouchSensity;
	private final float mYTouchSensity;
	private GLCamera mTargetCamera;

	/**
	 * @param glCamera
	 *            {@link GLCamera}
	 * @param sensityX
	 *            is the vertical value (should be around 2, smaller is faster)
	 * @param sensityY
	 *            is the horizontal value (should be around 8, smaller is
	 *            faster)
	 */
	public ActionBufferedCameraAR(GLCamera glCamera, float sensityX,
			float sensityY) {
		mTargetCamera = glCamera;
		mXTouchSensity = sensityX;
		mYTouchSensity = sensityY;
	}

	/**
	 * uses default accuracy.
	 * 
	 * @param camera - {@link gl.GLCamera}
	 */
	public ActionBufferedCameraAR(GLCamera camera) {
		this(camera, DEFAULT_X_TOUCH_SENSITY, DEFAULT_Y_TOUCH_SENSITY);
	}

	@Override
	public boolean onTouchMove(MotionEvent e1, MotionEvent e2,
			float screenDeltaX, float screenDeltaY) {
		screenDeltaX = screenDeltaX / mXTouchSensity;
		screenDeltaY = -screenDeltaY / mYTouchSensity;
		mTargetCamera.changeZAngleBuffered(screenDeltaX);
		mTargetCamera.changeZPositionBuffered(screenDeltaY);
		return true;
	}

	@Override
	public boolean onReleaseTouchMove() {
		mTargetCamera.resetBufferedAngle();
		return true;
	}

}
