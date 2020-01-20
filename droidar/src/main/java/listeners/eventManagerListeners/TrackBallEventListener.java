package listeners.eventManagerListeners;

import android.view.MotionEvent;

public interface TrackBallEventListener {
	boolean onTrackballEvent(float x, float y, MotionEvent event);
}
