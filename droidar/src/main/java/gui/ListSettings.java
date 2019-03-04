package gui;

import android.widget.ListAdapter;

import commands.Command;
import commands.undoable.UndoableCommand;

public class ListSettings {
	protected ListAdapter adapter;

	protected Command myDefaultClickCommand;
	protected UndoableCommand myDefaultLongClickCommand;

	/**
	 * will be executed when a item in the list is clicked and the onClick event
	 * is executed correctly (returns true)
	 */
	protected Command myCommandOnCorrectClick;
	/**
	 * will be executed when a item in the list is long clicked and the onClick
	 * event is executed correctly (returns true)
	 */
	protected Command myCommandOnCorrectLongClick;

	protected boolean closeOnCorrectClick = true;

	protected Command myMenuCommands;

	private String myActivityName;

	public ListSettings(ListAdapter adapter, boolean closeOnCorrectClick,
			Command commandOnCorrectClick, Command commandOnCorrectLongClick,
			Command defaultClickCommand,
			UndoableCommand defaultLongClickCommand,
			UndoableCommand menuCommands, String activityName) {
		this.adapter = adapter;
		this.closeOnCorrectClick = closeOnCorrectClick;
		myCommandOnCorrectClick = commandOnCorrectClick;
		myCommandOnCorrectLongClick = commandOnCorrectLongClick;
		myDefaultClickCommand = defaultClickCommand;
		myDefaultLongClickCommand = defaultLongClickCommand;
		myMenuCommands = menuCommands;
		myActivityName = activityName;
	}

	public CharSequence getActivityTitle() {
		return myActivityName;
	}

}
