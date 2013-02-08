package gui.simpleUI;

@Deprecated
public interface EditItem {
	/**
	 * @param group
	 *            Use this parameter to add {@link ModifierInterface} -Objects
	 *            to it. All the basic modifiers are contained in the
	 *            {@link SimpleUIv1} class
	 * @param message
	 */
	public void customizeScreen(ModifierGroup group, Object message);

}