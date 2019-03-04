package geo;

import gl.GLCamera;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;

public class DefaultNodeEdgeListener extends SimpleNodeEdgeListener {

	public DefaultNodeEdgeListener(GLCamera glCamera) {
		super(glCamera);
	}

	@Override
	public MeshComponent getEdgeMesh(GeoGraph targetGraph, GeoObj startPoint,
			GeoObj endPoint) {
		return Edge.getDefaultMesh(targetGraph, startPoint, endPoint, null);
	}

	@Override
	public MeshComponent getNodeMesh() {
		return GLFactory.getInstance().newDiamond(null);
	}

}