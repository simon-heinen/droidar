package commands.logic;

import listeners.SelectionListener;
import util.Log;
import util.Wrapper;
import worldData.AbstractObj;

import commands.undoable.UndoableCommand;

/**
 * TODO is this now the exact same class as CommandSetWrapperToValue ??
 * 
 * everything that implements {@link SelectionListener} can be selected by
 * passing through a {@link SelectionInfo}
 * 
 * @author Spobo
 * 
 */
public class CommandSetWrapperToValue2 extends UndoableCommand {

	private Wrapper mySelectionTarget;
	private SelectionListener myObjectToSelect;
	private Object mySelectionBackup;

	public CommandSetWrapperToValue2(Wrapper selectionTarget) {
		mySelectionTarget = selectionTarget;
	}

	public CommandSetWrapperToValue2(Wrapper selectionTarget,
			SelectionListener objectToSelect) {
		mySelectionTarget = selectionTarget;
		myObjectToSelect = objectToSelect;
	}

	public CommandSetWrapperToValue2(Wrapper selectionWrapper, String infoText) {
		this(selectionWrapper);
		getInfoObject().setShortDescr(infoText);
	}

	@Override
	public boolean override_do() {
		Log.d("Commands", "Trying to set selection to predefined object: "
				+ myObjectToSelect);
		if (myObjectToSelect != null) {
			// backup the current selected object:
			mySelectionBackup = mySelectionTarget.getObject();
			mySelectionTarget.setTo(myObjectToSelect);
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean override_do(Object transfairObject) {
		Log.d("Commands",
				"Trying to set selection (is=" + mySelectionTarget.getObject()
						+ ") to " + transfairObject);
		if (transfairObject instanceof Wrapper) {
			mySelectionBackup = (mySelectionTarget).getObject();
			mySelectionTarget.setTo(((Wrapper) transfairObject).getObject());
			Log.d("Commands", "    Selection set correctly.");
			return true;
		}
		if (transfairObject instanceof AbstractObj) {
			mySelectionBackup = (mySelectionTarget).getObject();
			mySelectionTarget.setTo(transfairObject);
			return true;
		}
		return false;
	}

	@Override
	public boolean override_undo() {
		mySelectionTarget.setTo(mySelectionBackup);
		return true;
	}

}
