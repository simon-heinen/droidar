package commands.system;

import setup.ArSetup;

import commands.undoable.UndoableCommand;

public class CommandShowCameraPreview extends UndoableCommand {

	private ArSetup mySetup;
	private boolean showCameraPreview;

	/**
	 * @param setup
	 * @param show
	 *            false=camera is switched off
	 */
	public CommandShowCameraPreview(ArSetup setup, boolean show) {
		mySetup = setup;
		showCameraPreview = show;
	}

	@Override
	public boolean override_do() {
//TODO: Fix
//		if (showCameraPreview)
//			mySetup.resumeCameraPreview();
//		else
//			mySetup.pauseCameraPreview();
		return true;
	}

	@Override
	public boolean override_undo() {
//TODO: Fix
//		if (showCameraPreview)
//			mySetup.pauseCameraPreview();
//		else
//			mySetup.resumeCameraPreview();
		return true;
	}

}
