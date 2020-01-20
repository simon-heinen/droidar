package gui.simpleUI;

import android.app.Activity;

public interface UIConfig {

	Theme loadTheme();

	ModifierInterface loadCloseButtonsFor(
            final Activity currentActivity, final ModifierGroup group);
}
