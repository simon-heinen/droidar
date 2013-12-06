package de.rwth.setups;

import geo.GeoObj;
import gl.GL1Renderer;
import gl.GLFactory;
import gui.GuiSetup;
import gui.RadarView;
import setup.DefaultArSetup;
import util.Vec;
import worlddata.SystemUpdater;
import worlddata.World;
import android.app.Activity;
import android.util.Log;
import commands.Command;
import commands.ui.CommandShowToast;
import components.SimpleTooFarAwayComp;
import entry.ISetupEntry;

public class FarAwayPOIScenarioSetup extends DefaultArSetup {

	private String LOG_TAG = "FarAwayPOIScenarioSetup";
	private RadarView radar;
	
	@Override
	public void initFieldsIfNecessary() {
		super.initFieldsIfNecessary();
		radar = new RadarView(getActivity(), (int) (getScreenWidth() / 3),
				getCamera(), getWorld().getAllItems());
	}

	@Override
	public void addObjectsTo(GL1Renderer renderer, World world,
			final GLFactory objectFactory) {
		GeoObj o = new GeoObj();
		o.setComp(objectFactory.newCube());
		// place 20 meters north of the user:
		o.setVirtualPosition(new Vec(0, 20, 0));
		o.setComp(new SimpleTooFarAwayComp(30, getCamera(), getActivity()));
		world.add(o);
	}

	@Override
	public void addElementsToUpdateThread(SystemUpdater updater) {
		super.addElementsToUpdateThread(updater);
		updater.addObjectToUpdateCycle(radar);
	}

	@Override
	public void addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {
		super.addElementsToGuiSetup(guiSetup, activity);
		guiSetup.addViewToTop(radar);
		guiSetup.addButtonToBottomView(new Command() {

			@Override
			public boolean execute() {
				// float[] rayPos = new float[4];
				// float[] rayDir = new float[4];
				CommandShowToast.show(getActivity(), "altitude="
						+ getCamera().getGPSPositionVec().z);

				return true;
			}
		}, "Show altitude");

		guiSetup.addButtonToBottomView(new Command() {

			@Override
			public boolean execute() {
				Vec pos = getCamera().getGPSPositionVec();
				Log.d(LOG_TAG, "Placing object at " + pos);

				final GeoObj o = new GeoObj(pos.y, pos.x, pos.z);
				o.setComp(GLFactory.getInstance().newArrow());
				o.setComp(new SimpleTooFarAwayComp(30, getCamera(),
						getActivity()));
				o.setOnClickCommand(new Command() {

					@Override
					public boolean execute() {
						CommandShowToast.show(getActivity(), "o.getAltitude()="
								+ o.getAltitude());
						return true;
					}
				});
				Log.d(LOG_TAG, "virtual pos=" + o.getVirtualPosition());
				Log.d(LOG_TAG, "cam pos=" + getCamera().getPosition());
				getWorld().add(o);
				return true;
			}
		}, "Place GeoObj");
	}
}
