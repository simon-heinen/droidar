package gl;

import gl.scenegraph.MeshComponent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import listeners.SelectionListener;
import system.Setup;
import util.Log;
import util.Wrapper;

import commands.Command;
import commands.system.CommandDeviceVibrate;

public class ObjectPicker {

	private static final int TOUCH_TAB = 1;
	private static final int TOUCH_DOUBLE_TAB = 2;
	private static final int TOUCH_LONG_PRESS = 3;
	private static final String LOG_TAG = "Object Picker";

	private static ObjectPicker myInstance = new ObjectPicker();

	/**
	 * TODO move this to the renderer to be a little faster?
	 */
	public static boolean readyToDrawWithColor = false;

	/**
	 * TODO solve problem with byte[] to string conversion for comparing. there
	 * might also be a problem with this to string-key-concept because 0 15 10
	 * will be the same key as 0 151 0!
	 */
	private HashMap<String, Wrapper> myObjectLookUpTable = new HashMap<String, Wrapper>();
	public int x = 0;
	public int y = 0;

	private int clickType = 0;
	private Command myFeedbackCommand;

	public void setMyFeedbackCommand(Command myFeedbackCommand) {
		this.myFeedbackCommand = myFeedbackCommand;
	}

	public void pickObject(GL10 gl) {
		readyToDrawWithColor = false;

		ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(4);
		pixelBuffer.order(ByteOrder.nativeOrder());
		gl.glReadPixels(x, y, 1, 1, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
				pixelBuffer);
		byte b[] = new byte[4];
		pixelBuffer.get(b);
		findObjectForValue(b);
	}

	private void findObjectForValue(byte[] b) {
		final String key = "" + b[0] + b[1] + b[2];
		Wrapper wrapper = myObjectLookUpTable.get(key);
		Log.d(LOG_TAG, "Analysis of Pixel at " + x + ", " + y);
		Log.v(LOG_TAG, "   > Pixelvalues: " + key);
		Log.v(LOG_TAG, "   > Picked object: " + wrapper);

		if (wrapper == null && !key.equals("000")) {
			Log.d(LOG_TAG,
					"   > Possible picking problem found! Trying to fix it");
			wrapper = tryToFindCorrectObjectFor(b);
		}

		if (wrapper != null && wrapper.getObject() instanceof SelectionListener) {

			SelectionListener s = (SelectionListener) wrapper.getObject();

			// Log.d("Color Picking", "Selection listener: " + s);
			// Log.d("Color Picking", "s.getOnClickCommand(): " +
			// s.getOnClickCommand());
			// Log.d("Color Picking", "s.getOnDoubleClickCommand(): " +
			// s.getOnDoubleClickCommand());
			// Log.d("Color Picking", "s.getOnLongClickCommand(): " +
			// s.getOnLongClickCommand());
			// Log.d("Color Picking", "Click type: " + clickType);

			switch (clickType) {
			case TOUCH_TAB: {
				Command c = s.getOnClickCommand();
				if (c != null) {
					giveSelectFeedbackIfEnabled();
					c.execute(wrapper);
				}
				break;
			}
			case TOUCH_DOUBLE_TAB: {
				Command c = s.getOnDoubleClickCommand();
				if (c != null) {
					giveSelectFeedbackIfEnabled();
					c.execute(wrapper);
				}
				break;
			}
			case TOUCH_LONG_PRESS: {
				Command c = s.getOnLongClickCommand();
				if (c != null) {
					giveSelectFeedbackIfEnabled();
					c.execute(wrapper);
				}
				break;
			}
			}
			clickType = 0;
		}
	}

	private void giveSelectFeedbackIfEnabled() {
		if (myFeedbackCommand != null)
			myFeedbackCommand.execute();
	}

	private Wrapper tryToFindCorrectObjectFor(byte[] b) {
		/*
		 * The problem with color calculation is, that its done by the gpu (this
		 * is my guess) and different devices will return different values for
		 * the same color. eg on the Motorola Milestone the order has to be
		 * key=b[3] b[2] b[1] and rounding by the gpu seems to be buggy too on
		 * many devices
		 */
		for (int i = -1; i < 2; i++) {
			for (int i2 = -1; i2 < 2; i2++) {
				for (int i3 = -1; i3 < 2; i3++) {
					{
						String k = getKey(b[0] + i, b[1] + i2, b[2] + i3);
						// Log.d("Color Picking", "possible key=" + k);
						Wrapper w = myObjectLookUpTable.get(k);
						if (w != null) {
							Log.d(LOG_TAG, "Solution found. Modifing key to "
									+ k);
							/*
							 * Actually the key isn't modified, the wrapper is
							 * just registered with the other value too, so that
							 * the Wrapper will be found the next time
							 */
							myObjectLookUpTable
									.put(getKey(b[0], b[1], b[2]), w);
							return w;
						}
					}
					{
						String k = getKey(b[2] + i3, b[1] + i2, b[0] + i);
						// Log.d("Color Picking", "possible key=" + k);
						Wrapper w = myObjectLookUpTable.get(k);
						if (w != null) {
							Log.d(LOG_TAG, "Solution found. Modifing key to "
									+ k);
							/*
							 * Actually the key isn't modified, the wrapper is
							 * just registered with the other value too, so that
							 * the Wrapper will be found the next time
							 */
							myObjectLookUpTable
									.put(getKey(b[0], b[1], b[2]), w);
							return w;
						}
					}
				}
			}
		}

		Log.d(LOG_TAG, "No solution for picking problem found :(");
		return null;
	}

