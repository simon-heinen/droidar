package entry;

/**
 * This will determine what type of entry point are we currently using.  This is 
 * important because if the entry is an <code>android.app.Activity</code> it will need to have the 
 * ability to create its on UI in full detail. However, if the entry point is a <code>android.app.Fragment</code>
 * then some of the UI my be handled by the base activity that contains the <code>android.app.Fragmnent</code>.
 * This also prevents having to use <code>instanceOf</code> to determine the type and to support future types 
 * like GoogleGlass.  
 * This will mostly be used by the <code>system.Setup</code>.   
 * @author rvieras
 *
 */
public enum ArType {
	ACTIVITY,
	FRAGMENT
}
