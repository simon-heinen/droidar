package gui;

import system.TouchEventInterface;
import util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class CustomGestureListener extends
		GestureDetector.SimpleOnGestureListener {

	private TouchEventInterface myListener;

	public CustomGestureListener(TouchEventInterface listener) {
		myListener = listener;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		/*
		 * return true so that the GestureListener nows he can go on and detect
		 * the gesture..
		 */
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		myListener.onScroll(e1, e2, distanceX, distanceY);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Log.d("GUI", "onLongPress");
		myListener.onLongPress(e);
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Log.d("GUI", "onSingeTab");
		myListener.onSingleTab(e);
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Log.d("GUI", "onDoubleTab");
		myListener.onDoubleTap(e);
		return true;
	}

}
