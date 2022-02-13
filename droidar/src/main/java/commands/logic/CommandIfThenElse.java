package commands.logic;

import listeners.ProcessListener;
import util.Log;

import commands.Command;

public class CommandIfThenElse extends Command {

	private Command ifCommand;
	private Command thenCommand;
	private Command elseCommand;
	private ProcessListener myProcessListener;

	public CommandIfThenElse(Command cmdif, Command cmdthen, Command celse) {
		ifCommand = cmdif;
		thenCommand = cmdthen;
		elseCommand = celse;
	}

	@Override
	public boolean execute() {
		updateProcess(1);
		if (ifCommand.execute()) {
			if (thenCommand != null) {
				Log.d("Commands", "ifCommand sussesfull, doing then command"
						+ thenCommand);
				updateProcess(2);
				return thenCommand.execute();
			}
		} else {
			if (elseCommand != null) {
				Log.d("Commands",
						"ifCommand not sussesfull, doing else command: "
								+ elseCommand);
				updateProcess(3);
				return elseCommand.execute();
			}
			Log.d("Commands",
					"ifCommand not sussesfull but no else command set");
		}
		return false;
	}

	private void updateProcess(int i) {
		if (myProcessListener != null) {
			myProcessListener.onProcessStep(i, 3, null);
		}
	}

	@Override
	public boolean execute(Object transfairObject) {
		if (ifCommand.execute(transfairObject)) {
			if (thenCommand != null)
				return thenCommand.execute();
		} else {
			if (elseCommand != null)
				return elseCommand.execute();
		}
		return false;
	}

	public void setProcessListener(ProcessListener l) {
		myProcessListener = l;
	}

}
