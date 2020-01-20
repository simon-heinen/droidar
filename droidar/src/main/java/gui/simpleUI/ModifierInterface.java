package gui.simpleUI;

import android.content.Context;
import android.view.View;

public interface ModifierInterface {

	View getView(Context context);

	/**
	 * @return false if window should not be closed because something could not
	 *         be saved
	 */
    boolean save();

}