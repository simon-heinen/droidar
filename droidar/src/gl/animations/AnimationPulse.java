package gl.animations;

import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worlddata.Updateable;
import worlddata.Visitor;

/**
 * Animation the will pulse the object.
 */
public class AnimationPulse extends GLAnimation {

	private final float mSpeed;
	private final Vec mLowerEnd;
	private final Vec mUperEnd;
	private Vec mCurrentScale;
	private Vec mTargetScale;
	private float mAccuracy;
	private boolean mMode; // true=morph to uperEnd false=morph to lowerEnd

	/**
	 * @param speed
	 *            1 to 10
	 * @param lowerEnd
	 *            The lower end to pulse.
	 * @param uperEnd
	 *            The upper end to pulse.
	 * @param accuracy
	 *            should be 0.2f (or something between 0.01f and 0.5f)
	 */
	public AnimationPulse(float speed, Vec lowerEnd, Vec uperEnd, float accuracy) {
		this.mSpeed = speed;
		this.mAccuracy = accuracy;
		this.mLowerEnd = lowerEnd.copy();
		this.mUperEnd = uperEnd.copy();
		this.mCurrentScale = mLowerEnd.copy();
		this.mTargetScale = mUperEnd.copy();
		this.mMode = true;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		Vec.morphToNewVec(mCurrentScale, mTargetScale, timeDelta * mSpeed);
		final Vec distance = Vec.sub(mCurrentScale, mTargetScale);
		if ((Vec.abs(distance.x) < mAccuracy)
				&& (Vec.abs(distance.y) < mAccuracy)
				&& (Vec.abs(distance.z) < mAccuracy)) {
			if (mMode) {
				mMode = false;
				mTargetScale = mUperEnd;
			} else {
				mMode = true;
				mTargetScale = mLowerEnd;
			}
		}
		return true;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		gl.glScalef(mCurrentScale.x, mCurrentScale.y, mCurrentScale.z);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

}
