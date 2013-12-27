package gl.animations;

import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worlddata.Updateable;
import worlddata.Visitor;

/**
 * This animation simulated the movement of a metronome. For more details see
 * the constructor
 * 
 * @author Spobo
 * 
 */
public class AnimationSwingRotate extends GLAnimation {

	private final float mSpeed;
	private final Vec mDEnd;
	private final Vec mUEnd;
	private Vec mCurrentPos;
	private Vec mTargetPos;
	private float mAccuracy;
	private int mMode; // 1=morph to uperEnd 0=morph to lowerEnd

	/**
	 * this works as an metronome, it pendels from lowerEnd vector to upperEnd
	 * vetor. could be combined with {@link AnimationBounce}
	 * 
	 * @param speed
	 *            20-40
	 * @param lowerEnd
	 *            new Vec(135, 0, 0)
	 * 
	 * @param upperEnd
	 *            new Vec(225, 0, 0)
	 * @param accuracy
	 *            should be 0.2f (or something between 0.01f and 0.5f)
	 */
	public AnimationSwingRotate(float speed, Vec lowerEnd, Vec upperEnd,
			float accuracy) {
		this.mSpeed = speed;
		this.mAccuracy = accuracy;
		this.mDEnd = lowerEnd.copy();
		this.mUEnd = upperEnd.copy();
		this.mCurrentPos = mDEnd.copy();
		this.mTargetPos = mUEnd.copy();
		this.mMode = 1;
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
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		gl.glRotatef(mCurrentPos.z, 0, 0, 1);
		gl.glRotatef(mCurrentPos.x, 1, 0, 0);
		gl.glRotatef(mCurrentPos.y, 0, 1, 0);
	}
}
