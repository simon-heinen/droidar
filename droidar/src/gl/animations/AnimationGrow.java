package gl.animations;

import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import worlddata.UpdateTimer;
import worlddata.Updateable;
import worlddata.Visitor;

/**
 * Animation to show a growing animation.
 */
public class AnimationGrow extends GLAnimation {
	private float mGrothSize;
	private float mGrothFactor;
	private UpdateTimer mStopCondition;

	/**
	 * Constructor.
	 * 
	 * @param timeTillFullGrothInSeconds
	 *            The amount of time to fully grow the animation in seconds.
	 */
	public AnimationGrow(float timeTillFullGrothInSeconds) {
		mStopCondition = new UpdateTimer(timeTillFullGrothInSeconds, null);
		mGrothFactor = 1 / timeTillFullGrothInSeconds;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		gl.glScalef(mGrothSize, mGrothSize, mGrothSize);
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		if (mStopCondition.update(timeDelta, parent)) {
			return false;
		}
		mGrothSize += mGrothFactor * timeDelta;
		if (mGrothSize > 1) {
			mGrothSize = 1;
		}
		return true;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}
}
