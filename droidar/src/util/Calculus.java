package util;

import android.content.Context;
import android.opengl.Matrix;
import android.webkit.WebView;

public class Calculus {

	// array of transpose source matrix
	private static float[] src = new float[16];
	// temp array for pairs
	private static float[] tmp = new float[12];
	// Holds the destination matrix while we're building it up.
	private static float[] dst = new float[16];

	public static int randomInt(int min, int max) {
		return (int) (min + (Math.random() * (max + 1 - min)));
	}

	public static float getRandomFloat(float lowerBorder, float uperBorder) {
		return (float) (Math.random() * (uperBorder - lowerBorder) + lowerBorder);
	}

	public static float varyValueByPercent(float value, int percentToVary) {
		float p = value * percentToVary / 100;
		return getRandomFloat(value - p, value + p);
	}

	/**
	 * Copied from {@link Matrix#invertM(float[], int, float[], int)} for better
	 * performance
	 * 
	 * @param mInv
	 * @param mInvOffset
	 * @param m
	 * @param mOffset
	 * @return
	 */
	public static boolean invertM(float[] mInv, int mInvOffset, float[] m,
			int mOffset) {
		// Invert a 4 x 4 matrix using Cramer's Rule

		// transpose matrix
		Matrix.transposeM(src, 0, m, mOffset);

		// calculate pairs for first 8 elements (cofactors)
		tmp[0] = src[10] * src[15];
		tmp[1] = src[11] * src[14];
		tmp[2] = src[9] * src[15];
		tmp[3] = src[11] * src[13];
		tmp[4] = src[9] * src[14];
		tmp[5] = src[10] * src[13];
		tmp[6] = src[8] * src[15];
		tmp[7] = src[11] * src[12];
		tmp[8] = src[8] * src[14];
		tmp[9] = src[10] * src[12];
		tmp[10] = src[8] * src[13];
		tmp[11] = src[9] * src[12];

		// calculate first 8 elements (cofactors)
		dst[0] = tmp[0] * src[5] + tmp[3] * src[6] + tmp[4] * src[7];
		dst[0] -= tmp[1] * src[5] + tmp[2] * src[6] + tmp[5] * src[7];
		dst[1] = tmp[1] * src[4] + tmp[6] * src[6] + tmp[9] * src[7];
		dst[1] -= tmp[0] * src[4] + tmp[7] * src[6] + tmp[8] * src[7];
		dst[2] = tmp[2] * src[4] + tmp[7] * src[5] + tmp[10] * src[7];
		dst[2] -= tmp[3] * src[4] + tmp[6] * src[5] + tmp[11] * src[7];
		dst[3] = tmp[5] * src[4] + tmp[8] * src[5] + tmp[11] * src[6];
		dst[3] -= tmp[4] * src[4] + tmp[9] * src[5] + tmp[10] * src[6];
		dst[4] = tmp[1] * src[1] + tmp[2] * src[2] + tmp[5] * src[3];
		dst[4] -= tmp[0] * src[1] + tmp[3] * src[2] + tmp[4] * src[3];
		dst[5] = tmp[0] * src[0] + tmp[7] * src[2] + tmp[8] * src[3];
		dst[5] -= tmp[1] * src[0] + tmp[6] * src[2] + tmp[9] * src[3];
		dst[6] = tmp[3] * src[0] + tmp[6] * src[1] + tmp[11] * src[3];
		dst[6] -= tmp[2] * src[0] + tmp[7] * src[1] + tmp[10] * src[3];
		dst[7] = tmp[4] * src[0] + tmp[9] * src[1] + tmp[10] * src[2];
		dst[7] -= tmp[5] * src[0] + tmp[8] * src[1] + tmp[11] * src[2];

		// calculate pairs for second 8 elements (cofactors)
		tmp[0] = src[2] * src[7];
		tmp[1] = src[3] * src[6];
		tmp[2] = src[1] * src[7];
		tmp[3] = src[3] * src[5];
		tmp[4] = src[1] * src[6];
		tmp[5] = src[2] * src[5];
		tmp[6] = src[0] * src[7];
		tmp[7] = src[3] * src[4];
		tmp[8] = src[0] * src[6];
		tmp[9] = src[2] * src[4];
		tmp[10] = src[0] * src[5];
		tmp[11] = src[1] * src[4];

		// calculate second 8 elements (cofactors)
		dst[8] = tmp[0] * src[13] + tmp[3] * src[14] + tmp[4] * src[15];
		dst[8] -= tmp[1] * src[13] + tmp[2] * src[14] + tmp[5] * src[15];
		dst[9] = tmp[1] * src[12] + tmp[6] * src[14] + tmp[9] * src[15];
		dst[9] -= tmp[0] * src[12] + tmp[7] * src[14] + tmp[8] * src[15];
		dst[10] = tmp[2] * src[12] + tmp[7] * src[13] + tmp[10] * src[15];
		dst[10] -= tmp[3] * src[12] + tmp[6] * src[13] + tmp[11] * src[15];
		dst[11] = tmp[5] * src[12] + tmp[8] * src[13] + tmp[11] * src[14];
		dst[11] -= tmp[4] * src[12] + tmp[9] * src[13] + tmp[10] * src[14];
		dst[12] = tmp[2] * src[10] + tmp[5] * src[11] + tmp[1] * src[9];
		dst[12] -= tmp[4] * src[11] + tmp[0] * src[9] + tmp[3] * src[10];
		dst[13] = tmp[8] * src[11] + tmp[0] * src[8] + tmp[7] * src[10];
		dst[13] -= tmp[6] * src[10] + tmp[9] * src[11] + tmp[1] * src[8];
		dst[14] = tmp[6] * src[9] + tmp[11] * src[11] + tmp[3] * src[8];
		dst[14] -= tmp[10] * src[11] + tmp[2] * src[8] + tmp[7] * src[9];
		dst[15] = tmp[10] * src[10] + tmp[4] * src[8] + tmp[9] * src[9];
		dst[15] -= tmp[8] * src[9] + tmp[11] * src[10] + tmp[5] * src[8];

		// calculate determinant
		float det = src[0] * dst[0] + src[1] * dst[1] + src[2] * dst[2]
				+ src[3] * dst[3];

		// calculate matrix inverse
		det = 1 / det;
		for (int j = 0; j < 16; j++)
			mInv[j + mInvOffset] = dst[j] * det;

		return true;
	}

	public interface TermResultListener {
		public void returnResult(String result);
	}

	/**
	 * TODO doesn't work
	 * 
	 * @param context
	 * @param inputTerm
	 *            e.g. "(1+3)/4 * 2 - 7"
	 * @param resultListener
	 */
	@Deprecated
	public static void calculateTermResult(Context context, String inputTerm,
			TermResultListener resultListener) {
		WebView webView = new WebView(context);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(resultListener, "JavaCallback");
		webView.loadUrl("javascript:window.JavaCallback" + ".returnResult("
				+ inputTerm + ")");
	}

	public static float[] createIdentityMatrix() {
		float[] result = new float[16];
		result[0] = 1;
		result[5] = 1;
		result[10] = 1;
		result[15] = 1;
		return result;
	}

	public static float morphToNewValue(float factor, float newX, float currentX) {
		return currentX + (factor * (newX - currentX));
	}

}
