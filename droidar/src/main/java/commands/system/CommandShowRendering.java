package commands.system;

import gl.GLRenderer;

import commands.undoable.UndoableCommand;

public class CommandShowRendering extends UndoableCommand {

	private GLRenderer myR;
	private boolean b;

	public CommandShowRendering(GLRenderer r, boolean show) {
		myR = r;
		b = show;
	}

	@Override
	public boolean override_do() {
		if (myR != null) {
			if (b)
				myR.resume();
			else
				myR.pause();
			return true;
		}
		return false;
	}

	@Override
	public boolean override_undo() {
		if (myR != null) {
			if (b)
				myR.pause();
			else
				myR.resume();
			return true;
		}
		return false;
	}

}
