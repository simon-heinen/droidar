package listeners;

import commands.Command;
/**
 * Interface for handling UI interaction.  
 */
public interface SelectionListener {

	/**
	 * Get the on click command. 
	 * @return - {@link commands.Command}
	 */
	Command getOnClickCommand();

	/**
	 * Get the on long click command. 
	 * @return - {@link commands.Command}
	 */
	Command getOnLongClickCommand();

	/**
	 * Get the on map click command. 
	 * @return - {@link commands.Command}
	 */
	Command getOnMapClickCommand();

	/**
	 * Get the on double click command. 
	 * @return - {@link commands.Command}
	 */
	Command getOnDoubleClickCommand();

	/**
	 * This will enable the selection mechanism (like color-picking in the
	 * {@link gl.scenegraph.MeshComponent}) if possible.
	 * 
	 * @param cmd - {@link commands.Command}
	 */
	void setOnClickCommand(Command cmd);

	/**
	 * This will enable the selection mechanism (like color-picking in the
	 * {@link gl.scenegraph.MeshComponent}) if possible.
	 * 
	 * @param cmd - {@link commands.Command}
	 */
	void setOnDoubleClickCommand(Command cmd);

	/**
	 * This will enable the selection mechanism (like color-picking in the
	 * {@link gl.scenegraph.MeshComponent}) if possible.
	 * 
	 * @param cmd - - {@link commands.Command}
	 */
	void setOnLongClickCommand(Command cmd);

	/**
	 * This will enable the selection mechanism (like color-picking in the
	 * {@link gl.scenegraph.MeshComponent}) if possible.
	 * 
	 * @param cmd - - {@link commands.Command}
	 */
	void setOnMapClickCommand(Command cmd);

}
