package gamelogic;

import gui.simpleUI.EditItem;
import gui.simpleUI.ModifierGroup;

import java.util.Arrays;

import util.Log;
import worldData.Entity;
import worldData.Updateable;
import worldData.Visitor;

import commands.Command;

public class GameParticipant implements Entity, EditItem {

	private static final String LOG_TAG = "GameParticipant";
	private StatList myStatList;
	private ActionList myActionList;
	private GameItemList myGameItemList;
	private String myType;
	private String myName;
	private int myIconId;
	private Updateable myParent;

	public GameParticipant(String type, String participantName, int iconId) {
		myType = type;
		myName = participantName;
		myIconId = iconId;
	}

	public String getName() {
		return myName;
	}

	public String getType() {
		return myType;
	}

	public int getIconId() {
		return myIconId;
	}

	public StatList getStatList() {
		if (myStatList == null)
			myStatList = new StatList();
		return myStatList;
	}

	/**
	 * @param statName
	 * @return {@link Float#NaN} if the stat could not be found!
	 */
	public float getStatValue(String statName) {
		if (myStatList == null) {
			Log.e(LOG_TAG, "Tryed to get " + statName
					+ " from emplty statList (was null)");
			return Float.NaN;
		}
		Stat s = myStatList.get(statName);
		if (s == null) {
			Log.e(LOG_TAG, "Stat " + statName
					+ " could not be found! Returning Float.NaN");
			return Float.NaN;
		}
		return s.getValue();
	}

	public boolean setStatValue(String statName, float newStatValue) {
		if (myStatList == null)
			return false;
		Stat s = getStatList().get(statName);
		if (s == null)
			return false;
		s.setValue(newStatValue);
		return true;
	}

	public ActionList getActionList() {
		if (myActionList == null)
			myActionList = new ActionList();
		return myActionList;
	}

	public GameItemList getGameItemList() {
		if (myGameItemList == null)
			myGameItemList = new GameItemList();
		return myGameItemList;
	}

	public ActionFeedback doAction(String actionName, GameParticipant target) {
		if (actionName == null)
			return null;
		GameAction a = getActionList().get(actionName);
		if (a != null) {
			ActionFeedback feedback = a.doAction(this, target);
			FeedbackReports.getInstance().addFeedback(feedback);
			return feedback;
		}
		return null;
	}

	public void generateEditGUI(ModifierGroup s) {
		if (myStatList != null) {
			myStatList.generateEditGUI(s);
		}
		if (myActionList != null)
			myActionList.generateEditGUI(s);

		if (myGameItemList != null)
			myGameItemList.generateEditGUI(s);
	}

	public void generateViewGUI(ModifierGroup s) {
		if (myStatList != null)
			myStatList.generateViewGUI(s);
		if (myActionList != null)
			myActionList.generateViewGUI(s);
		if (myGameItemList != null)
			myGameItemList.generateViewGUI(s);
	}

	@Override
	public Updateable getMyParent() {
		return myParent;
	}

	@Override
	public void setMyParent(Updateable parent) {
		myParent = parent;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		setMyParent(parent);
		if (myActionList != null)
			myActionList.update(timeDelta, parent);
		if (myStatList != null)
			myStatList.update(timeDelta, parent);
		if (myGameItemList != null)
			myGameItemList.update(timeDelta, parent);
		return true;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

	public boolean addStat(Stat stat) {
		return getStatList().add(stat);
	}

	public boolean addAction(GameAction action) {
		if (action.getOnClickCommand() == null)
			setDefaultExecuteAction(action);
		return getActionList().add(action);
	}

	private void setDefaultExecuteAction(final GameAction action) {
		action.setOnClickCommand(new Command() {
			@Override
			public boolean execute() {
				return action.doAction(GameParticipant.this, null)
						.actionCorrectlyExecuted();
			}
		});
	}

	@Override
	public void customizeScreen(ModifierGroup group, Object message) {

		if (message instanceof String) {
			String m = (String) message;
			String[] keywords = { "Edit", "edit", "editscreen", "Edit screen",
					"edit mode", "editmode", "Editmode" }; // TODO
			if (Arrays.asList(keywords).contains(m)) {
				generateEditGUI(group);
			}
		} else {
			generateViewGUI(group);
		}

	}

}
