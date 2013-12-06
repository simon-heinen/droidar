package actions;

import gl.GLCamRotationController;
import gl.GLUtilityClass;
import setup.ArSetup;
import system.EventManager;
import util.Calculus;
import worlddata.Updateable;
import actions.algos.Algo;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.Surface;
/**
 * Base action for those that need sensor data to perform specific tasks. 
 * TODO: Refactor this class. 
 */
public abstract class ActionWithSensorProcessing extends Action {

	private static final String LOG_TAG = "ActionWithSensorProcessing";
	private static final int SENSOR_ARRAY_SIZE = 3;
	private final GLCamRotationController mTargetCamera;

	private Algo mMagnetAlgo;
	private Algo mAccelAlgo;
	private Algo mOrientAlgo;
	private Algo mAccelBufferAlgo;
	private Algo mMagnetBufferAlgo;
	private Algo mOrientationBufferAlgo;

	private float[] mAccelValues = new float[SENSOR_ARRAY_SIZE];
	private float[] mMagnetValues = new float[SENSOR_ARRAY_SIZE];
	private float[] mOrientValues = new float[SENSOR_ARRAY_SIZE];

	private boolean mAccelChanged;
	private float[] mNewAccelValues;
	private boolean mMagnetoChanged;
	private float[] mNewMagnetValues;
	private boolean mOrientationDataChanged;
	private float[] mNewOrientValues;

	private final float[] mUnrotatedMatrix = Calculus.createIdentityMatrix();
	private float[] mRotationMatrix = Calculus.createIdentityMatrix();

	private final int mScreenRotation;

	/**
	 * Constructor.
	 * @param targetCamera - {@link gl.GLCamRotationController}
	 */
	public ActionWithSensorProcessing(GLCamRotationController targetCamera) {
		mTargetCamera = targetCamera;
		initAlgos();
		mScreenRotation = ArSetup.getScreenOrientation();
	}

	@Override
	public boolean onTouchMove(MotionEvent e1, MotionEvent e2,
			float screenDeltaX, float screenDeltaY) {
		mTargetCamera.changeZAngleBuffered(screenDeltaY);
		return true;
	}

	@Override
	public synchronized boolean onAccelChanged(float[] values) {
		if (mAccelAlgo != null) {
			mNewAccelValues = mAccelAlgo.execute(values);
		} else {
			mNewAccelValues = values;
		}
		mAccelChanged = true;
		return true;
	}

	@Override
	public synchronized boolean onMagnetChanged(float[] values) {
		if (mMagnetAlgo != null) {
			mNewMagnetValues = mMagnetAlgo.execute(values);
		} else {
			mNewMagnetValues = values;
		}
		mMagnetoChanged = true;
		return true;
	}

	@Override
	public synchronized boolean onOrientationChanged(float[] values) {
		if (mOrientAlgo != null) {
			mNewOrientValues = mOrientAlgo.execute(values);
		} else {
			mNewOrientValues = values;
		}
		mOrientationDataChanged = true;
		return true;
	}

	@Override
	public synchronized boolean update(float timeDelta, Updateable parent) {
		if (mMagnetoChanged || mAccelChanged || mOrientationDataChanged) {
			if (mMagnetoChanged || mAccelChanged) {
				if (mAccelChanged) {
					mAccelChanged = false;
					if (mAccelBufferAlgo != null) {
						mAccelBufferAlgo.execute(mAccelValues,
								mNewAccelValues, timeDelta);
					} else {
						mAccelValues = mNewAccelValues;
					}
				}
				if (mMagnetoChanged) {
					mMagnetoChanged = false;
					if (mMagnetBufferAlgo != null) {
						mMagnetBufferAlgo.execute(mMagnetValues,
								mNewMagnetValues, timeDelta);
					} else {
						mMagnetValues = mNewMagnetValues;
					}
				}
				SensorManager.getRotationMatrix(mUnrotatedMatrix, null,
						mAccelValues, mMagnetValues);
			} else if (mOrientationDataChanged) {
				mOrientationDataChanged = false;
				if (mOrientationBufferAlgo != null) {
					mOrientationBufferAlgo.execute(mOrientValues,
							mNewOrientValues, timeDelta);
				} else {
					mOrientValues = mNewOrientValues;
				}
				GLUtilityClass.getRotationMatrixFromVector(mUnrotatedMatrix,
						mOrientValues);
			}

			if (EventManager.isTabletDevice) {
				SensorManager.remapCoordinateSystem(mUnrotatedMatrix,
						SensorManager.AXIS_X, SensorManager.AXIS_Y,
						mRotationMatrix);
			} else {
				if (mScreenRotation == Surface.ROTATION_90) {
					SensorManager.remapCoordinateSystem(mUnrotatedMatrix,
							SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
							mRotationMatrix);
				} else {
					mRotationMatrix = mUnrotatedMatrix;
				}
			}
			mTargetCamera.setRotationMatrix(mRotationMatrix, 0);
		}
		return true;
	}

	@Override
	public boolean onReleaseTouchMove() {
		mTargetCamera.resetBufferedAngle();
		return true;
	}
	
	
	private void initAlgos() {
		mMagnetAlgo = createMagnetAlgo();
		mAccelAlgo = createAccelAlgo();
		mOrientAlgo = createOrientAlgo();
		mAccelBufferAlgo = createAccelBufferAlgo();
		mMagnetBufferAlgo = createMagnetBufferAlgo();
		mOrientationBufferAlgo = createOrientBufferAlgo();
	};

	
	protected abstract Algo createMagnetAlgo();
	
	protected abstract Algo createAccelAlgo();
	
	protected abstract Algo createOrientAlgo();
	
	protected abstract Algo createMagnetBufferAlgo();
	
	protected abstract Algo createAccelBufferAlgo();
	
	protected abstract Algo createOrientBufferAlgo();

	

	
	

}