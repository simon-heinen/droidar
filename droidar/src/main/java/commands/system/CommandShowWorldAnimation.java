package commands.system;

import worldData.SystemUpdater;

import commands.undoable.UndoableCommand;

public class CommandShowWorldAnimation extends UndoableCommand {

	private SystemUpdater myW;
	private boolean startWorldThread;

	/**
	 * @param updater
	 * @param show
	 *            false=animation is paused
	 */
	public CommandShowWorldAnimation(SystemUpdater updater, boolean show) {
		myW = updater;
		startWorldThread = show;
	}

	@Override
	public boolean override_do() {
		if (startWorldThread && myW != null)
			myW.resumeUpdater();
		else
			myW.pauseUpdater();
		return true;
	}

	@Override
	public boolean override_undo() {
		if (startWorldThread && myW != null)
			myW.pauseUpdater();
		else
			myW.resumeUpdater();

		return true;
	}

}
