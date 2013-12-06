package listeners.eventManagerListeners;

import util.Vec;
/**
 *  Listener for camera rotation updates. 
 */
public interface CamRotationVecUpdateListener {
	/**
	 * @param target - target location in {@link util.Vec} 
	 * @param values - current location in {@link util.Vec}
	 * @param timeDelta - time between last update. 
	 */
	void onCamRotationVecUpdate(Vec target, Vec values, float timeDelta);
}
