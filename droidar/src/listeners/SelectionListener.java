package listeners;

import gl.scenegraph.MeshComponent;

import commands.Command;

public interface SelectionListener {

	Command getOnClickCommand();

	Command getOnLongClickCommand();

	Command getOnMapClickCommand();

	Command getOnDoubleClickCommand();

	/**
	 * This will enable the selection mechanism (like color-picking in the
	 * {@link MeshComponent}) if possible.
	 * 
	 * @param cmd - {@link commands.Command}
	 */
	void setOnClickCommand(Command cmd);

	/**
	 * This will enable the selection mechanism (like color-picking in the
	 * {@link MeshComponent}) if possible.
	 * 
	 * @param cmd - {@link commands.Command}
	 */
	void setOnDoubleClickCommand(Command cmd);

	/**
	 * This will enable the selection mechanism (like color-picking in the
	 * {@link MeshComponent}) if possible
	 * 
	 * @param cmd - - {@link commands.Command}
	 */
	public void setOnLongClickCommand(Command cmd);

	public void setOnMapClickCommand(Command cmd);

}
