package de.rwth;

import gl.Color;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import gl.scenegraph.TriangulatedRenderData;

public class AndroidMeshData {
	static float vertices[] = { -0.6f, 0f, -1.41667f, -0.6f, 0f, -0.5f, -0.4f,
			0f, -0.5f, -0.4f, 0f, -0.16667f, -0.2f, 0f, -0.16667f, -0.2f, 0f,
			-0.5f, 0.2f, 0f, -0.5f, 0.2f, 0f, -0.16667f, 0.4f, 0f, -0.16667f,
			0.4f, 0f, -0.5f, 0.6f, 0f, -0.5f, 0.6f, 0f, -1.41667f, -0.9f, 0f,
			-1.41667f, -0.9f, 0f, -0.7f, -0.7f, 0f, -0.7f, -0.7f, 0f,
			-1.41667f, 0.9f, 0f, -1.41667f, 0.9f, 0f, -0.7f, 0.7f, 0f, -0.7f,
			0.7f, 0f, -1.41667f, -0.6f, 0f, -1.666667f, -0.6f, 0f, -2f, -0.2f,
			0f, -2.6667f, 0.2f, 0f, -2.6667f, 0.6f, 0f, -2f, 0.6f, 0f,
			-1.666667f, -0.4f, 0f, -3f, 0.4f, 0f, -3f, -0.2f, 0f, -2f, -0.3f,
			0f, -2.166667f, -0.2f, 0f, -2.33333f, -0.1f, 0f, -2.166667f, 0.2f,
			0f, -2f, 0.3f, 0f, -2.166667f, 0.2f, 0f, -2.33333f, 0.1f, 0f,
			-2.166667f, };
	static short indices[] = { 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8,
			8, 9, 9, 10, 10, 11, 11, 0, 12, 13, 13, 14, 14, 15, 15, 12, 16, 17,
			17, 18, 18, 19, 19, 16, 20, 21, 21, 22, 22, 23, 23, 24, 24, 25, 25,
			20, 22, 26, 23, 27, 28, 29, 29, 30, 30, 31, 31, 28, 32, 33, 33, 34,
			34, 35, 35, 32 };

	static MeshComponent getAndroidMesh() {
		Shape mesh = new Shape();
		TriangulatedRenderData r = new TriangulatedRenderData();
		r.setDrawModeToLines();
		r.setVertexArray(vertices);
		r.setIndeceArray(indices);
		mesh.setMyRenderData(r);
		mesh.setColor(Color.green());
		return mesh;
	}

}
