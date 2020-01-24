package gl.scenegraph;

import gl.GLUtilityClass;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;

public class RenderData {

	private static final String LOG_TAG = "RenderData";
	protected FloatBuffer vertexBuffer;
	protected int verticesCount;
	protected FloatBuffer normalsBuffer;

	public int drawMode = GL10.GL_TRIANGLES;

	/**
	 * call whenever a {@link Shape} changes
	 * 
	 * @param shape
	 */
	public void updateShape(ArrayList<Vec> shape) {
		setVertexArray(turnShapeToFloatArray(shape));
		normalsBuffer = getNormalsBuffer(shape);
	}

	private FloatBuffer getNormalsBuffer(ArrayList<Vec> shape) {

		// don't use normals if the shape does not consist of triangles:
		if (shape.size() % 3 != 0)
			return null;

		/*
		 * This will take always 3 vecs and calculate the normal of the triangle
		 * defined by these 3 vecs. So the size of the float array has to be
		 * shapesize *3 ! to store the x y and z value of the normal vector
		 */

		float[] normalsArray = new float[shape.size() * 3];
		int currentNormalsIndex = 0;

		// Log.d(LOG_TAG, "shape.size()=" + shape.size());

		for (int i = 0; i < shape.size(); i += 3) {
			Vec v1 = Vec.sub(shape.get(i), shape.get(i + 1));
			Vec v2 = Vec.sub(shape.get(i), shape.get(i + 2));
			Vec normalVec = Vec.calcNormalVec(v1, v2).normalize();

			/*
			 * TODO implement Newell's Method to have a more general approach:
			 * http://www.opengl.org/wiki/Calculating_a_Surface_Normal
			 */

			// Log.d(LOG_TAG, "     >" + i + " u=" + v1);
			// Log.d(LOG_TAG, "     >" + i + " v=" + v2);
			// Log.d(LOG_TAG, "     >" + i + " normal=" + normalVec);

			/*
			 * each vertex neads an own normal vector!
			 */
			currentNormalsIndex = addNormalVectorForVertex(normalsArray,
					currentNormalsIndex, normalVec);
			currentNormalsIndex = addNormalVectorForVertex(normalsArray,
					currentNormalsIndex, normalVec);
			currentNormalsIndex = addNormalVectorForVertex(normalsArray,
					currentNormalsIndex, normalVec);

		}

		return GLUtilityClass.createAndInitFloatBuffer(normalsArray);
	}

	private int addNormalVectorForVertex(float[] normalsArray, int j,
			Vec normalVec) {
		normalsArray[j] = normalVec.x;
		j++;
		normalsArray[j] = normalVec.y;
		j++;
		normalsArray[j] = normalVec.z;
		j++;
		return j;
	}

	public void setVertexArray(float[] floatArray) {
		vertexBuffer = GLUtilityClass.createAndInitFloatBuffer(floatArray);
	}

	public void setNormalsBuffer(FloatBuffer normalsBuffer) {
		this.normalsBuffer = normalsBuffer;
	}

	public void setDrawModeToTriangles() {
		drawMode = GL10.GL_TRIANGLES;
	}

	public void setDrawModeToLines() {
		drawMode = GL10.GL_LINES;
	}

	protected RenderData() {
	}

	protected float[] turnShapeToFloatArray(ArrayList<Vec> shape) {
		float[] vertices = new float[shape.size() * 3];
		verticesCount = shape.size();
		int i = 0;
		for (Vec v : shape) {
			i = addNormalVectorForVertex(vertices, i, v);
		}
		return vertices;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gl.Renderable#draw(javax.microedition.khronos.opengles.GL10)
	 */

	public void draw(GL10 gl) {
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// Specifies the location and data format of an array of vertex
		// coordinates to use when rendering.
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		if (normalsBuffer != null) {
			// Enable normals array (for lightning):
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, normalsBuffer);
		}
		gl.glDrawArrays(drawMode, 0, verticesCount);

		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

		// Disable normals array (for lightning):
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}

}
