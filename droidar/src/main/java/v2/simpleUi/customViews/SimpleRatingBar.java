package v2.simpleUi.customViews;

import java.util.ArrayList;
import java.util.List;

import v2.simpleUi.util.ColorCollections;
import v2.simpleUi.util.IO;
import v2.simpleUi.util.ImageTransform;
import v2.simpleUi.util.NameGenerator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleRatingBar extends TextView {

	private static final String LOG_TAG = "SimpleRatingBar";

	public interface RatingItem {

		String getName();

		boolean isSpam();

		/**
		 * will only be called if the item is not marked as spam or as cleared
		 * 
		 * @return e.g. 100 for 100%
		 */
		int getRatingInPercent();

		boolean isCleared();

		void onClearClick();

		void onSpamClick();

		void onRatingSend(int rating);

		boolean save();

	}

	public static RatingItem newDefaultRatingItem(final String name) {

		return new RatingItem() {

			boolean isCleared = true;
			boolean isSpam = false;
			private int rating = (int) (Math.random() * 100);

			@Override
			public String getName() {
				return name;
			}

			@Override
			public boolean isSpam() {
				return isSpam;
			}

			@Override
			public int getRatingInPercent() {
				return rating;
			}

			@Override
			public boolean isCleared() {
				return isCleared;
			}

			@Override
			public void onClearClick() {
				isSpam = false;
				isCleared = true;
			}

			@Override
			public void onSpamClick() {
				isSpam = true;
				isCleared = false;
			}

			@Override
			public void onRatingSend(int rating) {
				this.rating = rating;
				isSpam = false;
				isCleared = false;
			}

			@Override
			public boolean save() {
				return true;
			}

		};
	}

	private static final int DEFAULT_HEIGTH_PER_LINE_IN_DIP = 60;
	private static final int DEFAULT_PADDING = 10;

	private static final int TEXT_SIZE_DEFAULT_IN_DIP = 22;
	private static final float STEP_SIZE_FOR_TEXT_SIZE_STEPS = 2.5f;

	private static final int SHADOW_LAYER_SIZE = 1;
	private static final float LINE_THICKNES = 3;

	private static final int bonusButtonWidth = 8;
	private static final int ICON_ALPHA = 130;
	private static final int ICON_MARGIN = 30;

	private static final int borderColor = ColorCollections.l2pBlueDark;
	// private static final int spamBoxCheckedColor =
	// ColorCollections.l2pGrayLight;
	private static final int spamBoxCheckedColor = ColorCollections.l2pOrange;
	private static final int badColor = ColorCollections.l2pGray;
	private static final int goodColor = ColorCollections.l2pGrayLight;
	private static final int clearedStartColor = ColorCollections.l2pBlue;
	private static final int clearedEndColor = ColorCollections.l2pBlueDark;

	private static final int spamBoxNotCheckedColor = badColor;

	private Bitmap clearIcon;
	private Bitmap spamIcon;
	private Bitmap badIcon;
	private Bitmap goodIcon;

	int xWidth;
	int x1Size;
	int xLeftStartPos;
	int yTopStartPos;
	int yHeight;
	int y1Size;

	private List<RatingItem> items;
	private List<Bitmap> itemRatingBars;

	private Paint iconPaint;
	private Paint linePaint;
	private Paint boxBorderPaint;
	private Paint clearBoxPaint;
	private Paint spamBoxCheckedPaint;
	private Paint spamBoxNotCheckedPaint;
	private Paint ratingPaint;
	private TextPaint textPaint;

	// @Deprecated
	// private Paint backgroundPaint;

	private int clearIconId;
	private int spamIconId;
	private int badIconId;
	private int goodIconId;

	public SimpleRatingBar(Context context, int clear, int spam, int bad,
			int good, ArrayList<RatingItem> attributeNames) {
		super(context);
		clearIconId = clear;
		spamIconId = spam;
		badIconId = bad;
		goodIconId = good;
		init(context, attributeNames);
	}

	public SimpleRatingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, null);
	}

	public SimpleRatingBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		initSimpleRatingBarSpecificViewParamsForTheTextView();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	private int dipToPixels(int dipValue) {
		Resources r = getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dipValue, r.getDisplayMetrics());
		return px;
	}

	private void initSimpleRatingBarSpecificViewParamsForTheTextView() {
		int completeHeigth = DEFAULT_HEIGTH_PER_LINE_IN_DIP * items.size();
		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, dipToPixels(completeHeigth));
		setLayoutParams(p);
		setGravity(Gravity.CENTER_HORIZONTAL);
		setShadowLayer(SHADOW_LAYER_SIZE, 1, 1, Color.BLACK);

		setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);
	}

	private void init(Context context, ArrayList<RatingItem> attributeNames) {
		initBitmaps(context);
		if (isInEditMode() || attributeNames == null) {
			items = loadExampleNames();
		} else {
			items = attributeNames;
		}
		initPaints();
	}

	private void initBitmaps(Context context) {
		if (isInEditMode() || clearIconId == 0 || spamIconId == 0
				|| badIconId == 0 || goodIconId == 0) {
			Bitmap b = ImageTransform.createDummyBitmap();
			clearIcon = b;
			spamIcon = b;
			badIcon = b;
			goodIcon = b;
		} else {
			clearIcon = loadAndProcessImage(context, clearIconId);
			spamIcon = loadAndProcessImage(context, spamIconId);
			badIcon = loadAndProcessImage(context, badIconId);
			goodIcon = loadAndProcessImage(context, goodIconId);
		}
	}

	private Bitmap loadAndProcessImage(Context context, int id) {
		Bitmap b = IO.loadBitmapFromId(context, id);
		{
			Bitmap oldReference = b;
			b = ImageTransform.makeSquare(b);
			if (b != oldReference)
				if (!isInEditMode())
					oldReference.recycle();
		}
		// {
		// Bitmap oldReference = b;
		// b = ImageTransform.addMargin(b, ICON_MARGIN);
		// if (b != oldReference)
		// if (!isInEditMode())
		// oldReference.recycle();
		// }

		return b;
	}

	private void initPaints() {

		iconPaint = new Paint();
		iconPaint.setAlpha(ICON_ALPHA);

		iconPaint.setAntiAlias(true);

		// backgroundPaint = new Paint();
		// backgroundPaint.setAlpha(255);

		linePaint = new Paint();
		boxBorderPaint = new Paint();
		boxBorderPaint.setColor(borderColor);
		boxBorderPaint.setStyle(Style.STROKE);
		boxBorderPaint.setStrokeWidth(LINE_THICKNES);
		boxBorderPaint.setAntiAlias(true);

		clearBoxPaint = new Paint();
		spamBoxCheckedPaint = new Paint();
		spamBoxCheckedPaint.setColor(spamBoxCheckedColor);
		spamBoxNotCheckedPaint = new Paint();
		spamBoxNotCheckedPaint.setColor(spamBoxNotCheckedColor);

		textPaint = new TextPaint();
		textPaint.setColor(getCurrentTextColor());
		textPaint.setShadowLayer(SHADOW_LAYER_SIZE, 1, 1, Color.BLACK);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setAntiAlias(true);

		ratingPaint = new Paint();
		ratingPaint.setAntiAlias(true);
	}

	private void updateGradients() {
		ratingPaint.setShader(new LinearGradient(2 * x1Size, 0, xWidth, y1Size,
				badColor, goodColor, Shader.TileMode.CLAMP));
		clearBoxPaint.setShader(new LinearGradient(0, 0, xWidth, y1Size,
				clearedStartColor, clearedEndColor, Shader.TileMode.CLAMP));
	}

	private ArrayList<RatingItem> loadExampleNames() {

		NameGenerator g = new NameGenerator();
		ArrayList<RatingItem> n = new ArrayList<RatingItem>();
		for (int i = 0; i < 10; i++) {
			String name = g.getName();
			for (int j = 0; j < Math.random() * 5; j++) {
				name += " " + g.getName();
			}
			n.add(newDefaultRatingItem(name));
		}
		return n;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);

		if (updateWidthAndHeigth(getWidth(), getHeight())) {
			updateFieldsAndIcons();
			updateBars();
		}

		// canvas.drawRect(xLeftStartPos, yTopStartPos, getWidth()
		// - getPaddingRight(), getHeight() - getPaddingBottom(),
		// backgroundPaint);

		int currentYPos = yTopStartPos;
		for (int i = 0; i < itemRatingBars.size(); i++) {
			canvas.drawBitmap(itemRatingBars.get(i), xLeftStartPos,
					currentYPos, linePaint);
			currentYPos += y1Size;
		}
	}

	private boolean updateWidthAndHeigth(int width, int height) {
		width = width - getPaddingLeft() - getPaddingRight();
		height = height - getPaddingTop() - getPaddingBottom();
		xLeftStartPos = getPaddingLeft();
		yTopStartPos = getPaddingTop();
		boolean result = false;
		if (xWidth != width) {
			xWidth = width;
			result = true;
		}
		if (yHeight != height) {
			yHeight = height;
			result = true;
		}
		return result;
	}

	/**
	 * Call this to update the UI
	 */
	private void refreshCompleteUI() {
		updateWidthAndHeigth(getWidth(), getHeight());
		// dont call updateFieldsAndIcons();
		updateBars();
		postInvalidate();
	}

	private void updateBars() {
		if (itemRatingBars == null)
			itemRatingBars = new ArrayList<Bitmap>();
		else
			itemRatingBars.clear();

		for (int i = 0; i < items.size(); i++) {
			itemRatingBars.add(createLineBitmap(items.get(i)));
		}

	}

	private void updateFieldsAndIcons() {
		y1Size = yHeight / items.size();
		x1Size = y1Size + bonusButtonWidth;

		updateGradients();

		Bitmap oldReference = spamIcon;
		spamIcon = resizeImageAndAddMargin(spamIcon);
		if (!isInEditMode())
			oldReference.recycle();
		oldReference = clearIcon;
		clearIcon = resizeImageAndAddMargin(clearIcon);
		if (!isInEditMode())
			oldReference.recycle();
		oldReference = badIcon;
		badIcon = resizeImageAndAddMargin(badIcon);
		if (!isInEditMode())
			oldReference.recycle();
		oldReference = goodIcon;
		goodIcon = resizeImageAndAddMargin(goodIcon);
		if (!isInEditMode())
			oldReference.recycle();
	}

	private Bitmap resizeImageAndAddMargin(Bitmap b) {
		Bitmap oldReference = b;
		b = ImageTransform.resizeBitmap(b, y1Size, y1Size);
		if (!isInEditMode())
			oldReference.recycle();
		return b;
	}

	private Bitmap createLineBitmap(RatingItem item) {

		Log.d(LOG_TAG, "new bitmapt for " + item.getName());

		int xPos1 = 0;
		int xPos2 = x1Size;
		int xPos3 = x1Size * 2;
		int xPos4 = xWidth - x1Size;
		int yPos = 0;
		Bitmap lineBitmap = Bitmap.createBitmap(xWidth, yHeight,
				Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(lineBitmap);

		if (item.isCleared()) {
			canvas.drawRect(xPos1, 0, xWidth, y1Size, clearBoxPaint);
		} else if (item.isSpam()) {
			canvas.drawRect(xPos2, 0, xPos3, y1Size, spamBoxCheckedPaint);
		} else {
			canvas.drawRect(xPos2, 0, xPos3, y1Size, spamBoxNotCheckedPaint);
			drawRatingBar(item, xPos3, canvas);
		}
		canvas.drawRect(xPos1, 0, xPos2, y1Size, clearBoxPaint);
		canvas.drawBitmap(clearIcon, xPos1 + bonusButtonWidth / 2, yPos,
				iconPaint);
		canvas.drawRect(xPos1, 0, xPos2, y1Size, boxBorderPaint);

		canvas.drawBitmap(spamIcon, xPos2 + bonusButtonWidth / 2, yPos,
				iconPaint);
		canvas.drawRect(xPos2, 0, xPos3 - 1, y1Size, boxBorderPaint);

		canvas.drawBitmap(badIcon, xPos3 + bonusButtonWidth, yPos, iconPaint);
		canvas.drawBitmap(goodIcon, xPos4, yPos, iconPaint);

		drawItemText(item, xPos3, canvas);

		canvas.drawRect(xPos3, 0, xWidth - 1, y1Size, boxBorderPaint);
		return lineBitmap;
	}

	private void drawItemText(RatingItem item, int xTextStartPos, Canvas canvas) {
		String text = item.getName();
		if (isInEditMode()) {
			int textCenterX = (xWidth + 2 * x1Size) / 2;
			int textCenterY = 0;
			textCenterY = y1Size - 10;
			textPaint.setTextSize(dipToPixels(TEXT_SIZE_DEFAULT_IN_DIP));
			canvas.drawText(text, textCenterX, textCenterY, textPaint);
		} else {
			int yStart = 0;
			int textAreaWidth = xWidth - 2 * x1Size;
			int textAreaHeigth = y1Size;
			drawText(canvas, xTextStartPos, yStart, textAreaWidth,
					textAreaHeigth, text, textPaint,
					dipToPixels(TEXT_SIZE_DEFAULT_IN_DIP),
					STEP_SIZE_FOR_TEXT_SIZE_STEPS);
		}
	}

	private static void drawText(Canvas canvas, int xStart, int yStart,
			int xWidth, int yHeigth, String textToDisplay,
			TextPaint paintToUse, float startTextSizeInPixels,
			float stepSizeForTextSizeSteps) {

		// Text view line spacing multiplier
		float mSpacingMult = 1.0f;
		// Text view additional line spacing
		float mSpacingAdd = 0.0f;
		StaticLayout l = null;
		do {
			paintToUse.setTextSize(startTextSizeInPixels);
			startTextSizeInPixels -= stepSizeForTextSizeSteps;
			l = new StaticLayout(textToDisplay, paintToUse, xWidth,
					Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
		} while (l.getHeight() > yHeigth);

		int textCenterX = xStart + (xWidth / 2);
		int textCenterY = (yHeigth - l.getHeight()) / 2;

		canvas.save();
		canvas.translate(textCenterX, textCenterY);
		l.draw(canvas);
		canvas.restore();
	}

	private void drawRatingBar(RatingItem item, int xPos3, Canvas canvas) {
		float rating = item.getRatingInPercent();
		int ratingBarLength = (int) (rating / 100f * (xWidth - 2 * x1Size));
		canvas.drawRect(xPos3, 0, xPos3 + ratingBarLength, y1Size, ratingPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getX() >= 2 * x1Size) {
			// tryToSwitchOfParentScrollView();
			getParent().requestDisallowInterceptTouchEvent(true);
			sendRatingEvent(event.getX(), event.getY());
			return true;
		} else {
			getParent().requestDisallowInterceptTouchEvent(false);
			return buttonGestureDetector.onTouchEvent(event);
		}
	}

	private void sendRatingEvent(float x, float y) {
		int nr = (int) (y / y1Size);
		if (nr >= 0 && nr < items.size()) {
			RatingItem i = items.get(nr);
			int rating = (int) (100 * (x - 2 * x1Size) / (xWidth - 2 * x1Size));
			if (rating > 100)
				rating = 100;

			if (i.getRatingInPercent() != rating) {
				i.onRatingSend(rating);
				updateBitmatForItemNr(nr, i);
			}
		}
	}

	private GestureDetector buttonGestureDetector = new GestureDetector(
			new GestureDetector.SimpleOnGestureListener() {

				public boolean onDown(MotionEvent e) {
					return true;
				};

				public boolean onSingleTapUp(MotionEvent e) {
					sendClichEvent(e.getX(), e.getY());
					return true;
				}

			});

	private void sendClichEvent(float x, float y) {
		int nr = (int) (y / y1Size);
		if (nr >= 0 && nr < items.size()) {
			if (x < x1Size) {
				// Clear click
				RatingItem i = items.get(nr);
				i.onClearClick();
				updateBitmatForItemNr(nr, i);
			} else if (x < 2 * x1Size) {
				// spam click
				RatingItem i = items.get(nr);
				i.onSpamClick();
				updateBitmatForItemNr(nr, i);
			}
		}
	}

	private void updateBitmatForItemNr(int nr, RatingItem i) {
		itemRatingBars.set(nr, createLineBitmap(i));
		postInvalidate();
	}

}
