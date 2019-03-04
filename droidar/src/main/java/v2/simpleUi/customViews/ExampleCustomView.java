package v2.simpleUi.customViews;

import v2.simpleUi.util.ImageTransform;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ExampleCustomView extends View {

	private static final int DEFAULT_MAX_WIDTH_IN_DIP = 80;
	private static final float DEFAULT_EDITOR_MAX_WIDTH_IN_DIP = 180;

	private static final String LOG_TAG = "GameElementView";

	private Paint paint;
	private Paint loadingPaint;
	private Paint loadingLinePaint;

	float myLoadingAngle = 0;

	private Bitmap myIcon;
	private Bitmap mutable;
	private Canvas stampCanvas;

	private int myWidth;
	private int myHeight;

	private Xfermode myXfermode;
	private RectF arcRect;
	private int myMaxWidth;

	// private String debug;

	public ExampleCustomView(Context context, int iconid) {
		super(context);
		init((int) ImageTransform.dipToPixels(this, DEFAULT_MAX_WIDTH_IN_DIP),
				ImageTransform.loadBitmapFromIdInCustomView(this, iconid));
	}

	@Deprecated
	public ExampleCustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init((int) ImageTransform.dipToPixels(this,
				DEFAULT_EDITOR_MAX_WIDTH_IN_DIP),
				ImageTransform.createDummyBitmap());
	}

	public void setIcon(Bitmap icon) {
		myIcon = icon;
		resizeIconToViewSize();
	}

	private void resizeIconToViewSize() {
		if (myIcon != null) {
			myIcon = ImageTransform.resizeBitmap(myIcon, myHeight, myWidth);
			myIcon = ImageTransform.createBitmapWithRoundCorners(myIcon, 8f);
		}
	}

	private void drawLoadingCircle(Canvas canvas, Paint paint) {
		if (myLoadingAngle != 0 && myLoadingAngle != 360) {

			// Draw the Minutes-Arc into that rectangle
			canvas.drawArc(arcRect, -90, myLoadingAngle, true, paint);
		}
	}

	private void init(int maxWidth, Bitmap icon) {

		paint = new Paint();
		myXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
		loadingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		loadingPaint.setColor(Color.RED);
		loadingPaint.setAlpha(100);

		loadingLinePaint = new Paint();
		loadingLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		loadingLinePaint.setColor(Color.BLACK);
		loadingLinePaint.setStyle(Paint.Style.STROKE);
		loadingLinePaint.setStrokeWidth(3);

		myMaxWidth = maxWidth;
		setSize(maxWidth, icon);

		if (isInEditMode())
			loadDemoValues();
		setIcon(icon);
	}

	public void setSize(int newWidth, Bitmap icon) {
		myWidth = newWidth;
		if (myWidth > myMaxWidth)
			myWidth = myMaxWidth;
		if (icon != null) {
			myHeight = (int) ((float) (icon.getHeight())
					/ (float) (icon.getWidth()) * (float) (myWidth));
		} else {
			myHeight = myWidth;
		}
		Log.w(LOG_TAG, "New height=" + myHeight);
		Log.w(LOG_TAG, "New width=" + myWidth);

		if (myHeight <= 0 || myWidth <= 0) {
			Log.e(LOG_TAG, "height or width were 0!");
			Log.w(LOG_TAG, "   > icon=" + icon);
			Log.w(LOG_TAG, "   > icon.getHeight()=" + icon.getHeight());
			Log.w(LOG_TAG, "   > icon.getWidth()=" + icon.getWidth());
			Log.w(LOG_TAG, "   > recommendedWidth=" + newWidth);
			showDebugInfos();
		}

		float x = myWidth * 0.5f;
		arcRect = new RectF(-x, -x, myWidth + x, myHeight + x);

		mutable = Bitmap.createBitmap(myWidth, myHeight,
				Bitmap.Config.ARGB_8888);
		stampCanvas = new Canvas(mutable);
		resizeIconToViewSize();
	}

	public void showDebugInfos() {
		Log.w(LOG_TAG, "   > myHeight=" + myHeight);
		Log.w(LOG_TAG, "   > myWidth=" + myWidth);
		Log.w(LOG_TAG, "   > myIcon=" + myIcon);
		Log.w(LOG_TAG, "   > myLoadingAngle=" + myLoadingAngle);
	}

	/**
	 * This method will only be called when the view is displayed in the eclipse
	 * xml layout editor
	 */
	private void loadDemoValues() {
		setLoadingAngle(230);
	}

	public void setLoadingAngle(float myLoadingAngle) {
		this.myLoadingAngle = myLoadingAngle;
		this.postInvalidate();
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

	public void onResizeEvent(int recommendedHeight, int recommendedWidth) {
		int min = Math.min(recommendedHeight, recommendedHeight);
		Log.i(LOG_TAG, "New recommended width=" + recommendedWidth);
		Log.i(LOG_TAG, "New recommended heigth=" + recommendedHeight);
		Log.d(LOG_TAG, "Choosen minimum=" + min);
		setSize(min, myIcon);
		setMeasuredDimension(myWidth, myHeight);
	}

	@Override
	protected void onDraw(Canvas onDrawCanvas) {

		// stampCanvas.drawARGB(0, 0, 0, 0);
		stampCanvas.drawBitmap(myIcon, 0, 0, paint);
		// Bitmap i2 = generateDebugImage2(getContext());
		// canvas.drawBitmap(i2, 0, 0, paint);
		drawLoadingCircle(stampCanvas, loadingPaint);
		drawLoadingCircle(stampCanvas, loadingLinePaint);
		paint.setXfermode(myXfermode);
		stampCanvas.drawBitmap(myIcon, 0, 0, paint);
		paint.setXfermode(null);

		onDrawCanvas.drawBitmap(mutable, 0, 0, paint);

		// if (debug != null) { // TODO remove this
		// paint.setColor(Color.RED);
		// canvas.drawText(debug, 0, myHalfSize, paint);
		// }
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return onTouch(event.getX() - myWidth / 2, event.getY() - myHeight / 2);
	}

	private boolean onTouch(float x, float y) {
		double distFromCenter = Math.sqrt(x * x + y * y);

		setLoadingAngle((float) (Math.random() * 359));
		postInvalidate();
		return true;
	}

}
