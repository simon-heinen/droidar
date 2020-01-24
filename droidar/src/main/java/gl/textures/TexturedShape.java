package gl.textures;

import gl.scenegraph.Shape;

import java.util.ArrayList;

import util.Log;
import util.Vec;
import android.graphics.Bitmap;

public class TexturedShape extends Shape {

	/**
	 * this values are corresponding to the shape edges
	 */
	ArrayList<Vec> myTexturePositions = new ArrayList<Vec>();

	/**
	 * Please read
	 * {@link TextureManager#addTexture(TexturedRenderData, Bitmap, String)} for
	 * information about the parameters
	 * 
	 * @param textureName
	 * @param texture
	 */
	public TexturedShape(String textureName, Bitmap texture) {
		super(null);
		myRenderData = new TexturedRenderData();
		/*
		 * TODO redesign this so that the input texture is projected on the mesh
		 * correctly
		 */
		if (texture != null) {
			texture = TextureManager.getInstance().resizeBitmapIfNecessary(
					texture);
			TextureManager.getInstance().addTexture(
					(TexturedRenderData) myRenderData, texture, textureName);
		} else {
			Log.e("TexturedShape",
					"got null-bitmap! check bitmap creation process");
		}
	}

	public void add(Vec vec, int x, int y) {
		getMyShapeArray().add(vec);
		// z coordinate not needed for 2d textures:
		myTexturePositions.add(new Vec(x, y, 0));
		myRenderData.updateShape(getMyShapeArray());
		((TexturedRenderData) myRenderData)
				.updateTextureBuffer(myTexturePositions);
	}

	@Override
	public void add(Vec v) {
		// TODO throw error
		super.add(v);
	}

}
