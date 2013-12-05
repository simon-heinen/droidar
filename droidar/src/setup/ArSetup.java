package setup;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import logger.ARLogger;
import system.EventManager;
import system.SimpleLocationManager;
import system.TaskManager;
import util.EfficientList;
import util.Vec;
import worldData.SystemUpdater;
import android.app.Activity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import commands.Command;
import commands.CommandGroup;
import commands.system.CommandDeviceVibrate;
import commands.undoable.CommandProcessor;
import entry.ArType;
import entry.ISetupEntry;
import gl.GLFactory;
import gl.LightSource;
import gl.ObjectPicker;
import gl.textures.TextureManager;
import gui.GuiSetup;
import gui.InfoScreenSettings;

/**
 * Extend this class and implement all abstract methods to initialize your AR
 * application. More information can be found in the JavaDoc of the specific
 * methods and for a simple default AR setup use the {@link setup.DefaultARSetup}
 * 
 */
public abstract class ArSetup implements ISetupSteps, ISetupLifeCycle {
	private static final String LOG_TAG = "ArSetup";
	private CommandGroup mOptionsMenuCommands;
	private ISetupEntry mEntry;
	private GuiSetup mGuiSetup;
	private SystemUpdater mWorldUpdater;
	private Thread mWorldThread;
	private static Integer mScreenOrientation = Surface.ROTATION_90;
	
	//magic numbers
	private static final int VIBRATEDURATION = 30;
	private static final int XLIGHTPOS = 5;
	private static final int YLIGHTPOS = 5;
	private static final int ZLIGHTPOS = 5;

	/**
	 * Constructor.
	 * @param pEntry - {@link entry.ArFragment} or {@link entry.ArActivity}
	 */
	public ArSetup(ISetupEntry pEntry) {
		mEntry = pEntry;
	}
	
	/**
	 * Constructor.
	 */
	public ArSetup() {
		this(null);
	}
	
	/**
	 * Set the entry for this setup. 
	 * @param pEntry - {@link entry.ISetupEntry}
	 */
	public void setEntry(ISetupEntry pEntry) {
		mEntry = pEntry;
	}

	/**
	 * Default initialization is {@link Surface#ROTATION_90}, use landscape on
	 * default mode if the initialization does not work.
	 * 
	 * @return {@link Surface#ROTATION_0} or {@link Surface#ROTATION_180} would
	 *         mean portrait mode and 90 and 270 would mean landscape mode
	 *         (should be the same on tablets and mobile devices
	 */
	public static int getScreenOrientation() {
		if (mScreenOrientation == null) {
			ARLogger.error(LOG_TAG, "screenOrientation was not set! Will "
					+ "asume default 90 degree rotation for screen");
			return Surface.ROTATION_90;
		}
		return mScreenOrientation;
	}

	/**
	 * @return This will just return {@link ArSetup#getActivity()}. Direct
	 *         access to this field is also possible
	 */
	public Activity getActivity() {
		if (mEntry == null) {
			return null;
		} else {
			return mEntry.getActivity();
		}
	}

	/**
	 * This method has to be executed in the activity which want to display the
	 * AR content.
	 * 
	 * @param pEntry - {@link entry.ArFragment} or {@link entry.ArActivity}
	 */
	public void run(ISetupEntry pEntry) {
		if (pEntry != null) {
			mEntry = pEntry;
		}

		setFullScreen();

		keepScreenOn();

		loadDeviceDependentSettings(getActivity());

		EventManager.getInstance().registerListeners(getActivity(), true);

		initFieldsIfNecessary();

		addWorldsToRenderer(mEntry.getAugmentedView().getRenderer(),
				GLFactory.getInstance(), EventManager.getInstance()
						.getCurrentLocationObject());

		addActionsToEvents(EventManager.getInstance(), mEntry
				.getAugmentedView().getGLSurfaceView(), mWorldUpdater);

		addElementsToUpdateThread(mWorldUpdater);

		addElementsToOverlay(mEntry.getAugmentedView(), getActivity());
		
		mWorldThread = new Thread(mWorldUpdater);
		mWorldThread.start();
		
	}
	
	/**
	 * This method has to be executed in the activity which want to display the
	 * AR content.
	 * 
	 */
	public void run() {
		run(null);
	}

