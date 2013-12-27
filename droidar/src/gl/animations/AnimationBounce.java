package gl.animations;

import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worlddata.Updateable;
import worlddata.Visitor;

/**
 * Animation to bounce.
 */
public class AnimationBounce extends GLAnimation {

	private final int mSpeed;
	private final Vec mDEnd;
	private final Vec mUEnd;
	private Vec mCurrentPos;
	private Vec mTargetPos;
	private float mAccuracy;
	private int mMode; // 1=morph to uperEnd 0=morph to lowerEnd

	/**
	 * @param speed
	 *            The bounce speed
	 * @param relativeLowerEnd
	 *            lower bound to bounce too
	 * @param relativeUperEnd
	 *            upper bound to bounce too
	 * @param accuracy
	 *            should be 0.2f (or something between 0.01f and 0.5f)
	 */
	public AnimationBounce(int speed, Vec relativeLowerEnd,
			Vec relativeUperEnd, float accuracy) {
		mSpeed = speed;
		mAccuracy = accuracy;
		mDEnd = relativeLowerEnd.copy();
		mUEnd = relativeUperEnd.copy();
		mCurrentPos = mDEnd.copy();
		mTargetPos = mUEnd.copy();
		mMode = 1;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		Vec.morphToNewVec(mCurrentPos, mTargetPos, timeDelta * mSpeed);
		final Vec distance = Vec.sub(mCurrentPos, mTargetPos);
		if ((Vec.abs(distance.x) < mAccuracy)
				&& (Vec.abs(distance.y) < mAccuracy)
				&& (Vec.abs(distance.z) < mAccuracy)) {
			if (mMode == 0) {
				mMode = 1;
				mTargetPos = mUEnd;
			} else {
				mMode = 0;
				mTargetPos = mDEnd;
			}
		}
		return true;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		gl.glTranslatef(mCurrentPos.x, mCurrentPos.y, mCurrentPos.z);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}
}
