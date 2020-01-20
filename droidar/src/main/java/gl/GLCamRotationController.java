package gl;

public interface GLCamRotationController {

	/**
	 * This method can be used to set the rotation matrix received from the
	 * sensor data or from any other source (like a marker tracker -> see marker
	 * tracker extension)
	 * 
	 * @param rotMatrix
	 * @param offset
	 */
    void setRotationMatrix(float[] rotMatrix, int offset);

	/**
	 * This will reset the rotation vector of the virtual camera
	 */
	@Deprecated
    void resetBufferedAngle();

	/**
	 * This will change the z value of the camera-rotation by adding/subtracting
	 * the specified deltaZ value.
	 * 
	 * @param deltaZ
	 */
	@Deprecated
    void changeZAngleBuffered(float deltaZ);

}