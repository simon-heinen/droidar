package commands.geo;

import geo.Edge;
import geo.GeoGraph;
import geo.GeoObj;
import gl.scenegraph.MeshComponent;
import util.Wrapper;

import commands.undoable.UndoableCommand;

public class CommandAddPathToGeoGraph extends UndoableCommand {

	private Wrapper myStart;
	private Wrapper myEnd;
	private Wrapper myTarget;
	private GeoGraph backupGraph;
	private Edge backupEdge;
	private MeshComponent myMesh;

	public CommandAddPathToGeoGraph(Wrapper startW, Wrapper endW,
			Wrapper targetGraphW, MeshComponent edgeMesh) {
		myStart = startW;
		myEnd = endW;
		myTarget = targetGraphW;
		myMesh = edgeMesh;
	}

	@Override
	public boolean override_do() {
		if (myTarget.getObject() instanceof GeoGraph) {
			if (myStart.getObject() instanceof GeoObj
					&& myEnd.getObject() instanceof GeoObj) {
				backupGraph = (GeoGraph) myTarget.getObject();
				backupEdge = backupGraph.addEdge((GeoObj) myStart.getObject(),
						(GeoObj) myEnd.getObject(), myMesh);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean override_undo() {
		if (backupGraph != null && backupEdge != null) {
			backupGraph.remove(backupEdge);
			return true;
		}
		return false;
	}

}
