package de.rwth;

import geo.GeoObj;
import gl.Color;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.animations.AnimationFaceToCamera;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import gui.GuiSetup;
import markerDetection.MarkerDetectionSetup;
import markerDetection.MarkerObjectMap;
import markerDetection.UnrecognizedMarkerListener;
import system.EventManager;
import util.IO;
import util.Vec;
import worldData.Obj;
import worldData.SystemUpdater;
import worldData.World;
import actions.ActionBufferedCameraAR;
import android.app.Activity;

import commands.system.CommandPlaySound;

public class ExampleMarkerRenderSetup extends MarkerDetectionSetup {

	private GLCamera camera;
	private World world;

	@Override
	public UnrecognizedMarkerListener _a2_getUnrecognizedMarkerListener() {
		return null;
	}

	@Override
	public void _a3_registerMarkerObjects(MarkerObjectMap markerObjectMap) {
		markerObjectMap.put(new CameraMarker(0, camera));
	}

	@Override
	public void _a_initFieldsIfNecessary() {
		camera = new GLCamera();
		world = new World(camera);
	}

	@Override
	public void _b_addWorldsToRenderer(GL1Renderer renderer,
			GLFactory objectFactory, GeoObj currentPosition) {
		renderer.addRenderElement(world);
		Obj o = new Obj();
		o.setComp(AndroidMeshData.getAndroidMesh());
		world.add(o);
		// initWorld(world);
	}

	private synchronized void initWorld(World world) {

		world.add(GLFactory.getInstance().newSolarSystem(new Vec(0, 0, 5)));
		world.add(GLFactory.getInstance().newHexGroupTest(new Vec(0, 0, -0.1f)));

		MeshComponent c = GLFactory.getInstance().newCube(null);
		c.setPosition(new Vec(-3, 3, 0));
		c.setScale(new Vec(0.5f, 0.5f, 0.5f));
		c.setOnClickCommand(new CommandPlaySound("/sdcard/train.mp3"));
		Obj geoC = new Obj();
		geoC.setComp(c);
		world.add(geoC);

		MeshComponent c2 = GLFactory.getInstance().newCube(null);
		c2.setPosition(new Vec(3, 3, 0));
		// GeoObj geoC = new GeoObj(GeoObj.normaluhr, c);
		Obj geoC2 = new Obj();
		geoC2.setComp(c2);
		world.add(geoC2);

		Obj hex = new Obj();
		Shape hexMesh = GLFactory.getInstance().newHexagon(
				new Color(0, 0, 1, 0.7f));
		hexMesh.getPosition().add(new Vec(0, 0, -1));
		hexMesh.scaleEqual(4.5f);
		hex.setComp(hexMesh);

		world.add(hex);

		Obj grid = new Obj();
		MeshComponent gridMesh = GLFactory.getInstance().newGrid(Color.blue(),
				1, 10);
		grid.setComp(gridMesh);
		world.add(grid);

		Obj treangle = new Obj();
		MeshComponent treangleMesh = GLFactory.getInstance().newTexturedSquare(
				"worldIconId",
				IO.loadBitmapFromId(myTargetActivity, R.drawable.icon));
		treangleMesh.setPosition(new Vec(0, -2, 1));
		treangleMesh.setPosition(new Vec(0, 0, 0));
		treangleMesh.addChild(new AnimationFaceToCamera(camera, 0.5f));
		treangle.setComp(treangleMesh);
		world.add(treangle);

	}

	@Override
	public void _c_addActionsToEvents(EventManager eventManager,
			CustomGLSurfaceView arView, SystemUpdater updater) {
		arView.addOnTouchMoveListener(new ActionBufferedCameraAR(camera));

	}

	@Override
	public void _d_addElementsToUpdateThread(SystemUpdater updater) {
		updater.addObjectToUpdateCycle(world);

	}

	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {
	}

}
