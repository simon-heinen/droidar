package gl.animations;

import gl.Renderable;
import gl.scenegraph.MeshComponent;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worlddata.Obj;
import worlddata.Updateable;
import worlddata.Visitor;

/**
 * Animation to face another object.
 */
public class AnimationFaceToObj extends GLAnimation {

	private static final float DEFAULT_UPDATE_DELAY = 0.5f;
	private static final int ADJUST_OBJECT_ROTATION = 90;
	private Obj mTargetObject;
	private float mLastUpdateAway = 0;
	private float mUpdateDelay;
	private Vec mRotationVec = new Vec();
	private Vec mNewRotationVec = new Vec();

	private Vec mAdjustmentVec;
	private Vec mTargetObjectPosition;
	private boolean mDontChangeXRotation;

	/**
	 * Constructor.
	 * 
	 * @param targetCamera
	 *            {@link Obj}
	 * @param updateDelay
	 *            around 0.5f s
	 * @param dontChangeXRotation
	 *            if this is false, the mesh will also change the rotation x
	 *            value, otherwise only the z value to face to the camera
	 */
	public AnimationFaceToObj(Obj targetCamera, float updateDelay, boolean dontChangeXRotation) {
		mTargetObject = targetCamera;

		mUpdateDelay = updateDelay;
		mTargetObjectPosition = mTargetObject.getPosition();
		mDontChangeXRotation = dontChangeXRotation;
	}

	/**
	 * Constructor.
	 * 
	 * @param targetObj
	 *            {@link Obj}
	 * @param updateDelay
	 *            The amount of time to update. Normally should be .5fs
	 */
	public AnimationFaceToObj(Obj targetObj, float updateDelay) {
		this(targetObj, updateDelay, true);
	}

	/**
	 * Constructor.
	 * 
	 * @param targetObj
	 *            {@link Obj}
	 */
	public AnimationFaceToObj(Obj targetObj) {
		this(targetObj, DEFAULT_UPDATE_DELAY, true);
	}

	/**
	 * @param targetObj
	 *            {@link Obj}
	 * @param updateDelay
	 *            0.5f
	 * @param adjustmentVec
	 *            Adjustment vector
	 */
	public AnimationFaceToObj(Obj targetObj, float updateDelay, Vec adjustmentVec) {
		this(targetObj, updateDelay);
		mAdjustmentVec = adjustmentVec;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		timeDelta = Math.abs(timeDelta);
		mLastUpdateAway += timeDelta;
		if (mLastUpdateAway > mUpdateDelay) {
			updateRotation(parent);
			mLastUpdateAway = 0;
		}
		if (mDontChangeXRotation) {
			Vec.morphToNewAngleVec(mRotationVec, 0, 0, mNewRotationVec.z, timeDelta);
		} else {
			Vec.morphToNewAngleVec(mRotationVec, mNewRotationVec.x, mNewRotationVec.y, mNewRotationVec.z, timeDelta);
		}
		return true;
	}

	private Vec mAbsolutePosition = new Vec();

	private void updateRotation(Updateable parent) {
		if (parent instanceof MeshComponent) {
			mAbsolutePosition.setToZero();
			((MeshComponent) parent).getAbsoluteMeshPosition(mAbsolutePosition);
			// Log.d("face camera animation", "mesh position: "+pos);
			mNewRotationVec.toAngleVec(mAbsolutePosition, mTargetObjectPosition);
			/*
			 * substract 90 from the x value becaute calcanglevec returns 90 if
			 * the rotation should be the horizon (which would mean no object
			 * rotation)
			 */
			mNewRotationVec.x -= ADJUST_OBJECT_ROTATION;
			mNewRotationVec.z *= -1;
		}
	}

	@Override
	public void render(GL10 gl, Renderable parent) {

		gl.glRotatef(mRotationVec.z, 0, 0, 1);
		gl.glRotatef(mRotationVec.x, 1, 0, 0);
		gl.glRotatef(mRotationVec.y, 0, 1, 0);

		if (mAdjustmentVec != null) {
			/*
			 * if an adjustment vector is set this adjustment has to be done
			 * AFTER the rotation to be easy to use, see constructor for infos
			 * about adjustment
			 */
			gl.glRotatef(mAdjustmentVec.x, 1, 0, 0);
			gl.glRotatef(mAdjustmentVec.z, 0, 0, 1);
			gl.glRotatef(mAdjustmentVec.y, 0, 1, 0);
		}
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

}
