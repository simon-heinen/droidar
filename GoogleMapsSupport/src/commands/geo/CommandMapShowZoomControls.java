package commands.geo;

import geo.GMap;

import commands.Command;

public class CommandMapShowZoomControls extends Command {

	private GMap myMap;
	private boolean showControls;

	public CommandMapShowZoomControls(GMap map, boolean showZoomControls) {
		myMap = map;
		showControls = showZoomControls;
	}

	@Override
	public boolean execute() {
		myMap.enableZoomButtons(showControls);
		return false;
	}

}
