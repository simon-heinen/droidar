package v2.simpleUi;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;

/**
 * You need to add <br>
 * android:name="v2.simpleUi.SimpleUiApplication"<br>
 * in the application tag in the manifest.
 * 
 * 
 * 
 * ((SimpleUiApplication) a.getApplication()).getTransfairList().put( newKey,
 * itemToDisplay);
 * 
 * 
 * 
 * 
 * @author Simon Heinen
 * 
 */
public class SimpleUiApplication extends Application {
	private HashMap<String, Object> transferList;
	private static Context context;

	public HashMap<String, Object> getTransferList() {
		if (transferList == null) {
			transferList = new HashMap<String, Object>();
		}
		return transferList;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.context = getApplicationContext();
	}

	public static void setContext(Context context) {
		if (context != null) {
			SimpleUiApplication.context = context;
		}
	}

	public static Context getContext() {
		return context;
	}
}
