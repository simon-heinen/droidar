package v2.simpleUi;

import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import v2.simpleUi.util.ImageTransform;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class M_HalfHalf implements ModifierInterface, UiDecoratable {

	private ModifierInterface myLeft;
	private ModifierInterface myRight;
	private UiDecorator myDecorator;
	private Integer minimumHeigthInDip;
	private boolean bothViewsSameHeigth;

	private float weightOfLeft = 1;
	private float weightOfRight = 1;

	public M_HalfHalf(ModifierInterface left, ModifierInterface right) {
		myLeft = left;
		myRight = right;
	}

	public M_HalfHalf(ModifierInterface left, ModifierInterface right,
			float weightOfLeft, float weightOfRight) {
		this(left, right);
		setWeightOfLeft(weightOfLeft);
		setWeightOfRight(weightOfRight);

	}

	public M_HalfHalf(ModifierInterface left, ModifierInterface right,
			int minimumLineHeigthInDIP, boolean bothViewsSameHeigth) {
		this(left, right);
		this.minimumHeigthInDip = minimumLineHeigthInDIP;
		this.bothViewsSameHeigth = bothViewsSameHeigth;
	}

	public void setWeightOfLeft(float weightOfLeft) {
		this.weightOfLeft = weightOfLeft;
	}

	public void setWeightOfRight(float weightOfRight) {
		this.weightOfRight = weightOfRight;
	}

	@Override
	public View getView(Context context) {

		LinearLayout l = new LinearLayout(context);
		l.setPadding(l.getPaddingLeft(), l.getPaddingTop() + 10,
				l.getPaddingRight(), l.getPaddingBottom());
		l.setGravity(Gravity.CENTER_VERTICAL);

		if (minimumHeigthInDip != null) {
			// params = new LinearLayout.LayoutParams(
			// LayoutParams.FILL_PARENT,
			// MeasureSpec.makeMeasureSpec(
			// (int) ImageTransform.dipToPixels(
			// context.getResources(), minimumHeigthInDip),
			// MeasureSpec.EXACTLY), 1);

			l.setMinimumHeight((int) ImageTransform.dipToPixels(
					context.getResources(), minimumHeigthInDip));
		}

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, l, level + 1,
					UiDecorator.TYPE_CONTAINER);
			myDecorator.setCurrentLevel(level + 1);
		}

		View left = myLeft.getView(context);
		View right = myRight.getView(context);

		l.addView(left);
		l.addView(right);

		if (bothViewsSameHeigth) {
			LayoutParams lparams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
					weightOfLeft);
			LayoutParams rparams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
					weightOfRight);
			left.setLayoutParams(lparams);
			right.setLayoutParams(rparams);
			int h = (int) ImageTransform.dipToPixels(context.getResources(),
					minimumHeigthInDip);
			left.setMinimumHeight(h);
			right.setMinimumHeight(h);
		} else {
			// TODO or always use FILL_PARENT for height?
			LayoutParams lparams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					weightOfLeft);
			LayoutParams rparams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					weightOfRight);
			left.setLayoutParams(lparams);
			right.setLayoutParams(rparams);
		}

		if (myDecorator != null) {
			myDecorator.setCurrentLevel(myDecorator.getCurrentLevel() - 1);
		}

		return l;
	}

	@Override
	public boolean save() {
		return myLeft.save() && myRight.save();
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		boolean l = false;
		boolean r = false;
		if (myLeft instanceof UiDecoratable) {
			l = ((UiDecoratable) myLeft).assignNewDecorator(decorator);
		}
		if (myRight instanceof UiDecoratable) {
			r = ((UiDecoratable) myRight).assignNewDecorator(decorator);
		}
		return l && r;
	}

}
