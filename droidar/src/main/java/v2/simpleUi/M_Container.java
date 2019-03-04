package v2.simpleUi;

import java.util.ArrayList;

import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import v2.simpleUi.util.BGUtils;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class M_Container extends ArrayList<ModifierInterface> implements
		ModifierInterface, UiDecoratable {

	private static final int MOST_OUTER_PADDING = 13;
	private static final int OUTER_BACKGROUND_DIMMING_COLOR = android.graphics.Color
			.argb(200, 0, 0, 0);
	private static final BGUtils BACKGROUND = BGUtils.newGrayBackground();
	private UiDecorator myDecorator;

	@Override
	public View getView(Context target) {

		LinearLayout containerForAllItems = new LinearLayout(target);
		ScrollView scrollContainer = new ScrollView(target);
		LinearLayout mostOuterBox = new LinearLayout(target);

		LayoutParams layParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);

		containerForAllItems.setLayoutParams(layParams);
		containerForAllItems.setPadding(MOST_OUTER_PADDING, MOST_OUTER_PADDING,
				MOST_OUTER_PADDING, MOST_OUTER_PADDING);
		containerForAllItems.setOrientation(LinearLayout.VERTICAL);

		scrollContainer.setLayoutParams(layParams);
		scrollContainer.addView(containerForAllItems);

		mostOuterBox.setGravity(Gravity.CENTER);
		mostOuterBox.setBackgroundColor(OUTER_BACKGROUND_DIMMING_COLOR);
		mostOuterBox.setPadding(MOST_OUTER_PADDING, MOST_OUTER_PADDING,
				MOST_OUTER_PADDING, MOST_OUTER_PADDING);
		mostOuterBox.addView(scrollContainer);

		BACKGROUND.applyTo(scrollContainer);

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(target, mostOuterBox, level + 1,
					UiDecorator.TYPE_CONTAINER);
			myDecorator.decorate(target, scrollContainer, level + 2,
					UiDecorator.TYPE_CONTAINER);
			myDecorator.setCurrentLevel(level + 3);
		}

		createViewsForAllModifiers(target, containerForAllItems);

		if (myDecorator != null) {
			/*
			 * Then reduce level again to the previous value
			 */
			myDecorator.setCurrentLevel(myDecorator.getCurrentLevel() - 3);
		}

		return mostOuterBox;
	}

	protected void createViewsForAllModifiers(Context target,
			LinearLayout containerForAllItems) {
		for (ModifierInterface m : this) {
			if (m != null) {
				View v = m.getView(target);
				if (v != null)
					containerForAllItems.addView(v);
			}
		}
	}

	@Override
	public boolean save() {
		boolean result = true;
		for (ModifierInterface m : this) {
			if (m != null) {
				result &= m.save();
			}
		}
		return result;
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		boolean result = true;
		myDecorator = decorator;
		for (ModifierInterface m : this) {
			if (m instanceof UiDecoratable) {
				result &= ((UiDecoratable) m).assignNewDecorator(decorator);
			} else {
				/*
				 * if not all children are UiDecoratables the overall result
				 * will be false
				 */
				result = false;
			}
		}
		return result;
	}

}
