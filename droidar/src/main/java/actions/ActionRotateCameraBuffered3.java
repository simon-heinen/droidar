package actions;

import gl.GLCamRotationController;
import actions.algos.BufferAlgo3;
import actions.algos.SensorAlgo1;

public class ActionRotateCameraBuffered3 extends ActionWithSensorProcessing {

	public ActionRotateCameraBuffered3(GLCamRotationController targetCamera) {
		super(targetCamera);
	}

	@Override
	protected void initAlgos() {
		accelAlgo = new SensorAlgo1(0.5f);
		magnetAlgo = new SensorAlgo1(0.8f);

		orientAlgo = new SensorAlgo1(0.5f);// TODO
		orientationBufferAlgo = new BufferAlgo3(0.2f, 0.1f, 4); // TODO

		accelBufferAlgo = new BufferAlgo3(0.2f, 0.1f, 4);
		magnetBufferAlgo = new BufferAlgo3(0.2f, 0.1f, 4);
	}

}
