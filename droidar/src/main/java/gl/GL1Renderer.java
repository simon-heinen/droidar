package gl;

import gl.textures.TextureManager;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import util.Log;
import util.Vec;
import android.opengl.GLU;
import android.os.SystemClock;

/**
 * This is the OpenGL renderer used for the {@link CustomGLSurfaceView}
 * 
 * @author Spobo
 * 
 */
public class GL1Renderer extends GLRenderer {

	private static final String LOG_TAG = "GLRenderer";

	private boolean useLightning = false;
	private boolean switchLightning = false;

	public void setUseLightning(boolean useLightning) {
		this.switchLightning = true;
		this.useLightning = useLightning;
	}

	/**
	 * the light list should be contained in the {@link GL1Renderer} because
	 * there is a global maximum of 8 lights in the complete OpenGL ES
	 * environment and not only per world
	 */
	private ArrayList<LightSource> myLights;

	public ArrayList<LightSource> getMyLights() {
		if (myLights == null) {
			myLights = new ArrayList<LightSource>();
		}
		return myLights;
	}

	/**
	 * TODO move to extra object! And:
	 * 
	 * Fog isn't fully supported yet because the color picking mechanism wont
	 * work with fog enabled. fog should be disabled for the picking frames.
	 * this has to be implemented first
	 */
	private static final boolean USE_FOG = false;
	private static final float FOG_END_DISTANCE = 25.0f;
	private static final float FOG_START_DISTANCE = 2.0f;
	private static final FloatBuffer FOG_COLOR = new Color(0, 0, 0, 0)
			.toFloatBuffer();
	private static final boolean FLASH_SCREEN = false;

	private final ArrayList<Renderable> elementsToRender = new ArrayList<Renderable>();

	private boolean readyToPickPixel;

