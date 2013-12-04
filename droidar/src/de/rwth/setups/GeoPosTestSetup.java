package de.rwth.setups;

import entry.ISetupEntry;
import geo.GeoObj;
import gl.GL1Renderer;
import gl.GLFactory;
import setup.DefaultArSetup;
import util.Vec;
import worldData.World;

public class GeoPosTestSetup extends DefaultArSetup {
	@Override
	public void addObjectsTo(GL1Renderer renderer, World world,
			GLFactory objectFactory) {
		GeoObj o = new GeoObj(50.779058, 6.060429);
		o.setComp(GLFactory.getInstance().newSolarSystem(new Vec()));
		world.add(o);
	}

}
