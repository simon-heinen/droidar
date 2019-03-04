package listeners;

import gl.scenegraph.MeshComponent;

import commands.Command;

public interface SelectionListener {

	public Command getOnClickCommand();

	public Command getOnLongClickCommand();

	public Command getOnMapClickCommand();

	public Command getOnDoubleClickCommand();

	/**
	 * This will enable the selection mechanism (like color-picking in the
	 * {@link MeshComponent}) if possible
	 * 
	 * @param c
	 */
	public void setOnClickCommand(Command c);

	/**
	 * This will enable the selection mechanism (like color-picking in the
	 * {@link MeshComponent}) if possible
	 * 
	 * @param c
	 */
	public void setOnDoubleClickCommand(Command c);

	/**
	 * This will enable the selection mechanism (like color-picking in the
	 * {@link MeshComponent}) if possible
	 * 
	 * @param c
	 */
	public void setOnLongClickCommand(Command c);

	public void setOnMapClickCommand(Command c);

}
