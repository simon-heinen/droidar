package commands.geo;

import geo.GeoGraph;
import geo.GeoObj;
import gl.scenegraph.RenderList;
import util.EfficientList;
import util.Wrapper;
import worldData.RenderableEntity;
import android.util.Log;

import commands.Command;

public class CommandFindWayInGraph extends Command {

	private Wrapper mySource;
	private Wrapper mySearchTermW;
	private Wrapper myResults;
	private GeoObj myStartPos;

	// private GeoObj myTarget;

	public CommandFindWayInGraph(Wrapper source, GeoObj startPos,
			Wrapper searchTermWrapper, Wrapper wrapperToStoreTheResults) {
		mySource = source;
		myStartPos = startPos;
		mySearchTermW = searchTermWrapper;
		myResults = wrapperToStoreTheResults;
	}

	public CommandFindWayInGraph(Wrapper source, Wrapper searchTermWrapper,
			Wrapper wrapperToStoreTheResults) {
		mySource = source;
		mySearchTermW = searchTermWrapper;
		myResults = wrapperToStoreTheResults;
	}

	@Override
	public boolean execute() {
		if (mySearchTermW.getType() != Wrapper.Type.String)
			return false;
		if (mySearchTermW.getStringValue() == "")
			return false;
		if (mySource == null)
			return false;

		String searchTerm = mySearchTermW.getStringValue();
		GeoGraph r = getPathFor(searchTerm);
		if (r != null) {
			myResults.setTo(r);
			Log.d("GeoGraph", "Way in graph found!");
			return true;
		}

		Log.d("GeoGraph", "No way found :(");
		return false;
	}

	private GeoGraph getPathFor(String searchTerm) {

		if (mySource.getObject() instanceof GeoGraph) {
			GeoObj target = ((GeoGraph) mySource.getObject())
					.findBestPointFor(searchTerm);
			return ((GeoGraph) mySource.getObject()).findPath(myStartPos,
					target);
		}
		if (mySource.getObject() instanceof RenderList) {
			EfficientList<RenderableEntity> a = ((RenderList) mySource
					.getObject()).getAllItems();
			final int length = ((RenderList) mySource.getObject())
					.getAllItems().myLength;
			for (int i = 0; i < length; i++) {
				if (a.get(i) instanceof GeoGraph) {
					GeoObj target = ((GeoGraph) a.get(i))
							.findBestPointFor(searchTerm);
					/*
					 * if something found in the graph return the path to it
					 * else keep on searching
					 */
					if (target != null)
						return ((GeoGraph) a.get(i)).findPath(myStartPos,
								target);
				}
			}

		}
		return null;
	}

}
