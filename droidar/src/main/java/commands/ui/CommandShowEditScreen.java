package commands.ui;

import gui.simpleUI.EditItem;
import gui.simpleUI.SimpleUIv1;
import android.app.Activity;

import commands.Command;

public class CommandShowEditScreen extends Command {

	private Activity myCurrentActivity;
	private EditItem myObjectToEdit;
	private Object myOptionalMessage;

	public CommandShowEditScreen(Activity currentActivity, EditItem objectToEdit) {
		myCurrentActivity = currentActivity;
		myObjectToEdit = objectToEdit;
	}

	public CommandShowEditScreen(Activity currentActivity,
			EditItem objectToEdit, Object optionalMessage) {
		myCurrentActivity = currentActivity;
		myObjectToEdit = objectToEdit;
		myOptionalMessage = optionalMessage;
	}

	@Override
	public boolean execute() {
		SimpleUIv1.showEditScreen(myCurrentActivity, myObjectToEdit,
				myOptionalMessage);
		return true;
	}
}
