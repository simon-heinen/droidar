package gui.simpleUI.modifiers;

import gui.simpleUI.AbstractModifier;
import gui.simpleUI.ModifierInterface;
import gui.simpleUI.SimpleUIv1;

import java.util.ArrayList;
import java.util.List;

import util.Log;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public abstract class ListModifier extends AbstractModifier {

	public static final CharSequence CLEAR_LIST_TEXT = "Clear List";

	private List<String> myItemTypes;
	private LinearLayout myListView;
	private ArrayList<ModifierInterface> myList;
	private boolean myItemsDeletable;

	public ListModifier(List<String> itemTypes, boolean itemsDeletable) {
		myItemTypes = itemTypes;
		myItemsDeletable = itemsDeletable;
	}

	private void addItemToList(Context context, String itemType) {
		ModifierInterface newItem = newItemRequest(itemType);
		if (newItem != null) {
			if (myList == null)
				myList = new ArrayList<ModifierInterface>();
			myList.add(newItem);
			addListItemView(myListView, newItem, myList.size() - 1, context);

		}
	}

	private void clearList() {
		if (clearListRequest()) {
			myList.clear();
			myListView.removeAllViews();
		}
	}

	/**
	 * When this event happens the user requested a new item. You as the
	 * developer should now create this new item and return a Modifier for it
	 * 
	 * @param itemType
	 *            Will be one of the item types you passed when creating the
	 *            Modifier
	 * @return the {@link ModifierInterface} which represents the new item in
	 *         the UI
	 */
	public abstract ModifierInterface newItemRequest(String itemType);

	public abstract ArrayList<ModifierInterface> getListItems();

	public abstract boolean deleteItem(int posInList, ModifierInterface m);

	/**
	 * @return false if the list sould not be cleared
	 */
	public abstract boolean clearListRequest();

	@Override
	public View getView(final Context context) {

		LinearLayout listControls = new LinearLayout(context);
		listControls.setGravity(Gravity.CENTER_HORIZONTAL);
		// listControls.setLayoutParams(p);

		for (String itemType : myItemTypes) {
			listControls.addView(newItemAddButton(context, itemType));
		}

		if (myItemsDeletable) {
			Button clearButton = new Button(context);
			clearButton.setText(CLEAR_LIST_TEXT);
			clearButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearList();
				}

			});
			listControls.addView(clearButton);
		}

		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		myListView = new LinearLayout(context);
		myListView.setOrientation(LinearLayout.VERTICAL);
		myListView.setLayoutParams(p);

		myList = getListItems();
		for (int i = 0; i < myList.size(); i++) {
			addListItemView(myListView, myList.get(i), i, context);
		}

		LinearLayout listView = new LinearLayout(context);
		listView.setOrientation(LinearLayout.VERTICAL);

		LinearLayout scrollContainer = new LinearLayout(context);
		scrollContainer.setLayoutParams(p);
		scrollContainer.setGravity(Gravity.CENTER_HORIZONTAL);
		HorizontalScrollView scroller = new HorizontalScrollView(context);
		scroller.addView(listControls);
		scrollContainer.addView(scroller);

		listView.addView(scrollContainer);
		listView.addView(myListView);

		listView.setPadding(SimpleUIv1.DEFAULT_PADDING,
				SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING,
				SimpleUIv1.DEFAULT_PADDING);
		listControls.setPadding(SimpleUIv1.DEFAULT_PADDING,
				SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING,
				SimpleUIv1.DEFAULT_PADDING);
		myListView.setPadding(SimpleUIv1.DEFAULT_PADDING,
				SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING,
				SimpleUIv1.DEFAULT_PADDING);
		if (getTheme() != null) {
			getTheme().applyOuter1(listView);
			getTheme().applyOuter2(listControls);
			getTheme().applyOuter2(myListView);
		}

		return listView;
	}

	private View newItemAddButton(final Context context, final String itemType) {
		Button b = new Button(context);
		b.setText("+ " + itemType + " +");
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addItemToList(context, itemType);
			}
		});
		if (getTheme() != null) {
			getTheme().applyNormal1(b);
		}
		return b;
	}

	private void addListItemView(final LinearLayout targetView,
			final ModifierInterface modifierInterface, final int posInList,
			Context context) {

		/*
		 * Set the weights of the elements 9 to 1 to not waste space with the
		 * delete button
		 */
		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams p1 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 9);
		LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);

		final LinearLayout l = new LinearLayout(context);
		l.setLayoutParams(p);
		l.setGravity(Gravity.CENTER_VERTICAL);
		View v = modifierInterface.getView(context);
		v.setLayoutParams(p1);
		l.addView(v);

		if (myItemsDeletable) {
			Button deleteButton = new Button(context);
			deleteButton.setText("-");
			v.setLayoutParams(p2);
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					try {
						if (deleteItem(posInList, modifierInterface)) {
							if (!myList.remove(modifierInterface)) {
								Log.e("SmartUI", "There was some "
										+ "inconsisancy in the SartUI "
										+ "Code (method addListItemView"
										+ "(..)! Check the code, find "
										+ "the error, get famous");
							}
							targetView.removeView(l);
						}
					} catch (Exception e) {
						Log.e("SmartUI",
								"Item could not be removed correctly. "
										+ "Check SmartUI code!");
						e.printStackTrace();
					}
				}
			});
			l.addView(deleteButton);
		}

		targetView.addView(l);
	}

	@Override
	public boolean save() {
		boolean result = true;
		for (ModifierInterface m : myList) {
			result &= m.save();
		}
		return result;
	}

}