package gl;

import gl.animations.AnimationFaceToCamera;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import util.Log;
import util.Vec;
import worldData.Visitor;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * I rapidly changing text (like a distance in meters) should not be created
 * like in {@link GLFactory#newTextObject(String, Vec, Context, GLCamera)}
 * because a new texture would have to be generated each time. This class was
 * designed for such scenarios. It is slower to render for large numbers of
 * {@link GLText} objects (because of the {@link MeshComponent} overhead) but if
 * you have rapidly changing text you should use this class.
 * 
 * @author Spobo
 * 
 */
public class GLText extends MeshComponent {

	private static final float CHAR_DIST = 0.5f;
	private static final float CHAR_SIZE = 1f;
	private static final String LOG_TAG = "GLText";
	private String myText;
	private boolean textLoaded;
	private Context myContext;
	private final HashMap<String, MeshComponent> myTextMap;
	private GLCamera myCamera;

	/**
	 * @param text
	 *            The text that should be displayed. Can be changed with
	 *            {@link GLText#changeTextTo(String)}
	 * @param context
	 * @param textMap
	 *            hte already created characters will be stored in here, so pass
	 *            a new {@link HashMap} or maybe also a already initialized
	 *            {@link HashMap} if you want to use a custom font or something
	 *            similar
	 * @param glCamera
	 *            to allow the text to face the camera
	 */
	public GLText(String text, Context context,
			HashMap<String, MeshComponent> textMap, GLCamera glCamera) {
		super(null);
		myText = text;
		myContext = context;
		myTextMap = textMap;
		myCamera = glCamera;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

	@Override
	public void draw(GL10 gl, Renderable parent) {
		if (!textLoaded) {
			loadText(myText);
			textLoaded = true;
		}
	}

	protected void loadText(String text) {
		int textLength = text.length();
		for (int i = 0; i < textLength; i++) {
			MeshComponent m = loadCharMesh("" + text.charAt(i), i, textLength);
			this.addChild(m);
		}
		this.addAnimation(new AnimationFaceToCamera(myCamera));

	}

	protected MeshComponent loadCharMesh(String s, int charNr, int textLenght) {
		MeshComponent value = loadFromMap(s);
		if (value == null) {
			value = createNewCharMesh(s);
			registerValueInMap(s, value);
		} else {
			Log.d(LOG_TAG, "Char allready loaded: " + s);
		}
		// to allow positioning wrap the result:
		float pos = charNr * -CHAR_DIST;
		pos -= textLenght / 2 * -CHAR_DIST;
		MeshComponent box = new Shape(null, new Vec(pos, 0, 0));
		box.addChild(value);
		return box;
	}

	private void registerValueInMap(String key, MeshComponent value) {
		if (myTextMap != null) {
			myTextMap.put(key, value);
		}
	}

	private MeshComponent loadFromMap(String key) {
		if (myTextMap != null) {
			return myTextMap.get(key);
		}
		return null;
	}

	protected MeshComponent createNewCharMesh(String s) {
		MeshComponent mesh = GLFactory.getInstance().newTexturedSquare(
				"char" + s, generateText(s), CHAR_SIZE);
		return mesh;
	}

	protected Bitmap generateText(String s) {
		TextView v = new TextView(myContext);
		v.setTypeface(null, Typeface.BOLD);
		v.setText(s);
		return util.IO.loadBitmapFromView(v);
	}

	public void changeTextTo(String s) {
		myText = s;
		clearChildren();
		textLoaded = false;
	}

}
