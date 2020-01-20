package gl.scenegraph;

import android.opengl.GLES10;
import android.opengl.GLES20;

import gl.Color;
import gl.GLUtilityClass;
import gl.ObjectPicker;

import java.nio.FloatBuffer;
import java.util.ArrayList;

//import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.glColorPointer;
import static android.opengl.GLES10.glDisableClientState;
import static android.opengl.GLES10.glEnableClientState;
import static android.opengl.GLES10.glNormalPointer;
import static android.opengl.GLES10.glVertexPointer;
import static android.opengl.GLES20.glDrawArrays;

public class MultiColorRenderData extends RenderData {

	private FloatBuffer colorBuffer;

	/**
	 * called from a {@link MultiColoredShape} when an edge is added
	 * 
	 * @param myColors
	 */
	public void updateColorBuffer(ArrayList<Color> myColors) {
		colorBuffer = GLUtilityClass.createAndInitFloatBuffer(tryToDesignColorArray(myColors));
	}

	private float[] tryToDesignColorArray(ArrayList<Color> myColors) {
		// every edge needs a color so iterate over the indiceCount
		if ((myColors == null) || (myColors.size() < 2)) return null;
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
	public void draw(/*GL10 gl*/GLES20 unused) {

		if (ObjectPicker.readyToDrawWithColor)
			/*
			 * when the object picker needs to draw a frame, the normal draw
			 * method for all meshes is used to use the picking color
			 */
			super.draw(/*gl*/unused);
		else {

			// Enabled the vertices buffer for writing and to be used during
			// rendering.
			/*gl.*/glEnableClientState(GLES10.GL_VERTEX_ARRAY);
			// Specifies the location and data format of an array of vertex
			// coordinates to use when rendering.

			/*gl.*/glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer);

			// Enable the color array buffer to be used during rendering.
			/*gl.*/glEnableClientState(GLES10.GL_COLOR_ARRAY);
			// Point out the where the color buffer is.
			/*gl.*/glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer); // 4 for RGBA

			if (normalsBuffer != null) {
				// Enable normals array (for lightning):
				/*gl.*/glEnableClientState(GLES10.GL_NORMAL_ARRAY);
				/*gl.*/glNormalPointer(GLES10.GL_FLOAT, 0, normalsBuffer);
			}

			/*gl.*/glDrawArrays(drawMode, 0, verticesCount);

			/*gl.*/glDisableClientState(GLES10.GL_COLOR_ARRAY);
			// Disable the vertices buffer.
			/*gl.*/glDisableClientState(GLES10.GL_VERTEX_ARRAY);
		}
	}
}
