package commands.geo;

import geo.GMap;
import android.util.Log;

import commands.Command;

public class CommandClearMapOverlays extends Command {

	private GMap myMap;

	public CommandClearMapOverlays(GMap map) {
		myMap = map;
	}

	@Override
	public boolean execute() {
		if (myMap != null && myMap.getOverlays() != null) {
			Log.d("Commands", "Clearing map");
			myMap.getOverlays().clear();
			return true;
		}
		if (myMap == null)
			Log.w("Commands",
					"CommandClearMapOverlays command: myMap was null!");
		return false;
	}

}
