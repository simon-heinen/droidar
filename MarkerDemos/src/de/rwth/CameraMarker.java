package de.rwth;

import gl.GLCamera;
import gl.MarkerObject;

/**
 * The camera marker will set the world coordinates to the center of a marker
 * and move the camera around it
 * 
 * @author Spobo
 * 
 */
public class CameraMarker implements MarkerObject {

	private int myId;
	private GLCamera myCamera;

	/**
	 * The camera marker will set the world coordinates to the center of a
	 * marker and move the camera around it
	 * 
	 * @param id
	 * @param camera
	 */
	public CameraMarker(int id, GLCamera camera) {
		myId = id;
		myCamera = camera;
	}

	@Override
	public int getMyId() {
		return myId;
	}

	@Override
	public void OnMarkerPositionRecognized(float[] rotMatrix, int start, int end) {
		myCamera.setRotationMatrix(rotMatrix, start);
	}

}
