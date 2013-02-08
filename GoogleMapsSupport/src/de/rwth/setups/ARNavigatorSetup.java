package de.rwth.setups;

import geo.CustomItemizedOverlay;
import geo.GMap;
import geo.GeoGraph;
import geo.GeoObj;
import geo.GeoUtils;
import geo.NodeListener;
import gl.Color;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.GLRenderer;
import gl.animations.AnimationRotate;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.RenderList;
import gui.GuiSetup;
import gui.InfoScreenSettings;
import gui.MetaInfos;
import listeners.ItemSelectedListener;
import listeners.ObjectCreateListener;
import system.ErrorHandler;
import system.EventManager;
import system.Setup;
import util.EfficientListQualified;
import util.IO;
import util.Vec;
import util.Wrapper;
import worldData.SystemUpdater;
import worldData.World;
import actions.Action;
import actions.ActionBufferedCameraAR;
import actions.ActionCalcRelativePos;
import actions.ActionMoveCameraBuffered;
import actions.ActionRotateCameraBuffered;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.maps.MyLocationOverlay;
import commands.Command;
import commands.CommandGroup;
import commands.geo.CommandAddGeoGraphsToMap;
import commands.geo.CommandAddGeoObjToGeoGraph;
import commands.geo.CommandAddPathToGeoGraph;
import commands.geo.CommandClearMapOverlays;
import commands.geo.CommandFindWayInGraph;
import commands.geo.CommandFindWayWithGMaps;
import commands.geo.CommandMapCenter;
import commands.geo.CommandMapShowZoomControls;
import commands.logic.CommandIfThenElse;
import commands.logic.CommandSetWrapperToValue;
import commands.logic.CommandSetWrapperToValue2;
import commands.logic.CommandWrapperEqualsCondition;
import commands.system.CommandAddHighPrioTask;
import commands.system.CommandShowCameraPreview;
import commands.system.CommandShowRendering;
import commands.system.CommandShowWorldAnimation;
import commands.ui.CommandAnimateZoom;
import commands.ui.CommandMapEnlargeToFullScreen;
import commands.ui.CommandMinimizeMap;
import commands.ui.CommandShowListActivity;
import commands.ui.CommandShowToast;

import de.rwth.R;

public class ARNavigatorSetup extends Setup {

	

	private SystemUpdater myWorldUpdater;
	private GLCamera camera;
	private World world;
	private GMap map;
	private GLRenderer myRenderer;

	private RenderList searches;

	private Wrapper searchesW;

	private RenderList customGraphs;
	private Wrapper customsW;
	private Wrapper currentCustomGraphW;

	private Wrapper selectedGraphW;
	private Wrapper selectedItemW;
	private Wrapper lastAddedItemW;

	private Wrapper minimizedFlag;

	// private MetaInfos selItemInfo;
	// private MetaInfos notSelItemInfo;
	private MetaInfos selGraphInfo;
	private MetaInfos notSelGraphInfo;

	private Wrapper ownPositionW;
	private GeoGraph ownPosition;
	private ItemSelectedListener myItemSelected;
	private ObjectCreateListener newCustomGraphListener;
	private int graphCounter;
	private NodeListener addNewGeoObjToCustomGraphListener;
	private ObjectEditListener listenerSetEventsToClicksForSearchGraph;

