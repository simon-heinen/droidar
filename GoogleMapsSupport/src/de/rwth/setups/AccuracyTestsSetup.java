package de.rwth.setups;

import geo.CustomItemizedOverlay;
import geo.GMap;
import geo.GeoGraph;
import geo.GeoObj;
import gl.Color;
import gl.GL1Renderer;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;
import gui.GuiSetup;
import gui.InfoScreenSettings;
import system.ConcreteSimpleLocationManager;
import system.DefaultARSetup;
import system.SimpleLocationManager;
import system.StepManager.OnStepListener;
import system.StepSettingsControllerView;
import util.IO;
import util.LimitedQueue;
import util.Vec;
import v2.simpleUi.SimpleUI;
import worldData.MoveComp;
import worldData.Obj;
import worldData.World;
import android.app.Activity;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import commands.Command;
import commands.ui.CommandInUiThread;
import components.TimerComp;
import components.ViewPosCalcerComp;

public class AccuracyTestsSetup extends DefaultARSetup {

	private GeoGraph measureData = new GeoGraph(false);
	private GeoGraph pins = new GeoGraph(false);

	private ViewPosCalcerComp viewPosCalcer;
	private MoveComp moveComp;
	private Obj selectedObj;

	@Override
	public void addObjectsTo(GL1Renderer renderer, World world,
			GLFactory objectFactory) {

		system.ErrorHandler.enableEmailReports("simon.heinen@gmail.com",
				"AccuracyTestsSetup Error");

		world.add(pins);
		world.add(measureData);

		viewPosCalcer = new ViewPosCalcerComp(camera, 150, 0.1f) {
			@Override
			public void onPositionUpdate(worldData.Updateable parent,
					Vec targetVec) {
				if (parent instanceof Obj) {
					Obj obj = (Obj) parent;
					MoveComp m = obj.getComp(MoveComp.class);
					if (m != null) {
						m.myTargetPos = targetVec;
					}
				}
			}
		};
		moveComp = new MoveComp(4);

	}

	private Obj newObject() {
		final Obj obj = new Obj();
		Color c = Color.getRandomRGBColor();
		c.alpha = 0.7f;
		MeshComponent diamond = GLFactory.getInstance().newDiamond(c);
		diamond.setScale(new Vec(0.4f, 0.4f, 0.4f));
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

	@Override
	public void _f_addInfoScreen(InfoScreenSettings infoScreenData) {
		
		infoScreenData.addText("This setup visualizes the position calculations");
		infoScreenData.addText("You can place objects around you with the 'Place next' button");
		infoScreenData.addText("Press menu to change the step settings");
		infoScreenData.addText("The blue dots which appear automatically are the predicted positions you will go");
		infoScreenData.addText("To display the GPS measurements press the 'show measurements' button");
		
	}
	
	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {

		super._e2_addElementsToGuiSetup(guiSetup, activity);

		final GMap map = GMap.newDefaultGMap((MapActivity) getActivity(),
				GoogleMapsKey.pc1DebugKey);

		try {
			map.addOverlay(new CustomItemizedOverlay(measureData, IO
					.loadDrawableFromId(getActivity(),
							de.rwth.R.drawable.mapdotgreen)));
			map.addOverlay(new CustomItemizedOverlay(pins, IO
					.loadDrawableFromId(getActivity(),
							de.rwth.R.drawable.mapdotblue)));

		} catch (Exception e) {
			e.printStackTrace();
		}

		guiSetup.addViewToBottomRight(map, 0.5f, 200);
		guiSetup.addButtonToBottomView(new Command() {

			@Override
			public boolean execute() {
				if (map.getVisibility() == View.VISIBLE)
					map.setVisibility(View.GONE);
				else
					map.setVisibility(View.VISIBLE);
				return true;
			}
		}, "Show/Hide \n map");

		guiSetup.addItemToOptionsMenu(new Command() {

			@Override
			public boolean execute() {
				SimpleUI.showInfoDialog(getActivity(), "Save",
						new StepSettingsControllerView(getActivity()));
				return true;
			}
		}, "Step Settings");

		guiSetup.addButtonToBottomView(new Command() {

			@Override
			public boolean execute() {
				pins.add(GLFactory.getInstance().newPositionMarker(camera));
				return true;
			}
		}, "Mark current pos");

		final TextView t = new TextView(getActivity());

		SimpleLocationManager.getInstance(getActivity()).getStepManager()
				.registerStepListener(getActivity(), new OnStepListener() {

					private boolean a;

					@Override
					public void onStep(final double compassAngle,
							double steplength) {

						ConcreteSimpleLocationManager l = (ConcreteSimpleLocationManager) SimpleLocationManager
								.getInstance(getActivity());
						final Location pos = l.getLastStepPos();
						if (pos != null) {
							final GeoObj marker = new GeoObj(pos);
							marker.setComp(GLFactory.getInstance().newCircle(
									Color.blueTransparent()));
							addARemoveAfterSomeSecondsComp(measureData, 3,
									marker);
							measureData.add(marker);

							new CommandInUiThread() {

								@Override
								public void executeInUiThread() {
									a = !a;
									String text;
									if (a)
										text = "<<< Step event in direction="
												+ compassAngle + "; accuracy="
												+ pos.getAccuracy();
									else
										text = ">>> Step direction="
												+ compassAngle + "; accuracy="
												+ pos.getAccuracy();
									t.setText(text);
								}
							}.execute();

						}

					}
				});

		guiSetup.addViewToTop(t);

		guiSetup.addButtonToRightView(new Command() {

			@Override
			public boolean execute() {
				world.add(newObject());
				return true;
			}

		}, "Place next");

		guiSetup.addButtonToBottomView(new Command() {

			@Override
			public boolean execute() {
				LimitedQueue<Location> list = ((ConcreteSimpleLocationManager) SimpleLocationManager
						.getInstance(getActivity())).getLastPositions();
				for (int i = 0; i < list.size(); i++) {
					Location l = list.get(i);
					final GeoObj marker = new GeoObj(l);
					marker.setComp(GLFactory.getInstance().newCircle(
							Color.greenTransparent()));
					addARemoveAfterSomeSecondsComp(measureData, i, marker);
					measureData.add(marker);
				}

				final GeoObj average = new GeoObj(SimpleLocationManager
						.getInstance(getActivity())
						.getCurrentBUfferedLocation());
				average.setComp(GLFactory.getInstance().newCircle(Color.red()));
				addARemoveAfterSomeSecondsComp(measureData, list.size() + 5,
						average);
				measureData.add(average);

				return true;
			}

		}, "show measurements");

	}

	private void addARemoveAfterSomeSecondsComp(final GeoGraph container,
			int countdownTimeInSeconds, final GeoObj marker) {
		marker.setComp(new TimerComp(countdownTimeInSeconds, new Command() {

			@Override
			public boolean execute() {
				container.remove(marker);
				return true;
			}
		}));
	}
}
