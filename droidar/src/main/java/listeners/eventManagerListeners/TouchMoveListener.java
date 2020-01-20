package listeners.eventManagerListeners;

import android.view.MotionEvent;

public interface TouchMoveListener {

	boolean onTouchMove(MotionEvent e1, MotionEvent e2,
                        float screenDeltaX, float screenDeltaY);

	boolean onReleaseTouchMove();
}
