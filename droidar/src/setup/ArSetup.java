package setup;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import logger.ARLogger;
import system.EventManager;
import system.Setup;
import system.SimpleLocationManager;
import system.TaskManager;
import util.EfficientList;
import util.Log;
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
import gamelogic.FeedbackReports;
import gl.GLFactory;
import gl.LightSource;
import gl.ObjectPicker;
import gl.textures.TextureManager;
import gui.GuiSetup;
import gui.InfoScreenSettings;

public abstract class ArSetup implements ISetupSteps, ISetupLifeCycle {

	private static final String LOG_TAG = "ArSetup";
	private CommandGroup mOptionsMenuCommands;
	private ISetupEntry mEntry;
	private GuiSetup mGuiSetup;
	private SystemUpdater mWorldUpdater;
	private Thread mWorldThread;
	private static Integer mScreenOrientation = Surface.ROTATION_90;

	public ArSetup(){
		
	}
	public ArSetup(ISetupEntry pEntry) {
		mEntry = pEntry;
	}

	/**
	 * Default initialization is {@link Surface#ROTATION_90}, use landscape on
	 * default mode if the initialization does not work
	 * 
	 * @return {@link Surface#ROTATION_0} or {@link Surface#ROTATION_180} would
	 *         mean portrait mode and 90 and 270 would mean landscape mode
	 *         (should be the same on tablets and mobile devices
	 */
	public int getScreenOrientation() {
		if (mScreenOrientation == null) {
			ARLogger.error(LOG_TAG, "screenOrientation was not set! Will "
					+ "asume default 90 degree rotation for screen");
			return Surface.ROTATION_90;
		}
		return mScreenOrientation;
	}

	/**
	 * @return This will just return {@link Setup#myTargetActivity}. Direct
	 *         access to this field is also possible
	 */
	public Activity getActivity() {
		return mEntry.getActivity();
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
	public void run(ISetupEntry pEntry) {
		mEntry = pEntry;

		setFullScreen();

		keepScreenOn();

		loadDeviceDependentSettings(getActivity());

		EventManager.getInstance().registerListeners(getActivity(), true);

		_a_initFieldsIfNecessary();

		_b_addWorldsToRenderer(mEntry.getAugmentedView().getRenderer(),
				GLFactory.getInstance(), EventManager.getInstance()
						.getCurrentLocationObject());

		_c_addActionsToEvents(EventManager.getInstance(), mEntry
				.getAugmentedView().getGLSurfaceView(), mWorldUpdater);

		_d_addElementsToUpdateThread(mWorldUpdater);

		_e1_addElementsToOverlay(mEntry.getAugmentedView(), getActivity());
		
		mWorldThread = new Thread(mWorldUpdater);
		mWorldThread.start();
		
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

	public SystemUpdater getSystemUpdater() {
		return mWorldUpdater;
	}

	@Override
	public boolean _a2_initLightning(ArrayList<LightSource> lights) {
		lights.add(LightSource.newDefaultAmbientLight(GL10.GL_LIGHT0));
		lights.add(LightSource.newDefaultSpotLight(GL10.GL_LIGHT1, new Vec(5,
				5, 5), new Vec(0, 0, 0)));
		return true;
	}

	private void initAllSingletons() {
		initEventManagerInstance(getActivity());
		SimpleLocationManager.resetInstance();
		TextureManager.resetInstance();
		TaskManager.resetInstance();
		ObjectPicker.resetInstance(new CommandDeviceVibrate(getActivity(), 30));
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

	private static void loadDeviceDependentSettings(Activity activity) {
		try {
			Display display = ((WindowManager) activity
					.getSystemService(Activity.WINDOW_SERVICE))
					.getDefaultDisplay();
			mScreenOrientation = (Integer) display.getClass()
					.getMethod("getRotation", null).invoke(display, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void _e1_addElementsToOverlay(FrameLayout overlayView,
			Activity activity) {
		mGuiSetup = new GuiSetup(this, mEntry.getAugmentedView().getGuiView());
		_e2_addElementsToGuiSetup(getGuiSetup(), activity);
	}

	/**
	 * 
	 * @return
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

	public boolean onCreateOptionsMenu(Menu menu) {
		if (mOptionsMenuCommands != null) {
			return fillMenuWithCommandsFromCommandgroup(menu,
					mOptionsMenuCommands);
		}
		return false;
	}

	/*
	 * is used by the GuiSetup class to add elements to the options menu
	 */
	public void addItemToOptionsMenu(Command menuItem, String menuItemText) {
		if (mOptionsMenuCommands == null) {
			mOptionsMenuCommands = new CommandGroup();
		}
		menuItem.getInfoObject().setShortDescr(menuItemText);
		mOptionsMenuCommands.add(menuItem);
	}

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

	public void pauseEventManager() {
		EventManager.getInstance().pauseEventListeners();
	}

	public void resumeEventManager() {
		EventManager.getInstance().resumeEventListeners(getActivity(), true);
	}

	public void pauseUpdater() {
		if (mWorldUpdater != null) {
			Log.d(LOG_TAG, "Pausing world updater now");
			mWorldUpdater.pauseUpdater();
		}
	}

	public void resumeUpdater() {
		if (mWorldUpdater != null) {
			mWorldUpdater.resumeUpdater();
		}
	}

	public boolean onKeyDown(Activity a, int keyCode, KeyEvent event) {
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

	@Override
	public void onCreate() {
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
