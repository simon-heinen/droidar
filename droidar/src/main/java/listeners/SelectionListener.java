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
	 * {@link MeshComponent}) if possible
	 * 
	 * @param c
	 */
    void setOnClickCommand(Command c);

	/**
	 * This will enable the selection mechanism (like color-picking in the
	 * {@link MeshComponent}) if possible
	 * 
	 * @param c
	 */
    void setOnDoubleClickCommand(Command c);

	/**
	 * This will enable the selection mechanism (like color-picking in the
	 * {@link MeshComponent}) if possible
	 * 
	 * @param c
	 */
    void setOnLongClickCommand(Command c);

	void setOnMapClickCommand(Command c);

}
