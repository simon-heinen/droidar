package de.rwth.setups;

import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import util.Calculus;
import worldData.RenderableEntity;
import worldData.Updateable;
import worldData.Visitor;
import android.util.Log;

public class TimeModifier implements RenderableEntity {

	private static final String LOG_TAG = "TimeModifier";
	private static final float TRESHOLD = 0.001f;
	private static final float DEFAULT_ADJUSTMENT_SPEED = 4;
	private RenderableEntity myChild;
	private float myCurrentFactor;
	private float myNewFactor;
	private float myAdjustmentSpeed;
	private Updateable myParent;

	public TimeModifier(float timeFactor) {
		this(timeFactor, DEFAULT_ADJUSTMENT_SPEED);
	}

	public TimeModifier(float timeFactor, float adjustmentSpeed) {
		myCurrentFactor = timeFactor;
		myNewFactor = timeFactor;
		myAdjustmentSpeed = adjustmentSpeed;
	}

	@Override
	public Updateable getMyParent() {
		return myParent;
	}

	@Override
	public void setMyParent(Updateable parent) {
		myParent = parent;

	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		setMyParent(parent);
		if (different(myCurrentFactor, myNewFactor))
			myCurrentFactor = Calculus.morphToNewValue(timeDelta
					* myAdjustmentSpeed, myNewFactor, myCurrentFactor);
		else
			myCurrentFactor = myNewFactor;
		if (myCurrentFactor == 0)
			return true;
		if (myChild != null)
			return myChild.update(timeDelta * myCurrentFactor, parent);

		Log.e(LOG_TAG, "Child was not set");
		return false;
	}

	private boolean different(float a, float b) {
		return Math.abs(a - b) > TRESHOLD;
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (myChild != null)
			return myChild.accept(visitor);

		Log.e(LOG_TAG, "Child was not set");
		return false;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		if (myChild != null)
			myChild.render(gl, parent);
		else
			Log.e(LOG_TAG, "Child was not set");
	}

	public void setChild(RenderableEntity l) {
		myChild = l;
	}

	public void setTimeFactor(float newTimeFactor) {
		myNewFactor = newTimeFactor;
	}

	public float getTimeFactor() {
		return myNewFactor;
	}

}
