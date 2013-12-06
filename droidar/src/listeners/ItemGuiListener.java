package listeners;

import android.view.View;
import android.view.ViewGroup;

/**
 * This listener is used to define a custom gui view for an object and it is
 * called everytime a gui view is needed.
 * 
 * @author Spobo
 * 
 */
public interface ItemGuiListener {
	/**
	 * @param viewToUseIfNotNull - {@link android.view.View}
	 * @param parentView - {@link android.view.ViewGroup}
	 * @return - {@link android.view.View} of the requested item. 
	 */
	View requestItemView(View viewToUseIfNotNull, ViewGroup parentView);

}
