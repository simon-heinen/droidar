package de.rwth;

import geo.GeoObj;
import gl.Color;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.GLRenderer;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import gui.GuiSetup;
import markerDetection.MarkerDetectionSetup;
import markerDetection.MarkerObjectMap;
import markerDetection.UnrecognizedMarkerListener;
import system.EventManager;
import util.Vec;
import worldData.Obj;
import worldData.SystemUpdater;
import worldData.World;
import actions.Action;
import actions.ActionMoveCameraBuffered;
import actions.ActionRotateCameraBuffered;
import android.app.Activity;

import commands.Command;

public class MultiMarkerSetup extends MarkerDetectionSetup {

	private GLCamera camera;
	private World world;
	private MeshComponent mesh1;
	private MeshComponent mesh2;

	@Override
	public void _a_initFieldsIfNecessary() {
		camera = new GLCamera(new Vec(0, 0, 10));
		world = new World(camera);
		mesh1 = new Shape();

		mesh1.addChild(GLFactory.getInstance().newCoordinateSystem());
		// mesh.add(GLFactory.getInstance().newCircle(new Color(0, 0, 1,
		// 0.6f)));
		mesh1.addChild(GLFactory.getInstance().newCube());

		mesh2 = new Shape();
		mesh2.addChild(GLFactory.getInstance().newCoordinateSystem());
		mesh2.addChild(GLFactory.getInstance().newCircle(
				new Color(0, 0, 1, 0.6f)));
		// mesh1.add(GLFactory.getInstance().newCube());

	}

	@Override
	public UnrecognizedMarkerListener _a2_getUnrecognizedMarkerListener() {
		return new UnrecognizedMarkerListener() {

			@Override
			public void onUnrecognizedMarkerDetected(int markerCode,
					float[] mat, int startIdx, int endIdx, int rotationValue) {
				System.out.println("unrecognized markerCode=" + markerCode);
			}
		};

	}

	@Override
	public void _a3_registerMarkerObjects(MarkerObjectMap markerObjectMap) {
		markerObjectMap.put(new SimpleMeshPlacer(0, mesh1, camera));
		markerObjectMap.put(new SimpleMeshPlacer(1, mesh2, camera));

		/*
		 * example for more complex behavior:
		 */
		markerObjectMap.put(new BasicMarker(2, camera) {

			MeshComponent targetMesh;
			boolean firstTime = true;

			@Override
			public void setObjectPos(Vec positionVec) {
				/*
				 * the first time this method is called an object could be
				 * created and added to the world
				 */
				if (firstTime) {
					firstTime = false;
					Obj aNewObject = new Obj();
					targetMesh = GLFactory.getInstance().newArrow();
					aNewObject.setComp(targetMesh);
					world.add(aNewObject);
				}
				targetMesh.setPosition(positionVec);
			}

			@Override
			public void setObjRotation(float[] rotMatrix) {
				if (targetMesh != null) {
					targetMesh.setRotationMatrix(rotMatrix);
				}
			}
		});
	}

	@Override
	public void _b_addWorldsToRenderer(GL1Renderer renderer,
			GLFactory objectFactory, GeoObj currentPosition) {
		renderer.addRenderElement(world);
		Obj o = new Obj();
		o.setComp(mesh1);
		world.add(o);

		Obj o2 = new Obj();
		o2.setComp(mesh2);
		world.add(o2);

		world.add(objectFactory.newHexGroupTest(new Vec()));

	}

	@Override
	public void _c_addActionsToEvents(EventManager eventManager,
			CustomGLSurfaceView arView, SystemUpdater updater) {
		arView.addOnTouchMoveListener(new ActionMoveCameraBuffered(camera, 5,
				25));
		Action rot = new ActionRotateCameraBuffered(camera);
		updater.addObjectToUpdateCycle(rot);
		eventManager.addOnOrientationChangedAction(rot);
		eventManager.addOnTrackballAction(new ActionMoveCameraBuffered(camera,
				1, 25));

	}

	@Override
	public void _d_addElementsToUpdateThread(SystemUpdater updater) {
		updater.addObjectToUpdateCycle(world);

	}

	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {
		guiSetup.addButtonToBottomView(new Command() {

			@Override
			public boolean execute() {

				Vec rayPosition = new Vec();
				Vec rayDirection = new Vec();
				camera.getPickingRay(rayPosition, rayDirection,
						GLRenderer.halfWidth, GLRenderer.halfHeight);

				System.out.println("rayPosition=" + rayPosition);
				System.out.println("rayDirection=" + rayDirection);

				rayDirection.setLength(5);

				mesh1.setPosition(rayPosition.add(rayDirection));

				return false;
			}
		}, "Place 2 meters infront");

	}
}
