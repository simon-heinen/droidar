package v2.simpleUi.uiDecoration;

public interface UiDecoratable {

	/**
	 * 
	 * 
	 * @param decorator
	 *            e.g. the {@link ExampleDecorator}
	 * @return true if the decorator could be assigned to all children and their
	 *         sub-children
	 */
	public boolean assignNewDecorator(UiDecorator decorator);

}
