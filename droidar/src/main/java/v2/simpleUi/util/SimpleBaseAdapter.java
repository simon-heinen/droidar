package v2.simpleUi.util;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * This class is a subclass of {@link BaseAdapter} and allows to create a simple
 * to use {@link ListAdapter} for any {@link ListView}. Updates in the list will
 * be detected by the adapter automatically, so the list can be used as usual
 * 
 * <br>
 * 
 * The items in the list have to implement the {@link HasItsOwnView} interface.
 * This allows much more flexible {@link ListView} designs with custom UI for
 * each list element.
 * 
 * @author Spobo
 * 
 */
public class SimpleBaseAdapter extends BaseAdapter {

	public interface HasItsOwnView {

		/**
		 * @param context
		 *            This object is needed to create new views
		 * @param convertView
		 *            it might be possible to use this view if it is not null
		 *            and the correct type
		 * @param parent
		 *            the parent view the item view will be contained in. The
		 *            item view does not have to be added to the parent manually
		 * @param simpleBaseAdapter
		 *            if the displayed list is changed call
		 *            {@link SimpleBaseAdapter#notifyDataSetChanged()}
		 * @param containerList
		 *            the list where the item is contained in
		 * @param positionInList
		 *            the position in the list
		 * @return
		 */
		public View getView(Context context, View convertView,
				ViewGroup parent, SimpleBaseAdapter simpleBaseAdapter,
				List<? extends HasItsOwnView> containerList, int positionInList);

		public void onItemClick(View itemView, int posInList);

		public boolean onItemLongClick(View itemView, int posInList);
	}

	private static final int UPDATE_SPEED = 1000;
	private List<? extends HasItsOwnView> myList;
	private int oldSize;
	private boolean keepUpdaterRunning = true;

	/**
	 * @param activity
	 *            if the {@link ListView} should auto-update itself when the
	 *            {@link List} is changed pass the {@link Activity} which
	 *            displays the {@link ListView}, otherwise if auto-updates are
	 *            not necessary because the list does not change or the notify
	 *            method of the adapter is called manually on each change pass
	 *            null
	 * @param listToDisplay
	 *            the {@link List} that should be displayed
	 */
	public SimpleBaseAdapter(final Activity activity,
			List<? extends HasItsOwnView> listToDisplay) {
		myList = listToDisplay;
		if (activity != null)
			createAutoUpdaterForTheListAdapter(activity);
	}

	/**
	 * call this method when the auto-update thread should be stopped
	 */
	public void stopAutoUpdatesOfTheListView() {
		keepUpdaterRunning = false;
	}

	private void createAutoUpdaterForTheListAdapter(final Activity a) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (keepUpdaterRunning) {
					try {
						Thread.sleep(UPDATE_SPEED);
						a.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									refreshCount(myList.size());
								} catch (Exception e) {
									keepUpdaterRunning = false;
								}
							}
						});
					} catch (InterruptedException e) {
						keepUpdaterRunning = false;
					}
				}
			}
		}).start();
	}

	@Override
	public int getCount() {
		return refreshCount(myList.size());
	}

	@Override
	public Object getItem(int position) {
		refreshCount(myList.size());
		return myList.get(position);
	}

	@Override
	public long getItemId(int position) {
		refreshCount(myList.size());
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		refreshCount(myList.size());
		return myList.get(position).getView(parent.getContext(), convertView,
				parent, this, myList, position);
	}

	/**
	 * This should allow the modification of the adapters list without a manual
	 * call for {@link SimpleBaseAdapter#notifyDataSetChanged()} each time
	 * 
	 * @param currentSize
	 * @return the old size if it changed. This is needed for the
	 *         {@link SimpleBaseAdapter#getCount()} method! Do not modify!
	 */
	private int refreshCount(int currentSize) {
		if (oldSize != currentSize) {
			int result = oldSize;
			oldSize = currentSize;
			notifyDataSetChanged();
			return result;
		}
		return currentSize;
	}

}
