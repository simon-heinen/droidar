package worldData;

import gui.MetaInfos;

/**
 * TODO write why this interface is useful! TODO redesign this, not a way to do
 * it.. HasInfoInterface is not a good method and does not fit the standards
 * 
 * @author Spobo
 * 
 */
public interface HasInfosInterface {

	/**
	 * @return normally not null, on default a infoObject is initialized of
	 *         there was none before this request
	 */
	public MetaInfos getInfoObject();

	/**
	 * @return true if the object currently has a info object
	 */
	public boolean HasInfoObject();

}
