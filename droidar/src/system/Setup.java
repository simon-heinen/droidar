package system;

import gamelogic.FeedbackReports;
import geo.GeoObj;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.GLRenderer;
import gl.LightSource;
import gl.ObjectPicker;
import gl.textures.TextureManager;
import gui.GuiSetup;
import gui.InfoScreen;
import gui.InfoScreenSettings;
import gui.simpleUI.EditItem;
import gui.simpleUI.ModifierGroup;
import gui.simpleUI.SimpleUIv1;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import listeners.SetupListener;
import util.EfficientList;
import util.Log;
import util.Vec;
import worldData.SystemUpdater;
import worldData.World;
import actions.Action;
import actions.ActionCalcRelativePos;
import actions.ActionRotateCameraBuffered;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.SystemClock;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import commands.Command;
import commands.CommandGroup;
import commands.system.CommandDeviceVibrate;
import commands.undoable.CommandProcessor;

import de.rwth.R;

/**
 * Extend this class and implement all abstract methods to initialize your AR
 * application. More information can be found in the JavaDoc of the specific
 * methods and for a simple default AR setup use the {@link DefaultARSetup}
 * 
 * <br>
 * To launch another activity from a setup do something like this: <br>
 * <br>
 * getActivity().startActivity(new Intent(getActivity(),
 * MyOtherActivity.class));
 * 
 * @author Spobo
 * 
 */
@SuppressWarnings("deprecation")
public abstract class Setup {

	public static int defaultArLayoutId = R.layout.defaultlayout;

	private static final String LOG_TAG = "Setup";

	public static boolean isOldDeviceWhereNothingWorksAsExpected;

	public static boolean displaySetupStepLogging = true;
	private static final String STEP0 = "Resetting all static stuff, singletons, etc..";
	private static final String STEP1 = "Registering exeption handler";
	private static final String STEP2 = "Loading device dependent settings";
	private static final String STEP3 = "Creating OpenGL overlay";
	private static final String STEP4 = "Initializing EventManager";
	private static final String STEP5 = "Initializing system values";
	private static final String STEP6 = "Creating OpenGL content";
	private static final String STEP7 = "Creating camera overlay";
	private static final String STEP8 = "Enabling user input";
	private static final String STEP9 = "Creating world updater";
	private static final String STEP10 = "Creating gui overlay";
	private static final String STEP11 = "Adding all overlays";
	private static final String STEP12 = "Show info screen";
	private static final String STEP13 = "Entering fullscreen mode";
	private static final String STEP_DONE = "All Setup-steps done!";

	/**
	 * use {@link Setup#getActivity()} instead
	 * 
	 * This is the activity which is created to display the AR content (camera
	 * preview, opengl-layer and UI-layer)
	 */
	@Deprecated
	public Activity myTargetActivity;
	private CommandGroup myOptionsMenuCommands;
	public CustomGLSurfaceView myGLSurfaceView;
	public CameraView myCameraView;
	private FrameLayout myOverlayView;
	private SetupListener mySetupListener;
	private double lastTime;
	/**
	 * TODO make this accessible
	 */
	private final boolean gotoFullScreenMode = true;
	private final boolean useAccelAndMagnetoSensors;

	private GuiSetup guiSetup;

	private GLRenderer glRenderer;

	private SystemUpdater worldUpdater;

	private static Integer screenOrientation = Surface.ROTATION_90;

	public Setup() {
		this(true);
	}

	public Setup(Activity target, SetupListener listener,
			boolean useAccelAndMagnetoSensors) {
		this(useAccelAndMagnetoSensors);
		mySetupListener = listener;
	}

	// TODO remove boolean here and add to EventManager.setListeners..!
	public Setup(boolean useAccelAndMagnetoSensors) {
		this.useAccelAndMagnetoSensors = useAccelAndMagnetoSensors;
	}

