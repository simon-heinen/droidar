package commands;

import listeners.ProcessListener;
import system.Container;
import util.EfficientList;
import util.Log;

import commands.undoable.UndoableCommand;

/**
 * The CommandGroup can hold several {@link UndoableCommand}s and is a Command
 * itself (Composite pattern)
 * 
 * @author Spobo
 * 
 */
public class CommandGroup extends UndoableCommand implements Container<Command> {

	public CommandGroup() {
	}

	public CommandGroup(String string) {
		getInfoObject().setShortDescr(string);
	}

	public EfficientList<Command> myList = new EfficientList<Command>();
	private ProcessListener myProcessListener;

	/*
	 * TODO dont call ((Command) list[i]).execute(); call .override_do() to
	 * avoid registration in the CommandProcessorList, same for the other
	 * methods here!
	 */
	@Override
	public boolean override_do() {
		if (myList.myLength > 0) {
			Log.d("Commands", "CG '" + this + "' (size=" + myList.myLength
					+ ") NO parameter");
			boolean result = true;
			for (int i = 0; i < myList.myLength; i++) {
				if (myProcessListener != null)
					myProcessListener.onProcessStep(i, myList.myLength,
							myList.get(i));
				Log.d("Commands",
						"   + CG " + this + " EXECUTING " + myList.get(i)
								+ " (NO parameter)");
				result |= myList.get(i).execute();
			}
			return result;
		}
		return false;
	}

	@Override
	public boolean override_do(Object transfairObject) {
		if (myList.myLength > 0) {
			Log.d("Commands", "CG+P '" + this + "' (size=" + myList.myLength
					+ ") PARAM=" + transfairObject);
			boolean result = true;
			for (int i = 0; i < myList.myLength; i++) {
				Log.d("Commands",
						"   + CG+P " + this + " EXECUTING " + myList.get(i)
								+ " (PARAM=" + transfairObject + ")");
				result |= myList.get(i).execute(transfairObject);
			}
			return result;
		}
		return false;
	}

	@Override
	public boolean remove(Command x) {
		return myList.remove(x);
	}

	@Override
	public boolean override_undo() {
		if (myList.myLength > 0) {
			Log.i("Commands", "Undoing (without parameter) Command-Group '"
					+ this + "' (size=" + myList.myLength + ")");
			boolean result = true;
			for (int i = myList.myLength - 1; i >= 0; i++) {
				if (myList.get(i) instanceof UndoableCommand) {
					result |= ((UndoableCommand) myList.get(i)).override_undo();
				}
			}
			return result;
		}
		return false;
	}

	@Override
	public boolean add(Command c) {
		return myList.add(c);
	}

	@Override
	public EfficientList<Command> getAllItems() {
		if (myList == null)
			myList = new EfficientList<Command>();
		return myList;
	}

	@Override
	public void clear() {
		myList.clear();
	}

	@Override
	public int length() {
		return getAllItems().myLength;
	}

	@Override
	public boolean isCleared() {
		return getAllItems().myLength == 0;
	}

	@Override
	public void removeEmptyItems() {
		// commands cant be removed automatically so this will do nothing
	}

	@Override
	public String toString() {
		if (this.HasInfoObject())
			return "CG " + getInfoObject().getShortDescr();
		return super.toString();
	}

	public void setProcessListener(ProcessListener t) {
		// TODO think of other point to use ProcessListener..
		myProcessListener = t;
	}

	@Override
	public boolean insert(int pos, Command item) {
		return myList.insert(pos, item);
	}
}
