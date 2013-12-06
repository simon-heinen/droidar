package listeners.eventManagerListeners;

import android.view.MotionEvent;
/**
 * Listen for touch move events.
 */
public interface TouchMoveListener {

	/**
	 * @param e1 - {@link android.view.MotionEvent} first motion
	 * @param e2 - {@link android.view.MotionEvent} second motion
	 * @param screenDeltaX - change in x
	 * @param screenDeltaY - change in y 
	 * @return - process successful
	 */
	boolean onTouchMove(MotionEvent e1, MotionEvent e2,
			float screenDeltaX, float screenDeltaY);

	/**
	 * @return process successful
	 */
	boolean onReleaseTouchMove();
}
