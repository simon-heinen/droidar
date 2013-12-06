package actions;

import listeners.EventListener;
import logger.ARLogger;
import util.Log;
import util.Vec;
import worlddata.Updateable;
import android.location.Location;
import android.view.MotionEvent;

/**
 * Base implementation to provide actions to various augmented elements. 
 */
public abstract class Action implements EventListener, Updateable {

	private static final float SMOOTH_ROTATION_SPEED = 2;
	//private static final float SMOOTH_MOTION_SPEED = 3;
	private static final String LOG_TAG = "action event";

	private boolean mAccelNotCatchedOutputFlag;
	private boolean mMagnetNotCatchedOutputFlag;

	@Override
	public boolean onOrientationChanged(float[] values) {
		ARLogger.error(LOG_TAG,
						"onOrientationChanged not catched by defined action: "
						+ this.getClass());
		return false;
	}

	@Override
	public boolean onLocationChanged(Location location) {
		String s = "";
		s += "Time: " + location.getTime() + "\n";
		s += "\t Latitude:  " + location.getLatitude() + "\n";
		s += "\t Longitude: " + location.getLongitude() + "\n";
		s += "\t Altitude:  " + location.getAltitude() + "\n";
		s += "\t Accuracy:  " + location.getAccuracy() + "\n";
		ARLogger.debug(LOG_TAG, s);
		ARLogger.error(LOG_TAG, "changeLocation not catched by defined action: "
				+ this.getClass());
		return false;
	}

	@Override
	public boolean onTouchMove(MotionEvent e1, MotionEvent e2,
			float screenDeltaX, float screenDeltaY) {
		Log.e("action event",
				"onTouch not catched by defined action: " + this.getClass());
		return false;
	}

	@Override
	public boolean onAccelChanged(float[] values) {
		if (!mAccelNotCatchedOutputFlag) {
			mAccelNotCatchedOutputFlag = true;
			ARLogger.error(LOG_TAG,
					"AccelerationValuesChanged not catched by defined action: "
							+ this.getClass());
		}
		return false;
	}

	@Override
	public boolean onMagnetChanged(float[] values) {
		if (!mMagnetNotCatchedOutputFlag) {
			mMagnetNotCatchedOutputFlag = true;
			ARLogger.error(LOG_TAG,
					"MagnetValuesChanged not catched by defined action: "
							+ this.getClass());
		}
		return false;
	}

	@Override
	public boolean onReleaseTouchMove() {
		ARLogger.error(LOG_TAG,
				"onReleaseTouchMove not catched by defined action: "
						+ this.getClass());
		return false;
	}

	@Override
	public boolean onTrackballEvent(float x, float y, MotionEvent event) {
		ARLogger.error(LOG_TAG, "onTrackballEvent not catched by defined action: "
				+ this.getClass());
		return false;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		ARLogger.error(LOG_TAG,
				"update event was not handeld correctly by this action type:"
						+ this.getClass().toString());
		ARLogger.error(LOG_TAG,
				"    > As a reaction to this, the action will now be removed from "
						+ "the update cycle! Impelemnt the update method in the specified "
						+ "action and return true to fix this error!");
		return false;
	}

	@Override
	public void onCamRotationVecUpdate(Vec target, Vec values, float timeDelta) {
		Vec.morphToNewAngleVec(target, values.x, values.y, values.z, timeDelta
				* SMOOTH_ROTATION_SPEED);
	}

}