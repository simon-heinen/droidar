package gl.scenegraph;

import android.opengl.GLES10;
import android.opengl.GLES20;

import gl.GLUtilityClass;

import java.nio.ShortBuffer;
import java.util.ArrayList;

//import javax.microedition.khronos.opengles.GL10;

import util.Vec;

import static android.opengl.GLES10.glDisableClientState;
import static android.opengl.GLES10.glEnableClientState;
import static android.opengl.GLES10.glNormalPointer;
import static android.opengl.GLES10.glVertexPointer;
import static android.opengl.GLES20.glDrawElements;

public class TriangulatedRenderData extends RenderData {

	protected ShortBuffer indexBuffer;
	protected int indiceCount;

	public TriangulatedRenderData() { }

	@Override
	public void updateShape(ArrayList<Vec> shapeArray) {
		setVertexArray(turnShapeToFloatArray(shapeArray));
		setIndeceArray(triangulationOfShape(shapeArray));
	}

	public void setIndeceArray(short[] s) {
		indexBuffer = GLUtilityClass.createAndInitShortBuffer(s);
	}

	/**
	 * every verticle has to be defined at least one time in the indicesArray,
	 * for example there are 4 verticles than you will have to add all numbers 0
	 * to 3 to the indiceArray: like this: short[] indices = { 0, 1, 2, 0, 2, 3
	 * };
	 * 
	 * this algo works only for convex 2d shapes and should be replaced by a
	 * better one (for example this
	 * http://www.cs.unc.edu/~dm/CODE/GEM/chapter.html )
	 * 
	 * TODO replace this by a real 3d trianguation algorithm
	 * 
	 * @param shape
	 * @return
	 */
	protected short[] triangulationOfShape(ArrayList<Vec> shape) {
		System.out.println("shape.size() " + shape.size());
		indiceCount = (shape.size() - 2) * 3;
		if (indiceCount < 1) return null;
		short[] indices = new short[indiceCount];
		short a = 1;
		short b = 2;

		for (int i = 0; i < indiceCount - 2; i += 3) {
			indices[i] = 0;
			indices[i + 1] = a;
			indices[i + 2] = b;
			a = b;
			b++;
		}
		return indices;
	}

	@Override
	public void draw(GLES20 unused) {
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		// Specifies the location and data format of an array of vertex
		// coordinates to use when rendering.
		glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer);

		if (normalsBuffer != null) {
			// Enable normals array (for lightning):
			glEnableClientState(GLES10.GL_NORMAL_ARRAY);
			glNormalPointer(GLES10.GL_FLOAT, 0, normalsBuffer);
		}

		glDrawElements(drawMode, indexBuffer.limit(), GLES10.GL_UNSIGNED_SHORT, indexBuffer);

		// Disable the vertices buffer.
		glDisableClientState(GLES10.GL_VERTEX_ARRAY);
	}

}
