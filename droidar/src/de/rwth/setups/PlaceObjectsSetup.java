package de.rwth.setups;

import geo.GeoObj;
import gl.Color;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;
import gui.GuiSetup;
import setup.ArSetup;
import system.ErrorHandler;
import system.EventManager;
import util.Vec;
import util.Wrapper;
import worlddata.Obj;
import worlddata.SystemUpdater;
import worlddata.World;
import actions.Action;
import actions.ActionBufferedCameraAR;
import actions.ActionCalcRelativePos;
import actions.ActionMoveCameraBuffered;
import actions.ActionRotateCameraBuffered;
import android.app.Activity;
import commands.Command;

public class PlaceObjectsSetup extends ArSetup {
	private GLCamera camera;
	private World world;

	private Wrapper placeObjectWrapper;

	@Override
	public void initFieldsIfNecessary() {

		// allow the user to send error reports to the developer:
		ErrorHandler.enableEmailReports("droidar.rwth@gmail.com",
				"Error in DroidAR App");

		placeObjectWrapper = new Wrapper();

	}

	@Override
	public void addWorldsToRenderer(GL1Renderer renderer,
			GLFactory objectFactory, GeoObj currentPosition) {
		camera = new GLCamera(new Vec(0, 0, 10));
		world = new World(camera);

		Obj placerContainer = new Obj();
		placerContainer.setComp(objectFactory.newArrow());
		world.add(placerContainer);

		placeObjectWrapper.setTo(placerContainer);

		renderer.addRenderElement(world);
	}

	@Override
	public void addActionsToEvents(EventManager eventManager,
			CustomGLSurfaceView arView, SystemUpdater updater) {
		arView.addOnTouchMoveAction(new ActionBufferedCameraAR(camera));
		Action rot1 = new ActionRotateCameraBuffered(camera);
		//Action rot2 = new ActionPlaceObject(camera, placeObjectWrapper, 50);

		updater.addObjectToUpdateCycle(rot1);
		//updater.addObjectToUpdateCycle(rot2);

		eventManager.addOnOrientationChangedAction(rot1);
		//eventManager.addOnOrientationChangedAction(rot2);
		eventManager.addOnTrackballAction(new ActionMoveCameraBuffered(camera,
				5, 25));
		eventManager.addOnLocationChangedAction(new ActionCalcRelativePos(
				world, camera));
	}

	@Override
	public void addElementsToUpdateThread(SystemUpdater worldUpdater) {
		worldUpdater.addObjectToUpdateCycle(world);
	}

	@Override
	public void addElementsToGuiSetup(GuiSetup guiSetup, Activity context) {

		guiSetup.addButtonToTopView(new Command() {
			@Override
			public boolean execute() {
				final Obj placerContainer = newObject();
				world.add(placerContainer);
				placeObjectWrapper.setTo(placerContainer);
				return true;
			}

			private Obj newObject() {
				final Obj placerContainer = new Obj();
				Color c = Color.getRandomRGBColor();
				c.alpha = 0.7f;
				MeshComponent arrow = GLFactory.getInstance().newDiamond(c);
				arrow.setOnClickCommand(new Command() {
					@Override
					public boolean execute() {
						placeObjectWrapper.setTo(placerContainer);
						return true;
					}
				});
				placerContainer.setComp(arrow);
				return placerContainer;
			}
		}, "Place next!");

		guiSetup.setTopViewCentered();
	}

}
