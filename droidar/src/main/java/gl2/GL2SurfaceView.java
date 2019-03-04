package gl2;

import gl.CustomGLSurfaceView;
import android.content.Context;

public class GL2SurfaceView extends CustomGLSurfaceView {

	public GL2SurfaceView(Context context) {
		super(context);
		/*
		 * Create an OpenGL ES 2.0 context.
		 * 
		 * IMPORTANT:
		 * 
		 * If the following line causes COMPILE ERRORS set the Android version
		 * to 2.2 or higher (or update the droidar project to the latest version
		 * ;)
		 */
		setEGLContextClientVersion(2);
	}

}
