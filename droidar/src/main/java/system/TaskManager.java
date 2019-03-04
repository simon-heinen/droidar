package system;

import listeners.ProcessListener;
import util.EfficientList;
import util.Log;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import commands.Command;

public class TaskManager implements Runnable, ProcessListener {

	private static final String LOG_TAG = "Tast Manager";
	private static TaskManager myInstance = new TaskManager();

	public static TaskManager getInstance() {
		return myInstance;
	}

	private TaskList myTasks;
	private boolean isRunning;
	private Thread managerThread;
	private ProgressBar myProgressWheel;
	private TextView myProgressText;
	private TextView myProgressSizeText;

	private Handler mHandler = new Handler(Looper.getMainLooper());

	private String myIdleText = "";
	private String myWorkingPrefix = "<";
	private String myWorkingMiddle = "/";
	private String myWorkingSuffix = ">";

	public void addHighPrioTask(Command commandToAdd) {
		if (myTasks == null) {
			myTasks = new TaskList();
		}
		myTasks.addHighPrioTask(commandToAdd);
		updateProgressIfNecesarry(myTasks.getMyHighPrioTasks().myLength);
		startTaskManagerIfNecesarry();
	}

	public void addNormalPrioTask(Command commandToAdd) {
		if (myTasks == null) {
			myTasks = new TaskList();
		}
		myTasks.addNormalPrioTask(commandToAdd);
		updateProgressIfNecesarry(myTasks.getMyNormalPrioTasks().myLength);
		startTaskManagerIfNecesarry();
	}

	public void addLowPrioTask(Command commandToAdd) {
		if (myTasks == null) {
			myTasks = new TaskList();
		}
		myTasks.addLowPrioTask(commandToAdd);
		updateProgressIfNecesarry(myTasks.getMyLowPrioTasks().myLength);
		startTaskManagerIfNecesarry();
	}

