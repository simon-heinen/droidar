package gui;

import util.IO;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Add the following constructor to your class which extends
 * {@link SimpleCustomView}<br>
 * <br>
 * 
 * //add @deprecated to remember not to use this constructor later<br>
 * public YourSimpleCustomViewSubclass(Context context, AttributeSet attrs) {<br>
 * super(context, attrs);<br>
 * yourInitStuffHere();<br>
 * }<br>
 * 
 * <br>
 * <br>
 * 
 * @author Spobo
 * 
 */
public abstract class SimpleCustomView extends View {

	private static final String LOG_TAG = "SimpleCustomView";

	/**
	 * The Eclipse UI editor cant preview ressources loaded from the assets
	 * folder so a dummy bitmap is used instead
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public Bitmap loadBitmapFromId(Context context, int id) {
		if (isInEditMode() || id == 0) {
			return createDummyBitmap();
		} else {
			return IO.loadBitmapFromId(context, id);
		}
	}

	public Bitmap createDummyBitmap() {
		int size = 128;
		Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
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
		// ImageTransform.improveSaturation(b, b, filterKernel, 1, 1, 1, 255,
		// 255);

		return b;
	}

	private void setRandomColor(Paint p, int alpha) {
		p.setColor(Color.argb(alpha, (int) (Math.random() * 255),
				(int) (Math.random() * 255), (int) (Math.random() * 255)));
	}

	public Bitmap createDummyBitmap2() {
		int size = 128;
		Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(Color.BLUE);
		p.setStyle(Paint.Style.FILL);
		p.setStrokeWidth(10);
		drawCircle(c, size / 2, size / 2, size / 2, p);
		return b;
	}

	public SimpleCustomView(Context context) {
		super(context);
	}

	public SimpleCustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measuredWidth = getDefaultSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		int measuredHeigth = getDefaultSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		onResizeEvent(measuredHeigth, measuredWidth);
	}

	/**
	 * Converts the dip into its equivalent pixel size
	 * 
	 * @param sizeInDip
	 * @return size in pixels
	 */
	public float dipToPixels(float sizeInDip) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				sizeInDip, getResources().getDisplayMetrics());
	}

	/**
	 * When the view wants to resize it will call this method with the
	 * recommended size values. the final size then has to be set via the
	 * following call:
	 * 
	 * <br>
	 * <br>
	 * this.setMeasuredDimension(newHeight, newWidth);<br>
	 * 
	 * @param recommendedHeigth
	 * @param recommendedWidth
	 */
	public abstract void onResizeEvent(int recommendedHeight,
			int recommendedWidth);

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
	public void drawCircle(Canvas canvas, float cx, float cy, float radius,
			Paint paint) {
		if (isInEditMode()) {
			RectF arcRect = new RectF(cx - radius, cy - radius, cx + radius, cy
					+ radius);
			// Draw the Minutes-Arc into that rectangle
			canvas.drawArc(arcRect, -90, 360, false, paint);
		} else {
			canvas.drawCircle(cx, cy, radius, paint);
		}

	}

}
