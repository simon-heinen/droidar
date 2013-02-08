package gl;

import util.Vec;

public interface HasRotation {

	public Vec getRotation();

	/**
	 * Will rotate the object COUNTERCLOCKWISE
	 * 
	 * @param rotation
	 *            An example to rotate an object 45 degree CLOCKWISE around the
	 *            Z axis would be "new Vec(0,0,-45)"
	 */
	public void setRotation(Vec rotation);
}
