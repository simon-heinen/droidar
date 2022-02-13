package gl.animations;

import gl.Color;
import gl.ObjectPicker;
import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worldData.Updateable;
import worldData.Visitor;

/**
 * TODO what is the difference between this and ColorMorph? what was the intend?
 * 
 * @author Spobo
 * 
 */
public class AnimationAlphaBlend extends GLAnimation {

	private float mySpeed;
	private Color myLowerColor;
	private Color myUpperColor;
	private float myAccur;
	private Color myCurrentColor;
	private Color myTargetColor;
	/**
	 * true = upperEnd, false = lowerEnd
	 */
	private boolean mode;

	/**
	 * @param speed
	 *            should be 0.5 to
	 * @param startColor
	 * @param endColor
	 * @param accur
	 *            0.2f ood value to start
	 */
	public AnimationAlphaBlend(float speed, Color startColor, Color endColor,
			float accur) {
		mySpeed = speed;
		myCurrentColor = startColor.copy();
		myTargetColor = endColor.copy();
		myLowerColor = startColor.copy();
		myUpperColor = endColor.copy();
		myAccur = accur;
		mode = true;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		final Vec distance = Color.morphToNewColor(myCurrentColor,
				myTargetColor, timeDelta * mySpeed);

		if ((Vec.abs(distance.x) < myAccur) && (Vec.abs(distance.y) < myAccur)
				&& (Vec.abs(distance.z) < myAccur)) {
			if (mode) {
				mode = false;
				myTargetColor = myLowerColor;
			} else {
				mode = true;
				myTargetColor = myUpperColor;
			}
		}
		return true;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		if (!ObjectPicker.readyToDrawWithColor)
			gl.glColor4f(myCurrentColor.red, myCurrentColor.green,
					myCurrentColor.blue, myCurrentColor.alpha);

	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}
}
