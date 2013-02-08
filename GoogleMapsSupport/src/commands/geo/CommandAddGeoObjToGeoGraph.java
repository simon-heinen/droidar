package commands.geo;

import geo.GeoGraph;
import geo.GeoObj;
import geo.NodeListener;
import util.Wrapper;

import commands.undoable.UndoableCommand;

public class CommandAddGeoObjToGeoGraph extends UndoableCommand {

	private Wrapper mySource;
	private Wrapper myTarget;
	private GeoGraph backupGraph;
	private GeoObj backupObj;
	private NodeListener myListener;

	/**
	 * @param sourceW
	 *            can be null if you want to recieve the geoObj just
	 * @param targetW
	 * @param listener
	 *            can be null, then the {@link GeoObj} is simply added to the
	 *            {@link GeoGraph}
	 */
	public CommandAddGeoObjToGeoGraph(Wrapper sourceW, Wrapper targetW,
			NodeListener listener) {
		mySource = sourceW;
		myTarget = targetW;
		myListener = listener;
	}

	@Override
	public boolean override_do() {
		if (mySource != null && myTarget.getObject() instanceof GeoGraph
				&& mySource.getObject() instanceof GeoObj) {
			backupGraph = (GeoGraph) myTarget.getObject();
			backupObj = (GeoObj) mySource.getObject();
			if (myListener == null) {
				return backupGraph.add(backupObj);
			} else {
				return myListener.addNodeToGraph(backupGraph, backupObj);
			}
		}
		return false;
	}

	@Override
	public boolean override_undo() {
		if (backupGraph != null && backupObj != null) {
			backupGraph.remove(backupObj);
			return true;
		}
		return false;
	}

}
