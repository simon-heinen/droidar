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
	
	private static final int HALF_CIRCLE_DEG = 180;
	private static final int RAY_DIRECTION_SIZE = 4;

	private GLCamera mTargetCamera;
	private final float mXReduction;
	private final float mYReduction;
	private Vec mAccelerationVec = new Vec();
	private float mMaxSpeed;

	private float mYFactor;
	private float mXFactor;

	/**
	 * Constructor.
	 * @param camera - {@link gl.GLCamera}
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
		mTargetCamera = camera;
		mXReduction = xReduction;
		mYReduction = yReduction;
		mMaxSpeed = maxSpeed;
	}

	@Override
	public boolean onTouchMove(MotionEvent e1, MotionEvent e2,
			float screenDeltaX, float screenDeltaY) {

		mYFactor = (-e2.getX() + e1.getX()) / mYReduction;
		mXFactor = (e1.getY() - e2.getY()) / mXReduction;

		return true;
	}

	@Override
	public boolean onReleaseTouchMove() {
		mXFactor = 0;
		mYFactor = 0;
		return true;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		if (mXFactor != 0 || mYFactor != 0) {

			float[] rayDir = new float[RAY_DIRECTION_SIZE];
			mTargetCamera.getCameraViewDirectionRay(null, rayDir);

			mAccelerationVec.x = rayDir[0];
			mAccelerationVec.y = rayDir[1];
			mAccelerationVec.z = rayDir[2];

			mAccelerationVec.normalize();
			mAccelerationVec.mult(mXFactor);
			/*
			 * now the yFactor which has to be added orthogonal to the x
			 * direction (with z value 0)
			 */
			// Vec normalizedOrtoh =
			// Vec.getOrthogonalHorizontal(accelerationVec);

			Vec yDir = new Vec(mYFactor, 0, 0);
			yDir.rotateAroundZAxis(HALF_CIRCLE_DEG - mTargetCamera
					.getCameraAnglesInDegree()[0]);

			// System.out.println("yDir="+yDir);

			mAccelerationVec.add(yDir);

			if (mAccelerationVec.getLength() > mMaxSpeed) {
				mAccelerationVec.setLength(mMaxSpeed);
			}

			mTargetCamera.changeNewPosition(mAccelerationVec.x * timeDelta,
					mAccelerationVec.y * timeDelta, mAccelerationVec.z
							* timeDelta);

		}

		return true;
	}
}
