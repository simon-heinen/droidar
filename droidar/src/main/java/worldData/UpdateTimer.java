package worldData;

import commands.Command;

public class UpdateTimer implements Updateable {

	private static final float DEF_RETRY_TIME = 0.2f;
	/**
	 * set the interval (in s) the {@link UpdateTimer} is triggered and its
	 * command is executed. Example values: 5s; 0.5s
	 */
	private float myTriggerValue;
	private Command myCommand;
	private float time = 0;
	private float myRetryTime = DEF_RETRY_TIME;

	/**
	 * @param timeTrigger
	 *            set the interval (in s) the {@link UpdateTimer} is triggered
	 *            and its command is executed. Example values: 5s; 0.5s
	 * @param commandToExecute
	 *            This command will be executed if the timeTrigger value is
	 *            reached. The {@link Command#execute()}-method has to return
	 *            true to reset the timer! So when the command returns false, it
	 *            will be executed in the next update round (about 20ms after
	 *            the current one) again! <br>
	 * <br>
	 *            Additionally it is possible to set the command to null and
	 *            react on the return value of {@link UpdateTimer#update(float)}
	 *            directly. In some situations this might be easier.
	 */
	public UpdateTimer(float timeTrigger, Command commandToExecute) {
		myTriggerValue = timeTrigger;
		myCommand = commandToExecute;
	}

	/**
	 * See {@link UpdateTimer#UpdateTimer(float, Command)}
	 * 
	 * @param timeTrigger
	 * @param commandToExecute
	 * @param retryTime
	 *            The time in s when the next try will happen. The default value
	 *            is 0.2s
	 */
	public UpdateTimer(float timeTrigger, Command commandToExecute,
			float retryTime) {
		this(timeTrigger, commandToExecute);
		this.myRetryTime = retryTime;
	}

	/**
	 * @param myTriggerValue
	 *            set the interval (in s) the {@link UpdateTimer} is triggered
	 *            and its command is executed. Example values: 5s; 0.5s
	 */
	public void setTriggerTime(float myTriggerValue) {
		this.myTriggerValue = myTriggerValue;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		time += timeDelta;
		if (time > myTriggerValue) {

			/*
			 * if there is no command or the existing command is executed
			 * correctly the timer will be reseted else not!
			 */
			if (myCommand == null || myCommand.execute()) {
				time = 0;
				return true;
			} else {
				/*
				 * The command could not be executed correctly, so now substract
				 * the retry time from the time to control when the next command
				 * execution try will happen
				 */
				time -= myRetryTime;
				if (time < 0)
					time = 0;
			}
		}
		return false;
	}
}