	@Override
	public void _a_initFieldsIfNecessary() {

		// allow the user to send error reports to the developer:
		ErrorHandler.enableEmailReports("droidar.rwth@gmail.com",
				"Error in DroidAR App");

		// Define how selected items will look like:
		// selItemInfo = new MetaInfos();
		// selItemInfo.setColor(new Color(1, 0, 0.2f, 0.8f));
		// notSelItemInfo = new MetaInfos();
		// notSelItemInfo.setColor(new Color(0.8f, 0.8f, 0.8f, 0.8f));

		// Define how selected graphs will look like:
		selGraphInfo = new MetaInfos();
		selGraphInfo.setColor(new Color(1, 0, 0.2f, 0.6f));
		notSelGraphInfo = new MetaInfos();
		// notSelGraphInfo.setColor(new Color(0.8f, 0.8f, 0.8f, 0.6f));

		// store the device-position in a geo-graph:
		ownPosition = new GeoGraph();
		ownPosition.add(EventManager.getInstance().getCurrentLocationObject());
		ownPositionW = new Wrapper(ownPosition);

		// the searches wrapper to store the found graph:
		searches = new RenderList();
		searchesW = new Wrapper(searches);

		// the wrapper to hold all custom graphs (to be displayed in the
		// listview e.g.):
		customGraphs = new RenderList();
		GeoGraph defaultCustomGraph = newCustomGraph();
		customGraphs.add(defaultCustomGraph);
		customsW = new Wrapper(customGraphs);

		currentCustomGraphW = new Wrapper(defaultCustomGraph);
		selectedGraphW = new Wrapper();
		selectedItemW = new Wrapper();
		lastAddedItemW = new Wrapper();

		minimizedFlag = new Wrapper(true);

		/*
		 * When an item is selected this listener is executed and the metainfo
		 * object can be set to a temporary new style
		 */
		myItemSelected = new ItemSelectedListener() {
			@Override
			public boolean onItemSelected(MetaInfos metaInfos) {
				MetaInfos m = new MetaInfos();
				m.setShortDescr("<<" + metaInfos.getShortDescr().toUpperCase()
						+ ">>");
				m.setColor(Color.white());
				m.setMyIconId(R.drawable.penguin64);
				m.addTextToLongDescr(metaInfos.getLongDescrAsString());

				// apply the temporary style to the metainfos:
				metaInfos.setSelected(m);
				return true;
			}
		};

		newCustomGraphListener = new ObjectCreateListener() {
			@Override
			public boolean setWrapperToObject(Wrapper targetWrapper) {
				targetWrapper.setTo(newCustomGraph());
				return true;
			}
		};

		addNewGeoObjToCustomGraphListener = new NodeListener() {

			@Override
			public boolean addNodeToGraph(GeoGraph graph, GeoObj obj) {
				obj.setComp(GLFactory.getInstance().newDiamond(
						graph.getInfoObject().getColor()));
				obj.getGraphicsComponent().enableMeshPicking(obj);
				Command clickCommand = setSelectedGraphTo(new Wrapper(graph));
				obj.setOnClickCommand(clickCommand);
				return graph.add(obj);
			}

			@Override
			public boolean addLastNodeToGraph(GeoGraph graph, GeoObj objectToAdd) {
				return addNodeToGraph(graph, objectToAdd);
			}

			@Override
			public boolean addFirstNodeToGraph(GeoGraph graph,
					GeoObj objectToAdd) {
				return addNodeToGraph(graph, objectToAdd);
			}
		};

		listenerSetEventsToClicksForSearchGraph = new ObjectEditListener() {
			@Override
			public boolean onChangeWrapperObject(Wrapper wrapper,
					Object passedObject) {
				if (wrapper.getObject() instanceof GeoGraph) {
					EfficientListQualified<GeoObj> geoObjs = ((GeoGraph) wrapper
							.getObject()).getAllItems();
					for (int i = 0; i < geoObjs.myLength; i++) {
						GeoObj o = geoObjs.get(i);
						o.setComp(GLFactory.getInstance().newDiamond(
								((GeoGraph) wrapper.getObject())
										.getInfoObject().getColor()));
						/*
						 * select the graph the geoObject belongs to when the
						 * geoObj is clicked
						 */
						o.setOnClickCommand(setSelectedGraphTo(new Wrapper(
								wrapper.getObject())));
						/*
						 * and enable picking for the complete mesh which
						 * represents the geoObject in the 3d world
						 */
						o.getGraphicsComponent().enableMeshPicking(o);
					}
					return true;
				}
				return false;
			}
		};

	}

	/**
	 * this can be used whenever a new custom graph is created. It will
	 * automatically assign a random color, a new name and a icon.
	 * 
	 * @return
	 */
	private GeoGraph newCustomGraph() {
		GeoGraph g = new GeoGraph();
		g.setUseEdges(true);
		// g.setOnClickCommand(c)
		graphCounter++;
		g.getInfoObject().setShortDescr("Custom Graph " + graphCounter);
		g.getInfoObject().addTextToLongDescr("This is a \n long \n text");
		g.getInfoObject().setColor(Color.getRandomRGBColor());
		g.getInfoObject().setMyIconId(R.drawable.penguin64);
		return g;
	}

