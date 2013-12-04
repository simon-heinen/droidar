package entry;

import preview.AugmentedView;
import android.app.Activity;

/**
 *  The required methods for and entry to communicate to a {@link setup.ArSetup}. 
 *
 */
public interface ISetupEntry {
	
	/**
	 * Retrieve the contain/parent activity.  Depending on the <code>ISetupEntry.getType()</code> this
	 * may be either the containing activity or the main displayed activity. 
	 * @return - {@link android.app.Activity}
	 */
	Activity getActivity();
	
	/**
	 * Retrieve the type of the current class.
	 * @return - {@link entry.ArType}
	 */
	ArType getType();
	
	/**
	 * Retrieve the augmented view this is displayed to the user. 
	 * @return - {@link preview.AugmentedView}
	 */
	AugmentedView getAugmentedView();
}
