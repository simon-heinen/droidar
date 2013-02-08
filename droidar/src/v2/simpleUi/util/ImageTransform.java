package v2.simpleUi.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;

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

		Bitmap result = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_4444);
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

	public static Bitmap createDummyBitmap() {
		int size = 128;
		Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
		Canvas c = new Canvas(b);
		Paint p = new Paint();

		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(20);
		int alpha = 255;
		p.setColor(Color.rgb(50, 0, 0));
		p.setAlpha(alpha);
		c.drawLine(0, 0, size, size, p);
		p.setColor(Color.BLUE);
		p.setAlpha(alpha);
		c.drawLine(0, size, size, 0, p);
		p.setColor(Color.RED);
		p.setAlpha(alpha);
		c.drawLine(0, size / 2, size, size / 2, p);
		p.setColor(Color.YELLOW);
		p.setAlpha(alpha);
		c.drawLine(size / 2, 0, size / 2, size, p);

		float[] filterKernel = { 1, 0, 0, 0, 1, 0, 0, 0, 1 };
		// improveSaturation(b, b, filterKernel, 1, 1, 1, 255,
		// 255);

		return b;
	}

	public static Bitmap createDummyBitmap2(View v) {
		int size = 128;
		Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
		Canvas c = new Canvas(b);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(Color.BLUE);
		p.setStyle(Paint.Style.FILL);
		p.setStrokeWidth(10);
		drawCircle(v, c, size / 2, size / 2, size / 2, p);
		return b;
	}

	/**
	 * use {@link ImageTransform#dipToPixels(Resources, Integer)} instead
	 * 
	 * @param sizeInDip
	 * @return size in pixels
	 */
	@Deprecated
	public static float dipToPixels(View v, float sizeInDip) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				sizeInDip, v.getResources().getDisplayMetrics());
	}

	/**
	 * Converts the dip into its equivalent pixel size
	 * 
	 * @param resources
	 *            get the ressources object e.g. via context.getRessources or
	 *            view.getRessources
	 * @param sizeInDip
	 * @return
	 */
	public static float dipToPixels(Resources resources, Integer sizeInDip) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				sizeInDip, resources.getDisplayMetrics());
	}

	/**
	 * The Eclipse UI editor cant preview ressources loaded from the assets
	 * folder so a dummy bitmap is used instead
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public static Bitmap loadBitmapFromIdInCustomView(View v, int id) {
		if (v.isInEditMode() || id == 0) {
			return createDummyBitmap();
		} else {
			return IO.loadBitmapFromId(v.getContext(), id);
		}
	}

	/**
	 * Use this method instead of
	 * {@link Canvas#drawCircle(float, float, float, Paint)} or the Eclipse UI
	 * Editor preview will be incorrect
	 * 
	 * @param canvas
	 * @param cx
	 * @param cy
	 * @param radius
	 * @param paint
	 */
	public static void drawCircle(View v, Canvas canvas, float cx, float cy,
			float radius, Paint paint) {
		if (v.isInEditMode()) {
			RectF arcRect = new RectF(cx - radius, cy - radius, cx + radius, cy
					+ radius);
			// Draw the Minutes-Arc into that rectangle
			canvas.drawArc(arcRect, -90, 360, false, paint);
		} else {
			canvas.drawCircle(cx, cy, radius, paint);
		}

	}

	/**
	 * TODO compare to createBitmapWithRoundCorners(..) and combine to one
	 * method
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	@Deprecated
	public static Bitmap createRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xff424242);
		canvas.drawRoundRect(new RectF(rect), pixels, pixels, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
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

	public static Bitmap makeSquare(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		if (width == height)
			return bitmap;

		int size = width > height ? width : height;

		Bitmap newB = Bitmap.createBitmap(size, size, bitmap.getConfig());
		Canvas c = new Canvas(newB);

		int left = 0;
		int top = 0;

		if (width < height)
			left = Math.abs((width - height) / 2);
		else
			top = Math.abs((width - height) / 2);

		c.drawBitmap(bitmap, left, top, new Paint());

		return newB;
	}

	public static Bitmap addMargin(Bitmap bitmap, int marginSize) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Bitmap newB = Bitmap.createBitmap(width + 2 * marginSize, height + 2
				* marginSize, bitmap.getConfig());
		Canvas c = new Canvas(newB);
		c.drawBitmap(bitmap, marginSize, marginSize, new Paint());

		return newB;
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
