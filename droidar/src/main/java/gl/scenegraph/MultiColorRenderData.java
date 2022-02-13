package gl.scenegraph;

import gl.Color;
import gl.GLUtilityClass;
import gl.ObjectPicker;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class MultiColorRenderData extends RenderData {

	private FloatBuffer colorBuffer;

	/**
	 * called from a {@link MultiColoredShape} when an edge is added
	 * 
	 * @param myColors
	 */
	public void updateColorBuffer(ArrayList<Color> myColors) {
		colorBuffer = GLUtilityClass
				.createAndInitFloatBuffer(tryToDesignColorArray(myColors));
	}

	private float[] tryToDesignColorArray(ArrayList<Color> myColors) {
		// every edge needs a color so iterate over the indiceCount
		if ((myColors == null) || (myColors.size() < 2))
			return null;
		int j = 0;
		float[] res = new float[myColors.size() * 4];
		for (int i = 0; i < myColors.size() * 4; i += 4) {
			res[i] = myColors.get(j).red;
			res[i + 1] = myColors.get(j).green;
			res[i + 2] = myColors.get(j).blue;
			res[i + 3] = myColors.get(j).alpha;
			j++;
			if (j >= myColors.size()) {
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
			 * method for all meshes is used to use the picking color
			 */
			super.draw(gl);
		else {

			// Enabled the vertices buffer for writing and to be used during
			// rendering.
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			// Specifies the location and data format of an array of vertex
			// coordinates to use when rendering.

			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

			// Enable the color array buffer to be used during rendering.
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			// Point out the where the color buffer is.
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer); // 4 for RGBA

			if (normalsBuffer != null) {
				// Enable normals array (for lightning):
				gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
				gl.glNormalPointer(GL10.GL_FLOAT, 0, normalsBuffer);
			}

			gl.glDrawArrays(drawMode, 0, verticesCount);

			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			// Disable the vertices buffer.
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}
	}

}