	private void setFullScreen() {
		if (mEntry.getType() == ArType.ACTIVITY) {
			if (getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE)) {
				getActivity().getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
			} else {

				getActivity().requestWindowFeature(
						Window.PROGRESS_VISIBILITY_ON);
				getActivity().getWindow().requestFeature(
						Window.FEATURE_PROGRESS);
				getActivity().setProgressBarVisibility(true);
			}
		}
	}

	private void keepScreenOn() {
		getActivity().getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/**
	 * Retrieve the system updater.
	 * @return - {@link worldData.SystemUpdater}
	 */
	public SystemUpdater getSystemUpdater() {
		return mWorldUpdater;
	}

	@Override
	public boolean initLightning(ArrayList<LightSource> lights) {
		lights.add(LightSource.newDefaultAmbientLight(GL10.GL_LIGHT0));
		lights.add(LightSource.newDefaultSpotLight(GL10.GL_LIGHT1, new Vec(XLIGHTPOS,
				YLIGHTPOS, ZLIGHTPOS), new Vec(0, 0, 0)));
		return true;
	}

	private void initAllSingletons() {
		initEventManagerInstance(getActivity());
		SimpleLocationManager.resetInstance();
		TextureManager.resetInstance();
		TaskManager.resetInstance();
		ObjectPicker.resetInstance(new CommandDeviceVibrate(getActivity(), VIBRATEDURATION));
		CommandProcessor.resetInstance();
	}

	/**
	 * You can create and set a subclass of {@link EventManager} here. To set
	 * the instance use
	 * {@link EventManager#initInstance(android.content.Context, EventManager)}
	 * 
	 * @param activity - {@link android.app.Activity}
	 */
	public void initEventManagerInstance(Activity activity) {
		EventManager.initInstance(activity, new EventManager());
	}

	/**
	 * Don't call the super method if you want do display an info screen on
	 * startup. Just use the {@link InfoScreenSettings} to add information and
	 * the rest will be done automatically
	 * 
	 * @param infoScreenData
	 *            See {@link InfoScreenSettings}
	 */
	public void addInfoScreen(InfoScreenSettings infoScreenData) {
		infoScreenData.setCloseInstantly();
	}

	private static void loadDeviceDependentSettings(Activity activity) {
		try {
			Display display = ((WindowManager) activity
					.getSystemService(Activity.WINDOW_SERVICE))
					.getDefaultDisplay();
			mScreenOrientation = (Integer) display.getClass()
					.getMethod("getRotation", new Class<?>[]{}).invoke(display, new Object[]{});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addElementsToOverlay(FrameLayout overlayView,
			Activity activity) {
		mGuiSetup = new GuiSetup(this, mEntry.getAugmentedView().getGuiView());
		addElementsToGuiSetup(getGuiSetup(), activity);
	}

	/**
	 * Retrieve the gui setup.
	 * @return {@link GuiSetup}
	 */
	public GuiSetup getGuiSetup() {
		return mGuiSetup;
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

	/**
	 * Create options menu.
	 * @param menu - {@link Menu}
	 * @return - {@link boolean} true if successfully created
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mOptionsMenuCommands != null) {
			return fillMenuWithCommandsFromCommandgroup(menu,
					mOptionsMenuCommands);
		}
		return false;
	}

	/**
	 * Add items to the options menu. 
	 * @param menuItem - {@link Command}
	 * @param menuItemText - {@link String} text displayed
	 */
	public void addItemToOptionsMenu(Command menuItem, String menuItemText) {
		if (mOptionsMenuCommands == null) {
			mOptionsMenuCommands = new CommandGroup();
		}
		menuItem.getInfoObject().setShortDescr(menuItemText);
		mOptionsMenuCommands.add(menuItem);
	}

	/**
	 * 
	 * @param featureId - {@link int} feature id
	 * @param item - {@link MenuItem}
	 * @return - {@link boolean} true if successfully
	 */
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (featureId == Window.FEATURE_OPTIONS_PANEL) {
			if (mOptionsMenuCommands != null) {
				return mOptionsMenuCommands.myList.get(item.getItemId())
						.execute();
			}
		}
		return false;
	}

	private void reloadTextures() {
		TextureManager.reloadTexturesIfNeeded();
	}

	/**
	 * Pause the event manager thread. 
	 */
	public void pauseEventManager() {
		EventManager.getInstance().pauseEventListeners();
	}

	/**
	 * Resume the event manager thread. 
	 */
	public void resumeEventManager() {
		EventManager.getInstance().resumeEventListeners(getActivity(), true);
	}

	/**
	 * Pause the updater thread. 
	 */
	public void pauseUpdater() {
		if (mWorldUpdater != null) {
			mWorldUpdater.pauseUpdater();
		}
	}

	/**
	 * Resume the updater thread.
	 */
	public void resumeUpdater() {
		if (mWorldUpdater != null) {
			mWorldUpdater.resumeUpdater();
		}
	}

	/**
	 * Handle user touch events.
	 * @param activity - {@link android.app.Activity}
	 * @param keyCode - {@link int} key code
	 * @param event - {@link KeyEvent}
	 * @return - {@link boolean} true if action is successful
	 */
	public boolean onKeyDown(Activity activity, int keyCode, KeyEvent event) {
		return EventManager.getInstance().onKeyDown(keyCode, event);
	}

	/**
	 * Retrieve the screen width based on orientation.
	 * @return - float
	 */
	@SuppressWarnings("deprecation")
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

	/**
	 * Retrieve the screen height based on orientation.
	 * @return - float
	 */
	@SuppressWarnings("deprecation")
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

	@Override
	public void onCreate() {
		ARLogger.debug(LOG_TAG, "onCreate");
		initAllSingletons();
		mWorldUpdater = new SystemUpdater();
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onPause() {
		ARLogger.debug(LOG_TAG, "onPause");
		pauseUpdater();
		pauseEventManager();
	}

	@Override
	public void onResume() {
		ARLogger.debug(LOG_TAG, "onResume");
		resumeUpdater();
		resumeEventManager();
		reloadTextures();
	}

	@Override
	public void onStop() {
		ARLogger.debug(LOG_TAG, "onStop");
		pauseUpdater();
		mWorldUpdater.killUpdaterThread();
	}

}
