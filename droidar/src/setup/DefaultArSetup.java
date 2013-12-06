package setup;

import entry.ISetupEntry;
import geo.GeoObj;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gui.GuiSetup;
import logger.ARLogger;
import system.EventManager;
import util.Vec;
import worlddata.SystemUpdater;
import worlddata.World;
import actions.ActionCalcRelativePos;
import actions.ActionMoveCameraBuffered;
import actions.ActionRotateCameraBuffered;
import actions.ActionWASDMovement;
import actions.ActionWaitForAccuracy;
import android.app.Activity;
import android.location.Location;

/**
 * Default AR setup that initializes all of the lighting and 
 * event management so that concrete classes only need to add the objects to the world. 
 *
 */
public abstract class DefaultArSetup extends ArSetup {
	
	private static final String LOG_TAG = "DefaultArSetup";
	
	private GLCamera mCamera;
	private World mWorld;
	private GL1Renderer mRenderer;
	
	private boolean mWaitForValidGps = false;
	private boolean mAddObjectsCalled = false;
	
	private ActionWASDMovement mWasdAction;
	private ActionRotateCameraBuffered mRotateGLCameraAction;
	private ActionWaitForAccuracy mMinAccuracyAction;
	
	//magic numbers
	private static final int XREDUCTION = 25;
	private static final int YREDUCTION = 50;
	private static final int MAXWASDSPEED = 20;	
	private static final int TRACKBALLFACTOR = 5;
	private static final int TOUCHSCREENFACTOR = 25;
	private static final float MINGPSACCURACY = 24.0f;
	private static final int MAXGPSPOSUPDATECOUNT = 10;

	
	/**
	 * Constructor.
	 * @param pEntry - {@link ISetupEntry}
	 * @param pWaitForValidGps - {@link boolean} true if you want to wait for gps before drawing
	 */
	public DefaultArSetup(ISetupEntry pEntry, boolean pWaitForValidGps) {
		super(pEntry);
		mWaitForValidGps = pWaitForValidGps;
	}
	
	/**
	 * Constructor. By default the {@link setup.DefaultArSetup#mWaitForValidGps} if set to false.
	 */
	public DefaultArSetup() {
		super();
		mWaitForValidGps = false;
	}
	
	/**
	 * Constructor. 
	 * @param pWaitForValidGps - {@link boolean} true if you want to wait for gps before drawing
	 */
	public DefaultArSetup(boolean pWaitForValidGps) {
		super();
		mWaitForValidGps = pWaitForValidGps;
	}

	@Override
	public void initFieldsIfNecessary() {
		mCamera = new GLCamera(new Vec(0, 0, 2));
		mWorld = new World(mCamera);
	}
	
	/**
	 * Return world.
	 * @return - {@link World}
	 */
	public World getWorld() {
		return mWorld;
	}

	/**
	 * Return gl camera.
	 * @return - {@link World}
	 */
	public GLCamera getCamera() {
		return mCamera;
	}

	@Override
	public void addWorldsToRenderer(GL1Renderer glRenderer,
			GLFactory objectFactory, GeoObj currentPosition) {
		mRenderer = glRenderer;
		if (!mWaitForValidGps) {
			addObjectsTo(glRenderer, mWorld, GLFactory.getInstance());
			mAddObjectsCalled = true;
		}
		glRenderer.addRenderElement(mWorld);
	}
	
	@Override
	public void addActionsToEvents(final EventManager eventManager,
			CustomGLSurfaceView arView, SystemUpdater updater) {
		mWasdAction = new ActionWASDMovement(mCamera, 
											XREDUCTION, 
											YREDUCTION, 
											MAXWASDSPEED);
		mRotateGLCameraAction = new ActionRotateCameraBuffered(mCamera);
		eventManager.addOnOrientationChangedAction(mRotateGLCameraAction);

		arView.addOnTouchMoveListener(mWasdAction);
		eventManager.addOnTrackballAction(new ActionMoveCameraBuffered(mCamera,
																		TRACKBALLFACTOR, 
																		TOUCHSCREENFACTOR));
		eventManager.addOnLocationChangedAction(new ActionCalcRelativePos(mWorld, mCamera));
		
		if (mWaitForValidGps) {
			mMinAccuracyAction = new ActionWaitForAccuracy(getActivity(), 
															MINGPSACCURACY, 
															MAXGPSPOSUPDATECOUNT) {
				@Override
				public void minAccuracyReachedFirstTime(Location l,
						ActionWaitForAccuracy a) {
					callAddObjectsToWorldIfNotCalledAlready();
					if (!eventManager.getOnLocationChangedAction().remove(a)) {
						ARLogger.debug(LOG_TAG, 
								"Could not remove minAccuracyAction from the onLocationChangedAction list");
					}
				}
			};
			eventManager.addOnLocationChangedAction(mMinAccuracyAction);
		}
	}
	
	private void callAddObjectsToWorldIfNotCalledAlready() {
		if (!mAddObjectsCalled) {
			addObjectsTo(mRenderer, mWorld, GLFactory.getInstance());
			mAddObjectsCalled = true;
		}
	}

	@Override
	public void addElementsToUpdateThread(SystemUpdater updater) {
		updater.addObjectToUpdateCycle(mWorld);
		updater.addObjectToUpdateCycle(mWasdAction);
		updater.addObjectToUpdateCycle(mRotateGLCameraAction);
	}

	@Override
	public void addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {
		
	}
	
	/**
	 * This will be called when the GPS accuracy is high enough.
	 * 
	 * @param renderer - {@link GL1Renderer}
	 * @param world - {@link World}
	 * @param objectFactory - {@link GLFactory}
	 */
	public abstract void addObjectsTo(GL1Renderer renderer, World world,
			GLFactory objectFactory);

}
