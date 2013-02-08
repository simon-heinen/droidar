package listeners;

import system.Setup;

/**
 * this listener interface can be used to enable feedback for the user and for
 * debugging purpose. To get an overview over the different steps take a look at
 * the constants in the {@link Setup}-class
 * 
 * Any custom setup can add additional steps by using the doSetupStep()-method
 * provided by the Setup-class.
 * 
 * @author Spobo
 * 
 */
public interface SetupListener {

	/**
	 * @param msTheLastStepTook
	 *            the milliseconds the last step did take to perform. This value
	 *            can be used for debugging and optimizations. It will be 0 when
	 *            the first step is done.
	 * @param statusText
	 *            This is the description of the current step. It can be used to
	 *            display it to the user for more feedback e.g.
	 */
	void onNextStep(double msTheLastStepTook, String statusText);

}
