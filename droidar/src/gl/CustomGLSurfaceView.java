package gl;

import gui.CustomGestureListener;

import java.util.ArrayList;
import java.util.List;

import listeners.eventManagerListeners.TouchMoveListener;
import system.EventManager;
import system.Setup;
import system.TouchEventInterface;
import util.Log;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;

import commands.Command;

/**
 * This is the custom {@link GLSurfaceView} which is used to render the OpenGL
 * content.
 * 
 * @author Spobo
 * 
 */
public class CustomGLSurfaceView extends GLSurfaceView implements
		TouchEventInterface {

	private static final long TOUCH_INPUT_SLEEP_TIME = 20;

	/**
	 * enables the opengl es debug output but reduces the frame-rate a lot!
	 */
	private static final boolean DEBUG_OUTPUT_ENABLED = false;

	private static final String LOG_TAG = "CustomGLSurfaceView";

	private float viewWidth = 320;

	private float viewHeight = 320;

	private List<TouchMoveListener> onTouchListeners;

	private GestureDetector myGestureDetector;

	private int startPosOnScreenWidth;
	private int startPosOnScreenHeigth;

	private boolean LANDSCAPE_MODE = true;

	public CustomGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initGLSurfaceView(context);
	}

	public CustomGLSurfaceView(Context context) {
		super(context);
		initGLSurfaceView(context);
	}

	private void initGLSurfaceView(Context context) {
		if (DEBUG_OUTPUT_ENABLED) {
			// Turn on error-checking and logging
			setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
		}

		int screenOrientation = Setup.getScreenOrientation();
		if (screenOrientation == Surface.ROTATION_90
				|| screenOrientation == Surface.ROTATION_270) {
			LANDSCAPE_MODE = true;
		} else {
			LANDSCAPE_MODE = false;
		}

		this.setFocusableInTouchMode(true);
		myGestureDetector = new GestureDetector(context,
				new CustomGestureListener(this));

		// Set 8888 pixel format because that's required for
		// a translucent window:
		this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		// Use a surface format with an Alpha channel:
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		this.setZOrderMediaOverlay(true);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		int[] startPosOnScreen = { 0, 0 };
		this.getLocationInWindow(startPosOnScreen);

		if (LANDSCAPE_MODE) {
			viewHeight = w;
			viewWidth = h;
			startPosOnScreenWidth = startPosOnScreen[1];
			startPosOnScreenHeigth = startPosOnScreen[0];
		} else {
			viewHeight = h;
			viewWidth = w;
			startPosOnScreenWidth = startPosOnScreen[0];
			startPosOnScreenHeigth = startPosOnScreen[1];
		}
		Log.i(LOG_TAG, "OpenGL view size:");
		Log.i(LOG_TAG, "  > startPosOnScreenx=" + startPosOnScreenWidth);
		Log.i(LOG_TAG, "  > startPosOnScreeny=" + startPosOnScreenHeigth);
		Log.i(LOG_TAG, "  > viewHeight=" + viewHeight);
		Log.i(LOG_TAG, "  > viewWidth=" + viewWidth);
	}

	public void addOnTouchMoveListener(TouchMoveListener onTouchListener) {
		if (onTouchListeners == null) {
			this.onTouchListeners = new ArrayList<TouchMoveListener>();
		}
		this.onTouchListeners.add(onTouchListener);
	}

	@Override
	public boolean dispatchTrackballEvent(MotionEvent event) {
		if (EventManager.getInstance().onTrackballEvent(event)) {
			return true;
		}
		return super.onTrackballEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		myGestureDetector.onTouchEvent(event);

		requestFocus();

		try {
			// Sleep 20ms to not flood the thread
			Thread.sleep(TOUCH_INPUT_SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (onTouchListeners != null) {
				for (int i = 0; i < onTouchListeners.size(); i++) {
					onTouchListeners.get(i).onReleaseTouchMove();
				}

			}
		}
		return true;
	}

	@Override
	public Command getOnDoubleTabCommand() {
		return null;
	}

	@Override
	public Command getOnLongPressCommand() {
		return null;
	}

	@Override
	public Command getOnTabCommand() {
		return null;
	}

	private float getOpenGlY(float y) {
		// first invert y values because the 0,0 point of the opengl view is
		// upper left and the 0,0 point of the sensor event ist lower left
		if (LANDSCAPE_MODE) {
			Log.i(LOG_TAG, "y=" + (viewWidth - y));
			return viewWidth - y;
		} else {
			Log.i(LOG_TAG, "y=" + (viewHeight - y));
			return viewHeight - y;
		}
	}

	private float getOpenGlX(float x) {
		Log.i(LOG_TAG, "x=" + x);
		return x;
	}

	@Override
	public void onDoubleTap(MotionEvent e) {
		ObjectPicker.getInstance().setDoubleClickPosition(getOpenGlX(e.getX()),
				getOpenGlY(e.getY()));
	}

	@Override
	public void onLongPress(MotionEvent e) {
		ObjectPicker.getInstance().setLongClickPosition(getOpenGlX(e.getX()),
				getOpenGlY(e.getY()));
	}

	@Override
	public void onSingleTab(MotionEvent e) {
		ObjectPicker.getInstance().setClickPosition(getOpenGlX(e.getX()),
				getOpenGlY(e.getY()));
	}

	@Override
	public void setOnDoubleTabCommand(Command c) {
	}

	@Override
	public void setOnLongPressCommand(Command c) {
	}

	@Override
	public void setOnTabCommand(Command c) {
	}

	@Deprecated
	public void addOnTouchMoveAction(TouchMoveListener action) {
		Log.d("EventManager", "Adding onTouchMoveAction");
		addOnTouchMoveListener(action);
	}

	@Override
	public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (onTouchListeners != null) {
			for (int i = 0; i < onTouchListeners.size(); i++) {
				onTouchListeners.get(i).onTouchMove(e1, e2, distanceX,
						distanceY);
			}
		}
	}

}
