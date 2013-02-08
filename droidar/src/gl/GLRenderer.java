package gl;

import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;

public abstract class GLRenderer implements Renderer {

	/**
	 * The maximum fps rate for the renderer. 40fps to be not so cpu intense
	 */
	protected static final float MAX_FPS = 40;
	public static float LENSE_ANGLE = 35.0f;
	public static float minViewDistance = 0.1f;
	public static float maxViewDistance = 700.0f;
	public static float halfWidth;
	public static float halfHeight;
	public static float height;
	public static float nearHeight;
	public static float aspectRatio;
	protected boolean pauseRenderer;
	protected long lastTimeInMs = SystemClock.uptimeMillis();

	public GLRenderer() {
		super();
	}

	public void resume() {
		pauseRenderer(false);
	}

	private synchronized void pauseRenderer(boolean pauseRenderer) {
		this.pauseRenderer = pauseRenderer;
	}

	public void pause() {
		this.pauseRenderer(true);
	}

}