	/**
	 * Default initialization is {@link Surface#ROTATION_90}, use landscape on
	 * default mode if the initialization does not work
	 * 
	 * @return {@link Surface#ROTATION_0} or {@link Surface#ROTATION_180} would
	 *         mean portrait mode and 90 and 270 would meen landscape mode
	 *         (should be the same on tablets and mobile devices
	 */
	public static int getScreenOrientation() {
		if (screenOrientation == null) {
			Log.e(LOG_TAG, "screenOrientation was not set! Will asume"
					+ " default 90 degree rotation for screen");
			return Surface.ROTATION_90;
		}
		return screenOrientation;
	}

	/**
	 * @return This will just return {@link Setup#myTargetActivity}. Direct
	 *         access to this field is also possible
	 */
	public Activity getActivity() {
		return myTargetActivity;
	}

	/**
	 * This method has to be executed in the activity which want to display the
	 * AR content. In your activity do something like this:
	 * 
	 * <pre>
	 * public void onCreate(Bundle savedInstanceState) {
	 * 	super.onCreate(savedInstanceState);
	 * 	new MySetup(this).run();
	 * }
	 * </pre>
	 * 
	 * @param target
	 * 
	 */
	public void run(Activity target) {
		myTargetActivity = target;
		Log.i(LOG_TAG, "Setup process is executed now..");

		debugLogDoSetupStep(STEP0);
		initAllSingletons();

		initializeErrorHandler();

		/*
		 * TODO move this to the end of the initialization method and use
		 * myTargetActivity.setProgress to display the progress of the loading
		 * before. maybe also change the text in the activity title to the
		 * current setup step in combination to the setProgress()
		 */

		// Fullscreen:
		if (getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE)) {
			if (gotoFullScreenMode) {
				debugLogDoSetupStep(STEP13);
				getActivity().getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
		} else {

			/*
			 * The title bar is changed to a progress bar to visualize the setup
			 * progress
			 * 
			 * displaying the following stuff does not work until the UI-update
			 * process is initialized by Activity.setContentView(..); so wrong
			 * place here:
			 */
			getActivity().requestWindowFeature(Window.PROGRESS_VISIBILITY_ON);
			getActivity().getWindow().requestFeature(Window.FEATURE_PROGRESS);
			getActivity().setProgressBarVisibility(true);
			/*
			 * TODO do not expect the opengl view to start at the top of the
			 * screen!
			 */
		}

		getActivity().getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// load device dependent settings:
		debugLogDoSetupStep(STEP2);
		loadDeviceDependentSettings(getActivity());

		/*
		 * set the orientation to always stay in landscape mode. this is
		 * necessary to use the camera correctly
		 * 
		 * targetActivity
		 * .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		 * 
		 * no more needed, landscape mode is forced by the AndroidManifest
		 */

		debugLogDoSetupStep(STEP3);
		glRenderer = initOpenGLRenderer();
		myGLSurfaceView = initOpenGLView(glRenderer);

		debugLogDoSetupStep(STEP4);
		// setting up the sensor Listeners:
		EventManager.getInstance().registerListeners(getActivity(),
				this.useAccelAndMagnetoSensors);

		debugLogDoSetupStep(STEP5);
		_a_initFieldsIfNecessary();

		debugLogDoSetupStep(STEP6);

		if (glRenderer instanceof GL1Renderer) {
			_b_addWorldsToRenderer((GL1Renderer) glRenderer,
					GLFactory.getInstance(), EventManager.getInstance()
							.getCurrentLocationObject());
		}
		initializeCamera();

		debugLogDoSetupStep(STEP8);

		// set sensorinput actions:
		worldUpdater = new SystemUpdater();
		_c_addActionsToEvents(EventManager.getInstance(), myGLSurfaceView,
				worldUpdater);

		debugLogDoSetupStep(STEP9);
		// and then init the worldupdater to be able to animate the world:
		_d_addElementsToUpdateThread(worldUpdater);

		// World Update Thread:
		Thread worldThread = new Thread(worldUpdater);

		worldThread.start();

		debugLogDoSetupStep(STEP10);

		// create the thierd view on top of cameraPreview and OpenGL view and
		// init it:
		myOverlayView = new FrameLayout(getActivity());
		/*
		 * after everything is initialized add the guiElements to the screen.
		 * this should be done last because the gui might need a initialized
		 * renderer object or worldUpdater etc
		 */
		_e1_addElementsToOverlay(myOverlayView, getActivity());
		// myTargetActivity.addContentView(myOverlayView, new LayoutParams(
		// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		addOverlaysAndShowInfoScreen();

		debugLogDoSetupStep(STEP_DONE);
	}

