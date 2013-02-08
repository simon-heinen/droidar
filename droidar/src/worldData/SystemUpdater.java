package worldData;

import util.EfficientList;
import android.os.SystemClock;
import android.util.Log;

public class SystemUpdater implements Runnable {

	private final String LOG_TAG = "SystemUpdater";
	/*
	 * delay should be reduced if the game process gets more complecated! TODO
	 * do this dynamically so that each update cycle is about 20ms long
	 */
	private static final long GAME_THREAD_DELAY = 20;
	private boolean running = true;

	/**
	 * this is the delay the WorldUpdater checks if it has to return from
	 * sleeping when paused and a resume request happens
	 */
	private static final long GAME_THREAD_NOT_KILLED_DELAY = 700;
	private boolean notKilled = true;
	private long lastTimeInMs;

	private EfficientList<Updateable> myObjectsToUpdate = new EfficientList<Updateable>();

	@Override
	public void run() {

		/*
		 * When the systemUpdater is started the first time, it will check for a
		 * special type of UpdatableObjects, the UpdatableWithInit objects and
		 * call their init method before executing the actual update loop
		 */
		for (int i = 0; i < myObjectsToUpdate.myLength; i++) {
			if (myObjectsToUpdate.get(i) instanceof UpdatableWithInit)
				((UpdatableWithInit) myObjectsToUpdate.get(i)).init();
		}
		lastTimeInMs = SystemClock.uptimeMillis();
		while (notKilled) {
			while (running) {
				final long currentTime = SystemClock.uptimeMillis();
				final float timeDelta = (currentTime - lastTimeInMs) / 1000.0f;
				lastTimeInMs = currentTime;
				for (int i = 0; i < myObjectsToUpdate.myLength; i++) {
					if (!myObjectsToUpdate.get(i).update(timeDelta, null)) {
						myObjectsToUpdate.remove(myObjectsToUpdate.get(i));
					}
				}

				try {
					Thread.sleep(GAME_THREAD_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
					killUpdaterThread();
				}
			}
			try {
				Thread.sleep(GAME_THREAD_NOT_KILLED_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
				killUpdaterThread();
			}
		}
	}

	public void addObjectToUpdateCycle(Updateable updateableObject) {
		if (myObjectsToUpdate.contains(updateableObject) == -1)
			myObjectsToUpdate.add(updateableObject);
		else
			Log.e(LOG_TAG, "The object " + updateableObject
					+ " will not be added twice to the Updater! "
					+ "Only add it once, check the code!");
	}

	public boolean removeObjectFromUpdateCylce(Updateable updateableObject) {
		return myObjectsToUpdate.remove(updateableObject);
	}

	public void pauseUpdater() {
		running = false;
	}

	public void resumeUpdater() {
		running = true;
	}

	/**
	 * This can't be undone! If you want to stop the {@link SystemUpdater} use
	 * pauseUpdater() only if you do not need any updates anymore you can kill
	 * it completely with this method
	 */
	public void killUpdaterThread() {
		notKilled = false;
		running = false;
	}

}
