package commands.geo;

import geo.GeoObj;
import geo.GeoUtils;
import system.EventManager;
import util.Wrapper;
import android.util.Log;

import commands.Command;

public class CommandFindWayWithGMaps extends Command {

	private GeoUtils myGeoUtils;
	private Wrapper mySearchTextWrapper;
	private Wrapper myResultingPathWrapper;
	private GeoObj myStartPos;
	private Wrapper byWalk;

	/**
	 * @param map
	 * @param startPos
	 *            if null then the current location of the user will be used as
	 *            the start-position
	 * @param searchTextWrapper
	 * @param sresults
	 */
	public CommandFindWayWithGMaps(GeoUtils map, GeoObj startPos,
			Wrapper searchTextWrapper, Wrapper sresults, Wrapper byWalk) {
		myGeoUtils = map;
		mySearchTextWrapper = searchTextWrapper;
		myResultingPathWrapper = sresults;
		myStartPos = startPos;
		this.byWalk = byWalk;
	}

	public CommandFindWayWithGMaps(GeoUtils map, Wrapper searchTextWrapper,
			Wrapper sresults, Wrapper byWalk) {
		this(map, null, searchTextWrapper, sresults, byWalk);
	}

	@Override
	public boolean execute() {
		String text = mySearchTextWrapper.getStringValue();
		if (text == "")
			return false;
		Log.d("Gmaps", "Searching point near " + text);
		GeoObj target = myGeoUtils.getBestLocationForAddress(text);
		if (target == null) {
			Log.d("Gmaps", "   -> No point for search string found..");
			return false;
		}
		Log.d("Gmaps", "Found point: " + target.toString());
		if (myStartPos == null) {
			Log.d("Gmaps", "Searching way from current location to " + target);
			return myGeoUtils.getPathFromAtoB(EventManager.getInstance()
					.getCurrentLocationObject(), target,
					myResultingPathWrapper, byWalk.getBooleanValue());
		}
		Log.d("Gmaps", "Searching way from " + myStartPos + " to " + target);
		return myGeoUtils.getPathFromAtoB(myStartPos, target, myResultingPathWrapper, byWalk
				.getBooleanValue());
	}

}
