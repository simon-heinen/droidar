package v2.simpleUi;

import android.content.Context;
import android.view.View;

public interface ModifierInterface {

	int DEFAULT_PADDING = 4;

	View getView(Context context);

	/**
	 * @return true if the save procedure was successful
	 */
    boolean save();

}