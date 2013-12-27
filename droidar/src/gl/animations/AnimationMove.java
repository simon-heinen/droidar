package gl.animations;

import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worlddata.Updateable;
import worlddata.Visitor;

/**
 * Animation to show movement.
 */
public class AnimationMove extends GLAnimation {

	private static final float MIN_DISTANCE = 0.01f;
	private float mTimeToMove;
	private Vec mRelativeTargetPos;
	private Vec mPos;
	private boolean mDone;

	/**
	 * Constructor.
	 * 
	 * @param timeToMove
	 *            The amount of time it will take the object to move to
	 * @param relativeTargetPos}
	 * @param relativeTargetPos
	 *            The position to move to
	 */
	public AnimationMove(float timeToMove, Vec relativeTargetPos) {
		this.mTimeToMove = timeToMove;
		this.mRelativeTargetPos = relativeTargetPos;
		mPos = new Vec();
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		gl.glTranslatef(mPos.x, mPos.y, mPos.z);
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		if (!mDone) {
			Vec.morphToNewVec(mPos, mRelativeTargetPos, timeDelta / mTimeToMove);
			if (Vec.distance(mPos, mRelativeTargetPos) < MIN_DISTANCE) {
				mDone = true;
			}
		}
		return true;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}
}
