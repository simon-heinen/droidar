package actions;

import geo.GeoUtils;
import system.EventManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.rwth.R;

/**
 * Action to wait for a valid GPS with good accuracy before drawing.
 */
public abstract class ActionWaitForAccuracy extends Action {

	private static final String TEXT_DIALOG_TITLE = "Do you want to cancel the accuracy detection?";

	private static final String TEXT_POSITION_ACCURACY = "Position Accuracy ";

	private static final String TEXT_SKIP_ACCURACY_DETECTION = "Skip accuracy detection (not recomended!)";

	// 1 minutes in ms:
	private static final long MAX_TIME_SINCE_LAST_UPDATE_IN_MS = 1000 * 60 * 1;

	private static final String LOG_TAG = "ActionWaitForAccuracy";

	private float mCurrentAccuracy;
	private float mMinAccuracy;
	private boolean mFirstTimeReached = false;

	private int mMaxPosUpdateCount;

	private int mStepCounter = 0;

	private Activity mActivity;

	private TextView mAccText;

	private ProgressBar mSteps;

	private View mViewContainer;

	private Button mWarningText;

	/**
	 * @param context
	 *            {@link Context}
	 * @param minAccuracy
	 *            should be >= 25m
	 * @param maxPosUpdateCount
	 *            The max number of update events before the position should be
	 *            accurate enough (something around 6)
	 */
	public ActionWaitForAccuracy(Activity context, float minAccuracy,
			int maxPosUpdateCount) {
		mActivity = context;
		mMinAccuracy = minAccuracy;
		mMaxPosUpdateCount = maxPosUpdateCount;
		analyseInitLocation(GeoUtils.getCurrentLocation(context));
	}

	private void analyseInitLocation(Location l) {
		if (l != null) {
			mCurrentAccuracy = l.getAccuracy();
			long passedTime = System.currentTimeMillis() - l.getTime();
			Log.i(LOG_TAG, "Last known pos accuracy=" + mCurrentAccuracy);
			Log.i(LOG_TAG, "Last known pos age=" + (passedTime / 1000f / 10f)
					+ " minutes");
			if (passedTime <= MAX_TIME_SINCE_LAST_UPDATE_IN_MS) {
				onLocationChanged(l);
			} else {
				Log.i(LOG_TAG, "Last known pos age was to old to use it "
						+ "as a current position, will now wait "
						+ "for position signal");
				mCurrentAccuracy = 1000; // 1000m
			}
		} else {
			GeoUtils.enableLocationProvidersIfNeeded(mActivity);
		}
	}

	@Override
	public boolean onLocationChanged(Location l) {
		Log.d(LOG_TAG, "Current signal accuracy=" + l.getAccuracy());
		Log.d(LOG_TAG, "Minimum needed accuracy=" + mMinAccuracy);
		Log.d(LOG_TAG, "Current pos update count=" + mStepCounter);
		Log.d(LOG_TAG, "Max pos updates=" + mMaxPosUpdateCount);
		mStepCounter++;
		mCurrentAccuracy = l.getAccuracy();
		updateUI();
		if (((mCurrentAccuracy != 0) && (mCurrentAccuracy <= mMinAccuracy))
				|| (mStepCounter >= mMaxPosUpdateCount)) {
			callFirstTimeAccReachedIfNotYetCalled(l);
			hideUI();
			return false;
		}
		return true;
	}

	private void callFirstTimeAccReachedIfNotYetCalled(Location location) {
		if (!mFirstTimeReached) {
			mFirstTimeReached = true;
			Log.d(LOG_TAG, "Required accuracy was reached!");
			minAccuracyReachedFirstTime(location, this);
		} else {
			Log.w(LOG_TAG, "callFirstTimeAccReachedIfNotYetCalled was "
					+ "called more then one time! This action should "
					+ "be removed once the accuracy was reached!");
		}
	}

	/**
	 * @param location
	 *            The {@link Location} object that was accurate enough
	 * @param a
	 *            the {@link ActionWaitForAccuracy} object which can be used to
	 *            remove it from the {@link EventListenerGroup} it was contained
	 *            in (e.g. the {@link EventManager#onLocationChangedList})
	 */
	public abstract void minAccuracyReachedFirstTime(Location location,
			ActionWaitForAccuracy a);

	public View getView() {
		mViewContainer = View.inflate(mActivity,
				R.layout.action_wait_for_accuracy_view, null);
		mAccText = (TextView) mViewContainer.findViewById(R.id.awfa_accText);
		mWarningText = (Button) mViewContainer.findViewById(R.id.awfa_warning);
		mWarningText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (GeoUtils.enableGPS(mActivity)) {
					mWarningText.setVisibility(View.GONE);
					waitSomeSecondsAndThenRegisterForGPSEvents();
				}
			}

		});

		ImageView i = (ImageView) mViewContainer.findViewById(R.id.awfa_image);
		i.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSkipPositionDetectionDialog();
			}

		});

		mSteps = (ProgressBar) mViewContainer.findViewById(R.id.awfa_steps);

		showDebugInfosAboutTheUiElements();
		updateUI();
		return mViewContainer;
	}

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

	private void showDebugInfosAboutTheUiElements() {
		Log.d(LOG_TAG, "mViewContainer=" + mViewContainer);
		Log.d(LOG_TAG, "   > mAccText=" + mAccText);
		Log.d(LOG_TAG, "   > mWarningText=" + mWarningText);
		Log.d(LOG_TAG, "   > mSteps=" + mSteps);
		Log.d(LOG_TAG, "   > mStepCounter=" + mStepCounter);
	}

	private void showSkipPositionDetectionDialog() {
		final Dialog dialog = new Dialog(mActivity);
		Button b = new Button(mActivity);
		b.setText(TEXT_SKIP_ACCURACY_DETECTION);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(LOG_TAG, "Trying to skip accuracy detection");
				callFirstTimeAccReachedIfNotYetCalled(GeoUtils
						.getCurrentLocation(mActivity));
				hideUI();
				dialog.dismiss();
			}
		});
		dialog.setContentView(b);
		dialog.setTitle(TEXT_DIALOG_TITLE);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	private void updateUI() {
		if ((mAccText != null) && (mSteps != null) && (mWarningText != null)) {
			mActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mAccText.setText(TEXT_POSITION_ACCURACY
							+ (int) ((mMinAccuracy / mCurrentAccuracy) * 100)
							+ "%");
					mSteps.setMax(mMaxPosUpdateCount);
					mSteps.setProgress(mStepCounter);
					showWarningIfGPSOff();
				}

			});
		}
	}

	private void hideUI() {
		if (mViewContainer != null) {
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.i(LOG_TAG, "Setting view container to invisible");
					mViewContainer.setVisibility(View.GONE);
				}
			});
		}
	}

	private void showWarningIfGPSOff() {
		if (GeoUtils.isGPSDisabled(mActivity)) {
			Log.d(LOG_TAG, "GPS disabled!");
			mWarningText.setVisibility(View.VISIBLE);
			mWarningText.setText("Enable GPS");
		} else {
			Log.d(LOG_TAG, "GPS enabled!");
			mWarningText.setVisibility(View.GONE);
		}
	}
}
