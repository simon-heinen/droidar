package actions;

import gl.GLCamRotationController;
import actions.algos.BufferAlgo1;
import actions.algos.SensorAlgo1;

public class ActionRotateCameraBuffered2 extends ActionWithSensorProcessing {

	public ActionRotateCameraBuffered2(GLCamRotationController targetCamera) {
		super(targetCamera);
	}

	@Override
	public void initAlgos() {
		accelAlgo = new SensorAlgo1(0.1f);
		magnetAlgo = new SensorAlgo1(1.4f);
		orientAlgo = new SensorAlgo1(0.005f);// TODO find correct values

		accelBufferAlgo = new BufferAlgo1(0.1f, 4f);
		magnetBufferAlgo = new BufferAlgo1(0.1f, 4f);
	}

}
