package commands.logic;

import util.Log;
import util.Wrapper;

import commands.Command;

public class CommandWrapperEqualsCondition extends Command {

	private Wrapper.Type mode;

	private Wrapper myW;
	private String mySValue;
	private boolean myBValue;
	private int myIValue;

	private Command myCommand;

	private Command myElseCommand;

	public CommandWrapperEqualsCondition(Wrapper w, String valueToCompareWith,
			Command commandToExecute) {
		mode = Wrapper.Type.String;
		myW = w;
		mySValue = valueToCompareWith;
		myCommand = commandToExecute;
	}

	public CommandWrapperEqualsCondition(Wrapper w, boolean valueToCompareWith,
			Command ifTrueCommand) {
		mode = Wrapper.Type.Bool;
		myW = w;
		myBValue = valueToCompareWith;
		myCommand = ifTrueCommand;
	}

	public CommandWrapperEqualsCondition(Wrapper w, boolean valueToCompareWith,
			Command ifTrueCommand, Command elseCommand) {
		this(w, valueToCompareWith, ifTrueCommand);
		myElseCommand = elseCommand;
	}

	@Override
	public boolean execute() {

		if (myW == null) {
			Log.e("Command Error",
					"CommandEqualsCondition.doMethod: wrapper object is null!");
			return false;
		}

		Log.d("Commands", "running equals command in mode=" + mode);

		switch (mode) {
		case String:
			if (myW.equals(mySValue)) {
				return myCommand.execute();
			} else if (myElseCommand != null) {
				return myElseCommand.execute();
			}
			return false;
		case Bool:
			Log.d("Commands",
					"myBool=" + myBValue + " wrapperBool="
							+ myW.getBooleanValue());
			if (myW.equals(myBValue)) {
				return myCommand.execute();
			} else if (myElseCommand != null) {
				return myElseCommand.execute();
			}
			return false;
		case Int:
			if (myW.equals(myIValue)) {
				return myCommand.execute();
			} else if (myElseCommand != null) {
				return myElseCommand.execute();
			}
			return false;
		}
		// TODO all cases checked? verify by writing a test
		return false;
	}

	@Override
	public boolean execute(Object transfairObject) {

		if (myW == null) {
			Log.e("Command Error",
					"CommandEqualsCondition.doMethod: wrapper object is null!");
			return false;
		}

		Log.d("Commands", "running equals command in mode=" + mode);

		switch (mode) {
		case String:
			if (myW.equals(mySValue)) {
				return myCommand.execute(transfairObject);
			} else if (myElseCommand != null) {
				return myElseCommand.execute(transfairObject);
			}
			return false;
		case Bool:
			Log.d("Commands",
					"myBool=" + myBValue + " wrapperBool="
							+ myW.getBooleanValue());
			if (myW.equals(myBValue)) {
				return myCommand.execute(transfairObject);
			} else if (myElseCommand != null) {
				return myElseCommand.execute(transfairObject);
			}
			return false;
		case Int:
			if (myW.equals(myIValue)) {
				return myCommand.execute(transfairObject);
			} else if (myElseCommand != null) {
				return myElseCommand.execute(transfairObject);
			}
			return false;
		}
		// TODO all cases checked? verify by writing a test
		return false;

	}

}
