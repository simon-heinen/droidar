package de.rwth;

import gl.GL1Renderer;
import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.backends.android.AndroidGL10;
import com.badlogic.gdx.backends.android.AndroidGL11;
import com.badlogic.gdx.backends.android.AndroidGLU;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.GLU;
import com.badlogic.gdx.graphics.Pixmap;

public class GDXConnection implements Renderable {
	private static boolean initGLStuff;
	private GL1Renderer myRenderer;

	public GDXConnection(GL1Renderer renderer) {
		Gdx.app = new AndroidApplication();
		myRenderer = renderer;

	}

	private void setupGL(javax.microedition.khronos.opengles.GL10 gl) {

		Gdx.gl10 = new AndroidGL10(gl);
		Gdx.gl = Gdx.gl10;
		Gdx.graphics = new Graphics() { // TODO

			@Override
			public boolean supportsDisplayModeChange() {
				return false;
			}

			@Override
			public void setTitle(String title) {

			}

			@Override
			public boolean setDisplayMode(DisplayMode displayMode) {
				return false;
			}

			@Override
			public boolean isGL20Available() {
				return false;
			}

			@Override
			public boolean isGL11Available() {
				if (Gdx.gl instanceof javax.microedition.khronos.opengles.GL11)
					return true;
				return false;
			}

			@Override
			public int getWidth() {
				return 0;
			}

			@Override
			public GraphicsType getType() {
				return null;
			}

			@Override
			public float getPpiY() {
				return 0;
			}

			@Override
			public float getPpiX() {
				return 0;
			}

			@Override
			public float getPpcY() {
				return 0;
			}

			@Override
			public float getPpcX() {
				return 0;
			}

			@Override
			public int getHeight() {
				return 0;
			}

			@Override
			public GLU getGLU() {
				return null;
			}

			@Override
			public GLCommon getGLCommon() {
				return null;
			}

			@Override
			public GL20 getGL20() {
				return null;
			}

			@Override
			public GL11 getGL11() {
				return null;
			}

			@Override
			public com.badlogic.gdx.graphics.GL10 getGL10() {
				return null;
			}

			@Override
			public int getFramesPerSecond() {
				return 0;
			}

			@Override
			public DisplayMode[] getDisplayModes() {
				return null;
			}

			@Override
			public float getDeltaTime() {
				return 0;
			}

			@Override
			public DisplayMode getDesktopDisplayMode() {
				return null;
			}

			@Override
			public boolean setDisplayMode(int width, int height,
					boolean fullscreen) {
				return false;
			}

			@Override
			public void setVSync(boolean vsync) {

			}

			@Override
			public BufferFormat getBufferFormat() {
				return null;
			}

			@Override
			public boolean supportsExtension(String extension) {

				return false;
			}

			@Override
			public float getDensity() {
				return 1;
			}

//			@Override
//			public void setIcon(Pixmap[] pixmaps) {
//			}

			@Override
			public void setContinuousRendering(boolean isContinuous) {

			}

			@Override
			public boolean isContinuousRendering() {
				return true;
			}

			@Override
			public void requestRendering() {

			}

			@Override
			public float getRawDeltaTime() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean isFullscreen() {
				// TODO Auto-generated method stub
				return false;
			}
		};
		if (gl instanceof javax.microedition.khronos.opengles.GL11) {
			String renderer = gl.glGetString(GL10.GL_RENDERER);
			if (renderer != null) { // silly GT-I7500
				if (!renderer.toLowerCase().contains("pixelflinger")
						&& !(android.os.Build.MODEL.equals("MB200")
								|| android.os.Build.MODEL.equals("MB220") || android.os.Build.MODEL
									.contains("Behold"))) {
					if (gl instanceof javax.microedition.khronos.opengles.GL11) {
						Gdx.gl11 = new AndroidGL11(
								(javax.microedition.khronos.opengles.GL11) gl);
					}
					Gdx.gl10 = Gdx.gl11;
				}
			}
		}

		Gdx.glu = new AndroidGLU();

	}

	@Override
	public void render(javax.microedition.khronos.opengles.GL10 gl,
			gl.Renderable parent) {
		if (!initGLStuff) {
			setupGL(gl);
			initGLStuff = true;
			myRenderer.removeRenderElement(this);
		}
	}

	public static void init(Activity activity, GL1Renderer renderer) {
		Gdx.files = new AndroidFiles(activity.getAssets());
		renderer.addRenderElement(new GDXConnection(renderer));
	}

}
