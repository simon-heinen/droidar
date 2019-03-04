package gamelogic;

import util.Log;

public class ActionFeedback {

	private static final String SPACES = "   ";
	private static final String A = "+ ";
	private String myLog;
	private boolean actionCorrectExecuted;

	public ActionFeedback(String name) {
		myLog = A + name + "\n";
		Log.v("Feedback", name);
	}

	public void addInfo(String name, float value) {
		Log.v("Feedback", name + "=" + value);
		myLog += SPACES + name + "=" + value + "\n";
		// TODO store in list
	}

	public void addInfo(String infoText) {
		Log.v("Feedback", infoText);
		myLog += SPACES + infoText + "\n";
	}

	public void setActionCorrectExecuted(boolean actionCorrectExecuted) {
		this.actionCorrectExecuted = actionCorrectExecuted;
	}

	public boolean actionCorrectlyExecuted() {
		return actionCorrectExecuted;
	}

	@Override
	public String toString() {
		return myLog;
	}

}
