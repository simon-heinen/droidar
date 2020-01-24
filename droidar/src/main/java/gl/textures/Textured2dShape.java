package gl.textures;

import gl.Renderable;
import gl.scenegraph.Shape;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11Ext;

import android.graphics.Bitmap;

/**
 * This Class uses the GL11 extension to dray a image to the screen which will
 * have no perspective etc!
 * 
 * @author Spobo
 * 
 */
public class Textured2dShape extends Shape {

	private float textureWidth = 0;
	private float textureHeight = 0;

	public Textured2dShape(Bitmap texture, String textureName) {
		super(null);
		myRenderData = new TexturedRenderData();
		if (texture != null) {
			TextureManager.getInstance().addTexture(
					(TexturedRenderData) myRenderData, texture, textureName);
			textureHeight = texture.getHeight();
			textureWidth = texture.getWidth();
		}
	}

	@Override
	public void draw(GL10 gl, Renderable parent) {
		if (myRenderData != null) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D,
					((TexturedRenderData) myRenderData).myTextureId);
			((GL11Ext) gl).glDrawTexfOES(myPosition.x, myPosition.y,
					myPosition.z, textureWidth, textureHeight);
		}
	}

}
