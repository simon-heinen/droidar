package commands.system;

import commands.Command;
import commands.undoable.CommandProcessor;

public class RedoCommand extends Command {

	@Override
	public boolean execute() {
		return CommandProcessor.getInstance().redo();
	}

}
