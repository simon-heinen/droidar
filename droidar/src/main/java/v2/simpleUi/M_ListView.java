package v2.simpleUi;

import java.util.List;

import v2.simpleUi.util.ImageTransform;
import v2.simpleUi.util.SimpleBaseAdapter;
import v2.simpleUi.util.SimpleBaseAdapter.HasItsOwnView;
import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public abstract class M_ListView implements ModifierInterface {

	/**
	 * is a duplicate of the original list
	 */
	private List<? extends HasItsOwnView> listToDisplay;

	public M_ListView() {

	}

	@Override
	public View getView(Context context) {
		// will make a copy of the list elements so that deleting etc and the
		// cancel wont affect the original list
		listToDisplay = loadList();
		ListView v = new ListView(context) {
			@Override
			public boolean onTouchEvent(MotionEvent ev) {
				/*
				 * block scolling of parents when the view is touched
				 */
				getParent().requestDisallowInterceptTouchEvent(true);
				return super.onTouchEvent(ev);
			}
		};
		int size = (int) ImageTransform.dipToPixels(v.getResources(),
				getListHeigthInDip());
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, size);
		int p = 5;
		lp.setMargins(2 * p, p, 2 * p, p);
		v.setLayoutParams(lp);
		v.setAdapter(new SimpleBaseAdapter((Activity) context, listToDisplay));
		v.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> myAdapter, View itemView,
					int posInList, long mylng) {
				if (listToDisplay != null)
					((HasItsOwnView) listToDisplay.get(posInList)).onItemClick(
							itemView, posInList);
			}
		});
		v.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View itemView,
					int posInList, long arg3) {
				if (listToDisplay != null)
					return ((HasItsOwnView) listToDisplay.get(posInList))
							.onItemLongClick(itemView, posInList);
				return false;
			}
		});
		return v;
	}

	public abstract Integer getListHeigthInDip();

	public abstract List<? extends HasItsOwnView> loadList();

	@Override
	public boolean save() {
		return save(listToDisplay);
	}

	public abstract boolean save(List<? extends HasItsOwnView> updatedList);

}
