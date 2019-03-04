package listeners.eventManagerListeners;

import android.location.Location;

public interface LocationEventListener {
	public abstract boolean onLocationChanged(Location location);
}
