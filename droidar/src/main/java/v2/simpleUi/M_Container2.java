package v2.simpleUi;

import java.util.ArrayList;

import v2.simpleUi.customViews.ExpandableLinearLayout;
import v2.simpleUi.customViews.ExpandableLinearLayout.OnExpandListener;
import android.R;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class M_Container2 extends ArrayList<ModifierInterface> implements
		ModifierInterface {

	private OnExpandListener listener;
	private ExpandableLinearLayout expandablePanel;
	private boolean collapsed = false;

	public M_Container2(String title, boolean collapsed) {
		this(title);
		this.collapsed = collapsed;
	}

	public M_Container2(String title) {

		final M_IconButtonWithText expandButton = new M_IconButtonWithText(
				R.drawable.arrow_up_float) {
			@Override
			public void onClick(Context context, ImageView clickedButton) {
				expandablePanel.switchBetweenCollapsedAndExpandedMode();
			}
		};

		final M_Caption caption = new M_Caption(title, 0.8f);
		caption.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				expandablePanel.switchBetweenCollapsedAndExpandedMode();
			}
		});

		listener = new OnExpandListener() {

			@Override
			public void onExpandFinished(Context context, View content) {
				expandButton.setIconId(R.drawable.arrow_up_float);
			}

			@Override
			public void onCollapseFinished(Context context, View content) {
				expandButton.setIconId(R.drawable.arrow_down_float);
			}

			@Override
			public void onCollapseStart(Context context,
					ExpandableLinearLayout v) {
				ImageView b = expandButton.getImageButton();
				if (b != null) {
					b.setAnimation(newRotateAnimation(b, 0, -180));
				}
			}

			@Override
			public void onExpandStart(Context context, ExpandableLinearLayout v) {
				ImageView b = expandButton.getImageButton();
				if (b != null) {
					b.setAnimation(newRotateAnimation(b, 0, 180));
				}
			}

			private Animation newRotateAnimation(ImageView b, float x, float y) {
				// Create an animation instance
				Animation an = new RotateAnimation(x, y,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);

				// Set the animation's parameters
				an.setDuration(600); // duration in ms
				return an;
			}

			@Override
			public void onViewWasDrawnFirstTime(Context context,
					ExpandableLinearLayout expandableLinearLayout) {
				int height = caption.getHeightInPixels() + 13;
				expandableLinearLayout.setCollapsedHeight(height);
				if (collapsed) {
					expandablePanel.collapse();
				}
			}
		};
		initHeaderOfContainer(expandButton, caption);
	}

	public void initHeaderOfContainer(final M_IconButtonWithText expandButton,
			final M_Caption caption) {
		add(new M_LeftRight(expandButton, 1, caption, 5));
	}

	@Override
	public boolean isEmpty() {
		// the first item is the caption
		return size() < 2;
	}

	@Override
	public View getView(Context context) {
		expandablePanel = new ExpandableLinearLayout(context, null, listener);
		for (int i = 0; i < this.size(); i++) {
			View v = this.get(i).getView(context);
			expandablePanel.addView(v);
		}

		for (ModifierInterface m : this) {

		}
		return expandablePanel;
	}

	@Override
	public boolean save() {
		boolean r = true;
		for (ModifierInterface m : this) {
			r &= m.save();
		}
		return r;
	}

}
