package gl.animations;

import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import worlddata.Updateable;
import worlddata.Visitor;

/**
 * Animation to shrink.
 */
public class AnimationShrink extends GLAnimation {

	private float mGrothSize = 1;
	private float mShrinkFactor;

	/**
	 * Constructor.
	 * 
	 * @param timeTillFullGrothInSeconds
	 *            The time till full size in seconds
	 */
	public AnimationShrink(float timeTillFullGrothInSeconds) {
		mShrinkFactor = 1 / timeTillFullGrothInSeconds;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		gl.glScalef(mGrothSize, mGrothSize, mGrothSize);
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		if (mGrothSize > 0) {
			mGrothSize -= mShrinkFactor * timeDelta;
		} else {
			mGrothSize = 0;
		}
		return true;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}
}
