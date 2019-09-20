package gl.textures;

import android.opengl.GLES10;

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
		textureBuffer = GLUtilityClass
				.createAndInitFloatBuffer(tryToDesignTextureArray(myTexturePositions));
	}

	private float[] tryToDesignTextureArray(ArrayList<Vec> myTexturePositions) {
		if ((myTexturePositions == null) || (myTexturePositions.size() < 2))
			return null;
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
	public void draw(/*GL10 gl*/) {

		if (ObjectPicker.readyToDrawWithColor)
			/*
			 * when the object picker needs to draw a frame, the normal draw
			 * method for all meshes is used to use the picking color instead of
			 * the texture
			 */
			super.draw(/*gl*/);
		else {

			// first disable color_array for save:
			/*gl.*/glDisableClientState(GLES10.GL_COLOR_ARRAY);

			// Enabled the vertices buffer for writing and to be used during
			// rendering.
			/*gl.*/glEnableClientState(GLES10.GL_VERTEX_ARRAY);
			// Specifies the location and data format of an array of vertex
			// coordinates to use when rendering.
			/*gl.*/glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer);

			/*gl.*/glEnable(GLES10.GL_TEXTURE_2D);

			/*gl.*/glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER,
					GLES10.GL_LINEAR);
			/*gl.*/glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER,
					GLES10.GL_LINEAR);

			/*gl.*/glBindTexture(GLES10.GL_TEXTURE_2D, myTextureId);
			/*gl.*/glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
			/*gl.*/glTexCoordPointer(2, GLES10.GL_FLOAT, 0, textureBuffer);

			if (normalsBuffer != null) {
				// Enable normals array (for lightning):
				/*gl.*/glEnableClientState(GLES10.GL_NORMAL_ARRAY);
				/*gl.*/glNormalPointer(GLES10.GL_FLOAT, 0, normalsBuffer);
			}

			/*gl.*/glDrawArrays(drawMode, 0, verticesCount);

			/*gl.*/glDisable(GLES10.GL_TEXTURE_2D);
			// Disable the vertices buffer.
			/*gl.*/glDisableClientState(GLES10.GL_VERTEX_ARRAY);
		}
	}

}
