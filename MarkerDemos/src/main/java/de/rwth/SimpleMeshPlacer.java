package de.rwth;

import util.Vec;
import gl.GLCamera;
import gl.MarkerObject;
import gl.scenegraph.MeshComponent;

public class SimpleMeshPlacer extends BasicMarker {

	protected MeshComponent myTargetMesh;

	public SimpleMeshPlacer(int id, MeshComponent mesh, GLCamera camera) {
		super(id, camera);
		myTargetMesh = mesh;
	}

	@Override
	public void setObjRotation(float[] rotMatrix) {
		myTargetMesh.setRotationMatrix(rotMatrix);
	}

	@Override
	public void setObjectPos(Vec positionVec) {
		myTargetMesh.setPosition(positionVec);
	}

}
