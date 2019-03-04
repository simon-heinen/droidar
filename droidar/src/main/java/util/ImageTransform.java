package util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageTransform {

	/**
	 * @param bitmap
	 *            the source
	 * @param factor
	 *            should be between 2f (very visible round corners) and 20f
	 *            (nearly no round corners)
	 * @return the result
	 */
	public static Bitmap createBitmapWithRoundCorners(Bitmap bitmap,
			float factor) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Bitmap result = Bitmap.createBitmap(width, height, bitmap.getConfig());
		Canvas canvas = new Canvas(result);
		Rect rect = new Rect(0, 0, width, height);
		RectF roundCornerFrameRect = new RectF(rect);
		float cornerRadius = width / factor;
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		canvas.drawRoundRect(roundCornerFrameRect, cornerRadius, cornerRadius,
				paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return result;
	}

	/**
	 * This will scale the image first to add soft corners
	 * 
	 * @param bitmap
	 * @param angle
	 * @param smoothingFactor
	 *            try 1.5f; (1 would be no smoothing at all)
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int angle,
			float smoothingFactor) {
		Bitmap result = ImageTransform.resizeBitmap(bitmap, bitmap.getHeight()
				* smoothingFactor, bitmap.getWidth() * smoothingFactor);
		result = rotateBitmap(result, angle);
		result = ImageTransform.resizeBitmap(result, result.getHeight()
				/ smoothingFactor, result.getWidth() / smoothingFactor);
		return result;
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap result = Bitmap.createBitmap(width, height, bitmap.getConfig());
		Canvas canvas = new Canvas(result);
		Matrix matrix = new Matrix();
		matrix.setRotate(angle, width / 2, height / 2);
		Paint p = new Paint();
		p.setAntiAlias(true);
		canvas.drawBitmap(bitmap, matrix, p);
		return result;
	}

	public static Bitmap resizeBitmap(Bitmap bitmap, float newHeight,
			float newWidth) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}

	/**
	 * From
	 * http://android-developers.blogspot.com/2012/01/levels-in-renderscript
	 * .html
	 * 
	 * TODO add description for parameters
	 * 
	 * @param sourceBitmap
	 * @param resultBitmap
	 * @param filterKernel
	 *            has to be a 3x3 matrix so float[9]. The kernal which will keep
	 *            all color values like they are is filterKernel={1,0,0, 0,1,0,
	 *            0,0,1}.
	 * @param mOverInWMinInB
	 *            from 0 to 1
	 * @param gammaValue
	 *            from 0 to 1
	 * @param mOutWMinOutB
	 *            from 0 to 1
	 * @param mInBlack
	 *            from 0 to 255
	 * @param mOutBlack
	 *            from 0 to 255
	 */
	public static void improveSaturation(Bitmap sourceBitmap,
			Bitmap resultBitmap, float[] filterKernel, float mOverInWMinInB,
			float gammaValue, float mOutWMinOutB, float mInBlack,
			float mOutBlack) {
		int[] mInPixels = new int[sourceBitmap.getHeight()
				* sourceBitmap.getWidth()];
		int[] mOutPixels = new int[resultBitmap.getHeight()
				* resultBitmap.getWidth()];
		sourceBitmap.getPixels(mInPixels, 0, sourceBitmap.getWidth(), 0, 0,
				sourceBitmap.getWidth(), sourceBitmap.getHeight());

		for (int i = 0; i < mInPixels.length; i++) {
			float r = (float) (mInPixels[i] & 0xff);
			float g = (float) ((mInPixels[i] >> 8) & 0xff);
			float b = (float) ((mInPixels[i] >> 16) & 0xff);

			float tr = r * filterKernel[0] + g * filterKernel[3] + b
					* filterKernel[6];
			float tg = r * filterKernel[1] + g * filterKernel[4] + b
					* filterKernel[7];
			float tb = r * filterKernel[2] + g * filterKernel[5] + b
					* filterKernel[8];
			r = tr;
			g = tg;
			b = tb;

			if (r < 0.f)
				r = 0.f;
			if (r > 255.f)
				r = 255.f;
			if (g < 0.f)
				g = 0.f;
			if (g > 255.f)
				g = 255.f;
			if (b < 0.f)
				b = 0.f;
			if (b > 255.f)
				b = 255.f;

			r = (r - mInBlack) * mOverInWMinInB;
			g = (g - mInBlack) * mOverInWMinInB;
			b = (b - mInBlack) * mOverInWMinInB;

			if (gammaValue != 1.0f) {
				r = (float) java.lang.Math.pow(r, gammaValue);
				g = (float) java.lang.Math.pow(g, gammaValue);
				b = (float) java.lang.Math.pow(b, gammaValue);
			}

			r = (r * mOutWMinOutB) + mOutBlack;
			g = (g * mOutWMinOutB) + mOutBlack;
			b = (b * mOutWMinOutB) + mOutBlack;

			if (r < 0.f)
				r = 0.f;
			if (r > 255.f)
				r = 255.f;
			if (g < 0.f)
				g = 0.f;
			if (g > 255.f)
				g = 255.f;
			if (b < 0.f)
				b = 0.f;
			if (b > 255.f)
				b = 255.f;

			mOutPixels[i] = ((int) r) + (((int) g) << 8) + (((int) b) << 16)
					+ (mInPixels[i] & 0xff000000);
		}

		resultBitmap.setPixels(mOutPixels, 0, resultBitmap.getWidth(), 0, 0,
				resultBitmap.getWidth(), resultBitmap.getHeight());
	}

	/**
	 * @param targetBitmap
	 * @param type
	 *            1 is Green-Blue, 2 is Red-Blue, 3 is Red - Green
	 */
	public static void switchColors(Bitmap targetBitmap, int type) {
		int width = targetBitmap.getWidth();
		int height = targetBitmap.getHeight();
		int[] srcPixels = new int[width * height];
		targetBitmap.getPixels(srcPixels, 0, width, 0, 0, width, height);
		int[] destPixels = new int[width * height];
		switch (type) {
		case 1:
			swapGreenBlue(srcPixels, destPixels);
			break;
		case 2:
			swapRedBlue(srcPixels, destPixels);
			break;
		case 3:
			swapRedGreen(srcPixels, destPixels);
			break;
		}
		targetBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
	}

	private static void swapGreenBlue(int[] src, int[] dest) {
		for (int i = 0; i < src.length; i++) {
			dest[i] = (src[i] & 0xffff0000) | ((src[i] & 0x000000ff) << 8)
					| ((src[i] & 0x0000ff00) >> 8);
		}
	}

	private static void swapRedBlue(int[] src, int[] dest) {
		for (int i = 0; i < src.length; i++) {
			dest[i] = (src[i] & 0xff00ff00) | ((src[i] & 0x000000ff) << 16)
					| ((src[i] & 0x00ff0000) >> 16);
		}
	}

	private static void swapRedGreen(int[] src, int[] dest) {
		for (int i = 0; i < src.length; i++) {
			dest[i] = (src[i] & 0xff0000ff) | ((src[i] & 0x0000ff00) << 8)
					| ((src[i] & 0x00ff0000) >> 8);
		}
	}

}
