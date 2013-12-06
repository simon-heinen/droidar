package listeners.eventManagerListeners;

import android.location.Location;
/**
 * Listener for when the location has been changed. 
 */
public interface LocationEventListener {
	/**
	 * @param location - new/current {@link android.location.Location}
	 * @return - process successful
	 */
	boolean onLocationChanged(Location location);
}
