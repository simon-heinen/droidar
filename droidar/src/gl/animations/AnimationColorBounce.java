package gl.animations;

import gl.Color;
import gl.ObjectPicker;
import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worlddata.Updateable;
import worlddata.Visitor;

/**
 * Animation to bounce between colors.
 * 
 * @author rvieras
 * 
 */
public class AnimationColorBounce extends GLAnimation {

	private float mSpeed;
	private Color mLowerColor;
	private Color mUpperColor;
	private float mAccur;
	private Color mCurrentColor;
	private Color mTargetColor;
	private boolean mMode; // true = upperEnd, false = lowerEnd

	/**
	 * @param speed
	 *            should be 0.5 to
	 * @param startColor
	 *            The starting color
	 * @param endColor
	 *            The ending color
	 * @param accur
	 *            0.2f ood value to start
	 */
	public AnimationColorBounce(float speed, Color startColor, Color endColor,
			float accur) {
		mSpeed = speed;
		mCurrentColor = startColor.copy();
		mTargetColor = endColor.copy();
		mLowerColor = startColor.copy();
		mUpperColor = endColor.copy();
		mAccur = accur;
		mMode = true;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {

		final Vec distance = Color.morphToNewColor(mCurrentColor,
				mTargetColor, timeDelta * mSpeed);

		if ((Vec.abs(distance.x) < mAccur) && (Vec.abs(distance.y) < mAccur)
				&& (Vec.abs(distance.z) < mAccur)) {
			if (mMode) {
				mMode = false;
				mTargetColor = mLowerColor;
			} else {
				mMode = true;
				mTargetColor = mUpperColor;
			}
		}
		return true;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {

		if (!ObjectPicker.readyToDrawWithColor) {
			gl.glColor4f(mCurrentColor.red, mCurrentColor.green,
					mCurrentColor.blue, mCurrentColor.alpha);
		}

	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

}
