package markerDetection;

import gl.MarkerObject;

import java.util.HashMap;



public class MarkerObjectMap extends HashMap<Integer, MarkerObject> {

	public void put(MarkerObject markerObject) {
		put(markerObject.getMyId(), markerObject);
	}

}
