package commands.system;

import commands.Command;
import commands.undoable.CommandProcessor;

public class UndoCommand extends Command {

	@Override
	public boolean execute() {
		return CommandProcessor.getInstance().undo();
	}

}
