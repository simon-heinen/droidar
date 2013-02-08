package commands.system;

import system.Setup;

import commands.undoable.UndoableCommand;

public class CommandShowCameraPreview extends UndoableCommand {

	private Setup mySetup;
	private boolean showCameraPreview;

	/**
	 * @param setup
	 * @param show
	 *            false=camera is switched off
	 */
	public CommandShowCameraPreview(Setup setup, boolean show) {
		mySetup = setup;
		showCameraPreview = show;
	}

	@Override
	public boolean override_do() {
		if (showCameraPreview)
			mySetup.resumeCameraPreview();
		else
			mySetup.pauseCameraPreview();
		return true;
	}

	@Override
	public boolean override_undo() {
		if (showCameraPreview)
			mySetup.pauseCameraPreview();
		else
			mySetup.resumeCameraPreview();
		return true;
	}

}
