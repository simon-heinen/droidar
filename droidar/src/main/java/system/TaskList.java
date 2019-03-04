package system;

import util.EfficientList;
import util.Log;

import commands.Command;
import commands.CommandGroup;
import commands.logic.CommandIfThenElse;

public class TaskList {

	private EfficientList<Command> myHighPrioTasks = new EfficientList<Command>();
	private EfficientList<Command> myNormalPrioTasks = new EfficientList<Command>();
	private EfficientList<Command> myLowPrioTasks = new EfficientList<Command>();

	public synchronized void addHighPrioTask(Command commandToAdd) {
		addCommandInTaskList(myHighPrioTasks, commandToAdd);
	}

	public synchronized void addNormalPrioTask(Command commandToAdd) {
		addCommandInTaskList(myNormalPrioTasks, commandToAdd);
	}

	public synchronized void addLowPrioTask(Command commandToAdd) {
		addCommandInTaskList(myLowPrioTasks, commandToAdd);
	}

	private void addCommandInTaskList(EfficientList<Command> list,
			Command commandToAdd) {
		Log.d("TaskManager", "Adding command (" + commandToAdd
				+ ") to taskList!");
		if (commandToAdd instanceof CommandGroup) {
			Log.d("TaskManager",
					"Adding taskmanager as listener to CommandGroup");
			((CommandGroup) commandToAdd).setProcessListener(TaskManager
					.getInstance());
		}
		if (commandToAdd instanceof CommandIfThenElse) {
			Log.d("TaskManager",
					"Adding taskmanager as listener to CommandGroup");
			((CommandIfThenElse) commandToAdd).setProcessListener(TaskManager
					.getInstance());
		}
		list.add(commandToAdd);
	}

	public synchronized EfficientList<Command> getMyHighPrioTasks() {
		return myHighPrioTasks;
	}

	public synchronized EfficientList<Command> getMyLowPrioTasks() {
		return myLowPrioTasks;
	}

	public synchronized EfficientList<Command> getMyNormalPrioTasks() {
		return myNormalPrioTasks;
	}

}
