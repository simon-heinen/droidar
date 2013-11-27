package actions;

import gl.GLCamRotationController;
import actions.algos.BufferAlgo1;
import actions.algos.BufferAlgo2;
import actions.algos.SensorAlgo1;
import actions.algos.SmoothingAlgo;

public class ActionRotateCameraBuffered extends ActionWithSensorProcessing {

	public ActionRotateCameraBuffered(GLCamRotationController targetCamera) {
		super(targetCamera);
	}

	@Override
	public void initAlgos() {
		//accelAlgo = new SensorAlgo1(0.1f);
		//accelAlgo = new SmoothingAlgo(true);
		//accelAlgo = new SmoothingAlgo(SmoothingAlgo.FilterType.BAND);
		//accelBufferAlgo = new BufferAlgo1(0.01f, 2f);

		//magnetAlgo = new SensorAlgo1(1.4f);
		//magnetAlgo = new SmoothingAlgo(SmoothingAlgo.FilterType.BAND);
		//magnetBufferAlgo = new BufferAlgo1(0.01f, 2f);
		
		//orientAlgo = new SmoothingAlgo(true);//new SensorAlgo1(0.35f);// TODO
		//orientAlgo = new SmoothingAlgo(SmoothingAlgo.FilterType.BAND);
		//orientationBufferAlgo = new BufferAlgo2(0.1f); // TODO
		
		//accelAlgo = new SmoothingAlgo(SmoothingAlgo.FilterType.BAND);
		//magnetAlgo = new SmoothingAlgo(SmoothingAlgo.FilterType.BAND);
		accelAlgo = new SensorAlgo1(0.5f);
		magnetAlgo = new SensorAlgo1(0.8f);
		accelBufferAlgo = new BufferAlgo2(0.2f);
		magnetBufferAlgo = new BufferAlgo2(0.2f);
		
		orientAlgo = new SensorAlgo1(0.8f);// TODO
		orientationBufferAlgo = new BufferAlgo2(0.2f); // TODO
	}

//	@Override
//	public synchronized boolean onOrientationChanged(float[] values) {
//		return false;
//	}

//	@Override
//	public synchronized boolean onAccelChanged(float[] values) {
//		return false;
//	}
//
//	@Override
//	public synchronized boolean onMagnetChanged(float[] values) {
//		return false;
//	}
	
	
}
