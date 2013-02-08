package listeners;

public interface ProcessListener {

	void onProcessStep(int pos, int max, Object objectToProcessNow);

}
