package gui.simpleUI;

import android.content.Context;
import android.view.View;

public interface ModifierInterface {

	public abstract View getView(Context context);

	/**
	 * @return false if window should not be closed because something could not
	 *         be saved
	 */
	public abstract boolean save();

}