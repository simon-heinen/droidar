package gl.animations;

import gl.Color;
import gl.HasColor;
import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import util.Log;
import util.Vec;
import worlddata.Updateable;
import worlddata.Visitor;

/**
 * Animation to change color.
 */
public class AnimationColorMorph extends GLAnimation {

	private static final float MIN_DISTANCE = 0.001f;
	private float mDurationInMS;
	private Color mTargetColor;

	/**
	 * Constructor.
	 * 
	 * @param durationInMS
	 *            The amount of time it takes to change the object color
	 * @param targetColor
	 *            The target color that the object will turn too
	 */
	public AnimationColorMorph(float durationInMS, Color targetColor) {
		mDurationInMS = durationInMS;
		mTargetColor = targetColor;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {

		if (parent instanceof HasColor) {
			Vec colorDistance = Color.morphToNewColor(
((HasColor) parent).getColor(), mTargetColor, timeDelta / mDurationInMS);
			if (!(colorDistance.getLength() > MIN_DISTANCE)) {
				Log.d("NodeListener", "color morph finnished for " + parent);
			}
			return (colorDistance.getLength() > MIN_DISTANCE);
		}
		return false;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}
}
