package gl.textures;

import gl.Renderable;
import gl.scenegraph.Shape;

//import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES10;
import android.opengl.GLES20;

import static android.opengl.GLES11Ext.glDrawTexfOES;
import static android.opengl.GLES20.glBindTexture;

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
			TextureManager.getInstance().addTexture((TexturedRenderData) myRenderData, texture, textureName);
			textureHeight = texture.getHeight();
			textureWidth = texture.getWidth();
		}
	}

	@Override
	public void draw(GLES20 unused, Renderable parent) {
		if (myRenderData != null) {
			glBindTexture(GLES10.GL_TEXTURE_2D, ((TexturedRenderData) myRenderData).myTextureId);
			/*((GL11Ext) gl).*/glDrawTexfOES(myPosition.x, myPosition.y, myPosition.z, textureWidth, textureHeight);
		}
	}
}
