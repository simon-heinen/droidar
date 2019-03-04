package system;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;

/**
 * This is a Singleton to pass objects between activities. The two essential
 * methods are:
 * 
 * startActivity(Activity currentActivity, Class ActivityClassToShow, Object
 * objectToPass): to start a new activity and pass an object to it.
 * 
 * loadObjFromNewlyCreatedActivity(Activity theNewActivity): to load the passed
 * object in the new Activity.
 * 
 * @author Spobo
 * 
 */
public class ActivityConnector {

	private static ActivityConnector myInstance = new ActivityConnector();
	public static final String KEY_IDENTIFIER = "key";

	private HashMap<String, Object> myHashMap = new HashMap<String, Object>();
	private int id;

	private ActivityConnector() {
	}

	public Object getObj(String id) {
		return myHashMap.get(id);
	}

	public String getNextKey(Object o) {
		id++;
		return "key=" + o.getClass() + " " + id;
	}

	public void addTransfairObject(String key, Object transfairObject) {
		myHashMap.put(key, transfairObject);
	}

	/**
	 * @param o
	 *            The object which wants to pass some data
	 * @param transfairObject
	 *            the object to send to the activity
	 * @return the key which can be used at .getObject(key)
	 */
	public String addTransfairObject(Object o, Object transfairObject) {
		String key = getNextKey(o);
		myHashMap.put(key, transfairObject);
		return key;
	}

	public static ActivityConnector getInstance() {
		return myInstance;
	}

	/**
	 * @param currentActivity
	 *            The current activity
	 * @param ActivityClassToShow
	 *            the .class object of the activity to start
	 * @param objectToPass
	 *            the object to pass (null if nothing should be passed)
	 */
	@SuppressWarnings("unchecked")
	public void startActivity(Activity currentActivity,
			Class ActivityClassToShow, Object objectToPass) {
		Intent intent = new Intent(currentActivity, ActivityClassToShow);
		if (objectToPass != null) {
			String key = addTransfairObject(currentActivity, objectToPass);
			// The key to the object will be stored in the extras of the
			// activity:
			intent.putExtra(ActivityConnector.KEY_IDENTIFIER, key);
		}
		currentActivity.startActivity(intent);
	}

	/**
	 * @param theNewActivity
	 *            the newly created activity
	 * @return The object which was passed or null if nothing was passed
	 */
	public Object loadObjFromNewlyCreatedActivity(Activity theNewActivity) {
		if (theNewActivity == null)
			return null;
		String key = theNewActivity.getIntent().getExtras()
				.getString(KEY_IDENTIFIER);
		Object o = getObj(key);
		// TODO remove o from the hashmap now?:
		// myHashMap.remove(key);
		return o;
	}

}
