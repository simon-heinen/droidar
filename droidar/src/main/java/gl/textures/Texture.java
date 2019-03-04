package gl.textures;

import util.EfficientList;
import util.Log;
import android.graphics.Bitmap;

public class Texture {

	private static final String LOG_TAG = "Texure";
	private Bitmap myImage;
	private String myName;
	private EfficientList<TexturedRenderData> myList;

	public Texture(TexturedRenderData target, Bitmap textureImage,
			String textureName) {
		myList = new EfficientList<TexturedRenderData>();
		myList.add(target);
		myImage = textureImage;
		myName = textureName;
	}

	public void idArrived(int id) {
		Log.d(LOG_TAG, "id=" + id + " arrived for " + myName + "("
				+ myList.myLength + " items use this texture)");
		for (int i = 0; i < myList.myLength; i++) {
			Log.d(LOG_TAG, "    -> Now setting id for: " + myList.get(i));
			myList.get(i).myTextureId = id;
		}
	}

	public void recycleImage() {
		if (TextureManager.recycleBitmapsToFreeMemory)
			myImage.recycle();
	}

	public Bitmap getImage() {
		if (myImage == null
				&& TextureManager.getInstance().getTextureReloader() != null)
			myImage = TextureManager.getInstance().getTextureReloader()
					.reload(myName);
		return myImage;
	}

	public String getName() {
		return myName;
	}

	public void addRenderData(TexturedRenderData target) {
		if (myList.contains(target) == -1) {
			myList.add(target);
			checkIfTextureIdAlreadyAvailableFor(target);
		}
	}

	private void checkIfTextureIdAlreadyAvailableFor(TexturedRenderData target) {
		if (myList.get(0) != null) {
			if (myList.get(0).myTextureId != TexturedRenderData.NO_ID_SET) {
				Log.d(LOG_TAG, "id=" + myList.get(0).myTextureId
						+ " already loaded for " + myName + "("
						+ myList.myLength + " items use this texture)");
				target.myTextureId = myList.get(0).myTextureId;
			}
		}
	}
}
