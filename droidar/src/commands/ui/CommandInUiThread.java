package commands.ui;

import android.os.Handler;
import android.os.Looper;

import commands.Command;

/**
 * This command is usefull if you want to change something in the Android UI
 * system. It is only allowed modify UI elements from the UI thread and this
 * {@link Command} will handle the access to this thread for you.
 * 
 * @author Spobo
 * 
 */
public abstract class CommandInUiThread extends Command {

	private Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	public final boolean execute() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				executeInUiThread();
			}

		});
		return true;
	}

	public abstract void executeInUiThread();

}
