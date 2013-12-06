package listeners;

import gui.MetaInfos;
/**
 * Item selected listener. 
 */
public interface ItemSelectedListener {
	/**
	 * @param metaInfos - {@link gui.MetaInfos}
	 * @return - true is everything processes correctly. 
	 */
	boolean onItemSelected(MetaInfos metaInfos);

}