	private String getKey(int a, int b, int c) {
		return "" + (byte) a + (byte) b + (byte) c;
	}

	/**
	 * @return the instance of the singelton
	 */
	public static ObjectPicker getInstance() {
		return myInstance;
	}

	public void setClickPosition(float x, float y) {
		clickType = TOUCH_TAB;
		setClick(x, y);
	}

	public void setLongClickPosition(float x, float y) {
		clickType = TOUCH_LONG_PRESS;
		setClick(x, y);
	}

	public void setDoubleClickPosition(float x, float y) {
		clickType = TOUCH_DOUBLE_TAB;
		setClick(x, y);
	}

	private void setClick(float x, float y) {
		this.x = (int) x;
		this.y = (int) y;
		readyToDrawWithColor = true;
	}

	/**
	 * @param info
	 * @param prefferedColor
	 *            if the object has an own color than set the key color near the
	 *            preffered color (would be the myColor attribute of a
	 *            {@link MeshComponent} normaly)
	 * @return the unique color the identify the object later
	 */
	public Color registerMesh(Wrapper info, Color prefferedColor) {
		Color myPickColor = getBestColor(prefferedColor);
		byte[] b = getByteArrayFromColor(myPickColor);
		String key = "" + b[0] + b[1] + b[2];
		Log.v(LOG_TAG, "   > New Color byte[]: {" + b[0] + ", " + b[1] + ", "
				+ b[2] + ", " + b[3] + "}");
		Log.v(LOG_TAG, "   > New Color key: " + key);
		myObjectLookUpTable.put(key, info);
		return myPickColor;
	}

	/**
	 * TODO write better algo? if alpha is set everything is darker as normal
	 * color so consider this
	 * 
	 * @param x
	 * @return
	 */
	private Color getBestColor(Color x) {
		Color c;
		boolean endlessLoop = false;
		if (x != null) {
			c = new Color(x.red, x.green, x.blue, 1);
		} else {
			c = new Color(0, 0, 0.01f, 1);
		}
		while (isAlreadyTaken(c)) {
			if (c.red < 1)
				c.red += 0.01f;
			else if (c.green < 1) {
				c.green += 0.01f;
				c.red = 0;
			} else if (c.blue < 1) {
				c.blue += 0.01f;
				c.red = 0;
				c.green = 0;
			} else if (!endlessLoop) {
				c.blue = 0;
				c.red = 0;
				c.green = 0;
				endlessLoop = true;
			} else {
				Log.e(LOG_TAG,
						"Woot.. All picking colors were taken.. and there are really a lot of colors.. double rainbow");
				return new Color(0, 0, 0, 0);
			}
		}
		return c;
	}

	private boolean isAlreadyTaken(Color c) {
		byte[] b = getByteArrayFromColor(c);
		String key = "" + b[0] + b[1] + b[2];
		if (myObjectLookUpTable.get(key) != null)
			return true;
		return false;
	}

	public static byte[] getByteArrayFromColor(Color c) {
		final byte[] b = new byte[4];
		boolean isOld = Setup.isOldDeviceWhereNothingWorksAsExpected;
		b[0] = floatToByteColorValue(c.red, isOld);
		b[1] = floatToByteColorValue(c.green, isOld);
		b[2] = floatToByteColorValue(c.blue, isOld);
		b[3] = floatToByteColorValue(c.alpha, isOld);
		return b;
	}

	/**
	 * the float value (from 0.0f to 1.0f) has to be mapped to the int value (0
	 * to 255) which then is converted to byte (for example 255=-1 in byte)
	 * 
	 * @param f
	 * @return
	 */
	public static byte floatToByteColorValue(float f, boolean oldDevice) {

		/*
		 * TODO:
		 * 
		 * currently this method does 0.895 -> -28 and opengl returns -27
		 * 
		 * -28 = 0xe4 in signed byte = 228 decimal if you treat the 0xe4 as
		 * unsigned. 228/255 = 0.894 in float
		 * 
		 * fix this and get -27 returned instead of -28
		 */

		if (oldDevice) {
			/*
			 * this is a bug-fix which is necessary due to rounding errors on
			 * older devices another part of this bugfix is the
			 * tryToFindCorrectObjectFor method
			 */
			if (f == 1)
				return -1;
			return (byte) (f * 256f);
		}
		return (byte) (f * 255f);
	}

	/**
	 * @param feedbackCommand
	 *            this command will be executed every time a successful click
	 *            appeared. Use a {@link CommandDeviceVibrate} here e.g.
	 */
	public static void resetInstance(Command feedbackCommand) {
		myInstance = new ObjectPicker();
		myInstance.setMyFeedbackCommand(feedbackCommand);
	}

	/**
	 * 565 format: RRRR RGGG GGGB BBBB <br>
	 * 
	 * @param f
	 * @return
	 */
	public static float rgb565to888(float f) {

		return 1 << 1;
	}

}
