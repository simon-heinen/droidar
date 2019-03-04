package system;

import gui.CustomGestureListener;
import android.view.MotionEvent;

import commands.Command;

/**
 * use this in combination with {@link CustomGestureListener}
 * 
 * @author Spobo
 * 
 */
public interface TouchEventInterface {

	void onLongPress(MotionEvent e);

	void onSingleTab(MotionEvent e);

	void onDoubleTap(MotionEvent e);

	public Command getOnTabCommand();

	public Command getOnLongPressCommand();

	public Command getOnDoubleTabCommand();

	public void setOnTabCommand(Command c);

	public void setOnLongPressCommand(Command c);

	/**
	 * double tab commands should only be in situations when not too many other
	 * tasks are running because otherwise it might be not very accurate and
	 * often a LongPress or two singeTab events will be fired instead of this
	 * doubleTab command
	 * 
	 * @param c
	 */
	public void setOnDoubleTabCommand(Command c);

	/**
	 * see
	 * {@link CustomGestureListener#onScroll(MotionEvent, MotionEvent, float, float)}
	 */
	void onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY);

}
