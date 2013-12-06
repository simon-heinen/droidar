package listeners.eventManagerListeners;

import android.view.MotionEvent;
/**
 * Listen for events from the track ball.  
 */
public interface TrackBallEventListener {
	/**
	 * @param x -  x axis
	 * @param y - y axis
	 * @param event - {@link android.view.MotionEvent}
	 * @return - process successful
	 */
	boolean onTrackballEvent(float x, float y, MotionEvent event);
}
