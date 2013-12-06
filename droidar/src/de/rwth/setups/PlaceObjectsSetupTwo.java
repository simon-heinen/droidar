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
import worlddata.MoveComp;
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
import components.ViewPosCalcerComp;
import entry.ISetupEntry;

public class PlaceObjectsSetupTwo extends ArSetup {
	private GLCamera camera;
	private World world;
	private ViewPosCalcerComp viewPosCalcer;
	private Obj selectedObj;
	private MoveComp moveComp;

	@Override
	public void initFieldsIfNecessary() {

		// allow the user to send error reports to the developer:
		ErrorHandler.enableEmailReports("droidar.rwth@gmail.com",
				"Error in DroidAR App");
		camera = new GLCamera(new Vec(0, 0, 15));
		world = new World(camera);
		viewPosCalcer = new ViewPosCalcerComp(camera, 150, 0.1f) {
			@Override
			public void onPositionUpdate(worlddata.Updateable parent,
					Vec targetVec) {
				if (parent instanceof Obj) {
					Obj obj = (Obj) parent;
					MoveComp m = obj.getComp(MoveComp.class);
					if (m != null) {
						m.mTargetPos = targetVec;
					}
				}
			}
		};
		moveComp = new MoveComp(4);
	}

	@Override
	public void addWorldsToRenderer(GL1Renderer renderer,
			GLFactory objectFactory, GeoObj currentPosition) {
		world.add(newObject());
		renderer.addRenderElement(world);
	}

	@Override
	public void addActionsToEvents(EventManager eventManager,
			CustomGLSurfaceView arView, SystemUpdater updater) {
		arView.addOnTouchMoveAction(new ActionBufferedCameraAR(camera));
		Action rot = new ActionRotateCameraBuffered(camera);
		updater.addObjectToUpdateCycle(rot);
		eventManager.addOnOrientationChangedAction(rot);

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
				world.add(newObject());
				return true;
			}

		}, "Place next");

		guiSetup.setTopViewCentered();
	}

	private Obj newObject() {
		final Obj obj = new Obj();
		Color c = Color.getRandomRGBColor();
		c.alpha = 0.7f;
		MeshComponent diamond = GLFactory.getInstance().newDiamond(c);
		obj.setComp(diamond);
		setComps(obj);
		diamond.setOnClickCommand(new Command() {
			@Override
			public boolean execute() {
				setComps(obj);
				return true;
			}

		});
		return obj;
	}

	private void setComps(Obj obj) {
		if (selectedObj != null) {
			selectedObj.remove(viewPosCalcer);
			selectedObj.remove(moveComp);
		}
		obj.setComp(viewPosCalcer);
		obj.setComp(moveComp);
		selectedObj = obj;
	}
}
