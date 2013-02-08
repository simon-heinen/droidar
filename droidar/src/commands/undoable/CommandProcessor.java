package commands.undoable;

public class CommandProcessor {

	private static CommandProcessor myInstance = new CommandProcessor();
	private CommandProcessorList commandList = new CommandProcessorList();

	public static CommandProcessor getInstance() {
		return myInstance;
	}

	public boolean undo() {
		return commandList.undo();
	}

	public boolean redo() {
		return commandList.redo();
	}

	public void addToList(UndoableCommand c) {
		commandList.add(c);
	}

	public static void resetInstance() {
		myInstance = new CommandProcessor();
	}

}
