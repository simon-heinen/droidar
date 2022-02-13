package v2.simpleUi;

import java.util.List;

import android.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public abstract class M_ListWrapper<T> implements ModifierInterface {

	private List<T> myList;
	private String addItemText;
	private M_Container currentContainer;
	private LinearLayout linLayContainer;

	public M_ListWrapper(List<T> list, String addItemText) {
		myList = list;
		this.addItemText = addItemText;
	}

	@Override
	public View getView(Context context) {
		linLayContainer = new LinearLayout(context);
		linLayContainer.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		linLayContainer.setOrientation(LinearLayout.VERTICAL);
		linLayContainer.addView(generateList(context));
		return linLayContainer;
	}

	public View generateList(Context context) {
		currentContainer = new M_Container();
		if (addItemText != null)
			currentContainer.add(createAddButton(context));
		if (myList != null) {
			for (final T item : myList) {
				int lw = 3;
				int rw = 1;
				M_LeftRight h = new M_LeftRight(getModifierFor(item), lw,
						new M_IconButtonWithText(R.drawable.ic_delete) {

							@Override
							public void onClick(Context context,
									ImageView clickedButton) {
								onDelete(item);
								refreshListContent(context);
							}

						}, rw);
				currentContainer.add(h);
			}
		}
		return currentContainer.getView(context);
	}

	public void refreshListContent(Context context) {
		linLayContainer.removeAllViews();
		linLayContainer.addView(generateList(context));
		linLayContainer.invalidate();
	}

	private ModifierInterface createAddButton(Context context) {
		return new M_Button(addItemText) {

			@Override
			public void onClick(Context context, Button clickedButton) {
				if (addNewObjectToList(context, myList)) {
					refreshListContent(context);
				}

			}

		};
	}

	public abstract boolean addNewObjectToList(Context context, List<T> list);

	public abstract ModifierInterface getModifierFor(T item);

	public abstract boolean onDelete(T item);

	@Override
	public boolean save() {
		if (currentContainer != null)
			return currentContainer.save();
		return false;
	}

}
