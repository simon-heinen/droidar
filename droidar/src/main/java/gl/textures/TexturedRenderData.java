package gl.textures;

import android.opengl.GLES10;
import android.opengl.GLES20;

import gl.GLUtilityClass;
import gl.ObjectPicker;
import gl.scenegraph.RenderData;

import java.nio.FloatBuffer;
import java.util.ArrayList;

//import javax.microedition.khronos.opengles.GL10;

import util.Vec;

import static android.opengl.GLES10.glDisableClientState;
import static android.opengl.GLES10.glEnableClientState;
import static android.opengl.GLES10.glNormalPointer;
import static android.opengl.GLES10.glTexCoordPointer;
import static android.opengl.GLES10.glVertexPointer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glTexParameterf;

public class TexturedRenderData extends RenderData {

	public static final int NO_ID_SET = -1;

	public int myTextureId = NO_ID_SET;
	private FloatBuffer textureBuffer;

	protected TexturedRenderData() {
	}

	public void updateTextureBuffer(ArrayList<Vec> myTexturePositions) {
		textureBuffer = GLUtilityClass.createAndInitFloatBuffer(tryToDesignTextureArray(myTexturePositions));
	}

	private float[] tryToDesignTextureArray(ArrayList<Vec> myTexturePositions) {
		if ((myTexturePositions == null) || (myTexturePositions.size() < 2)) return null;
		int j = 0;
		float[] res = new float[myTexturePositions.size() * 2];
		for (int i = 0; i < myTexturePositions.size() * 2; i += 2) {
			res[i] = myTexturePositions.get(j).x;
			res[i + 1] = myTexturePositions.get(j).y;

			j++;
			if (j >= myTexturePositions.size()) {
				j = 0;
			}
		}
		return res;
	}

	@Override
	public void draw(GLES20 unused) {

		if (ObjectPicker.readyToDrawWithColor)
			/*
			 * when the object picker needs to draw a frame, the normal draw
			 * method for all meshes is used to use the picking color instead of
			 * the texture
			 */
			super.draw(unused);
		else {

			// first disable color_array for save:
			glDisableClientState(GLES10.GL_COLOR_ARRAY);

			// Enabled the vertices buffer for writing and to be used during
			// rendering.
			glEnableClientState(GLES10.GL_VERTEX_ARRAY);
			// Specifies the location and data format of an array of vertex
			// coordinates to use when rendering.
			glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer);

			glEnable(GLES10.GL_TEXTURE_2D);

			glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_LINEAR);
			glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_LINEAR);

			glBindTexture(GLES10.GL_TEXTURE_2D, myTextureId);
			glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
			glTexCoordPointer(2, GLES10.GL_FLOAT, 0, textureBuffer);

			if (normalsBuffer != null) {
				// Enable normals array (for lightning):
				glEnableClientState(GLES10.GL_NORMAL_ARRAY);
				glNormalPointer(GLES10.GL_FLOAT, 0, normalsBuffer);
			}

			glDrawArrays(drawMode, 0, verticesCount);

			glDisable(GLES10.GL_TEXTURE_2D);
			// Disable the vertices buffer.
			glDisableClientState(GLES10.GL_VERTEX_ARRAY);
		}
	}

}
