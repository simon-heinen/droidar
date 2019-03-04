package gamelogic;

import util.EfficientList;

public class FeedbackReports {

	private static FeedbackReports myInstance = new FeedbackReports();

	public static FeedbackReports getInstance() {
		return myInstance;
	}

	private EfficientList<ActionFeedback> myFeedbacks = new EfficientList<ActionFeedback>();

	public void addFeedback(ActionFeedback feedback) {
		if (feedback != null)
			myFeedbacks.add(feedback);
	}

	public static void resetInstance() {
		myInstance = new FeedbackReports();
	}

}
