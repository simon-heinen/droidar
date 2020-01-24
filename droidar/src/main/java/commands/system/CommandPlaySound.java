package commands.system;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;

import commands.Command;

public class CommandPlaySound extends Command {

	private int mySoundId;
	private Context myContext;
	private String mySoundPath;

	public CommandPlaySound(Context context, int soundId) {
		myContext = context;
		mySoundId = soundId;
	}

	/**
	 * @param soundPath
	 *            eg "/sdcard/test.mp3"
	 */
	public CommandPlaySound(String soundPath) {
		mySoundPath = soundPath;
	}

	@Override
	public boolean execute() {
		if (myContext != null && mySoundId != 0) {
			MediaPlayer.create(myContext, mySoundId).start();
			return true;
		}
		if (mySoundPath != null) {
			try {
				MediaPlayer mp = new MediaPlayer();
				mp.setDataSource(mySoundPath);
				mp.prepare();
				mp.start();
				return true;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalStateException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

}
