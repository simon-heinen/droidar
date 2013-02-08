package v2.simpleUi;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public abstract class M_Spinner implements ModifierInterface {

	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public static class SpinnerItem {

		private int id;
		private String text;

		public SpinnerItem(int id, String text) {
			this.id = id;
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public int getId() {
			return id;
		}

		@Override
		public String toString() {
			return getText();
		}

	}

	private Spinner s;
	private boolean editable = true;
	private int selectedItemPos;
	private float weightOfDescription = 1;
	private float weightOfSpinner = 1;

	public void setWeightOfDescription(float weightOfDescription) {
		this.weightOfDescription = weightOfDescription;
	}

	public void setWeightOfInputText(float weightOfInputText) {
		this.weightOfSpinner = weightOfInputText;
	}

	@Override
	public View getView(Context context) {

		LinearLayout container = new LinearLayout(context);
		container.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);

		container.setGravity(Gravity.CENTER_VERTICAL);
		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfDescription);
		LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfSpinner);

		TextView nameText = new TextView(context);
		nameText.setText(getVarName());
		nameText.setLayoutParams(p);
		container.addView(nameText);

		s = new Spinner(context);
		s.setLayoutParams(p2);
		ArrayAdapter<SpinnerItem> a = new ArrayAdapter<SpinnerItem>(context,
				android.R.layout.simple_spinner_item, loadListToDisplay());
		a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(a);
		s.setPrompt(getVarName());
		setEditable(isEditable());
		setSelectedItemId(loadSelectedItemId());
		if (selectedItemPos != 0) {
			selectInSpinner(selectedItemPos);
		}

		container.addView(s);

		return container;
	}

	public abstract int loadSelectedItemId();

	public void setEditable(boolean editable) {
		this.editable = editable;
		if (s != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					s.setEnabled(isEditable());
					s.setFocusable(isEditable());
				}
			});
		}
	}

	public boolean isEditable() {
		return editable;
	}

	@Override
	public boolean save() {
		return save((SpinnerItem) s.getSelectedItem());
	}

	public abstract boolean save(SpinnerItem selectedItem);

	public abstract String getVarName();

	public abstract List<SpinnerItem> loadListToDisplay();

	public boolean setSelectedItemId(int selectedItemId) {

		List<SpinnerItem> list = loadListToDisplay();

		if (selectedItemId < list.size()
				&& list.get(selectedItemId).getId() == selectedItemId) {
			selectInSpinner(selectedItemId);
			return true;
		}

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId() == selectedItemId) {
				selectInSpinner(i);
				return true;
			}
		}
		return false;
	}

	public void selectInSpinner(int posInList) {
		this.selectedItemPos = posInList;
		if (s != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					s.setSelection(selectedItemPos);
				}
			});
		}
	}
}
