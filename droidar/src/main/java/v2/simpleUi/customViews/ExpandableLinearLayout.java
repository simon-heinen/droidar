package v2.simpleUi.customViews;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class ExpandableLinearLayout extends LinearLayout {

	// How long the animation should take
	private final int mAnimationDuration = 600;
	private static final int MOST_OUTER_PADDING = 3;

	private Integer expandedHeight;
	private Integer collapsedHeight = 136;

	private OnExpandListener mListener;
	private boolean isNotJetDrawn = true;

	public ExpandableLinearLayout(Context context) {
		this(context, null, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (expandedHeight == null || expandedHeight == 0) {
			expandedHeight = getHeight();
		}
	}

	public ExpandableLinearLayout(Context context, Integer collapsedHeight,
			OnExpandListener onExpandListener) {
		super(context);

		LayoutParams layParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);

		setLayoutParams(layParams);
		setPadding(MOST_OUTER_PADDING, MOST_OUTER_PADDING, MOST_OUTER_PADDING,
				MOST_OUTER_PADDING);
		setOrientation(LinearLayout.VERTICAL);
		mListener = onExpandListener;

		// How high the content should be in "collapsed" state
		if (collapsedHeight != null) {
			setCollapsedHeight(collapsedHeight);
		}
	}

	public void setOnExpandListener(OnExpandListener listener) {
		mListener = listener;
	}

	public void setCollapsedHeight(int collapsedHeight) {
		this.collapsedHeight = collapsedHeight;
	}

	@Override
	protected void onAnimationEnd() {
		if (getHeight() != collapsedHeight) {
			expandedHeight = getHeight();
			if (mListener != null) {
				mListener.onExpandFinished(getContext(),
						ExpandableLinearLayout.this);
			}
		} else {
			if (mListener != null) {
				mListener.onCollapseFinished(getContext(),
						ExpandableLinearLayout.this);
			}
		}
		super.onAnimationEnd();
	}

	public void switchBetweenCollapsedAndExpandedMode() {
		if (expandedHeight == null) {
			expandedHeight = getHeight();
		}
		if (getHeight() > collapsedHeight) {
			collapse();
		} else {
			expand();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (mListener != null && isNotJetDrawn) {
			isNotJetDrawn = false;
			mListener.onViewWasDrawnFirstTime(getContext(), this);
		}
	}

	public void collapse() {
		if (mListener != null) {
			mListener.onExpandStart(getContext(), this);
		}
		runAnnimation(createCollapseAction());
	}

	public void expand() {
		if (mListener != null) {
			mListener.onCollapseStart(getContext(), this);
		}
		runAnnimation(createExpandAction());
	}

	private Animation createExpandAction() {
		Animation a;
		a = new ExpandAnimation(collapsedHeight, expandedHeight);
		return a;
	}

	private Animation createCollapseAction() {
		Animation a;
		if (expandedHeight == null) {
			expandedHeight = getHeight();
		}
		a = new ExpandAnimation(expandedHeight, collapsedHeight);
		return a;
	}

	private void runAnnimation(Animation a) {
		a.setDuration(mAnimationDuration);
		// Need to do this or else the animation will not play if the height
		// is 0:
		if (getLayoutParams().height == 0) {
			android.view.ViewGroup.LayoutParams lp = getLayoutParams();
			lp.height = 1;
			setLayoutParams(lp);
			requestLayout();
		}
		startAnimation(a);
	}

	private class ExpandAnimation extends Animation {
		private final int mStartHeight;
		private final int mDeltaHeight;

		public ExpandAnimation(int startHeight, int endHeight) {
			mStartHeight = startHeight;
			mDeltaHeight = endHeight - startHeight;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			android.view.ViewGroup.LayoutParams lp = getLayoutParams();
			lp.height = (int) (mStartHeight + mDeltaHeight * interpolatedTime);
			setLayoutParams(lp);
		}

		@Override
		public boolean willChangeBounds() {
			return true;
		}
	}

	public interface OnExpandListener {
		public void onExpandFinished(Context context, View v);

		public void onViewWasDrawnFirstTime(Context context,
				ExpandableLinearLayout expandableLinearLayout);

		public void onCollapseStart(Context context, ExpandableLinearLayout v);

		public void onExpandStart(Context context, ExpandableLinearLayout v);

		public void onCollapseFinished(Context context, View v);
	}

}
