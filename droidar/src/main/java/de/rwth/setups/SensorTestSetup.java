package de.rwth.setups;

import geo.GeoObj;
import gl.Color;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.animations.AnimationRotate;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import gui.GuiSetup;
import system.ErrorHandler;
import system.EventManager;
import system.Setup;
import util.Vec;
import worldData.SystemUpdater;
import worldData.World;
import actions.Action;
import actions.ActionBufferedCameraAR;
import actions.ActionMoveCameraBuffered;
import actions.ActionRotateCameraBuffered;
import actions.ActionRotateCameraBuffered3;
import actions.ActionRotateCameraBuffered4;
import actions.ActionRotateCameraBufferedDebug;
import actions.ActionRotateCameraBufferedDirect;
import actions.ActionRotateCameraUnbuffered;
import actions.ActionRotateCameraUnbuffered2;
import actions.ActionUseCameraAngles2;
import android.app.Activity;

import commands.Command;

public class SensorTestSetup extends Setup {

	private GLCamera camera;
	private World world;
	private Action rotActionB1;
	private Action rotActionB3;
	private Action rotActionB4;
	private Action rotActionDebug;
	private Action rotActionUnB;
	private Action rotActionUnB2;
	private Action rotActionB2;

	@Override
	public void _a_initFieldsIfNecessary() {
		// allow the user to send error reports to the developer:
		ErrorHandler.enableEmailReports("droidar.rwth@gmail.com",
				"Error in DroidAR App");

		/*
		 * the following are just example rotate actions, take a look at the
		 * implementation to see how to create own CameraBuffered actions
		 */

		camera = new GLCamera();
		rotActionB1 = new ActionRotateCameraBuffered(camera);
		rotActionB2 = new ActionRotateCameraBufferedDirect(camera);
		rotActionB3 = new ActionRotateCameraBuffered3(camera);
		rotActionB4 = new ActionRotateCameraBuffered4(camera);
		new ActionRotateCameraBufferedDebug(camera);
		rotActionUnB = new ActionRotateCameraUnbuffered(camera);
		rotActionUnB2 = new ActionRotateCameraUnbuffered2(camera);

	}

	@Override
	public void _b_addWorldsToRenderer(GL1Renderer renderer,
			GLFactory objectFactory, GeoObj currentPosition) {

		world = new World(camera);

		MeshComponent compasrose = new Shape();

		MeshComponent middle = objectFactory.newDiamond(Color.green());
		middle.setPosition(new Vec(0, 0, -2.8f));
		middle.addChild(new AnimationRotate(40, new Vec(0, 0, 1)));
		compasrose.addChild(middle);

		int smallDistance = 10;
		int longDistance = 60;

		MeshComponent north = objectFactory.newDiamond(Color.redTransparent());
		north.setPosition(new Vec(0, smallDistance, 0));

		MeshComponent north2 = objectFactory.newDiamond(Color.red());
		north2.setPosition(new Vec(0, longDistance, 0));

		MeshComponent east = objectFactory.newDiamond(Color.blueTransparent());
		east.setPosition(new Vec(smallDistance, 0, 0));

		MeshComponent east2 = objectFactory.newDiamond(Color.blue());
		east2.setPosition(new Vec(longDistance, 0, 0));

		MeshComponent south = objectFactory.newDiamond(Color.blueTransparent());
		south.setPosition(new Vec(0, -smallDistance, 0));

		MeshComponent south2 = objectFactory.newDiamond(Color.blue());
		south2.setPosition(new Vec(0, -longDistance, 0));

		MeshComponent west = objectFactory.newDiamond(Color.blueTransparent());
		west.setPosition(new Vec(-smallDistance, 0, 0));

		MeshComponent west2 = objectFactory.newDiamond(Color.blue());
		west2.setPosition(new Vec(-longDistance, 0, 0));

		compasrose.addChild(north2);
		compasrose.addChild(north);
		compasrose.addChild(east2);
		compasrose.addChild(east);
		compasrose.addChild(south2);
		compasrose.addChild(south);
		compasrose.addChild(west2);
		compasrose.addChild(west);

		currentPosition.setComp(compasrose);
		world.add(currentPosition);

		renderer.addRenderElement(world);

	}

	@Override
	public void _c_addActionsToEvents(EventManager eventManager,
			CustomGLSurfaceView arView, SystemUpdater updater) {
		arView.addOnTouchMoveAction(new ActionBufferedCameraAR(camera));
		eventManager.addOnOrientationChangedAction(rotActionB1);
		eventManager.addOnTrackballAction(new ActionMoveCameraBuffered(camera,
				5, 25));

		eventManager
				.addOnOrientationChangedAction(new ActionUseCameraAngles2() {

					@Override
					public void onAnglesUpdated(float pitch, float roll,
							float compassAzimuth) {
						/*
						 * the angles could be used in some way here..
						 */
					}
				});

	}

	@Override
	public void _d_addElementsToUpdateThread(SystemUpdater worldUpdater) {
		worldUpdater.addObjectToUpdateCycle(world);
		worldUpdater.addObjectToUpdateCycle(rotActionB1);
		worldUpdater.addObjectToUpdateCycle(rotActionB3);
		worldUpdater.addObjectToUpdateCycle(rotActionB4);
		worldUpdater.addObjectToUpdateCycle(rotActionDebug);
		worldUpdater.addObjectToUpdateCycle(rotActionUnB);
		worldUpdater.addObjectToUpdateCycle(rotActionUnB2);
	}

	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {
		guiSetup.addButtonToBottomView(new myRotateAction(rotActionB1),
				"Camera Buffered 1");
		guiSetup.addButtonToBottomView(new myRotateAction(rotActionB2),
				"Camera Buffered 2");
		guiSetup.addButtonToBottomView(new myRotateAction(rotActionB3),
				"Camera Buffered 3");
		guiSetup.addButtonToBottomView(new myRotateAction(rotActionB4),
				"Camera Buffered 4");
		guiSetup.addButtonToBottomView(new myRotateAction(rotActionUnB),
				"Camera Unbuffered 1");
		guiSetup.addButtonToBottomView(new myRotateAction(rotActionUnB2),
				"Camera Unbuffered 2");
	}

	private class myRotateAction extends Command {

		private Action myAction;

		public myRotateAction(Action a) {
			myAction = a;
		}

		@Override
		public boolean execute() {
			EventManager.getInstance().getOnOrientationChangedAction().clear();
			EventManager.getInstance().getOnOrientationChangedAction()
					.add(myAction);
			return true;
		}

	}

}
