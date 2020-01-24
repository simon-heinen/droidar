package gl.textures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import util.HasDebugInformation;
import util.ImageTransform;
import util.Log;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

public class TextureManager implements HasDebugInformation {

	public interface TexturReloader {
		Bitmap reload(String textureName);
	}

	private static final String LOG_TAG = "Texture Manager";

	private static final int INIT_TEXTURE_MAP_SIZE = 40;

	private static TextureManager instance = new TextureManager();

	public static boolean recycleBitmapsToFreeMemory = false;

	/**
	 * TODO why am i using ArrayList here...
	 */
	private ArrayList<Texture> newTexturesToLoad;
	private int textureArrayOffset = 0;
	private int[] textureArray = new int[INIT_TEXTURE_MAP_SIZE];
	private HashMap<String, Texture> myTextureMap;
	private TexturReloader myReloader;

	/**
	 * @param target
	 *            The target mesh where the texture will be set to
	 * @param bitmap
	 *            The bitmap that should be used as the texture
	 * @param textureName
	 *            An unique name for the texture. Textures with the same name
	 *            will have the same OpenGL textures!
	 */
	public void addTexture(TexturedRenderData target, Bitmap bitmap,
			String textureName) {

		Texture t = loadTextureFromMap(textureName);

		if (t == null) {
			addTexture(new Texture(target, bitmap, textureName));
		} else {
			Log.d(LOG_TAG, "Texture for " + textureName
					+ " already added, so it will get the same texture id");
			t.addRenderData(target);
		}

	}

	private void addTexture(Texture t) {
		Log.d(LOG_TAG, "   > Texture for " + t.getName()
				+ " not jet added, so it will get a new texture id");
		addTextureToMap(t);
		if (newTexturesToLoad == null) {
			Log.i(LOG_TAG,
					"   > Texture Manage never used before, now its initialized");
			newTexturesToLoad = new ArrayList<Texture>();
		}
		newTexturesToLoad.add(t);
	}

	/**
	 * Dont forget to set {@link TextureManager#recycleBitmapsToFreeMemory} to
	 * true or the reloader wont be used anyway
	 * 
	 * @param reloader
	 */
	public void setTextureReloader(TexturReloader reloader) {
		this.myReloader = reloader;
	}

	private Texture loadTextureFromMap(String textureName) {
		if (myTextureMap == null)
			return null;
		return myTextureMap.get(textureName);
	}

	public void updateTextures(GL10 gl) {
		if (newTexturesToLoad != null && newTexturesToLoad.size() > 0) {
			try {
				while (textureArray.length - textureArrayOffset < newTexturesToLoad
						.size()) {
					Log.d(LOG_TAG, "Resizing textureArray!");
					textureArray = doubleTheArraySize(textureArray);
				}

				// generate and store id numbers in textureArray:
				gl.glGenTextures(newTexturesToLoad.size(), textureArray,
						textureArrayOffset);
				int newtextureArrayOffset = newTexturesToLoad.size();

				for (int i = 0; i < newTexturesToLoad.size(); i++) {

					Texture t = newTexturesToLoad.get(i);
					int newTextureId = textureArray[textureArrayOffset + i];

					t.idArrived(newTextureId);

					gl.glBindTexture(GL10.GL_TEXTURE_2D, newTextureId);

					gl.glTexParameterf(GL10.GL_TEXTURE_2D,
							GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
					gl.glTexParameterf(GL10.GL_TEXTURE_2D,
							GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
					gl.glTexParameterf(GL10.GL_TEXTURE_2D,
							GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
					gl.glTexParameterf(GL10.GL_TEXTURE_2D,
							GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

					gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
							GL10.GL_REPLACE);

					GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, t.getImage(), 0);

					int[] mCropWorkspace = new int[4];
					mCropWorkspace[0] = 0;
					mCropWorkspace[1] = t.getImage().getHeight();
					mCropWorkspace[2] = t.getImage().getWidth();
					mCropWorkspace[3] = -t.getImage().getHeight();

					// TODO maybe not working on any phone because using GL11?
					((GL11) gl)
							.glTexParameteriv(GL10.GL_TEXTURE_2D,
									GL11Ext.GL_TEXTURE_CROP_RECT_OES,
									mCropWorkspace, 0);

					t.recycleImage();

					int error = gl.glGetError();
					if (error != GL10.GL_NO_ERROR) {
						Log.e("SpriteMethodTest", "Texture Load GLError: "
								+ error);
					}

				}
				textureArrayOffset = newtextureArrayOffset;
				newTexturesToLoad.clear();
			} catch (Exception e) {
				showDebugInformation();
				e.printStackTrace();
			}
		}
	}

	private int[] doubleTheArraySize(int[] a) {
		int[] b = new int[a.length * 2];
		// copy old values:
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i];
		}
		return b;
	}

	private void addTextureToMap(Texture t) {
		if (myTextureMap == null)
			myTextureMap = new HashMap<String, Texture>();
		myTextureMap.put(t.getName(), t);
	}

	public static TextureManager getInstance() {
		return instance;
	}

	/**
	 * its important that the used textures have a size powered 2 (2,4,8,16,32..
	 * x 2,4,8..) so resize the bitmap if it has not the correct size
	 * 
	 * @param b
	 * @return
	 */
	public Bitmap resizeBitmapIfNecessary(Bitmap b) {
		int height = b.getHeight();
		int width = b.getWidth();
		int newHeight = getNextPowerOfTwoValue(height);
		int newWidth = getNextPowerOfTwoValue(width);
		if ((height != newHeight) || (width != newWidth)) {
			Log.v(LOG_TAG, "   > Need to resize bitmap: old height=" + height
					+ ", old width=" + width + ", new height=" + newHeight
					+ ", new width=" + newWidth);
			return ImageTransform.resizeBitmap(b, newHeight, newWidth);
		}
		return b;
	}

	public static int getNextPowerOfTwoValue(double x) {
		/*
		 * calc log2(x) (log2(x) can be calculated with log(x)/log(2)) and get
		 * the next bigger integer value. then calc 2^this value
		 */
		double x2 = Math.pow(2, Math.floor(Math.log(x) / Math.log(2)) + 1);
		if (x2 != x) {
			return (int) x2;
		}
		return (int) x;
	}

	@Override
	public void showDebugInformation() {
		Log.i(LOG_TAG, "Debug infos about the Texture Manager:");
		Log.i(LOG_TAG, "   > newTexturesToLoad=" + newTexturesToLoad);

		Log.i(LOG_TAG, "   > textureArray.length=" + textureArray.length);
		Log.i(LOG_TAG, "   > textureArrayOffset=" + textureArrayOffset);

		Log.i(LOG_TAG, "   > length-offset="
				+ (textureArray.length - textureArrayOffset));
		Log.i(LOG_TAG,
				"   > newTexturesToLoad.size()=" + newTexturesToLoad.size());

	}

	public static void resetInstance() {
		instance = new TextureManager();
	}

	public static void reloadTexturesIfNeeded() {

		try {
			Collection<Texture> a = getInstance().myTextureMap.values();
			resetInstance();
			Log.d(LOG_TAG, "Restoring " + a.size() + " textures");
			for (Iterator<Texture> iterator = a.iterator(); iterator.hasNext();) {
				getInstance().addTexture(iterator.next());
			}

		} catch (Exception e) {
			Log.e(LOG_TAG, "Error while restoring textures");
			e.printStackTrace();
		}
	}

	public TexturReloader getTextureReloader() {
		return myReloader;
	}

}