	public SystemUpdater getSystemUpdater() {
		return worldUpdater;
	}

	/**
	 * By overriding this (and not calling the super metzhod) the
	 * {@link ErrorHandler} system provided by DroidAR will be deactivated (e.g.
	 * while debugging)
	 */
	public void initializeErrorHandler() {
		debugLogDoSetupStep(STEP1);
		ErrorHandler.registerNewErrorHandler(getActivity());
	}

	/**
	 * If you don't override this method it will create 2 default
	 * {@link LightSource}s
	 * 
	 * @param lights
	 *            add all the {@link LightSource}s you want to use to this list
	 * @return true if lightning should be enabled
	 */
	public boolean _a2_initLightning(ArrayList<LightSource> lights) {
		lights.add(LightSource.newDefaultAmbientLight(GL10.GL_LIGHT0));
		lights.add(LightSource.newDefaultSpotLight(GL10.GL_LIGHT1, new Vec(5,
				5, 5), new Vec(0, 0, 0)));
		// TODO lights.add(LightSource.newDefaultDayLight(GL10.GL_LIGHT1, new
		// Date()));
		return true;
	}

	public void initializeCamera() {

		debugLogDoSetupStep(STEP7);
		myCameraView = initCameraView(myTargetActivity);

	}

	private void addOverlaysAndShowInfoScreen() {
		debugLogDoSetupStep(STEP11);
		InfoScreenSettings infoScreenData = new InfoScreenSettings(
				myTargetActivity);
		if (isOldDeviceWhereNothingWorksAsExpected) {
			Log.d(LOG_TAG, "This is an old device (old Android version)");
			addOverlaysInCrazyOrder();

			debugLogDoSetupStep(STEP12);
			_f_addInfoScreen(infoScreenData);
			if (!infoScreenData.closeInstantly()) {
				/*
				 * on old devices the info-dialog isn't necessary to fix the
				 * wrong order of the overlays, so it only has to be displayed
				 * if wanted by the developer
				 */
				showInfoDialog(infoScreenData);
			}
		} else {
			addOverlays();

			debugLogDoSetupStep(STEP12);
			_f_addInfoScreen(infoScreenData);
			showInfoDialog(infoScreenData);
		}
	}

	private void initAllSingletons() {
		/*
		 * a good examples why you should not use singletons if you can avoid
		 * it.. TODO change all the singletons here to injection singletons.
		 * more flexible then. this can be done here, so just insert instance
		 * instead of resetInstance
		 */

		initEventManagerInstance(this.getActivity());
		SimpleLocationManager.resetInstance();
		TextureManager.resetInstance();
		TaskManager.resetInstance();
		GLFactory.resetInstance();
		ObjectPicker.resetInstance(new CommandDeviceVibrate(myTargetActivity,
				30));
		CommandProcessor.resetInstance();
		FeedbackReports.resetInstance(); // TODO really reset it?

	}

	/**
	 * You can create and set a subclass of {@link EventManager} here. To set
	 * the instance use
	 * {@link EventManager#initInstance(android.content.Context, EventManager)}
	 */
	public void initEventManagerInstance(Activity a) {
		EventManager.initInstance(a, new EventManager());
	}

	protected CameraView initCameraView(Activity a) {
		if (isOldDeviceWhereNothingWorksAsExpected) {
			return new CameraViewForOldDevices(a);
		} else {
			return new CameraView(a);
		}
	}

