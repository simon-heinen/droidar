package actions;

import geo.GeoUtils;
import system.EventManager;
import android.app.Activity;
import android.app.Dialog;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public abstract class ActionWaitForAccuracy2 extends Action {
	private static final String TEXT_DIALOG_TITLE = "Do you want to cancel the accuracy detection?";
	private static final String TEXT_SKIP_ACCURACY_DETECTION = "Skip accuracy detection (not recomended!)";

	// 1 minutes in ms:
	private static final long MAX_TIME_SINCE_LAST_UPDATE_IN_MS = 1000 * 60 * 1;

	private static final String LOG_TAG = "ActionWaitForAccuracy";

	private float myCurrentAccuracy;
	private float myMinAccuracy;
	private boolean firstTimeReached = false;

	private int myMaxPosUpdateCount;

	private int stepCounter = 0;

	private Activity myActivity;

	/**
	 * @param context
	 * @param minAccuracy
	 *            should be >= 25m
	 * @param maxPosUpdateCount
	 *            The max number of update events before the position should be
	 *            accurate enough (something around 6)
	 */
	public ActionWaitForAccuracy2(Activity context, float minAccuracy,
			int maxPosUpdateCount) {
		myActivity = context;
		myMinAccuracy = minAccuracy;
		myMaxPosUpdateCount = maxPosUpdateCount;
		analyseInitLocation(GeoUtils.getCurrentLocation(context));
	}

	private void analyseInitLocation(Location l) {
		if (l != null) {
			myCurrentAccuracy = l.getAccuracy();
			long passedTime = System.currentTimeMillis() - l.getTime();
			Log.d(LOG_TAG, "Passed time since last location event="
					+ (passedTime / 1000f / 10f) + " minutes");
			if (passedTime <= MAX_TIME_SINCE_LAST_UPDATE_IN_MS)
				onLocationChanged(l);
		} else {
			GeoUtils.enableLocationProvidersIfNeeded(myActivity);
		}
	}

	@Override
	public boolean onLocationChanged(Location l) {
		Log.d(LOG_TAG, "Current signal accuracy=" + l.getAccuracy());
		Log.d(LOG_TAG, "Minimum needed accuracy=" + myMinAccuracy);
		Log.d(LOG_TAG, "Current pos update count=" + stepCounter);
		Log.d(LOG_TAG, "Max pos updates=" + myMaxPosUpdateCount);
		stepCounter++;
		myCurrentAccuracy = l.getAccuracy();
		updateUI(myActivity, (int) (myMinAccuracy / myCurrentAccuracy * 100),
				stepCounter);
		if ((myCurrentAccuracy != 0 && myCurrentAccuracy <= myMinAccuracy)
				|| (stepCounter >= myMaxPosUpdateCount)) {
			callFirstTimeAccReachedIfNotYetCalled(l);
		}
		return true;
	}

	/**
	 * This method is for display purpose only. If the UI does not have to react
	 * on updates do not do anything here
	 * 
	 * @param activity
	 * @param neededAccuracyInPercent
	 * @param numberOfMeasurments
	 */
	public abstract void updateUI(Activity activity,
			int neededAccuracyInPercent, int numberOfMeasurments);

	private void callFirstTimeAccReachedIfNotYetCalled(Location location) {
		if (!firstTimeReached) {
			firstTimeReached = true;
			Log.d(LOG_TAG, "Required accuracy was reached!");
			minAccuracyReachedFirstTime(location, this);
		} else
			Log.w(LOG_TAG, "callFirstTimeAccReachedIfNotYetCalled was "
					+ "called more then one time! This action should "
					+ "be removed once the accuracy was reached!");
	}

	/**
	 * @param location
	 *            The {@link Location} object that was accurate enough
	 * @param a
	 *            the {@link ActionWaitForAccuracy2} object which can be used to
	 *            remove it from the {@link EventListenerGroup} it was contained
	 *            in (e.g. the {@link EventManager#onLocationChangedList})
	 */
	public abstract void minAccuracyReachedFirstTime(Location location,
			ActionWaitForAccuracy2 a);

	private void waitSomeSecondsAndThenRegisterForGPSEvents() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				EventManager.getInstance().registerLocationUpdates();
				onGPSActivatedEvent();
			}
		}).start();

	}

	/**
	 * Override this if you need additional custom behavior as soon as the user
	 * activates GPS
	 */
	public void onGPSActivatedEvent() {
		// on default do nothing
	}

	/**
	 * call this if the user should be able to skip this procedure
	 */
	public void showSkipPositionDetectionDialog() {
		final Dialog dialog = new Dialog(myActivity);
		Button b = new Button(myActivity);
		b.setText(TEXT_SKIP_ACCURACY_DETECTION);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callFirstTimeAccReachedIfNotYetCalled(GeoUtils
						.getCurrentLocation(myActivity));
				dialog.dismiss();
			}
		});
		dialog.setContentView(b);
		dialog.setTitle(TEXT_DIALOG_TITLE);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

}
