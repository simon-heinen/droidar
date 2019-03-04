package de.rwth.setups;

import geo.GeoObj;
import gl.Color;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import gui.GuiSetup;
import gui.InfoScreenSettings;
import system.ErrorHandler;
import system.EventManager;
import system.Setup;
import util.Vec;
import worldData.Obj;
import worldData.SystemUpdater;
import worldData.World;
import actions.Action;
import actions.ActionCalcRelativePos;
import actions.ActionMoveCameraBuffered;
import actions.ActionRotateCameraBuffered;
import android.app.Activity;

import commands.ui.CommandShowToast;
import components.ProximitySensor;

import de.rwth.R;

public class CollectItemsSetup extends Setup {

	private GLCamera camera;
	private World world;
	private int catchedNumber;

	@Override
	public void _a_initFieldsIfNecessary() {
		// allow the user to send error reports to the developer:
		ErrorHandler.enableEmailReports("droidar.rwth@gmail.com",
				"Error in CollectItemsSetup");

	}

	@Override
	public void _b_addWorldsToRenderer(GL1Renderer renderer,
			GLFactory objectFactory, GeoObj currentPosition) {

		camera = new GLCamera(new Vec(0, 0, 1));
		world = new World(camera);

		MeshComponent arrow = GLFactory.getInstance().newArrow();
		arrow.setPosition(new Vec(0, 0, 4));

		MeshComponent circle = GLFactory.getInstance().newCircle(
				Color.redTransparent());
		circle.setScale(new Vec(4, 4, 4));
		// circle.myAnimation = new AnimationPulse(2, new Vec(0, 0, 0), new
		// Vec(4, 4, 4), 0.2f);

		final MeshComponent itemMesh = new Shape();
		itemMesh.addChild(arrow);
		itemMesh.addChild(circle);
		itemMesh.setPosition(Vec.getNewRandomPosInXYPlane(camera.getPosition(),
				5, 10));

		Obj itemToCollect = new Obj();
		itemToCollect.setComp(new ProximitySensor(camera, 3f) {
			@Override
			public void onObjectIsCloseToCamera(GLCamera myCamera2, Obj obj,
					MeshComponent m, float currentDistance) {
				catchedNumber++;
				new CommandShowToast(myTargetActivity, "You got me "
						+ catchedNumber + " times").execute();
				itemMesh.setPosition(Vec.getNewRandomPosInXYPlane(
						camera.getPosition(), 5, 20));
			}
		});

		itemToCollect.setComp(itemMesh);
		world.add(itemToCollect);
		renderer.addRenderElement(world);
	}

	@Override
	public void _c_addActionsToEvents(EventManager eventManager,
			CustomGLSurfaceView arView, SystemUpdater updater) {
		ActionMoveCameraBuffered move = new ActionMoveCameraBuffered(camera, 5,
				25);
		arView.addOnTouchMoveAction(move);
		eventManager.addOnTrackballAction(move);
		Action rot = new ActionRotateCameraBuffered(camera);
		updater.addObjectToUpdateCycle(rot);
		eventManager.addOnOrientationChangedAction(rot);
		eventManager.addOnLocationChangedAction(new ActionCalcRelativePos(
				world, camera));
	}

	@Override
	public void _d_addElementsToUpdateThread(SystemUpdater worldUpdater) {

		worldUpdater.addObjectToUpdateCycle(world);

	}

	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity context) {
	}

	@Override
	public void _f_addInfoScreen(InfoScreenSettings infoScreenData) {
		infoScreenData
				.addText("There will an object spawned close to you which you have to collect!");
		infoScreenData
				.addTextWithIcon(
						R.drawable.infoboxblue,
						"Step into the red area to collect the object. A counter will signalize how often you collected the object. Instead of actually walking to the object you can simulate movement by using the trackball or the touchscreen.");
	}

}
