package gl;

import android.opengl.GLES10;
import android.opengl.GLES20;

//import javax.microedition.khronos.opengles.GL10;

/**
 * Use this interface for custom rendering
 * 
 * @author Spobo
 * 
 */
public interface Renderable {
//	void render(GL10 gl, Renderable parent);
	void render(Renderable parent);
}
