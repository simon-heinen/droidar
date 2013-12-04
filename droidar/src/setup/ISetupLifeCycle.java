package setup;

/**
 * Life cycle events for {@link setup.ArSetup}. 
 *
 */
public interface ISetupLifeCycle {
	
	/**
	 * This method is called when the entry is created.  Object instantiation should occur 
	 * at this time. 
	 * Also note when this method is called is not mean that that it is currently in use. 
	 */
	void onCreate();
	
	/**
	 * This method is called when the entry is ready to begin.  This is when 
	 * all needed resources needed to be started
	 */
	void onStart(); 
	
	/**
	 * This method is called when the entry is currently in the background.  This is when 
	 * all needed resources should be halted but not destroyed. 
	 */
	void onPause();
	
	/**
	 * This method is called when the entry is being resumed from an <code>ISetupLifeCycle.onPause()</code>
	 * state.  All resources should resume at this time.  View is active to the user. 
	 */
	void onResume();
	
	/**
	 * This method is called when the entry is done.  All resources should be released or destroyed.
	 */
	void onStop();

}
