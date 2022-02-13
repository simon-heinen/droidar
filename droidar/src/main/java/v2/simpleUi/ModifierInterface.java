package v2.simpleUi;

import android.content.Context;
import android.view.View;

public interface ModifierInterface {

	public static final int DEFAULT_PADDING = 4;

	public abstract View getView(Context context);

	/**
	 * @return true if the save procedure was successful
	 */
	public abstract boolean save();

}