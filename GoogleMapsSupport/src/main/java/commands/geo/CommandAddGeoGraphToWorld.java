package commands.geo;

import geo.GeoGraph;
import util.Wrapper;
import worldData.World;
import android.util.Log;

import commands.undoable.UndoableCommand;

public class CommandAddGeoGraphToWorld extends UndoableCommand {

	private World myWorld;
	private Wrapper myGeoGraohWrapper;
	private GeoGraph x;

	public CommandAddGeoGraphToWorld(World world, Wrapper geoGraohWrapper) {
		myWorld = world;
		myGeoGraohWrapper = geoGraohWrapper;
	}

	@Override
	public boolean override_do() {
		if (myGeoGraohWrapper.getObject() instanceof GeoGraph) {
			x = ((GeoGraph) myGeoGraohWrapper.getObject());
			Log.d("Commands", "Adding Graph: \n " + x + " \n to world "
					+ myWorld);
			myWorld.add(x);
			return true;
		}
		return false;
	}

	@Override
	public boolean override_undo() {
		myWorld.remove(x);
		return true;
	}

}