	/**
	 * Don't call the super method if you want do display an info screen on
	 * startup. Just use the {@link InfoScreenSettings} to add information and
	 * the rest will be done automatically
	 * 
	 * @param infoScreenData
	 *            See {@link InfoScreenSettings}
	 */
	public void _f_addInfoScreen(InfoScreenSettings infoScreenData) {
		Log.d(LOG_TAG, "Info screen will be closed instantly");
		infoScreenData.setCloseInstantly();
	}

	private void showInfoDialog(InfoScreenSettings infoScreenData) {
		ActivityConnector.getInstance().startActivity(myTargetActivity,
				InfoScreen.class, infoScreenData);

	}

	private void addOverlaysInCrazyOrder() {
		addGLSurfaceOverlay();
		addCameraOverlay();
		addGUIOverlay();
	}

	private void addOverlays() {
		addCameraOverlay();
		addGLSurfaceOverlay();
		addGUIOverlay();
	}

	private void addGUIOverlay() {
		// add overlay view as an content view to the activity:
		myTargetActivity.addContentView(myOverlayView, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}

	public void addGLSurfaceOverlay() {
		if (Integer.parseInt(android.os.Build.VERSION.SDK) >= Build.VERSION_CODES.ECLAIR) {
			myGLSurfaceView.setZOrderMediaOverlay(true);
		}
		myTargetActivity.addContentView(myGLSurfaceView, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}

	public void addCameraOverlay() {
		if (myCameraView != null) {
			Log.d(LOG_TAG, "Camera preview added as view");
			myTargetActivity.addContentView(myCameraView, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		} else {
			Log.e(LOG_TAG,
					"Camera preview not added to view because it wasnt initialized !");
		}
	}

	private static void loadDeviceDependentSettings(Activity activity) {
		if (Integer.parseInt(android.os.Build.VERSION.SDK) <= Build.VERSION_CODES.DONUT) {
			/*
			 * Here is the problem: OpenGL seems to have rounding errors on
			 * different gpus (see ObjectPicker.floatToByteColorValue) Thus the
			 * G1 need a different value than a Nexus 1 eg.. TODO how to solve
			 * this problem?
			 */
			isOldDeviceWhereNothingWorksAsExpected = true;
			if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				screenOrientation = Surface.ROTATION_0;
			} else {
				screenOrientation = Surface.ROTATION_90;
			}
		} else {
			try {
				Display display = ((WindowManager) activity
						.getSystemService(Activity.WINDOW_SERVICE))
						.getDefaultDisplay();
				screenOrientation = (Integer) display.getClass()
						.getMethod("getRotation", null).invoke(display, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method can be called to visualize additional setup steps in the
	 * initialize-procedure of custom Setup subclass. One example would be to
	 * call this method every time a model is composed in the
	 * initFieldsIfNecessary() or the addWOrldsToRenderer() method. This could
	 * be useful to give feedback while creating complex models.
	 * 
	 * Do not forget to set the {@link Setup}.addToStepsCount(int
	 * additionalSteps) to the correct value, depending on how often call this
	 * method here!
	 * 
	 * @param statusText
	 */
	public void debugLogDoSetupStep(String statusText) {

		double msTheLastStepTook = 0;
		double currentTime = SystemClock.uptimeMillis();
		if (lastTime != 0) {
			msTheLastStepTook = currentTime - lastTime;
		}
		lastTime = currentTime;
		if (msTheLastStepTook != 0) {
			if (displaySetupStepLogging) {
				Log.d(LOG_TAG, "   -> Done (It took " + msTheLastStepTook
						+ "ms)");
			}
		}
		if (displaySetupStepLogging) {
			Log.d(LOG_TAG, "Next step: " + statusText);
		}

		/*
		 * displaying the following stuff does not work until the ui update
		 * process is initialized by Activity.setContentView(..); so wrong place
		 * here:
		 */
		// if (displaySetupStepInTitlebar) {
		// setupProgress += Window.PROGRESS_END / setupStepsCount;
		// myTargetActivity.setProgress(setupProgress);
		// myTargetActivity.setTitle(statusText);
		// }

		if (mySetupListener != null) {
			mySetupListener.onNextStep(msTheLastStepTook, statusText);
		}
	}

	/**
	 * 
	 * 
	 * this is called after the initialization of the AR view started. Doing
	 * field initialization here is a difference to doing it right in the
	 * constructor, because normally a Setup object is created not directly
	 * before it is used to start the AR view. So placing your field
	 * initialization here normaly means to reduce the amount of created objects
	 * if you are using more then one Setup.
	 * 
	 */
	public abstract void _a_initFieldsIfNecessary();

	/**
	 * first you should create a new {@link GLCamera} and a new {@link World}
	 * and then you can use the {@link GLFactory} object to add objects to the
	 * created world. When your world is build, add it to the
	 * {@link GL1Renderer} object by calling
	 * {@link GL1Renderer#addRenderElement(worldData.Renderable)}
	 * 
	 * @param glRenderer
	 *            here you should add your world(s)
	 * @param objectFactory
	 *            you could get this object your self wherever you want by
	 *            getting the singleton-instance of {@link GLFactory}
	 * @param currentPosition
	 *            might be null if no position information is available!
	 */
	public abstract void _b_addWorldsToRenderer(GL1Renderer glRenderer,
			GLFactory objectFactory, GeoObj currentPosition);

	/**
	 * This method should be used to add {@link Action}s to the
	 * {@link EventManager} and the {@link CustomGLSurfaceView} to specify the
	 * input-mechanisms. <br>
	 * <br>
	 * 
	 * Here is the typical AR example: The virtual camera should rotate when the
	 * device is rotated and it should move when the device moves to simulate
	 * the AR rotation and translation in a correct way. Therefore two actions
	 * have to be defined like this:<br>
	 * <br>
	 * <b> eventManager.addOnOrientationChangedAction(new
	 * ActionRotateCameraBuffered(camera)); <br>
	 * eventManager.addOnLocationChangedAction(new ActionCalcRelativePos(world,
	 * camera)); </b> <br>
	 * <br>
	 * 
	 * The {@link ActionRotateCameraBuffered} rotates the virtualCamera and the
	 * {@link ActionCalcRelativePos} calculates the virtual position of the
	 * camera and all the items in the virtual world. There are more
	 * {@link Action}s which can be defined in the {@link EventManager}, for
	 * example for keystrokes or other input types.<br>
	 * <br>
	 * 
	 * For more examples take a look at the different Setup examples.
	 * 
	 * @param eventManager
	 * 
	 * @param arView
	 *            The {@link CustomGLSurfaceView#addOnTouchMoveAction(Action)}
	 *            -method can be used to react on touch-screen input
	 * @param worldUpdater
	 */
	public abstract void _c_addActionsToEvents(EventManager eventManager,
			CustomGLSurfaceView arView, SystemUpdater updater);

	/**
	 * All elements (normally that should only be {@link World}s) which should
	 * be updated have to be added to the {@link SystemUpdater}. This update
	 * process is independent to the rendering process and can be used for all
	 * system-logic which has to be done periodically
	 * 
	 * @param updater
	 *            add anything you want to update to this updater via
	 *            {@link SystemUpdater#addObjectToUpdateCycle(worldData.Updateable)}
	 */
	public abstract void _d_addElementsToUpdateThread(SystemUpdater updater);

	/**
	 * here you can define or load any view you want and add it to the overlay
	 * View. If this method is implemented, the
	 * _e2_addElementsToGuiSetup()-method wont be called automatically
	 * 
	 * @param overlayView
	 *            here you have to add your created view
	 * @param activity
	 *            use this as the context for new views
	 */
	public void _e1_addElementsToOverlay(FrameLayout overlayView,
			Activity activity) {
		// the main.xml layout is loaded and the guiSetup is created for
		// customization. then the customized view is added to overlayView
		View sourceView = View.inflate(activity, defaultArLayoutId, null);
		guiSetup = new GuiSetup(this, sourceView);

		_e2_addElementsToGuiSetup(getGuiSetup(), activity);
		addDroidARInfoBox(activity);
		overlayView.addView(sourceView);
	}

	private void addDroidARInfoBox(final Activity currentActivity) {
		addItemToOptionsMenu(new Command() {

			@Override
			public boolean execute() {
				SimpleUIv1.showInfoScreen(currentActivity, new EditItem() {
					@Override
					public void customizeScreen(ModifierGroup group,
							Object message) {
						group.addModifier(new gui.simpleUI.modifiers.Headline(
								R.drawable.logo, "DroidAR"));
						group.addModifier(new gui.simpleUI.modifiers.InfoText(
								"This was creat" + "ed with the Droid"
										+ "AR fram" + "ework", Gravity.CENTER));
						group.addModifier(new gui.simpleUI.modifiers.InfoText(
								"droidar.goog" + "lecode.com", Gravity.CENTER));
					}
				}, null);
				return true;
			}
		}, "About");
	}

	public GuiSetup getGuiSetup() {
		return guiSetup;
	}

	/**
	 * Here you can add UI-elements like buttons to the predefined design
	 * (main.xml). If you want to overlay your own design, just override the
	 * {@link Setup}._e1_addElementsToOverlay() method and leave this one here
	 * empty.
	 * 
	 * @param guiSetup
	 * @param activity
	 *            this is the same activity you can get with
	 *            {@link Setup#myTargetActivity} but its easier to access this
	 *            way
	 */
	public abstract void _e2_addElementsToGuiSetup(GuiSetup guiSetup,
			Activity activity);

	public GLRenderer initOpenGLRenderer() {
		GL1Renderer r = new GL1Renderer();
		r.setUseLightning(_a2_initLightning(r.getMyLights()));
		return r;
	}

	public CustomGLSurfaceView initOpenGLView(GLRenderer glRenderer2) {
		CustomGLSurfaceView arView = new CustomGLSurfaceView(myTargetActivity);
		arView.setRenderer(glRenderer2);
		return arView;
	}

	/**
	 * This will kill the camera
	 */
	public void releaseCamera() {
		if (myCameraView != null) {
			Log.d(LOG_TAG, "Releasing camera preview " + myCameraView);
			myCameraView.releaseCamera();
		}
	}

	private boolean fillMenuWithCommandsFromCommandgroup(Menu menu,
			CommandGroup g) {
		EfficientList<Command> cList = g.myList;
		final int l = g.myList.myLength;
		for (int i = 0; i < l; i++) {
			menu.add(Menu.NONE, i, Menu.NONE, cList.get(i).getInfoObject()
					.getShortDescr());
		}
		return true;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		if (myOptionsMenuCommands != null) {
			return fillMenuWithCommandsFromCommandgroup(menu,
					myOptionsMenuCommands);
		}
		return false;
	}

	/*
	 * is used by the GuiSetup class to add elements to the options menu
	 */
	public void addItemToOptionsMenu(Command menuItem, String menuItemText) {
		if (myOptionsMenuCommands == null) {
			myOptionsMenuCommands = new CommandGroup();
		}
		menuItem.getInfoObject().setShortDescr(menuItemText);
		myOptionsMenuCommands.add(menuItem);
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (featureId == Window.FEATURE_OPTIONS_PANEL) {
			if (myOptionsMenuCommands != null) {
				return myOptionsMenuCommands.myList.get(item.getItemId())
						.execute();
			}
		}
		return false;
	}

	/**
	 * see {@link Activity#onDestroy}
	 * 
	 * @param a
	 */
	public void onDestroy(Activity a) {
		Log.d(LOG_TAG, "Default onDestroy behavior");
		pauseRenderer();
		pauseUpdater();
		worldUpdater.killUpdaterThread();
		releaseCamera();
		// killCompleteApplicationProcess();
	}

	/**
	 * This will kill the complete process and thereby also any other activity
	 * which uses this process. Only use this method if you want to implement a
	 * final "Exit application" button.
	 */
	public static void killCompleteApplicationProcess() {
		Log.w(LOG_TAG, "Killing complete process");
		System.gc();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}

	/**
	 * see {@link Activity#onStart}
	 * 
	 * @param a
	 */
	public void onStart(Activity a) {
		Log.d(LOG_TAG, "main onStart (setup=" + this + ")");
	}

	/**
	 * When this is called the activity is still visible!
	 * 
	 * see {@link Activity#onPause}
	 * 
	 * @param a
	 */
	public void onPause(Activity a) {
		Log.d(LOG_TAG, "main onPause (setup=" + this + ")");
	}

	/**
	 * see {@link Activity#onResume}
	 * 
	 * @param a
	 */
	public void onResume(Activity a) {
		Log.d(LOG_TAG, "main onResume (setup=" + this + ")");
	}

	/**
	 * When this is called the activity is no longer visible. Camera see
	 * {@link Activity#onStop}
	 * 
	 * @param a
	 */
	public void onStop(Activity a) {
		Log.d(LOG_TAG, "main onStop (setup=" + this + ")");
		pauseRenderer();
		pauseUpdater();
		pauseCameraPreview();
		pauseEventManager();
	}

	/**
	 * see {@link Activity#onRestart}
	 * 
	 * @param a
	 */
	public void onRestart(Activity a) {
		Log.d(LOG_TAG, "main onRestart (setup=" + this + ")");
		resumeRenderer();
		resumeUpdater();
		resumeCameraPreview();
		resumeEventManager();
		reloadTextures();
	}

	private void reloadTextures() {
		TextureManager.reloadTexturesIfNeeded();
	}

	public void pauseEventManager() {
		EventManager.getInstance().pauseEventListeners();
	}

	public void resumeEventManager() {
		EventManager.getInstance().resumeEventListeners(myTargetActivity,
				useAccelAndMagnetoSensors);
	}

	public void pauseUpdater() {
		if (worldUpdater != null) {
			Log.d(LOG_TAG, "Pausing world updater now");
			worldUpdater.pauseUpdater();
		}
	}

	public void resumeUpdater() {
		if (worldUpdater != null) {
			worldUpdater.resumeUpdater();
		}
	}

	public final void pauseCameraPreview() {
		if (myCameraView != null) {
			Log.d(LOG_TAG, "Pausing camera preview " + myCameraView);
			myCameraView.pause();
		}
	}

	public final void resumeCameraPreview() {
		if (myCameraView != null) {
			Log.d(LOG_TAG, "Resuming camera preview " + myCameraView);
			myCameraView.resumeCamera();
		}
	}

	public void pauseRenderer() {
		if (glRenderer != null) {
			Log.d(LOG_TAG, "Pausing renderer and GLSurfaceView now");
			glRenderer.pause();
			if (myGLSurfaceView != null) {
				myGLSurfaceView.onPause();
			}
		}
	}

	public void resumeRenderer() {
		if (glRenderer != null) {
			Log.d(LOG_TAG, "Resuming renderer and GLSurfaceView now");
		}
		glRenderer.resume();
		if (myGLSurfaceView != null) {
			myGLSurfaceView.onResume();
		}
	}

	public boolean onKeyDown(Activity a, int keyCode, KeyEvent event) {
		// if the keyAction isnt defined return false:
		return EventManager.getInstance().onKeyDown(keyCode, event);
	}

	public float getScreenWidth() {
		if (getScreenOrientation() == Surface.ROTATION_90
				|| getScreenOrientation() == Surface.ROTATION_270) {
			return getActivity().getWindowManager().getDefaultDisplay()
					.getHeight();
		} else {
			return getActivity().getWindowManager().getDefaultDisplay()
					.getWidth();
		}
	}

	public float getScreenHeigth() {
		if (getScreenOrientation() == Surface.ROTATION_90
				|| getScreenOrientation() == Surface.ROTATION_270) {
			return getActivity().getWindowManager().getDefaultDisplay()
					.getWidth();
		} else {
			return getActivity().getWindowManager().getDefaultDisplay()
					.getHeight();
		}
	}

}
