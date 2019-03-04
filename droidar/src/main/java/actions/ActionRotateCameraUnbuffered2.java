package actions;

import gl.GLCamRotationController;
import actions.algos.SensorAlgo1;

public class ActionRotateCameraUnbuffered2 extends ActionWithSensorProcessing {

	public ActionRotateCameraUnbuffered2(GLCamRotationController targetCamera) {
		super(targetCamera);
	}

	@Override
	protected void initAlgos() {
		accelAlgo = new SensorAlgo1(0.5f);
		magnetAlgo = new SensorAlgo1(0.8f);
		orientAlgo = new SensorAlgo1(0.5f);// TODO
	}

}