	@Override
	public void _b_addWorldsToRenderer(GL1Renderer renderer,
			GLFactory objectFactory, GeoObj currentPosition) {
		myRenderer = renderer;
		camera = new GLCamera(new Vec(0, 0, 1.5f));
		world = new World(camera);

		world.add(customGraphs); // to display all custom graphs
		world.add(searches); // to display the search results

		MeshComponent diamond = objectFactory.newDiamond(Color
				.blueTransparent());
		diamond.setPosition(new Vec(0, 0, -3.8f));
		diamond.addAnimation(new AnimationRotate(40, new Vec(0, 0, 1)));

		if (currentPosition == null)
			currentPosition = new GeoObj();

		currentPosition.setComp(diamond); // the diamond is added to the
											// geoObject
		// which always represents the current
		// device position
		world.add(currentPosition);

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
		myWorldUpdater = worldUpdater;
		worldUpdater.addObjectToUpdateCycle(world);
	}

	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity context) {

		map = new GMap(myTargetActivity, GoogleMapsKey.pc1DebugKey);
		guiSetup.addViewToBottomRight(map, 2, 130);
		map.setCenterToCurrentPos();
		map.setZoomLevel(20);
		map.setClickable(true);

		try {
			map.addOverlay(new CustomItemizedOverlay(ownPosition,
					IO.loadDrawableFromId(myTargetActivity,
							R.drawable.mapdotblue)));
		} catch (Exception e) {
			Log.e("Gmaps", "An itemized overlay could be created but "
					+ "not added to the Google-Maps view. A reason might "
					+ "be that the mapview could not determine its "
					+ "position. Check if the phone is in airplane-mode!");
			e.printStackTrace();
		}

		MyLocationOverlay o = new MyLocationOverlay(myTargetActivity, map);
		o.enableCompass();
		o.enableMyLocation();
		map.addOverlay(o);

		map.setOnDoubleTabCommand(ifMinimizedMaximazeMapElseAddPath(guiSetup));
		EventManager.getInstance().addOnKeyPressedCommand(
				KeyEvent.KEYCODE_BACK, minimizeMapIfMaximized());

		map.setOnTabCommand(ifMaximizedAddItemToCurrentCustomGraph());

		Wrapper walkWrapper = new Wrapper(true);
		CommandSetWrapperToValue disableWalkFlag = new CommandSetWrapperToValue(
				walkWrapper, false);
		CommandSetWrapperToValue enableWalkFlag = new CommandSetWrapperToValue(
				walkWrapper, true);

		guiSetup.addItemToOptionsMenu(showSearchesList(), "Searches..");
		guiSetup.addItemToOptionsMenu(showCustomsList(), "Custom graphs..");

		guiSetup.addSearchbarToView(guiSetup.getTopView(),
				searchCustomGraphsThenMapsAndDisplayRoute(walkWrapper),
				"Find Directions to..");
		guiSetup.addCheckBoxToView(guiSetup.getTopView(), "By car", false,
				disableWalkFlag, enableWalkFlag);
		guiSetup.addTaskmanagerToView(guiSetup.getTopView());
	}

	@Override
	public void _f_addInfoScreen(InfoScreenSettings infoScreenData) {

		infoScreenData
				.addTextWithIcon(
						R.drawable.infoboxblue2,
						"Enter a Streetname or any other type of location in the Searchbox. Press <Enter> and the way to the specified location will be calculated and displayed as an overlay. Just follow the path on the screen to reach your destination :)");
		infoScreenData
				.addTextWithIcon(
						R.drawable.infoboxblue2,
						"Custom graphs can be created on the map. Double-tab the map to enlage it. Then tap the screen to place waypoints and double tab to create connected waypoints. Custom graphs can be managed in a seperate list (press the Menu-button to access this list).");

		infoScreenData
				.addTextWithIcon(
						R.drawable.infoboxblue2,
						"Your position will be automatically updated as you move (you should enable GPS for this to work). As an alternative you can use the trackball to move the virtual overlay around. To adjust the height of the camera, drag the screen vertical and to rotate drag it horizontal.");

		infoScreenData
				.addTextWithIcon(
						R.drawable.warningcirclegray,
						"Before using this application make sure the compas works correctly. If it doesn't shake the device until the sensor recalibrates itselt.");

		infoScreenData
				.addTextWithIcon(
						R.drawable.warningcirclegray,
						"This is not a fully functional application. It was created to demonstrate the navigation capabilities of this framework and should be used with caution!");
	}

	private Command showCustomsList() {
		CommandGroup selectGraph = new CommandGroup("Custom graphs..");
		// selectGraph.add(new CommandSetSelected(selectedGraphW, null));
		// selectGraph.add(new CommandSetWrapperToValue(selectedGraphW));
		// selectGraph.add(new CommandSetWrapperToValue(currentCustomGraphW));
		// selectGraph.add(new CommandSetSelected(selectedGraphW,
		// myItemSelected));
		selectGraph.add(showSelectedGraphAndAllCustomGraphsOnMap());

		CommandGroup deleteSelectedGraph = new CommandGroup(
				"Delete selected graph");
		// deleteSelectedGraph.add(new CommandClearWrapperObject(selectedGraphW,
		// true));
		// deleteSelectedGraph.add(new
		// CommandRemoveEmptyItemsInWrapper(customsW));
		//
		CommandGroup longPressCommands = new CommandGroup();
		// longPressCommands.add(deleteSelectedGraph);

		CommandGroup menuCommands = new CommandGroup();
		menuCommands.add(createNewCustomGraph());
		// menuCommands.add(new CommandClearWrapperObject(customsW,
		// "Clear my graphes"));

		return new CommandShowListActivity(customsW, myTargetActivity, false,
				selectGraph, longPressCommands, null, null, menuCommands,
				"Custom Graphs");
	}

	private Command createNewCustomGraph() {
		CommandGroup g = new CommandGroup("Add graph");
		Wrapper newGraphW = new Wrapper();
		// g.add(new CommandCreateObjectInWrapper(newGraphW,
		// newCustomGraphListener));
		g.add(commandAddGraphToObjectGroup(newGraphW, customsW));
		return g;
	}

	private Command showSearchesList() {
		CommandGroup selectGraph = new CommandGroup("Searches..");
		// selectGraph.add(new CommandSetSelected(selectedGraphW, null));
		selectGraph.add(new CommandSetWrapperToValue(selectedGraphW));
		// selectGraph.add(new CommandSetSelected(selectedGraphW,
		// myItemSelected));
		selectGraph.add(showSelectedGraphAndAllCustomGraphsOnMap());

		CommandGroup deleteSelectedGraph = new CommandGroup(
				"Delete selected Graph");
		// deleteSelectedGraph.add(new CommandClearWrapperObject(selectedGraphW,
		// true));
		// deleteSelectedGraph
		// .add(new CommandRemoveEmptyItemsInWrapper(searchesW));
		CommandGroup longPressCommands = new CommandGroup();
		longPressCommands.add(deleteSelectedGraph);

		CommandGroup menuCommands = new CommandGroup();
		// menuCommands.add(new CommandClearWrapperObject(searchesW,
		// "Clear Searches"));

		return new CommandShowListActivity(searchesW, myTargetActivity, false,
				selectGraph, longPressCommands, null, null, menuCommands,
				"Search List");
	}

	private Command ifMaximizedAddItemToCurrentCustomGraph() {
		CommandGroup g = new CommandGroup();
		// sets the metainfos of the current selected item to unselected:
		// g.add(new CommandSetSelected(selectedItemW, null));
		g.add(new CommandSetWrapperToValue2(selectedItemW));
		g.add(new CommandSetWrapperToValue(lastAddedItemW, selectedItemW));
		// g.add(new CommandSetSelected(selectedItemW, myItemSelected));

		g.add(new CommandAddGeoObjToGeoGraph(selectedItemW,
				currentCustomGraphW, addNewGeoObjToCustomGraphListener));

		g.add(showSelectedGraphAndAllCustomGraphsOnMap());
		return new CommandWrapperEqualsCondition(minimizedFlag, false, g);
	}

	/**
	 * if the minimap is maximised and the user presses back then it will be
	 * minimized instead of exiting the app:
	 * 
	 * @return
	 */
	private Command minimizeMapIfMaximized() {
		CommandGroup minimizeMap = new CommandGroup();
		minimizeMap.add(new CommandMinimizeMap(map, 2, 130));
		minimizeMap.add(new CommandMapShowZoomControls(map, false));
		minimizeMap.add(new CommandShowCameraPreview(this, true));
		minimizeMap.add(new CommandShowWorldAnimation(myWorldUpdater, true));
		minimizeMap.add(new CommandShowRendering(myRenderer, true));
		minimizeMap.add(new CommandSetWrapperToValue(minimizedFlag, true));
		CommandWrapperEqualsCondition ifMaximizedMinimizeMap = new CommandWrapperEqualsCondition(
				minimizedFlag, false, minimizeMap);
		return ifMaximizedMinimizeMap;

	}

	private Command ifMinimizedMaximazeMapElseAddPath(GuiSetup guiSetup) {
		CommandGroup maximizeMinimap = new CommandGroup();
		CommandGroup showMapInLarge = new CommandGroup();
		showMapInLarge.add(new CommandMapEnlargeToFullScreen(map));
		showMapInLarge.add(new CommandMapShowZoomControls(map, true));
		maximizeMinimap.add(new CommandAnimateZoom(guiSetup
				.getMainContainerView(), showMapInLarge));
		maximizeMinimap.add(new CommandShowCameraPreview(this, false));
		maximizeMinimap
				.add(new CommandShowWorldAnimation(myWorldUpdater, false));
		maximizeMinimap.add(new CommandShowRendering(myRenderer, false));
		maximizeMinimap.add(new CommandSetWrapperToValue(minimizedFlag, false));

		CommandWrapperEqualsCondition ifMinimizedMaximazeElseShowItemMenu = new CommandWrapperEqualsCondition(
				minimizedFlag, true, maximizeMinimap,
				addPathToCurrentCustomGraph());

		return ifMinimizedMaximazeElseShowItemMenu;
	}

	private Command addPathToCurrentCustomGraph() {
		CommandGroup g = new CommandGroup("addPathToCurrentCustomGraph");
		// g.add(new CommandSetSelected(selectedItemW, null));
		g.add(new CommandSetWrapperToValue2(selectedItemW));
		// g.add(new CommandSetSelected(selectedItemW, myItemSelected));
		g.add(new CommandAddGeoObjToGeoGraph(selectedItemW,
				currentCustomGraphW, addNewGeoObjToCustomGraphListener));
		g.add(new CommandAddPathToGeoGraph(lastAddedItemW, selectedItemW,
				currentCustomGraphW, null));
		g.add(new CommandSetWrapperToValue(lastAddedItemW, selectedItemW));
		g.add(showSelectedGraphAndAllCustomGraphsOnMap());
		return g;
	}

	private Command searchCustomGraphsThenMapsAndDisplayRoute(
			Wrapper walkWrapper) {
		Wrapper sresults = new Wrapper();
		Wrapper searchTextWrapper = new Wrapper();

		CommandFindWayWithGMaps mapsearch = new CommandFindWayWithGMaps(
				new GeoUtils(myTargetActivity, camera), searchTextWrapper,
				sresults, walkWrapper);

		CommandGroup addGraphCommand = commandAddGraphToObjectGroup(sresults,
				searchesW);
		// addGraphCommand.add(new CommandChangeObjInWrapper(sresults,
		// listenerSetEventsToClicksForSearchGraph));

		CommandIfThenElse mapsearchAndDisplay = new CommandIfThenElse(
				mapsearch, addGraphCommand, new CommandShowToast(
						myTargetActivity, "Sorry: No way found :("));

		CommandIfThenElse ifFoundInGraphShowElseSearchMap = new CommandIfThenElse(
				new CommandFindWayInGraph(customsW, searchTextWrapper, sresults),
				addGraphCommand, mapsearchAndDisplay);

		CommandAddHighPrioTask addSearchToTaskList = new CommandAddHighPrioTask(
				ifFoundInGraphShowElseSearchMap);

		return new CommandIfThenElse(new CommandSetWrapperToValue(
				searchTextWrapper), addSearchToTaskList, null);
	}

	private CommandGroup commandAddGraphToObjectGroup(Wrapper sresults,
			Wrapper groupWrapper) {
		CommandGroup g = new CommandGroup("addGraphToObjectGroup");
		g.add(setSelectedGraphTo(sresults));
		// g.add(new CommandAddObjToObjGroup(selectedGraphW, groupWrapper));
		g.add(new CommandMapCenter(map, sresults));
		return g;
	}

	private Command setSelectedGraphTo(
			Wrapper wrapperOfGraphThatShouldBeSelected) {
		CommandGroup g = new CommandGroup("setSelectedGraphTo");
		// g.add(new CommandSetSelected(selectedGraphW, null));
		g.add(new CommandSetWrapperToValue(selectedGraphW,
				wrapperOfGraphThatShouldBeSelected));
		// g.add(new CommandSetSelected(selectedGraphW, myItemSelected));
		g.add(showSelectedGraphAndAllCustomGraphsOnMap());
		return g;
	}

	private Command showSelectedGraphAndAllCustomGraphsOnMap() {
		CommandGroup g = new CommandGroup("showSelectedGraphOnMap");
		if (map == null)
			Log.e("",
					"showSelectedGraphAndCurrentCustomGraphOnMap(): map was null");
		g.add(new CommandClearMapOverlays(map));

		g.add(new CommandAddGeoGraphsToMap(map, customsW, IO
				.loadDrawableFromId(myTargetActivity, R.drawable.mapdotyellow)));
		g.add(new CommandAddGeoGraphsToMap(map, selectedGraphW, IO
				.loadDrawableFromId(myTargetActivity, R.drawable.mapdotgreen)));
		g.add(new CommandAddGeoGraphsToMap(map, ownPositionW, IO
				.loadDrawableFromId(myTargetActivity, R.drawable.mapdotblue)));
		return g;
	}

}
