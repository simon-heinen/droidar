package entry;

import preview.AugmentedView;
import android.app.Activity;

public interface ISetupEntry {
	
	/**
	 * Retrieve the contain/parent activity.  Depending on the <code>ISetupEntry.getType()</code> this
	 * may be either the containing activity or the main displayed activity. 
	 * @return
	 */
	public Activity getActivity();
	
	/**
	 * Retrieve the type of the current class
	 * @return
	 */
	public ArType getType();
	
	/**
	 * Retrieve the augmented view this is displayed to the user. 
	 * @return
	 */
	public AugmentedView getAugmentedView();
}
