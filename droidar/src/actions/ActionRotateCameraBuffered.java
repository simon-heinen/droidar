package actions;

import gl.GLCamRotationController;
import actions.algos.Algo;
import actions.algos.BufferAlgo2;
import actions.algos.SensorAlgo1;

/**
 * Perform buffered camera rotation. 
 *
 */
public class ActionRotateCameraBuffered extends ActionWithSensorProcessing {
	
	//magic numbers
	private static final float ACCEL_ALGO_BARRIER = 0.5f;
	private static final float ACCEL_BUFFER_BARRIER = 0.2f;
	private static final float MAGNET_ALGO_BARRIER = 0.8f;
	private static final float MAGNET_BUFFER_BARRIER = 0.2f;
	private static final float ORIENT_ALGO_BARRIER = 0.8f;
	private static final float ORIENT_BUFFER_BARRIER = 0.8f;
	
	/**
	 * Constructor.
	 * @param targetCamera - {@link gl.GLCamRotationController}
	 */
	public ActionRotateCameraBuffered(GLCamRotationController targetCamera) {
		super(targetCamera);
	}

	@Override
	protected Algo createMagnetAlgo() {
		return new SensorAlgo1(MAGNET_ALGO_BARRIER);
	}

	@Override
	protected Algo createAccelAlgo() {
		return new SensorAlgo1(ACCEL_ALGO_BARRIER);
	}

	@Override
	protected Algo createOrientAlgo() {
		return new SensorAlgo1(ORIENT_ALGO_BARRIER);
	}

	@Override
	protected Algo createMagnetBufferAlgo() {
		return new BufferAlgo2(MAGNET_BUFFER_BARRIER);
	}

	@Override
	protected Algo createAccelBufferAlgo() {
		return new BufferAlgo2(ACCEL_BUFFER_BARRIER);
	}

	@Override
	protected Algo createOrientBufferAlgo() {
		return new BufferAlgo2(ORIENT_BUFFER_BARRIER);
	}
		
}
