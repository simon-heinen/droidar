package v2.simpleUi;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public abstract class M_RadioButtonList implements ModifierInterface {

	public interface SelectableItem {
		/**
		 * @return should be a positive number which is unique in its list of
		 *         {@link SelectableItem}s
		 */
		int getId();

		String getText();

	}

	private RadioGroup group;
	private boolean editable = true;
	private Handler myHandler = new Handler(Looper.getMainLooper());

	@Override
	public View getView(final Context context) {
		group = new RadioGroup(context);
		List<SelectableItem> list = getItemList();
		for (int i = 0; i < list.size(); i++) {
			final SelectableItem item = list.get(i);
			RadioButton b = new RadioButton(context);
			b.setId(item.getId());
			b.setText(item.getText());
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onItemSelectedByUser(context, item);
				}
			});
			group.addView(b);
		}
		setEditable(editable);
		return group;
	}

	@Override
	public boolean save() {
		for (SelectableItem i : getItemList()) {
			if (i.getId() == group.getCheckedRadioButtonId()) {
				return save(i);
			}
		}
		return false;
	}

	public abstract boolean save(SelectableItem item);

	/**
	 * This is called as soon as the user selects an {@link SelectableItem} in
	 * the list. Normally this method does not have to do anything and the save
	 * action should only happen in the
	 * {@link M_RadioButtonList#save(SelectableItem)} method!
	 * 
	 * @param context
	 * 
	 * @param item
	 */
	public abstract void onItemSelectedByUser(Context context,
			SelectableItem item);

	public abstract List<SelectableItem> getItemList();

	public void setEditable(final boolean editable) {
		this.editable = editable;
		if (group != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					group.setEnabled(editable);
					group.setFocusable(editable);
				}
			});

		}
	}
}
