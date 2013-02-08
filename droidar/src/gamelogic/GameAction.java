package gamelogic;

import util.Log;
import worldData.Updateable;

public abstract class GameAction extends GameElement {

	private static final String LOG_TAG = "GameAction";
	private StatList myStatList;
	private float myCooldownProgress;
	private float myCooldownTime;
	private boolean updateListeners;

	public GameAction(String uniqueName, float cooldownTime, int iconId) {
		super(uniqueName, iconId);
		setCooldownTime(cooldownTime);
		setCooldownProgress(cooldownTime);
	}

	/**
	 * @param initiator
	 * @param target
	 *            might be null so always consider that!
	 * 
	 * @return an ActionFeedback object with all information for fine-tuning and
	 *         balancing should be returned. if the method is manually called
	 *         the ActionFeedback should be registered in the FeedBackReports
	 *         singleton
	 */
	public abstract ActionFeedback onActionStart(GameParticipant initiator,
			GameParticipant target);

	@Override
	public boolean updateListeners() {
		return updateListeners;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		if (myCooldownProgress < myCooldownTime) {
			// update cooldown time:
			myCooldownProgress = myCooldownProgress + timeDelta;
			if (myCooldownProgress > myCooldownTime) {
				myCooldownProgress = myCooldownTime;
				super.update(timeDelta, parent);
				updateListeners = false;
			} else {
				super.update(timeDelta, parent);
			}
		} else {
			super.update(timeDelta, parent);
		}
		myStatList.update(timeDelta, this);
		return true;
	}

	@Override
	public boolean isAllowedToExecuteOnClickAction() {
		return myCooldownTime == myCooldownProgress;
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

	public boolean addStat(Stat stat) {
		return getStatList().add(stat);
	}

	public float getCooldownProgress() {
		return myCooldownProgress;
	}

	public float getCooldownTime() {
		return myCooldownTime;
	}

	public void setCooldownProgress(float cooldownProgress) {
		myCooldownProgress = cooldownProgress;
	}

	public void setCooldownTime(float myCooldownTime) {
		this.myCooldownTime = myCooldownTime;
	}

	public ActionFeedback doAction(GameParticipant initiator,
			GameParticipant target) {
		setCooldownProgress(0);
		updateListeners = true;
		return onActionStart(initiator, target);
	}

}
