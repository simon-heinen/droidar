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

	// x is the short side of the screen (in landscape mode)
	private final float TOUCH_SENSITY_X;
	// y is the long side of the screen
	private final float TOUCH_SENSITY_Y;
	private GLCamera myTargetCamera;

	/**
	 * @param glCamera
	 * @param sensityX
	 *            is the vertical value (should be around 2, smaller is faster)
	 * @param sensityY
	 *            is the horizontal value (should be around 8, smaller is
	 *            faster)
	 */
	public ActionBufferedCameraAR(GLCamera glCamera, float sensityX,
			float sensityY) {
		myTargetCamera = glCamera;
		TOUCH_SENSITY_X = sensityX;
		TOUCH_SENSITY_Y = sensityY;
	}

	/**
	 * uses default accuracy
	 * 
	 * @param camera
	 */
	public ActionBufferedCameraAR(GLCamera camera) {
		this(camera, 2, 36);
	}

	@Override
	public boolean onTouchMove(MotionEvent e1, MotionEvent e2,
			float screenDeltaX, float screenDeltaY) {
		screenDeltaX = screenDeltaX / TOUCH_SENSITY_X;
		screenDeltaY = -screenDeltaY / TOUCH_SENSITY_Y;
		myTargetCamera.changeZAngleBuffered(screenDeltaX);
		myTargetCamera.changeZPositionBuffered(screenDeltaY);
		return true;
	}

	@Override
	public boolean onReleaseTouchMove() {
		myTargetCamera.resetBufferedAngle();
		return true;
	}

}
