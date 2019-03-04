package actions;

import gl.GLCamera;
import util.Vec;
import worldData.Updateable;
import android.view.MotionEvent;

/**
 * This uses the virtual camera rotation to map input from the touchscreen or
 * the trackball etc and do something along the virtual camera axes (eg camera
 * movement or object movement or anything else). without mapping it to the
 * current camera rotation, a x+10 movement would always be along the virtual x
 * axis and not along the current camera x axis. <br>
 * 
 * Dont forget to register it at the worldUpdater!
 * 
 * @author Spobo
 * 
 */
public class ActionWASDMovement extends Action {

	protected GLCamera myTargetCamera;
	private final float xReduction;
	private final float yReduction;
	private Vec accelerationVec = new Vec();
	private float myMaxSpeed;

	private float yFactor;
	private float xFactor;

	/**
	 * @param camera
	 * @param xReduction
	 *            redutcion in x (W or S key) direction. Higher means slower.
	 *            Try 25f
	 * @param yReduction
	 *            redutcion in y (A or D key) direction. Higher means slower.
	 *            Try 50f
	 * @param maxSpeed
	 *            maximum movementSpeed. Try 20f
	 */
	public ActionWASDMovement(GLCamera camera, float xReduction,
			float yReduction, float maxSpeed) {
		myTargetCamera = camera;
		this.xReduction = xReduction;
		this.yReduction = yReduction;
		myMaxSpeed = maxSpeed;
	}

	@Override
	public boolean onTouchMove(MotionEvent e1, MotionEvent e2,
			float screenDeltaX, float screenDeltaY) {

		yFactor = (-e2.getX() + e1.getX()) / yReduction;
		xFactor = (e1.getY() - e2.getY()) / xReduction;

		return true;
	}

	@Override
	public boolean onReleaseTouchMove() {
		xFactor = 0;
		yFactor = 0;
		return true;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		if (xFactor != 0 || yFactor != 0) {

			float[] rayDir = new float[4];
			myTargetCamera.getCameraViewDirectionRay(null, rayDir);

			accelerationVec.x = rayDir[0];
			accelerationVec.y = rayDir[1];
			accelerationVec.z = rayDir[2];

			accelerationVec.normalize();
			accelerationVec.mult(xFactor);
			/*
			 * now the yFactor which has to be added orthogonal to the x
			 * direction (with z value 0)
			 */
			// Vec normalizedOrtoh =
			// Vec.getOrthogonalHorizontal(accelerationVec);

			Vec yDir = new Vec(yFactor, 0, 0);
			yDir.rotateAroundZAxis(180 - myTargetCamera
					.getCameraAnglesInDegree()[0]);

			// System.out.println("yDir="+yDir);

			accelerationVec.add(yDir);

			if (accelerationVec.getLength() > myMaxSpeed)
				accelerationVec.setLength(myMaxSpeed);

			myTargetCamera.changeNewPosition(accelerationVec.x * timeDelta,
					accelerationVec.y * timeDelta, accelerationVec.z
							* timeDelta);

		}

		return true;
	}
}
