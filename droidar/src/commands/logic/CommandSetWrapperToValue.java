package commands.logic;

import util.Log;
import util.Wrapper;
import util.Wrapper.Type;
import worldData.Obj;

import commands.undoable.UndoableCommand;

public class CommandSetWrapperToValue extends UndoableCommand {

	private final Wrapper myW;
	private Wrapper.Type mode = Wrapper.Type.None;

	private String mySValue;
	private String mySBackup;

	private boolean myBValue;
	private boolean myBBackup;

	private int myIValue;
	private int myIBackup;
	private Wrapper mySource;
	private Object myOBackup;
	private Object myOValue;
	private boolean ignorePassedObject;

	public CommandSetWrapperToValue(Wrapper w) {
		myW = w;
	}

	public CommandSetWrapperToValue(Wrapper w, String stringValue) {
		mode = Wrapper.Type.String;
		myW = w;
		mySValue = stringValue;
	}

	public CommandSetWrapperToValue(Wrapper w, int integerValue) {
		mode = Wrapper.Type.Int;
		myW = w;
		myIValue = integerValue;
	}

	public CommandSetWrapperToValue(Wrapper w, Object o) {
		mode = Wrapper.Type.Object;
		myW = w;
		myOValue = o;
	}

	public CommandSetWrapperToValue(Wrapper w, boolean booleanValue) {
		mode = Wrapper.Type.Bool;
		myW = w;
		myBValue = booleanValue;
	}

	public CommandSetWrapperToValue(Wrapper target, Wrapper source) {
		mode = Wrapper.Type.Wrapper;
		myW = target;
		mySource = source;
	}

	public CommandSetWrapperToValue(Wrapper targetWrapper,
			Obj objectToPutIntoWrapper, boolean ignorePassedObject) {
		this(targetWrapper, objectToPutIntoWrapper);
		this.ignorePassedObject = ignorePassedObject;

	}

	@Override
	public boolean override_do() {
		if (myW == null) {
			Log.e("Command Error",
					"CommandSetWrapperToValue.doMethod: wrapper object is null!");
			return false;
		}
		switch (mode) {
		case String:
			mySBackup = myW.getStringValue();
			myW.setTo(mySValue);
			return true;
		case Bool:
			myBBackup = myW.getBooleanValue();
			myW.setTo(myBValue);
			return true;
		case Int:
			myIBackup = myW.getIntValue();
			myW.setTo(myIValue);
			return true;
		case Object:
			myOBackup = myW.getObject();
			myW.setTo(myOValue);
			return true;
		case Wrapper:
			myOBackup = myW.getObject();
			myW.setTo(mySource.getObject());
			return true;
		}
		Log.e("Command Error",
				"CommandSetWrapperToValue.doMethod: mode wasn't set correctly!");
		return false;
	}

	@Override
	public boolean override_do(Object o) {

		if (ignorePassedObject) {
			Log.d("Commands Wrapper", "Ignoring passed object " + o.getClass());
			return false;
		}

		Log.d("Commands Wrapper", "Setting Wrapper(mode=" + mode
				+ ") to value (object=" + o.getClass() + ")");
		if (o instanceof String) {
			if (mode == Type.None || mode == Type.String) {
				Log.d("Commands", "    -> Setting string-wrapper to value=" + o);
				myW.setTo((String) o);
				return true;
			}
		}
		if (o instanceof Wrapper) {
			if (mode == Type.None || mode == Type.Wrapper) {
				Log.d("Commands", "    -> Setting 'wrapper'-wrapper to value="
						+ o);
				myW.setTo(((Wrapper) o));
				return true;
			}
		}
		if (o != null) {
			if (mode == Type.None || mode == Type.Wrapper
					|| mode == Type.Object) {

				/*
				 * special case: if the transfered object itself is a wrapper,
				 * extract the object inside the wrapper and use it istead of
				 * the wrapper itself:
				 */
				if (o instanceof Wrapper) {
					Object o2 = ((Wrapper) o).getObject();
					Log.d("Commands", "    -> Setting object-wrapper to value="
							+ o2 + "(type=" + o2.getClass() + ")");
					myOBackup = myW.getObject();
					myW.setTo(o2);
					return true;
				}

				Log.d("Commands", "    -> Setting object-wrapper to value=" + o
						+ "(type=" + o.getClass() + ")");
				myW.setTo(o);
				return true;
			} else {
				Log.d("Commands", "    -> wrapper doesnt want" + o
						+ ", because it does not have the type " + mode);
			}
		}
		// TODO check for other types
		return false;
	}

	@Override
	public boolean override_undo() {
		switch (mode) {
		case String:
			myW.setTo(mySBackup);
			return true;
		case Bool:
			myW.setTo(myBBackup);
			return true;
		case Int:
			myW.setTo(myIBackup);
			return true;
		case Wrapper:
			myW.setTo(myOBackup);
			return true;
		case Object:
			myW.setTo(myOBackup);
			return true;
		}

		Log.e("Command Error",
				"CommandSetWrapperToValue.undoMethod: mode wasn't set correctly!");
		return false;
	}

}
