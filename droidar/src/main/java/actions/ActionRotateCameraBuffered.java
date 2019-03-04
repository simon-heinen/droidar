package actions;

import gl.GLCamRotationController;

public class ActionRotateCameraBuffered extends ActionWithSensorProcessing {

	public ActionRotateCameraBuffered(GLCamRotationController targetCamera) {
		super(targetCamera);
	}

	@Override
	public void initAlgos() {
	}

	@Override
	public synchronized boolean onAccelChanged(float[] values) {
		return false;
	}

	@Override
	public synchronized boolean onMagnetChanged(float[] values) {
		return false;
	}
}
