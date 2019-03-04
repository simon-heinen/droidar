package de.rwth.setups;

import geo.Edge;
import geo.GeoGraph;
import geo.GeoObj;
import gl.Color;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.animations.AnimationColorBounce;
import gl.animations.AnimationPulse;
import gl.scenegraph.MeshComponent;
import gui.GuiSetup;
import gui.MetaInfos;
import system.ErrorHandler;
import system.EventManager;
import system.Setup;
import util.EfficientList;
import util.EfficientListQualified;
import util.Vec;
import worldData.SystemUpdater;
import worldData.World;
import actions.Action;
import actions.ActionBufferedCameraAR;
import actions.ActionCalcRelativePos;
import actions.ActionMoveCameraBuffered;
import actions.ActionRotateCameraBuffered;
import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import commands.Command;
import commands.ui.CommandShowEditScreen;

public class GraphCreationSetup extends Setup {

	private static final String LOG_TAG = "GraphCreationSetup";
	private GeoGraph myGraph;
	private World world;
	private GLCamera camera;
	// private EditText editText;
	private GeoObj mySelectedWaypoint;
	private GeoGraph mySearchresultGraph;
	private MetaInfos i;

	@Override
	public void _a_initFieldsIfNecessary() {
		// allow the user to send error reports to the developer:
		ErrorHandler.enableEmailReports("droidar.rwth@gmail.com",
				"Error in GraphCreationSetup");

		myGraph = new GeoGraph();
		camera = new GLCamera(new Vec(0, 0, 1f));
		world = new World(camera);
		world.add(myGraph);

		i = new MetaInfos();
		i.setShortDescr("test");
		i.addTextToLongDescr("long");
		i.setColor(Color.blueTransparent());

	}

	@Override
	public void _b_addWorldsToRenderer(GL1Renderer renderer,
			GLFactory objectFactory, GeoObj currentPosition) {
		renderer.addRenderElement(world);
	}

	@Override
	public void _c_addActionsToEvents(EventManager eventManager,
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
	public void _d_addElementsToUpdateThread(SystemUpdater worldUpdater) {
		worldUpdater.addObjectToUpdateCycle(world);
	}

	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {
		final EditText editText = guiSetup.addSearchbarToView(
				guiSetup.getTopView(), findWayToWaypoint(), "Waypoint name..");

		guiSetup.addButtonToBottomView(new Command() {
			@Override
			public boolean execute() {
				GeoObj newWaypoint = newWaypoint();
				String waypointName = editText.getText().toString();
				if (waypointName != "") {
					newWaypoint.getInfoObject().setShortDescr(waypointName);
					editText.setText("");
				}
				myGraph.add(newWaypoint);
				setSelected(newWaypoint);
				return true;
			}
		}, "New waypoint");

		guiSetup.addButtonToBottomView(new Command() {
			@Override
			public boolean execute() {
				GeoObj newWaypoint = newWaypoint();
				String waypointName = editText.getText().toString();
				if (waypointName != "") {
					newWaypoint.getInfoObject().setShortDescr(waypointName);
					editText.setText("");
				}
				myGraph.add(newWaypoint);
				if (mySelectedWaypoint != null) {
					Edge e = myGraph.addEdge(mySelectedWaypoint, newWaypoint,
							null);

				}

				setSelected(newWaypoint);
				return true;
			}
		}, "New connected waypoint");

	}

	private GeoObj newWaypoint() {
		final GeoObj p = new GeoObj();

		p.setMyPosition(camera.getGPSPositionVec());

		Log.d(LOG_TAG, "new geoObj with virtual pos=" + p.getVirtualPosition());

		MeshComponent myShape = GLFactory.getInstance().newDiamond(null);
		p.setComp(myShape);
		p.getGraphicsComponent().setColor(new Color(1, 0, 0, .6f));
		myShape.setOnClickCommand(new Command() {
			@Override
			public boolean execute() {
				setSelected(p);
				return true;
			}
		});

		myShape.setOnLongClickCommand(new CommandShowEditScreen(
				myTargetActivity, p));

		return p;
	}

	private void setSelected(GeoObj newWaypoint) {
		if (mySelectedWaypoint != null
				&& mySelectedWaypoint.getGraphicsComponent() != null)
			mySelectedWaypoint.getGraphicsComponent().removeAllAnimations();

		mySelectedWaypoint = newWaypoint;
		mySelectedWaypoint.getGraphicsComponent()
				.addAnimation(
						new AnimationPulse(2, new Vec(1, 1, 1),
								new Vec(2, 2, 2), 0.2f));
	}

	/**
	 * When the user presses enter a way to the entered waypoint is searched
	 * 
	 * @return
	 */
	private Command findWayToWaypoint() {
		Command c = new Command() {

			@Override
			public boolean execute(Object transfairObject) {
				if (transfairObject instanceof String) {

					String searchString = (String) transfairObject;
					GeoObj targetElement = myGraph
							.findBestPointFor(searchString);
					GeoObj currentClosestElement = myGraph
							.getClosesedObjTo(camera.getGPSPositionAsGeoObj());

					GeoGraph searchresultGraph = myGraph.findPath(
							currentClosestElement, targetElement);
					if (searchresultGraph != null) {
						if (mySearchresultGraph != null)
							unmarkGeoGraphAsSearchPath(myGraph.getAllItems());
						mySearchresultGraph = searchresultGraph;
						markGeoObjAsSearchPath(mySearchresultGraph
								.getAllItems());
					}

					return true;
				}
				return false;
			}

			@Override
			public boolean execute() {
				// should never be called
				return false;
			}

		};
		return c;

	}

	private void unmarkGeoGraphAsSearchPath(
			EfficientListQualified<GeoObj> objList) {
		if (objList != null) {
			for (int i = 0; i < objList.myLength; i++) {
				MeshComponent g = objList.get(i).getGraphicsComponent();
				if (g != null)
					g.removeAllAnimations();
			}
		}
	}

	private void markGeoObjAsSearchPath(EfficientList<GeoObj> objList) {
		// unselectCurrentSelectedWaypoint();
		setAnimToGeoObjList(objList, new AnimationColorBounce(2, Color.green(),
				Color.red(), 0.2f));
	}

	private void setAnimToGeoObjList(EfficientList<GeoObj> objList,
			AnimationColorBounce a) {
		for (int i = 0; i < objList.myLength; i++) {
			GeoObj g = objList.get(i);
			MeshComponent gf = g.getGraphicsComponent();
			if (gf != null) {
				gf.addAnimation(a);
			}
		}
	}

}