	private void startTaskManagerIfNecesarry() {
		if (!isRunning()) {
			managerThread = new Thread(this);
			try {
				managerThread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	@Override
	public void run() {
		/*
		 * do not built any infinite loops in here! this should be a one shot
		 * thing
		 */
		setRunning(true);

		boolean anythingLeftToDo = true;
		Log.i(LOG_TAG, "Start executing all tasks!");
		while (anythingLeftToDo) {
			anythingLeftToDo = false;
			while (checkIfIsNotEmpty(myTasks.getMyHighPrioTasks())) {
				Log.d(LOG_TAG, "Working on high priority tasks");
				anythingLeftToDo = runTasksInList(myTasks.getMyHighPrioTasks())
						|| anythingLeftToDo;
			}
			while (checkIfIsNotEmpty(myTasks.getMyNormalPrioTasks())) {
				Log.d(LOG_TAG, "Working on normal priority tasks");
				anythingLeftToDo = runTasksInList(myTasks
						.getMyNormalPrioTasks()) || anythingLeftToDo;
			}
			while (checkIfIsNotEmpty(myTasks.getMyLowPrioTasks())) {
				Log.d(LOG_TAG, "Working on low priority tasks");
				anythingLeftToDo = runTasksInList(myTasks.getMyLowPrioTasks())
						|| anythingLeftToDo;
			}

			// now check if anything was added
			anythingLeftToDo = checkIfIsNotEmpty(myTasks.getMyHighPrioTasks())
					|| anythingLeftToDo;
			anythingLeftToDo = checkIfIsNotEmpty(myTasks.getMyHighPrioTasks())
					|| anythingLeftToDo;
			anythingLeftToDo = checkIfIsNotEmpty(myTasks.getMyHighPrioTasks())
					|| anythingLeftToDo;
		}
		setRunning(false);
		Log.i(LOG_TAG, "Finshed with all tasks. Doing Harakiri now!");
	}

	private synchronized boolean checkIfIsNotEmpty(EfficientList<Command> l) {
		if (l.myLength > 0) {
			return true;
		}
		return false;
	}

	private boolean runTasksInList(EfficientList<Command> l) {
		initGui(myTasks.getMyHighPrioTasks().myLength);
		int i;
		for (i = 0; i < myTasks.getMyHighPrioTasks().myLength; i++) {
			onProcessStep(i, myTasks.getMyHighPrioTasks().myLength, l.get(i));
			// dont use if to better find possible errors:
			// if (l.myArray[i] != null)
			Command x = l.get(i);
			if (x != null) {
				x.execute();
				l.remove(x);
			}
		}
		resetGui();
		return false;
	}

	private void initGui(final int listLength) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (myProgressWheel != null) {
					myProgressWheel.setVisibility(View.VISIBLE);
					myProgressWheel.setMax(listLength);
					myProgressWheel.setProgress(0);
				}
			}
		});
	}

	private void resetGui() {

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				resetProgressText();
				resetProgressWheel();
				resetProgressSizeText();
			}
		});
	}

	private void updateProgressIfNecesarry(final int newListSize) {
		if (isRunning()) {
			if (myProgressWheel != null) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						myProgressWheel.setMax(newListSize);
					}
				});
			}
			if (myProgressSizeText != null) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						myProgressSizeText.setText(myWorkingMiddle
								+ newListSize + myWorkingSuffix);
					}
				});
			}
		}
	}

	public View getProgressWheel(Context context) {
		Log.d(LOG_TAG, "TM loading wheel");
		if (myProgressWheel == null) {
			Log.d(LOG_TAG, "TM new wheel");
			myProgressWheel = new ProgressBar(context);
		}

		if (myProgressWheel.getContext() != context) {
			Log.w(LOG_TAG, "TM wheel had wrong context");
			((ViewGroup) myProgressWheel.getParent())
					.removeView(myProgressWheel);
		}

		resetProgressWheel();
		return myProgressWheel;
	}

	private void resetProgressWheel() {
		if (myProgressWheel != null) {
			myProgressWheel.setVisibility(View.GONE);
		}
	}

	public View getProgressTextView(Context context, String idleText,
			String workingPrefix) {
		if (myProgressText == null) {
			myProgressText = new TextView(context);
		}

		if (myProgressText.getContext() != context) {
			Log.w(LOG_TAG, "TM text had wrong context");
			((ViewGroup) myProgressText.getParent()).removeView(myProgressText);
		}

		if (idleText != null) {
			myIdleText = idleText;
		}
		if (workingPrefix != null) {
			myWorkingPrefix = workingPrefix;
		}
		resetProgressText();
		return myProgressText;
	}

	private void resetProgressText() {
		if (myProgressText != null) {
			myProgressText.setText(myIdleText);
		}
	}

	public View getProgressSizeText(Context context, String idleText,
			String workingMiddle, String workingSuffix) {
		if (myProgressSizeText == null) {
			myProgressSizeText = new TextView(context);
		}

		if (myProgressSizeText.getContext() != context) {
			Log.w(LOG_TAG, "TM size text had wrong context");
			((ViewGroup) myProgressSizeText.getParent())
					.removeView(myProgressSizeText);
		}

		if (idleText != null) {
			myIdleText = idleText;
		}
		if (workingMiddle != null) {
			myWorkingMiddle = workingMiddle;
		}
		if (workingSuffix != null) {
			myWorkingSuffix = workingSuffix;
		}
		resetProgressSizeText();
		return myProgressSizeText;
	}

	private void resetProgressSizeText() {
		if (myProgressSizeText != null) {
			myProgressSizeText.setText(myIdleText);
		}
	}

	@Override
	public void onProcessStep(final int pos, final int max,
			Object objectToProcessNow) {

		// Update the progress bar
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (myProgressWheel != null) {
					myProgressWheel.setMax(max);
					myProgressWheel.setProgress(pos);
				}
				if (myProgressText != null) {
					myProgressText.setText(myWorkingPrefix + pos);
				}
				if (myProgressSizeText != null) {
					myProgressSizeText.setText(myWorkingMiddle + max
							+ myWorkingSuffix);
				}
			}
		});
	}

	public static void resetInstance() {
		myInstance = new TaskManager();
	}

}
