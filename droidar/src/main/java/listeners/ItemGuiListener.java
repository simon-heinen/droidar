package listeners;

import android.view.View;
import android.view.ViewGroup;

/**
 * This listener is used to define a custom gui view for an object and it is
 * called everytime a gui view is needed
 * 
 * @author Spobo
 * 
 */
public interface ItemGuiListener {

	View requestItemView(View viewToUseIfNotNull, ViewGroup parentView);

}
