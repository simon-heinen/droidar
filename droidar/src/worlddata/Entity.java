package worlddata;

import components.Visitable;
/**
 * Used for relationships between Objs. 
 */
public interface Entity extends Updateable, Visitable {

	/**
	 * This method can be called to move through the scene graph. Every subclass
	 * of {@link Entity} will have to take care that it refreshes its parent as
	 * soon as {@link worlddata.Updateable#update(float, worlddata.Updateable)} is called. <br>
	 * <br>.
	 * See also {@link Entity#setMyParent(worlddata.Updateable)}
	 * 
	 * @return - {@link worlddata.Updateable}
	 */
	Updateable getMyParent();

	/**
	 * This should be called as the first thing in
	 * {@link worlddata.Updateable#update(float, worlddata.Updateable)} if the component ha s
	 * children because these children might call {@link Entity#getMyParent()}
	 * to navigate through the Scene Graph.
	 * 
	 * If the component does not have any children this method can return null
	 * because no child will ever call it
	 * 
	 * @param parent - {@link worlddata.Updateable}
	 */
	void setMyParent(Updateable parent);

}
