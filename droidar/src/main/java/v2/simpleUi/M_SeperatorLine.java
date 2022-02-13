package v2.simpleUi;

import v2.simpleUi.util.BGUtils;
import v2.simpleUi.util.ImageTransform;
import android.content.Context;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public abstract class M_SeperatorLine implements ModifierInterface {

	private LinearLayout line;

	@Override
	public View getView(Context context) {
		line = new LinearLayout(context);
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
				(int) ImageTransform.dipToPixels(line.getResources(),
						getHeigthInDip()));
		int p = 10;
		lp.setMargins(p, 2 * p, p, 2 * p);
		line.setLayoutParams(lp);
		loadBGUtils().applyTo(line);
		return line;
	}

	public BGUtils loadBGUtils() {
		int[] colorsInGradient = BGUtils.createGrayGradient3();
		BGUtils bgUtils = new BGUtils(Orientation.LEFT_RIGHT, colorsInGradient,
				BGUtils.genCornerArray(2));
		return bgUtils;
	}

	public LinearLayout getLine() {
		return line;
	}

	public abstract Integer getHeigthInDip();

	@Override
	public boolean save() {
		return true;
	}

}
