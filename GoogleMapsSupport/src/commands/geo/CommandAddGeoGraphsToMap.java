package commands.geo;

import geo.CustomItemizedOverlay;
import geo.CustomPathOverlay;
import geo.GMap;
import geo.GeoGraph;
import gl.scenegraph.RenderList;
import util.EfficientList;
import util.Wrapper;
import worldData.RenderableEntity;
import android.graphics.drawable.Drawable;
import android.util.Log;

import commands.undoable.UndoableCommand;

public class CommandAddGeoGraphsToMap extends UndoableCommand {

	private GMap myMap;
	private Wrapper myGeoGraphWrapper;
	private Drawable myDefaultIcon;

	/**
	 * @param map
	 * @param wrapper
	 *            can contain a single {@link GeoGraph} or a {@link ObjGroup}
	 *            with several {@link GeoGraph}
	 * @param defaultIconForItemsOnMap
	 */
	public CommandAddGeoGraphsToMap(GMap map, Wrapper wrapper,
			Drawable defaultIconForItemsOnMap) {
		myMap = map;
		myGeoGraphWrapper = wrapper;
		myDefaultIcon = defaultIconForItemsOnMap;
	}

	@Override
	public boolean override_do() {
		if (myGeoGraphWrapper.getObject() instanceof GeoGraph) {
			addGraphToMap((GeoGraph) myGeoGraphWrapper.getObject());
			return true;
		}
		if (myGeoGraphWrapper.getObject() instanceof RenderList) {
			RenderList group = (RenderList) myGeoGraphWrapper.getObject();
			EfficientList<RenderableEntity> graphs = group.getAllItems();
			for (int i = 0; i < graphs.myLength; i++) {
				if (graphs.get(i) instanceof GeoGraph) {
					addGraphToMap((GeoGraph) graphs.get(i));
				}
			}
		}
		return false;
	}

	private void addGraphToMap(GeoGraph graph) {
		Log.d("Commands", "Adding graph '" + graph + "' to map");
		if (graph.isPath() || graph.isUsingItsEdges()) {
			Log.d("Commands", "    + Adding pathlines under those items");
			CustomPathOverlay path = new CustomPathOverlay(graph, false);
			myMap.addOverlay(path);
		}
		try {
			CustomItemizedOverlay items = new CustomItemizedOverlay(graph,
					myDefaultIcon);
			myMap.addOverlay(items);
		} catch (Exception e) {
			Log.e("Gmaps", "An itemized overlay could be created but "
					+ "not added to the Google-Maps view. A reason might "
					+ "be that the mapview could not determine its "
					+ "position. Check if the phone is in airplane-mode!");
			e.printStackTrace();
		}
	}

	@Override
	public boolean override_undo() {
		return false;
	}

}
