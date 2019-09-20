package gl;

import android.opengl.GLES10;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.glColorPointer;
import static android.opengl.GLES10.glDisableClientState;
import static android.opengl.GLES10.glDrawElements;
import static android.opengl.GLES10.glEnableClientState;
import static android.opengl.GLES10.glVertexPointer;

public class CoordinateAxis {

	private static FloatBuffer vertexBuffer;
	private static ByteBuffer indexBuffer;
	private static FloatBuffer colorBuffer;
	private static boolean init = false;

	public static void draw(/*GL10 gl*/) {
		if (!init)
			initIt();
		/*gl.*/glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		/*gl.*/glEnableClientState(GLES10.GL_COLOR_ARRAY);

		/*gl.*/glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer);
		/*gl.*/glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer);
		/*gl.*/glDrawElements(GLES10.GL_LINES, 6, GLES10.GL_UNSIGNED_BYTE, indexBuffer);
		/*gl.*/glDisableClientState(GLES10.GL_VERTEX_ARRAY);
		/*gl.*/glDisableClientState(GLES10.GL_COLOR_ARRAY);
	}

	private static void initIt() {
		init = true;
		// load the 3 axis and their colors:
		float[] vertices = { 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2 };
		float[] colors = { 0, 0, 0, 0.5f, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1 };
		byte[] indices = { 0, 1, 0, 2, 0, 3 };

		ByteBuffer vbb;
		vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		vbb = ByteBuffer.allocateDirect(colors.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		colorBuffer = vbb.asFloatBuffer();
		colorBuffer.put(colors);
		colorBuffer.position(0);

		indexBuffer = ByteBuffer.allocateDirect(indices.length);
		indexBuffer.put(indices);
		indexBuffer.position(0);

	}

}
