package gui.simpleUI;

import android.app.Activity;

public interface UIConfig {

	public Theme loadTheme();

	public ModifierInterface loadCloseButtonsFor(
			final Activity currentActivity, final ModifierGroup group);
}
