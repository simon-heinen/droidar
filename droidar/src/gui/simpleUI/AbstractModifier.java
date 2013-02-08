package gui.simpleUI;

public abstract class AbstractModifier implements ModifierInterface {

	private Theme myTheme;

	/**
	 * This will set a theme and all newly added children will get this theme
	 * too. So first set a theme, then add the children!
	 * 
	 * @param myTheme
	 */
	public void setTheme(Theme myTheme) {
		this.myTheme = myTheme;
	}

	public Theme getTheme() {
		return myTheme;
	}

}