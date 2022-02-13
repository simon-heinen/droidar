package v2.simpleUi.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public abstract class DragAndDropListener implements OnTouchListener {

	private RelativeLayout container;
	private ImageView imageView;
	private LayoutParams params;

	@Override
	public boolean onTouch(View v, MotionEvent motionEvent) {
		return drag(motionEvent, v);
	}

	private boolean drag(MotionEvent event, View v) {

		v.getParent().requestDisallowInterceptTouchEvent(true);

		Activity activity = (Activity) v.getContext();

		LayoutParams params = getDragDropContainerParams(activity);
		RelativeLayout container = getDragDropContainer(activity);

		ImageView imageView = getImageView(v, activity, container, params);

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE: {
			updatePos(event, v, params, imageView);
			break;
		}
		case MotionEvent.ACTION_UP: {
			updatePos(event, v, params, imageView);
			container.setVisibility(View.GONE);
			onElementDropped(event.getRawX(), event.getRawY());
			break;
		}
		case MotionEvent.ACTION_DOWN: {
			updatePos(event, v, params, imageView);
			container.setVisibility(View.VISIBLE);
			break;
		}
		}
		return true;
	}

	public abstract void onElementDropped(float rawX, float rawY);

	public void updatePos(MotionEvent event, View v, LayoutParams params,
			ImageView imageView) {
		params.topMargin = (int) event.getRawY() - (v.getHeight());
		params.leftMargin = (int) event.getRawX() - (v.getWidth() / 2);
		imageView.setLayoutParams(params);
	}

	private ImageView getImageView(View source, Activity activity,
			RelativeLayout c, LayoutParams params) {
		if (imageView == null) {
			final Bitmap bitmap = IO.loadBitmapFromView(source);
			final Paint shadowPaint = new Paint();
			shadowPaint.setAlpha(180);
			// http://stackoverflow.com/questions/7048941/how-to-use-the-lightingcolorfilter-to-make-the-image-form-dark-to-light
			shadowPaint.setColorFilter(new LightingColorFilter(0x11333333,
					0x00000000));

			imageView = new ImageView(activity) {

				@Override
				protected void onDraw(Canvas canvas) {
					// First draw shadow
					float dist = 2;
					canvas.drawBitmap(bitmap, dist, dist, shadowPaint);
					// then draw normal image view
					super.onDraw(canvas);
				}
			};
			imageView.setImageBitmap(bitmap);
			c.addView(imageView);
		}
		return imageView;
	}

	private android.widget.RelativeLayout.LayoutParams getDragDropContainerParams(
			Context context) {
		if (params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		}
		return params;
	}

	private RelativeLayout getDragDropContainer(Activity activity) {
		if (container == null) {
			container = new RelativeLayout(activity);
			activity.addContentView(container, params);
		}
		return container;
	}

}
