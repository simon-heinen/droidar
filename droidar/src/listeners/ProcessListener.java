package listeners;
/**
 * TODO: Not sure what this interface if for yet.
 */
public interface ProcessListener {

	/**
	 * Process an object. 
	 * @param pos - current
	 * @param max - max
	 * @param objectToProcessNow - object to process
	 */
	void onProcessStep(int pos, int max, Object objectToProcessNow);

}
