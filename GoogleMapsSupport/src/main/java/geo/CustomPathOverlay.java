package geo;

import util.EfficientListQualified;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/**
 * normaly you should use {@link CustomItemizedOverlay} because with this
 * overlay you cant select items on the {@link GMap}. but if you want to display
 * a path you will need this overlay and combine it with a
 * {@link CustomItemizedOverlay} for the path-waypoints if you want to be able
 * to select those
 * 
 * @author Spobo
 * 
 */
public class CustomPathOverlay extends com.google.android.maps.Overlay {

	GeoGraph myGraph;
	private boolean drawIcons;
	private boolean drawEdges;
	private boolean isPath;
	private Paint paintSettings;

	public CustomPathOverlay(GeoGraph l, boolean drawIcons) {
		myGraph = l;
		this.drawIcons = drawIcons;
		isPath = myGraph.isPath();
		drawEdges = myGraph.isUsingItsEdges();
		paintSettings = loadPaintSettings(l);
	}

	private Paint loadPaintSettings(GeoGraph l) {
		// TODO load from graph
		Paint mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(l.getInfoObject().getColor().toIntARGB());
		mPaint.setAlpha(150);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeWidth(3);
		return mPaint;
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {

		EfficientListQualified<GeoObj> nodes = myGraph.getAllItems();

		Path path = null;
		Point lastPoint = null;
		GeoObj lastGeoObj = null;
		for (int i = 0; i < nodes.myLength; i++) {
			GeoObj x = nodes.get(i);
			// ---translate the GeoPoint to screen pixels---
			Point currentPoint = new Point();
			mapView.getProjection().toPixels(GMap.toGeoPoint(x), currentPoint);
			if (isPath && lastPoint != null) {
				if (path == null)
					path = new Path();
				path.moveTo(lastPoint.x, lastPoint.y);
				path.lineTo(currentPoint.x, currentPoint.y);
				canvas.drawPath(path, paintSettings);
			} else if (drawEdges && lastPoint != null) {
				if (myGraph.hasEdge(lastGeoObj, x) != -1) {
					if (path == null)
						path = new Path();
					path.moveTo(lastPoint.x, lastPoint.y);
					path.lineTo(currentPoint.x, currentPoint.y);
					canvas.drawPath(path, paintSettings);
				}
			}
			lastPoint = currentPoint;
			lastGeoObj = x;
			if (drawIcons) {
				Bitmap bmp = x.getInfoObject().getIcon(mapView.getContext());
				canvas.drawBitmap(bmp, currentPoint.x,
						currentPoint.y - bmp.getHeight(), null);
			}
		}
		super.draw(canvas, mapView, shadow, when);
		return false; // return false is correct, see javadoc
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		// TODO implement this or force develp. to use customItemizedOverlay?
		return super.onTap(p, mapView);
	}

}
