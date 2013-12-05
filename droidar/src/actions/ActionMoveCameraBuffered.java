package actions;

import gl.GLCamera;

/**
 * can be used to move the virtual camera when touching the screen or using the
 * trackball etc.
 * 
 * @author Spobo
 * 
 */
public class ActionMoveCameraBuffered extends ActionDoAlongAxis {

	/**
	 * Constructor.
	 * @param camera - {@link gl.GLCamera}
	 * @param trackballFactor
	 *            something like 5
	 * @param touchscreenFactor
	 *            something like 25
	 */
	public ActionMoveCameraBuffered(GLCamera camera, float trackballFactor,
			float touchscreenFactor) {

		super(camera, trackballFactor, touchscreenFactor);

	}

	@Override
	public void doAlongViewAxis(float x, float y) {
		getTargetCamera().changeXYPositionBuffered(x, y);
	}

}
