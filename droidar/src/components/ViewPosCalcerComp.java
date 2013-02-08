package components;

import gl.GLCamera;
import util.Vec;
import worldData.Entity;
import worldData.UpdateTimer;
import worldData.Updateable;
import worldData.Visitor;
import android.util.Log;

public abstract class ViewPosCalcerComp implements Entity {

	private static final String LOG_TAG = "ViewPosCalcerComp";
	private GLCamera myCamera;
	private int myMaxDistance;
	private UpdateTimer timer;

	/**
	 * @param camera
	 * @param maxDistance
	 *            suggestion: around 20 to 100
	 * @param updateSpeed
	 *            e.g. every 0.1f seconds
	 */
	public ViewPosCalcerComp(GLCamera camera, int maxDistance, float updateSpeed) {
		myCamera = camera;
		myMaxDistance = maxDistance;
		timer = new UpdateTimer(updateSpeed, null);
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {

		if (timer.update(timeDelta, this)) {

			Vec targetVec = myCamera
					.getPositionOnGroundWhereTheCameraIsLookingAt();

			if (targetVec.getLength() > myMaxDistance) {
				targetVec.setLength(myMaxDistance);
			}

			onPositionUpdate(parent, targetVec);
		}
		return true;
	}

	@Override
	public Updateable getMyParent() {
		Log.e(LOG_TAG, "Get parent called which is not "
				+ "implemented for this component!");
		return null;
	}

	@Override
	public void setMyParent(Updateable parent) {
		// can't have children so the parent does not have to be stored
		Log.e(LOG_TAG, "Set parent called which is not "
				+ "implemented for this component!");
	}

	/**
	 * This will be called in constant time intervals
	 * 
	 * @param parent
	 * @param targetVec
	 */
	public abstract void onPositionUpdate(Updateable parent, Vec targetVec);

	@Override
	public boolean accept(Visitor visitor) {
		return true;
	}

}
