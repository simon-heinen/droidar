package listeners.eventManagerListeners;

import android.view.MotionEvent;

public interface TouchMoveListener {

	public boolean onTouchMove(MotionEvent e1, MotionEvent e2,
			float screenDeltaX, float screenDeltaY);

	public boolean onReleaseTouchMove();
}
