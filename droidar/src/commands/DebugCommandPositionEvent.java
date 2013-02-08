package commands;

import geo.GeoObj;
import listeners.eventManagerListeners.LocationEventListener;
import system.EventManager;
import util.Log;

public class DebugCommandPositionEvent extends Command {

	private static final String LOG_TAG = "DebugCommandPositionEvent";
	private LocationEventListener myAction;
	private GeoObj myPos;

	public DebugCommandPositionEvent(LocationEventListener action,
			GeoObj posToSet) {
		myAction = action;
		myPos = posToSet;
	}

	@Override
	public boolean execute() {
		myAction.onLocationChanged(myPos.toLocation());
		EventManager.getInstance().setCurrentLocation(myPos.toLocation());
		return true;
	}

	public static void goToCoords(double latitude, double longitude) {
		if (EventManager.getInstance().getOnLocationChangedAction() != null)

			for (LocationEventListener a : EventManager.getInstance()
					.getOnLocationChangedAction()) {
				new DebugCommandPositionEvent(a,
						new GeoObj(latitude, longitude)).execute();
			}
		else
			Log.e(LOG_TAG,
					"onLocationChangedAction was null so this debug command wont work");
	}
}
