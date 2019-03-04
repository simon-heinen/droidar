package gl.textures;

import gl.GLUtilityClass;
import gl.ObjectPicker;
import gl.scenegraph.RenderData;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;

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
	public void draw(GL10 gl) {

		if (ObjectPicker.readyToDrawWithColor)
			/*
			 * when the object picker needs to draw a frame, the normal draw
			 * method for all meshes is used to use the picking color instead of
			 * the texture
			 */
			super.draw(gl);
		else {

			// first disable color_array for save:
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

			// Enabled the vertices buffer for writing and to be used during
			// rendering.
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			// Specifies the location and data format of an array of vertex
			// coordinates to use when rendering.
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

			gl.glEnable(GL10.GL_TEXTURE_2D);

			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);

			gl.glBindTexture(GL10.GL_TEXTURE_2D, myTextureId);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

			if (normalsBuffer != null) {
				// Enable normals array (for lightning):
				gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
				gl.glNormalPointer(GL10.GL_FLOAT, 0, normalsBuffer);
			}

			gl.glDrawArrays(drawMode, 0, verticesCount);

			gl.glDisable(GL10.GL_TEXTURE_2D);
			// Disable the vertices buffer.
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}
	}

}
