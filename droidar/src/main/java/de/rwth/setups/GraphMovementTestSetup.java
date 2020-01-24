package de.rwth.setups;

import geo.DefaultNodeEdgeListener;
import geo.GeoGraph;
import geo.GeoObj;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLFactory;
import system.DefaultARSetup;
import system.EventManager;
import util.EfficientList;
import util.Vec;
import worldData.SystemUpdater;
import worldData.World;

public class GraphMovementTestSetup extends DefaultARSetup {

	@Override
	public void addObjectsTo(GL1Renderer renderer, World world,
			GLFactory objectFactory) {

		EfficientList<GeoObj> l = new EfficientList<GeoObj>();
		l.add(newOb(10, 10));
		l.add(newOb(10, 20));
		l.add(newOb(20, 20));
		l.add(newOb(20, 10));
		l.add(newOb(30, 30));
		l.add(newOb(40, 40));
		l.add(newOb(50, 50));
		l.add(newOb(60, 40));
		l.add(newOb(60, 30));
		l.add(newOb(50, 30));
		l.add(newOb(40, 30));
		l.add(newOb(30, 40));
		l.add(newOb(30, 50));
		l.add(newOb(30, 60));
		GeoGraph g = GeoGraph.convertToGeoGraph(l, true,
				new DefaultNodeEdgeListener(getCamera()));
		world.add(g);
	}

	private GeoObj newOb(float x, float y) {
		GeoObj o = new GeoObj();
		o.setVirtualPosition(new Vec(x, y, 0));
		return o;
	}

	@Override
	public void _c_addActionsToEvents(EventManager eventManager,
			CustomGLSurfaceView arView, SystemUpdater updater) {

		super._c_addActionsToEvents(eventManager, arView, updater);
		eventManager.getOnLocationChangedAction().clear();
	}

}
