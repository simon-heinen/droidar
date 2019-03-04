package actions;

import gl.GLCamRotationController;
import gl.GLUtilityClass;
import util.Calculus;
import actions.algos.Algo;
import actions.algos.BufferAlgo1;
import actions.algos.SensorAlgo1;
import android.hardware.SensorManager;
import android.view.MotionEvent;

/**
 * This is nearly the same like {@link ActionRotateCameraBuffered} (same values)
 * but without buffering in the update thread. This class is just for testing so
 * it is depricated and might be removed soon.
 * 
 * @author Spobo
 * 
 */
@Deprecated
public class ActionRotateCameraBufferedDirect extends Action {

	private GLCamRotationController myTargetCamera;

	public Algo magnetAlgo;
	public Algo accelAlgo;
	public Algo orientAlgo;
	public Algo accelBufferAlgo;
	public Algo magnetBufferAlgo;
	public Algo orientationBufferAlgo;

	private float[] myAccelValues = new float[3];
	private float[] myMagnetValues = new float[3];
	private float[] myOrientValues = new float[3];

	private boolean accelChanged;
	private float[] myNewAccelValues;
	private boolean magnetoChanged;
	private float[] myNewMagnetValues;
	private boolean orientationDataChanged;
	private float[] myNewOrientValues;

	private float[] unrotatedMatrix = Calculus.createIdentityMatrix();
	private float[] rotationMatrix = Calculus.createIdentityMatrix();

	private float timeDelta;

	public ActionRotateCameraBufferedDirect(GLCamRotationController targetCamera) {
		myTargetCamera = targetCamera;
		accelAlgo = new SensorAlgo1(0.1f);
		magnetAlgo = new SensorAlgo1(1.4f);
		orientAlgo = new SensorAlgo1(0.005f);// TODO find correct values

		accelBufferAlgo = new BufferAlgo1(0.1f, 4f);
		magnetBufferAlgo = new BufferAlgo1(0.1f, 4f);
		timeDelta = 0.02f;
	}

	@Override
	public boolean onTouchMove(MotionEvent e1, MotionEvent e2,
			float screenDeltaX, float screenDeltaY) {
		myTargetCamera.changeZAngleBuffered(screenDeltaY);
		return true;
	}

	@Override
	public synchronized boolean onAccelChanged(float[] values) {

		if (accelAlgo != null)
			myNewAccelValues = accelAlgo.execute(values);
		else
			myNewAccelValues = values;
		accelChanged = true;
		calc();
		return true;

	}

	@Override
	public synchronized boolean onMagnetChanged(float[] values) {
		if (magnetAlgo != null)
			myNewMagnetValues = magnetAlgo.execute(values);
		else
			myNewMagnetValues = values;
		magnetoChanged = true;
		calc();
		return true;

	}

	@Override
	public synchronized boolean onOrientationChanged(float[] values) {
		if (orientAlgo != null)
			myNewOrientValues = orientAlgo.execute(values);
		else
			myNewOrientValues = values;
		orientationDataChanged = true;
		calc();
		return true;

	}

	private void calc() {
		if (magnetoChanged || accelChanged || orientationDataChanged) {
			if (magnetoChanged || accelChanged) {
				// if accel or magnet changed:
				if (accelChanged) {
					accelChanged = false;
					if (accelBufferAlgo != null)
						accelBufferAlgo.execute(myAccelValues,
								myNewAccelValues, timeDelta);
					else
						myAccelValues = myNewAccelValues;
				}
				if (magnetoChanged) {
					magnetoChanged = false;
					if (magnetBufferAlgo != null)
						magnetBufferAlgo.execute(myMagnetValues,
								myNewMagnetValues, timeDelta);
					else
						myMagnetValues = myNewMagnetValues;
				}
				// first calc the unrotated matrix:
				SensorManager.getRotationMatrix(unrotatedMatrix, null,
						myAccelValues, myMagnetValues);
			} else if (orientationDataChanged) {
				orientationDataChanged = false;
				if (orientationBufferAlgo != null)
					orientationBufferAlgo.execute(myOrientValues,
							myNewOrientValues, timeDelta);
				else
					myOrientValues = myNewOrientValues;
				GLUtilityClass.getRotationMatrixFromVector(unrotatedMatrix,
						myOrientValues);
			}

			// then rotate it according to the screen rotation:
			SensorManager.remapCoordinateSystem(unrotatedMatrix,
					SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
					rotationMatrix);

			myTargetCamera.setRotationMatrix(rotationMatrix, 0);
		}
	}

	@Override
	public boolean onReleaseTouchMove() {
		myTargetCamera.resetBufferedAngle();
		return true;
	}

}
