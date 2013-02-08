package commands.undoable;

import java.util.ArrayList;

import util.Log;

public class CommandProcessorList extends ArrayList<UndoableCommand> {

	private static final String LOG_TAG = "Command Processor List";
	private int currentPos = 0;

	@Override
	public boolean add(UndoableCommand object) {
		// if there are redo commands after the current pos then remove these
		// objects before adding current command:
		currentPos++;
		if (this.size() >= currentPos) {
			this.removeRange(currentPos - 1, this.size());
		}
		Log.d(LOG_TAG, "Adding Command: " + object.getClass());
		return super.add(object);
	}

	@Override
	public void add(int location, UndoableCommand object) {
		currentPos++;
		if (this.size() >= currentPos) {
			this.removeRange(currentPos - 1, this.size());
		}
		super.add(location, object);
	}

	public boolean undo() {
		// dont delete the commands that are undone, just move myPos curser and
		// undo them:
		boolean result = false;

		if (currentPos - 1 >= 0) {
			Log.d(LOG_TAG, "Undoing Command: "
					+ this.get(currentPos - 1).getClass());
			result = this.get(currentPos - 1).override_undo();
		} else {
			Log.e(LOG_TAG, "Error - CommandList already at beginning: "
					+ currentPos);
			return false;
		}
		// if the command was undone correctly move to the pointer to the
		// previous position
		if (result)
			currentPos--;
		return result;
	}

	public boolean redo() {
		boolean result = false;

		if (currentPos < this.size()) {
			Log.d(LOG_TAG, "Redoing Command: "
					+ this.get(currentPos).getClass());
			result = this.get(currentPos).override_do();
		} else {
			Log.e(LOG_TAG, "CommandList at end: " + currentPos);
			return false;
		}
		if (result)
			currentPos++;
		else
			this.removeRange(currentPos, this.size());

		return result;
	}

	/**
	 * @param x
	 *            the number of commands to be removed
	 */
	public void removeLatestXCommands(int x) {
		this.removeRange(this.size() - x, this.size());
		currentPos -= x;
	}

	@Override
	public String toString() {
		String s = "Command List {myPos=" + currentPos + "}: (";
		for (int i = 0; i < this.size(); i++) {
			if (i == currentPos)
				s += " [[[";
			s += "<" + i + ">" + this.get(i).toString();
			if (i == currentPos)
				s += "]]] ";
			if (i + 1 < this.size())
				s += ", ";
		}
		s += ")";
		return s;
	}

}
