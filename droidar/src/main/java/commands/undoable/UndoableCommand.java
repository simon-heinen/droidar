package commands.undoable;

import gui.ListItem;
import android.app.ListActivity;

import commands.Command;

/**
 * TODO rename UndoableCommand
 * 
 * @author Spobo
 * 
 */
public abstract class UndoableCommand extends Command {

	// private boolean shouldBeLogged = true;

	/**
	 * Here the {@link CommandProcessor} is informed to execute this
	 * {@link UndoableCommand}. If you dont want the Command to be logged,
	 * override this method and call overide_do() directly
	 */
	@Override
	public boolean execute() {

		if (this.override_do()) {
			/*
			 * if the command was executed correctly with an parameter add it to
			 * the command processors list to enable undo for it:
			 */
			CommandProcessor.getInstance().addToList(this);
			return true;
		}
		return false;
	}

	public abstract boolean override_do();

	public abstract boolean override_undo();

	/**
	 * if an command might need some send information you sould call this method
	 * and pass this information as the transfairObject. if the command knows
	 * how to use this information it will use it, if not the normal execute()
	 * method will be called
	 * 
	 * @param transfairObject
	 */
	@Override
	public boolean execute(Object transfairObject) {

		if (this.override_do(transfairObject)) {
			/*
			 * if the command was executed correctly with an parameter add it to
			 * the command processors list to enable undo for it:
			 */
			CommandProcessor.getInstance().addToList(this);
			return true;
		} else {
			return this.execute();
		}
	}

	/**
	 * @param transfairObject
	 *            in some cases an object is passed to the command (eg when
	 *            clicking on a {@link ListItem} in a {@link ListActivity} then
	 *            the {@link ListActivity} will pass this item to the
	 *            onClick-Command because it might need it)
	 * @return true is it was handled correctly. if false is returned then the
	 *         default execute() method of the command will be executed!
	 */
	protected boolean override_do(Object transfairObject) {
		/*
		 * dont send out a warning because most commands wont need to implement
		 * it but the develpoer might use this method anyway to get shure no
		 * information is lost
		 */
		return false;
	}

}
