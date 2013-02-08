package gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import android.content.Context;
import android.hardware.SensorManager;

public class GLUtilityClass {

	/**
	 * TODO not efficient to create a new buffer object every time, pass an old
	 * one if available and use it instead?
	 * 
	 * @param source
	 * @return
	 */
	public static FloatBuffer createAndInitFloatBuffer(float[] source) {
		if (source == null)
			return null;
		/*
		 * a float is 4 bytes, therefore the number of elements in the array has
		 * to be multiplied with 4:
		 */
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(source.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer targetBuffer = byteBuffer.asFloatBuffer();
		targetBuffer.put(source);
		targetBuffer.position(0);
		return targetBuffer;
	}

	public static ShortBuffer createAndInitShortBuffer(short[] source) {
		if (source == null)
			return null;
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer indiceBiteBuffer = ByteBuffer
				.allocateDirect(source.length * 2);
		indiceBiteBuffer.order(ByteOrder.nativeOrder());
		ShortBuffer targetBuffer = indiceBiteBuffer.asShortBuffer();
		targetBuffer.put(source);
		targetBuffer.position(0);
		return targetBuffer;
	}

	/**
	 * this is a copy from
	 * {@link SensorManager#getRotationMatrixFromVector(float[] R, float[] rotationVector)}
	 * in newer android versions. Code from:
	 * http://android.git.kernel.org/?p=platform/frameworks/base
	 * .git;a=blob;f=core/java/android/hardware/SensorManager.java to keep
	 * compatibility to older Android versions
	 * 
	 * TODO the "if lenght==3" stuff could be removed?
	 * 
	 * @param R
	 * @param rotationVector
	 */
	public static void getRotationMatrixFromVector(float[] R,
			float[] rotationVector) {

		float q0;
		float q1 = rotationVector[0];
		float q2 = rotationVector[1];
		float q3 = rotationVector[2];

		if (rotationVector.length == 4) {
			q0 = rotationVector[3];
		} else {
			q0 = 1 - q1 * q1 - q2 * q2 - q3 * q3;
			q0 = (q0 > 0) ? (float) Math.sqrt(q0) : 0;
		}

		float sq_q1 = 2 * q1 * q1;
		float sq_q2 = 2 * q2 * q2;
		float sq_q3 = 2 * q3 * q3;
		float q1_q2 = 2 * q1 * q2;
		float q3_q0 = 2 * q3 * q0;
		float q1_q3 = 2 * q1 * q3;
		float q2_q0 = 2 * q2 * q0;
		float q2_q3 = 2 * q2 * q3;
		float q1_q0 = 2 * q1 * q0;

		if (R.length == 9) {
			R[0] = 1 - sq_q2 - sq_q3;
			R[1] = q1_q2 - q3_q0;
			R[2] = q1_q3 + q2_q0;

			R[3] = q1_q2 + q3_q0;
			R[4] = 1 - sq_q1 - sq_q3;
			R[5] = q2_q3 - q1_q0;

			R[6] = q1_q3 - q2_q0;
			R[7] = q2_q3 + q1_q0;
			R[8] = 1 - sq_q1 - sq_q2;
		} else if (R.length == 16) {
			R[0] = 1 - sq_q2 - sq_q3;
			R[1] = q1_q2 - q3_q0;
			R[2] = q1_q3 + q2_q0;
			R[3] = 0.0f;

			R[4] = q1_q2 + q3_q0;
			R[5] = 1 - sq_q1 - sq_q3;
			R[6] = q2_q3 - q1_q0;
			R[7] = 0.0f;

			R[8] = q1_q3 - q2_q0;
			R[9] = q2_q3 + q1_q0;
			R[10] = 1 - sq_q1 - sq_q2;
			R[11] = 0.0f;

			R[12] = R[13] = R[14] = 0.0f;
			R[15] = 1.0f;
		}
	}

	/**
	 * source:
	 * 
	 * http://code.google.com/p/gl2-android/source/browse/trunk/src/com/badlogic
	 * /gdx/GL2Test.java
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isGL2Available(Context context) {
		EGL10 egl = (EGL10) EGLContext.getEGL();
		EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

		int[] version = new int[2];
		egl.eglInitialize(display, version);

		int EGL_OPENGL_ES2_BIT = 4;
		int[] configAttribs = { EGL10.EGL_RED_SIZE, 4, EGL10.EGL_GREEN_SIZE, 4,
				EGL10.EGL_BLUE_SIZE, 4, EGL10.EGL_RENDERABLE_TYPE,
				EGL_OPENGL_ES2_BIT, EGL10.EGL_NONE };

		EGLConfig[] configs = new EGLConfig[10];
		int[] num_config = new int[1];
		egl.eglChooseConfig(display, configAttribs, configs, 10, num_config);
		egl.eglTerminate(display);
		return num_config[0] > 0;
	}

}
