package gui.simpleUI;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ModifierGroup extends AbstractModifier {

	public interface SaveListener {
		public void onSaveSuccessfull();

		public void onSaveFailed(ModifierInterface modifierThatRejectedSave);
	}

	private ArrayList<ModifierInterface> myList = new ArrayList<ModifierInterface>();
	private SaveListener mySaveListener;

	public ModifierGroup() {
	}

	public ModifierGroup(Theme myTheme) {
		setTheme(myTheme);
	}

	public void addModifier(ModifierInterface groupElement) {
		if (getTheme() != null && groupElement instanceof AbstractModifier) {
			if (((AbstractModifier) groupElement).getTheme() == null) {
				((AbstractModifier) groupElement).setTheme(getTheme());
			}
		}
		myList.add(groupElement);
	}

	public ArrayList<ModifierInterface> getMyList() {
		return myList;
	}

	@Override
	public View getView(Context context) {

		LinearLayout linLayout = new LinearLayout(context);

		linLayout.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < myList.size(); i++) {
			linLayout.addView(myList.get(i).getView(context));
		}

		ScrollView sv = new ScrollView(context);
		sv.addView(linLayout);
		sv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		if (getTheme() != null) {
			getTheme().applyOuter1(sv);
		}

		return sv;
	}

	@Override
	public boolean save() {
		boolean result = true;
		for (int i = 0; i < myList.size(); i++) {
			boolean saveSuccessfull = myList.get(i).save();
			result &= saveSuccessfull;
			if (!saveSuccessfull && mySaveListener != null)
				mySaveListener.onSaveFailed(myList.get(i));
		}
		if (result && mySaveListener != null)
			mySaveListener.onSaveSuccessfull();
		return result;
	}

	public void setSaveListener(SaveListener saveListener) {
		mySaveListener = saveListener;
	}

}