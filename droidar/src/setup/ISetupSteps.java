package setup;

import geo.GeoObj;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.LightSource;
import gui.GuiSetup;

import java.util.ArrayList;

import system.EventManager;
import system.Setup;
import actions.Action;
import actions.ActionCalcRelativePos;
import actions.ActionRotateCameraBuffered;
import android.app.Activity;
import android.widget.FrameLayout;
import worldData.SystemUpdater;
import worldData.World;

public interface ISetupSteps {
	
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
	public void _a_initFieldsIfNecessary();
	
	/**
	 * If you don't override this method it will create 2 default
	 * {@link LightSource}s
	 * 
	 * @param lights
	 *            add all the {@link LightSource}s you want to use to this list
	 * @return true if lightning should be enabled
	 */
	public boolean _a2_initLightning(ArrayList<LightSource> lights);
	
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
	public void _b_addWorldsToRenderer(GL1Renderer glRenderer,
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
	public void _c_addActionsToEvents(EventManager eventManager,
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
			Activity activity);
	
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

}