	@Override
	public void onDrawFrame(GL10 gl) {

		if (pauseRenderer) {
			startPauseLoop();
		}

		final long currentTime = SystemClock.uptimeMillis();

		// if the lightning was recently enabled/disabled
		if (switchLightning) {
			switchLightning = false;
			if (useLightning) {
				gl.glEnable(GL10.GL_LIGHTING);
			} else {
				gl.glDisable(GL10.GL_LIGHTING);
			}
		}

		if (ObjectPicker.readyToDrawWithColor) {
			readyToPickPixel = true;
			if (useLightning) {
				/*
				 * before the picking is executed lightning has to be disabled
				 * for the picking frame because it affects the colors of the
				 * objects and picking would not be possible with lightning
				 * enabled
				 */
				gl.glDisable(GL10.GL_LIGHTING);
			}
		}

		// first check if there are new textures to load into openGL:
		TextureManager.getInstance().updateTextures(gl); // TODO optimize? check
															// boolean
		boolean repeat;
		do {

			// Clears the screen and depth buffer.
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			for (int i = 0; i < elementsToRender.size(); i++) {
				// Reset the modelview matrix
				gl.glLoadIdentity();
				elementsToRender.get(i).render(gl, null);
			}

			repeat = false;
			if (readyToPickPixel) {
				ObjectPicker.getInstance().pickObject(gl);
				readyToPickPixel = false;
				// first time in life i would like to have a goto in Java;)
				if (!FLASH_SCREEN) {
					repeat = true;
				}
				// switch lights back on if lightning is used:
				if (useLightning) {
					gl.glEnable(GL10.GL_LIGHTING);
				}
			}
		} while (repeat);

		final float delta = (currentTime - lastTimeInMs);
		lastTimeInMs = currentTime;

		if (delta > 0 && 1000 / delta > MAX_FPS) {
			// System.out.println("delta=" + delta);
			// System.out.println("FPS=" + 1000 / delta);
			// System.out.println("1000/MAX_FPS-delta=" + (long) (1000 / MAX_FPS
			// - delta));
			try {
				Thread.sleep((long) (1000 / MAX_FPS - delta));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * do not kill the rendering thread, instead pause it this way because
	 * otherwise the opengl resources would be released and the thread cant be
	 * resatarted!
	 */
	private void startPauseLoop() {
		Log.d("OpenGL", "Renderer paused");
		while (pauseRenderer) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Log.d("OpenGL", "Renderer woken up");
	}

	/**
	 * This method will switch on all the defined light sources
	 * 
	 * @param gl
	 */
	public void enableLights(GL10 gl) {
		if (myLights.size() > 0) {
			gl.glEnable(GL10.GL_LIGHTING);
			for (int i = 0; i < myLights.size(); i++) {
				myLights.get(i).switchOn(gl);
			}
		}
	}

	public void disableLights(GL10 gl) {
		gl.glDisable(GL10.GL_LIGHTING);
		for (int i = 0; i < myLights.size(); i++) {
			myLights.get(i).switchOff(gl);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		Log.d("Activity", "GLSurfaceView.onSurfaceChanged");

		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);

		/*
		 * Select the projection matrix which transforms the point from view
		 * space to homogeneous clipping space. Clip space is a right-handed
		 * coordinate system (+Z into the screen) contained within a canonical
		 * clipping volume extending from (-1,-1,-1) to (+1,+1,+1):
		 */
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();

		/*
		 * GLU.gluPerspective parameters (see
		 * http://www.zeuscmd.com/tutorials/opengles/12-Perspective.php):
		 * 
		 * fovy - This specifies the field of view. A 90 degree angle means that
		 * you can see everything directly to the left right around to the right
		 * of you. This is not how humans see things. 45 degrees is a good value
		 * to start.
		 * 
		 * aspect - This specifies that aspect ratio that you desire. This is
		 * usually specified as the width divided by the height of the window.
		 * 
		 * zNear and zFar - This specifies the near and far clipping planes as
		 * normal.
		 */
		GL1Renderer.halfWidth = width / 2;
		GL1Renderer.halfHeight = height / 2;
		GL1Renderer.height = height;
		GL1Renderer.nearHeight = minViewDistance
				* (float) Math.tan((GL1Renderer.LENSE_ANGLE * Vec.deg2rad) / 2);
		GL1Renderer.aspectRatio = (float) width / (float) height;
		GLU.gluPerspective(gl, LENSE_ANGLE, aspectRatio, minViewDistance,
				maxViewDistance);
		// TODO what is a good value??

		/*
		 * Select the modelview matrix which transforms a point from model space
		 * to view space, using a right-handed coordinate system with +Y up, +X
		 * to the right, and -Z into the screen:
		 */
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		if (useLightning) {
			enableLights(gl);
		}

		if (USE_FOG) {
			addFog(gl);
		}

		/*
		 * update this here to get a goot init value for lastTimeInMs
		 */
		lastTimeInMs = SystemClock.uptimeMillis();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		Log.d("Activity", "GLSurfaceView.onSurfaceCreated");

		// Set the background color to black (and alpha to 0) ( rgba ).
		gl.glClearColor(0, 0, 0, 0);
		/*
		 * To enable flat shading use gl.glShadeModel(GL10.GL_FLAT); default is
		 * GL_SMOOTH and GL_FLAT renders faces always with the same color,
		 * shading... so its a little cheaper then GL_SMOOTH but the polygons
		 * wont look realistic!
		 */
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_DITHER);

		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// Really nice perspective calculations.
		// gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		/*
		 * Transparancy
		 * 
		 * "The only sure way to achieve visually correct results is to sort and
		 * render your primitives from back to front."
		 * 
		 * http://www.opengl.org/sdk/docs/man/xhtml/glBlendFunc.xml
		 */
		gl.glEnable(GL10.GL_BLEND);
		// gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_DST_ALPHA);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// Enable smooth shading for nice light effects

		gl.glShadeModel(GL10.GL_SMOOTH);
	}

	private void addFog(GL10 gl) {
		// TODO extract constants
		gl.glFogf(GL10.GL_FOG_MODE, GL10.GL_LINEAR);
		gl.glFogf(GL10.GL_FOG_START, FOG_START_DISTANCE);
		gl.glFogf(GL10.GL_FOG_END, FOG_END_DISTANCE);
		gl.glHint(GL10.GL_FOG_HINT, GL10.GL_NICEST);
		gl.glFogfv(GL10.GL_FOG_COLOR, FOG_COLOR);
		gl.glEnable(GL10.GL_FOG);

	}

	public void addRenderElement(Renderable elementToRender) {
		if (elementToRender == null) {
			Log.e(LOG_TAG, "Added element was NULL, cant be added!");
		}
		elementsToRender.add(elementToRender);
	}

	public boolean removeRenderElement(Renderable elementToRemove) {
		return elementsToRender.remove(elementToRemove);
	}

}
